package com.aouerfelli.subwatcher.repository

import android.database.sqlite.SQLiteConstraintException
import com.aouerfelli.subwatcher.database.Subreddit
import com.aouerfelli.subwatcher.database.SubredditQueries
import com.aouerfelli.subwatcher.network.AboutSubreddit
import com.aouerfelli.subwatcher.network.RedditService
import com.aouerfelli.subwatcher.network.Response
import com.aouerfelli.subwatcher.network.fetch
import com.aouerfelli.subwatcher.util.CoroutineDispatchers
import com.aouerfelli.subwatcher.util.extensions.forEachAsync
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubredditRepository @Inject constructor(
  private val api: RedditService,
  private val db: SubredditQueries,
  private val coroutineDispatchers: CoroutineDispatchers
) {

  private inline fun <T : Any, U : Any> Response<T>.mapToResult(
    transform: (T) -> U
  ): Result<U> {
    return when (this) {
      is Response.Success -> Result.success(transform(body))
      is Response.Failure -> when (this) {
        is Response.Failure.Fetch -> Result.networkError()
        Response.Failure.Parse -> Result.networkFailure()
      }
      Response.Error -> Result.connectionError()
    }
  }

  private fun AboutSubreddit.mapSubreddit(): Subreddit {
    return with(data) {
      Subreddit(
        name = SubredditName(displayName),
        iconUrl = iconImageUrl?.ifEmpty { null }?.let(::SubredditIconUrl),
        lastPosted = null
      )
    }
  }

  private suspend fun fetchSubreddit(name: SubredditName): Result<Subreddit> {
    val response = api.fetch { getAboutSubreddit(name.name) }
    return response.mapToResult { it.mapSubreddit() }
  }

  fun getSubredditsFlow(): Flow<List<Subreddit>> {
    return db.selectAll().asFlow().mapToList(coroutineDispatchers.io).distinctUntilChanged()
  }

  suspend fun getSubreddits(): List<Subreddit> {
    return withContext(coroutineDispatchers.io) {
      db.selectAll().executeAsList()
    }
  }

  suspend fun getSubreddit(subredditName: SubredditName): Subreddit? {
    return withContext(coroutineDispatchers.io) {
      db.select(subredditName).executeAsOneOrNull()
    }
  }

  private fun copyLastPosted(subreddit: Subreddit, lastPosted: SubredditLastPosted?): Subreddit {
    return subreddit.copy(lastPosted = lastPosted)
  }

  private suspend fun insertSubreddit(subreddit: Subreddit): Result<Subreddit> {
    return withContext(coroutineDispatchers.io) {
      try {
        db.insert(subreddit)
        Result.success(subreddit)
      } catch (e: SQLiteConstraintException) {
        Result.databaseFailure()
      }
    }
  }

  suspend fun addSubreddit(subredditName: SubredditName): Result<Subreddit> {
    val existingSubreddit = getSubreddit(subredditName)
    if (existingSubreddit != null) {
      return Result.databaseFailure()
    }

    val fetchResult = fetchSubreddit(subredditName)
    if (fetchResult !is Result.Success) {
      return fetchResult
    }

    return addSubreddit(fetchResult.data)
  }

  suspend fun addSubreddit(subreddit: Subreddit): Result<Subreddit> {
    // Attempt to update lastPosted if not already set
    val subredditToAdd = if (subreddit.lastPosted == null) {
      val lastPosted = getLastPosted(subreddit.name)
      if (lastPosted != null) {
        copyLastPosted(subreddit, lastPosted)
      } else {
        subreddit
      }
    } else {
      subreddit
    }
    return insertSubreddit(subredditToAdd)
  }

  suspend fun deleteSubreddit(subreddit: Subreddit): Result<Subreddit> {
    return withContext(coroutineDispatchers.io) {
      db.delete(subreddit.name)
      Result.success(subreddit)
    }
  }

  private suspend fun updateSubreddit(subreddit: Subreddit) {
    return withContext(coroutineDispatchers.io) {
      db.update(
        name = subreddit.name,
        iconUrl = subreddit.iconUrl,
        lastPosted = subreddit.lastPosted
      )
    }
  }

  private suspend fun refreshSubreddit(subreddit: Subreddit): Result<Subreddit> {
    val response = api.fetch { getAboutSubreddit(subreddit.name.name) }
    return response.mapToResult { body ->
      body.mapSubreddit()
        // Update subreddit details, preserving last posted time instead of resetting it
        .also { updateSubreddit(copyLastPosted(it, subreddit.lastPosted)) }
    }
  }

  suspend fun refreshSubreddits(): Result<Nothing> {
    fun checkResult(finalResult: Result<Nothing>, result: Result<*>): Result<Nothing> {
      if (finalResult != Result.connectionError()) {
        if (result == Result.connectionError()) {
          return Result.connectionError()
        } else if (result == Result.Error.NetworkError) {
          return Result.networkError()
        }
      }
      return finalResult
    }

    return coroutineScope {
      var finalResult: Result<Nothing> = Result.success()
      val subreddits = getSubreddits()
      subreddits.forEachAsync { subreddit ->
        refreshSubreddit(subreddit).also { finalResult = checkResult(finalResult, it) }
      }
      finalResult
    }
  }

  suspend fun checkForNewerPosts(subreddit: Subreddit): Pair<UInt, UInt>? {
    val newPostsResponse = api.fetch { getNewPosts(subreddit.name.name) }
    if (newPostsResponse !is Response.Success) {
      return null
    }
    val newPosts = newPostsResponse.body.data.children
    val subredditLastPosted = subreddit.lastPosted

    val unreadPostsAmount = if (subredditLastPosted == null) {
      // If this subreddit has not been checked previously, then assume all posts are new
      newPosts.size.toUInt()
    } else {
      // Checks for the number of posts newer than the last known one
      newPosts
        .count { (post) -> SubredditLastPosted(post.createdUtc.toLong()) > subredditLastPosted }
        .toUInt()
    }
    return unreadPostsAmount to newPosts.size.toUInt()
  }

  private suspend fun getLastPosted(subredditName: SubredditName): SubredditLastPosted? {
    val newPostsResponse = api.fetch { getNewPosts(subredditName.name) }
    if (newPostsResponse !is Response.Success) {
      return null
    }

    val newestPost = newPostsResponse.body.data.children.firstOrNull()
    return newestPost?.data?.createdUtc?.toLong()?.let(::SubredditLastPosted)
  }

  suspend fun updateLastPosted(subreddit: Subreddit) {
    val lastPosted = getLastPosted(subreddit.name)
    updateSubreddit(copyLastPosted(subreddit, lastPosted))
  }
}
