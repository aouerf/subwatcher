package com.aouerfelli.subwatcher

import android.app.Application
import android.os.StrictMode
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.aouerfelli.subwatcher.util.registerNotificationChannels
import com.aouerfelli.subwatcher.work.NewPostsWorker
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SubwatcherApplication : Application(), Configuration.Provider {

  @Inject
  lateinit var workerFactory: HiltWorkerFactory

  // WorkManager needs to be injected lazily to allow on-demand initialization to work properly
  @Inject
  lateinit var workManager: Lazy<WorkManager>

  override fun getWorkManagerConfiguration(): Configuration {
    return Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()
  }

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
      setupStrictMode()
    }
    registerNotificationChannels()
    enqueueWork()
  }

  private fun enqueueWork() {
    NewPostsWorker.enqueue(workManager.get())
  }

  private fun setupStrictMode() {
    StrictMode.setThreadPolicy(
      StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build()
    )
    StrictMode.setVmPolicy(
      StrictMode.VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build()
    )
  }
}
