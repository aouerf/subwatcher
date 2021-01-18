package com.aouerfelli.subwatcher.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import coil.ImageLoader
import com.aouerfelli.subwatcher.repository.SubredditRepository
import com.aouerfelli.subwatcher.util.extensions.mapAsync
import com.aouerfelli.subwatcher.util.notifyNewSubredditPosts
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import timber.log.Timber
import timber.log.debug
import java.time.Duration

class NewPostsWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted workerParams: WorkerParameters,
  private val repository: SubredditRepository,
  private val imageLoader: ImageLoader
) : CoroutineWorker(appContext, workerParams) {

  companion object {
    private const val WORK_NAME = "new_posts"

    fun enqueue(workManager: WorkManager) {
      val repeatInterval = Duration.ofHours(1)
      val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
      val workRequest = PeriodicWorkRequestBuilder<NewPostsWorker>(repeatInterval)
        .setConstraints(constraints)
        .build()
      workManager.enqueueUniquePeriodicWork(
        WORK_NAME,
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
      )
    }
  }

  @AssistedFactory
  interface Factory : WorkerAssistedInjectFactory<NewPostsWorker>

  override suspend fun doWork(): Result {
    Timber.debug { "$WORK_NAME worker running" }

    val subreddits = repository.getSubreddits()
    val details = subreddits.mapAsync { subreddit ->
      val newPosts = repository.checkForNewerPosts(subreddit)
      if (newPosts == null || newPosts.first == 0u) {
        null
      } else {
        subreddit to newPosts
      }
    }.filterNotNull().sortedByDescending { (subreddit, _) -> subreddit.name }
    applicationContext.notifyNewSubredditPosts(details, imageLoader)

    return Result.success()
  }
}
