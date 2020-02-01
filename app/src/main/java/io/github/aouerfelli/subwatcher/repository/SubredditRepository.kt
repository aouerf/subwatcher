package io.github.aouerfelli.subwatcher.repository

import android.database.sqlite.SQLiteConstraintException
import androidx.core.net.toUri
import coil.ImageLoader
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.SubredditEntityQueries
import io.github.aouerfelli.subwatcher.network.AboutSubreddit
import io.github.aouerfelli.subwatcher.network.RedditService
import io.github.aouerfelli.subwatcher.network.Response
import io.github.aouerfelli.subwatcher.network.fetch
import io.github.aouerfelli.subwatcher.util.toImageBlob
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

@Singleton
class SubredditRepository @Inject constructor(
  private val api: RedditService,
  private val db: SubredditEntityQueries,
  private val imageLoader: ImageLoader
) {

  private val ioDispatcher = Dispatchers.IO

  val subreddits = db.selectAll().asFlow().mapToList(ioDispatcher)

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

  private suspend fun AboutSubreddit.mapSubreddit(): Subreddit {
    return with(data) {
      Subreddit.Impl(
        id = SubredditId(id),
        name = SubredditName(displayName),
        iconImage = iconImageUrl?.ifEmpty { null }?.toUri()?.toImageBlob(imageLoader)
      )
    }
  }

  private suspend fun fetchSubreddit(name: SubredditName): Result<Subreddit> {
    val response = api.fetch { getAboutSubreddit(name.name) }
    return response.mapToResult { it.mapSubreddit() }
  }

  private suspend fun insertSubreddit(subreddit: Subreddit): Result<Subreddit> {
    return withContext(ioDispatcher) {
      try {
        db.insert(subreddit)
        Result.success(subreddit)
      } catch (e: SQLiteConstraintException) {
        Result.databaseFailure()
      }
    }
  }

  suspend fun addSubreddit(subredditName: String): Result<Subreddit> {
    val name = SubredditName(subredditName)

    val existingSubreddit = withContext(ioDispatcher) {
      db.select(name).executeAsOneOrNull()
    }
    if (existingSubreddit != null) {
      return Result.success(existingSubreddit)
    }

    val fetchResult = fetchSubreddit(name)
    if (fetchResult !is Result.Success) {
      return fetchResult
    }

    return addSubreddit(fetchResult.data)
  }

  suspend fun addSubreddit(subreddit: Subreddit): Result<Subreddit> {
    return insertSubreddit(subreddit)
  }

  suspend fun deleteSubreddit(subreddit: Subreddit): Result<Subreddit> {
    return withContext(ioDispatcher) {
      db.delete(subreddit.id)
      Result.success(subreddit)
    }
  }

  private suspend fun updateSubreddit(subreddit: Subreddit) {
    return withContext(ioDispatcher) {
      with(subreddit) {
        db.update(id = id, name = name, iconImage = iconImage)
      }
    }
  }

  private suspend fun refreshSubreddit(subreddit: Subreddit): Result<Subreddit> {
    val response = api.fetch { getAboutSubreddit(subreddit.name.name) }
    return response.mapToResult { body -> body.mapSubreddit().also { updateSubreddit(it) } }
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
      val subreddits = withContext(ioDispatcher) {
        db.selectAll().executeAsList()
      }
      // TODO: Wait for concurrent Flow (https://github.com/Kotlin/kotlinx.coroutines/issues/1147)
      subreddits.map { subreddit ->
        async {
          refreshSubreddit(subreddit).also { finalResult = checkResult(finalResult, it) }
        }
      }.awaitAll()
      finalResult
    }
  }
}
