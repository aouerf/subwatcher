package io.github.aouerfelli.subwatcher.repository

import io.github.aouerfelli.subwatcher.network.RedditService
import io.github.aouerfelli.subwatcher.util.nullIfEmpty
import io.github.aouerfelli.subwatcher.util.toEncodedImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubredditRepository @Inject constructor(private val service: RedditService) {

    private val _subreddits = ConflatedBroadcastChannel<Set<Subreddit>>(emptySet())
    val subreddits = _subreddits.asFlow().mapLatest { it.toList() }

    private suspend fun fetchSubreddit(subredditName: SubredditName): Subreddit {
        val aboutSubreddit = service.getAboutSubreddit(subredditName.value).data
        return Subreddit(
            id = SubredditId(aboutSubreddit.id),
            name = SubredditName(aboutSubreddit.displayName),
            iconImage = aboutSubreddit.iconImageUrl.nullIfEmpty()?.toEncodedImage()
        )
    }

    suspend fun addSubreddit(subredditName: String): Subreddit {
        val currentSubreddits = _subreddits.value
        val newSubredditName = SubredditName(subredditName)

        val duplicateSubreddit = currentSubreddits.firstOrNull { it.name == newSubredditName }
        if (duplicateSubreddit != null) {
            return duplicateSubreddit
        }

        val newSubreddit = fetchSubreddit(newSubredditName)
        _subreddits.send(currentSubreddits + newSubreddit)
        return newSubreddit
    }

    suspend fun refreshSubreddits() {
        coroutineScope {
            val newSubreddits = _subreddits.value.map { subreddit ->
                async(context = Dispatchers.IO) { fetchSubreddit(subreddit.name) }
            }.awaitAll().toSet()
            _subreddits.send(newSubreddits)
        }
    }
}
