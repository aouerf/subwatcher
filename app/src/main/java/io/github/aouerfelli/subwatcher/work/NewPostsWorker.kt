package io.github.aouerfelli.subwatcher.work

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import coil.ImageLoader
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.aouerfelli.subwatcher.repository.SubredditRepository
import io.github.aouerfelli.subwatcher.util.extensions.mapAsync
import io.github.aouerfelli.subwatcher.util.notifyNewSubredditPosts
import kotlinx.coroutines.flow.first
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

    @SuppressLint("NewApi") // Core library desugaring handles java.time backport
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

  @AssistedInject.Factory
  interface Factory : WorkerAssistedInjectFactory

  override suspend fun doWork(): Result {
    Timber.debug { "$WORK_NAME worker running" }

    val subreddits = repository.subreddits.first()
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
