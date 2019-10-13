package io.github.aouerfelli.subwatcher.repository

import android.database.sqlite.SQLiteConstraintException
import androidx.core.net.toUri
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.SubredditEntityQueries
import io.github.aouerfelli.subwatcher.network.AboutSubreddit
import io.github.aouerfelli.subwatcher.network.NetworkResponse
import io.github.aouerfelli.subwatcher.network.RedditService
import io.github.aouerfelli.subwatcher.network.fetch
import io.github.aouerfelli.subwatcher.util.nullIfEmpty
import io.github.aouerfelli.subwatcher.util.toImageBlob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubredditRepository @Inject constructor(
    private val service: RedditService,
    private val database: SubredditEntityQueries
) {

    val subreddits = database.selectAll().asFlow().mapToList()

    private val _states = ConflatedBroadcastChannel<State>()
    val states = _states.asFlow()

    private suspend fun AboutSubreddit.mapSubreddit(): Subreddit {
        return with(data) {
            Subreddit.Impl(
                id = SubredditId(id),
                name = SubredditName(displayName),
                iconImage = iconImageUrl?.nullIfEmpty()?.toUri()?.toImageBlob()
            )
        }
    }

    private suspend fun fetchSubreddit(name: SubredditName): Pair<State, Subreddit?> {
        return when (val response = service.fetch { getAboutSubreddit(name.value) }) {
            is NetworkResponse.Success -> State.SUCCESS to response.body.mapSubreddit()
            is NetworkResponse.Failure -> State.NETWORK_FAILURE to null
            NetworkResponse.Error -> State.CONNECTION_ERROR to null
        }
    }

    private fun insertSubreddit(subreddit: Subreddit): State {
        return try {
            database.insert(subreddit)
            State.SUCCESS
        } catch (e: SQLiteConstraintException) {
            State.DATABASE_FAILURE
        }
    }

    suspend fun addSubreddit(subredditName: String): Subreddit? {
        _states.offer(State.LOADING)

        val name = SubredditName(subredditName)

        val existingSubreddit = database.select(name).executeAsOneOrNull()
        if (existingSubreddit != null) {
            _states.offer(State.SUCCESS)
            return existingSubreddit
        }

        val (networkState, subreddit) = fetchSubreddit(name)
        if (subreddit == null) {
            _states.offer(networkState)
            return null
        }

        val databaseState = insertSubreddit(subreddit)
        _states.offer(databaseState)
        return if (databaseState == State.SUCCESS) subreddit else null
    }

    private suspend fun refreshSubreddit(subreddit: Subreddit): Boolean {
        when (val response = service.fetch { getAboutSubreddit(subreddit.name.value) }) {
            is NetworkResponse.Success -> {
                with(response.body.mapSubreddit()) {
                    database.update(id = id, name = name, iconImage = iconImage)
                }
            }
            is NetworkResponse.Failure -> {
                database.delete(subreddit.id)
            }
            NetworkResponse.Error -> {
                return false
            }
        }
        return true
    }

    suspend fun refreshSubreddits() {
        coroutineScope {
            _states.offer(State.LOADING)

            var noInternet = false
            val subreddits = database.selectAll().executeAsList()
            // TODO: Wait for concurrent Flow (https://github.com/Kotlin/kotlinx.coroutines/issues/1147)
            subreddits.map { subreddit ->
                async(Dispatchers.IO) { noInternet = !refreshSubreddit(subreddit) }
            }.awaitAll()

            val finalState = if (noInternet) State.CONNECTION_ERROR else State.SUCCESS
            _states.offer(finalState)
        }
    }
}
