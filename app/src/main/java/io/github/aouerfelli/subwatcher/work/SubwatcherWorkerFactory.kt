package io.github.aouerfelli.subwatcher.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

class SubwatcherWorkerFactory @Inject constructor(
  private val providers: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<WorkerAssistedInjectFactory>>
) : WorkerFactory() {

  override fun createWorker(
    appContext: Context,
    workerClassName: String,
    workerParameters: WorkerParameters
  ): ListenableWorker? {
    val workerClass = Class.forName(workerClassName)
    val workerProvider = requireNotNull(providers[workerClass]) {
      "No Worker provider found for worker $workerClassName."
    }
    return workerProvider.get().create(appContext, workerParameters)
  }
}
