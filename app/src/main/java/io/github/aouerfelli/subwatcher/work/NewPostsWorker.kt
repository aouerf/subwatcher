package io.github.aouerfelli.subwatcher.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.aouerfelli.subwatcher.repository.SubredditRepository
import io.github.aouerfelli.subwatcher.util.notifyNewSubredditPosts
import kotlinx.coroutines.flow.first
import timber.log.Timber
import timber.log.debug

class NewPostsWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted workerParams: WorkerParameters,
  private val repository: SubredditRepository
) : CoroutineWorker(appContext, workerParams) {

  companion object {
    const val WORK_NAME = "new_posts"
  }

  @AssistedInject.Factory
  interface Factory {
    fun create(appContext: Context, workerParams: WorkerParameters): NewPostsWorker
  }

  override suspend fun doWork(): Result {
    Timber.debug { "$WORK_NAME Worker running" }

    val list = repository.subreddits.first()
    list.forEach { subreddit ->
      val (unread, total) = repository.checkForNewerPosts(subreddit) ?: return Result.failure()
      if (unread > 0u) {
        applicationContext.notifyNewSubredditPosts(subreddit.name, unread, total)
      }
    }
    return Result.success()
  }
}
