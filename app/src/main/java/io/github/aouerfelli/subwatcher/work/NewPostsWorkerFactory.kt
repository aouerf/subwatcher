package io.github.aouerfelli.subwatcher.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject

// TODO: Generalize worker factory for future workers
class NewPostsWorkerFactory @Inject constructor(
  private val factory: NewPostsWorker.Factory
) : WorkerFactory() {

  override fun createWorker(
    appContext: Context,
    workerClassName: String,
    workerParameters: WorkerParameters
  ): ListenableWorker? {
    val newPostsWorkerClass = NewPostsWorker::class.java
    require(Class.forName(workerClassName) == newPostsWorkerClass) {
      "This WorkerFactory only supports ${newPostsWorkerClass.simpleName}, not $workerClassName."
    }
    return factory.create(appContext, workerParameters)
  }
}
