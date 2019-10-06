package io.github.aouerfelli.subwatcher.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.SubredditEntityQueries
import io.github.aouerfelli.subwatcher.network.RedditService
import io.github.aouerfelli.subwatcher.util.nullIfEmpty
import io.github.aouerfelli.subwatcher.util.toEncodedImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubredditRepository @Inject constructor(
    private val service: RedditService,
    private val databaseQueries: SubredditEntityQueries
) {

    val subreddits = databaseQueries.selectAll().asFlow().mapToList()

    private suspend fun fetchAndMapSubreddit(subredditName: SubredditName): Subreddit {
        val aboutSubreddit = service.getAboutSubreddit(subredditName.value).data
        return Subreddit.Impl(
            id = SubredditId(aboutSubreddit.id),
            name = SubredditName(aboutSubreddit.displayName),
            iconImage = aboutSubreddit.iconImageUrl.nullIfEmpty()?.toEncodedImage()
        )
    }

    suspend fun addSubreddit(subredditName: String): Subreddit {
        val newSubreddit = fetchAndMapSubreddit(SubredditName(subredditName))
        databaseQueries.insert(newSubreddit)
        return newSubreddit
    }

    suspend fun refreshSubreddits() {
        // TODO: Wait for concurrent Flow (https://github.com/Kotlin/kotlinx.coroutines/issues/1147)
        coroutineScope {
            val refreshedSubreddits = subreddits.first().map { subreddit ->
                async(context = Dispatchers.IO) { fetchAndMapSubreddit(subreddit.name) }
            }.awaitAll()

            databaseQueries.transaction {
                refreshedSubreddits.forEach { subreddit ->
                    with(subreddit) {
                        databaseQueries.update(id = id, name = name, iconImage = iconImage)
                    }
                }
            }
        }
    }
}
