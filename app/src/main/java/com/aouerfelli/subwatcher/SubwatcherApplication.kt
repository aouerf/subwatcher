package com.aouerfelli.subwatcher

import android.os.StrictMode
import androidx.work.Configuration
import androidx.work.WorkManager
import com.aouerfelli.subwatcher.util.DebugLogcatTree
import com.aouerfelli.subwatcher.util.registerNotificationChannels
import com.aouerfelli.subwatcher.work.NewPostsWorker
import dagger.android.support.DaggerApplication
import timber.log.Timber
import javax.inject.Inject

class SubwatcherApplication : DaggerApplication(), Configuration.Provider {

  // Configuration must be injected before WorkManager for on-demand initialization
  @Inject
  lateinit var workManagerConfig: Configuration
  @Inject
  lateinit var workManager: WorkManager

  override fun applicationInjector() = DaggerAppComponent.factory().create(this)

  override fun getWorkManagerConfiguration() = workManagerConfig

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(DebugLogcatTree())
      setupStrictMode()
    }
    registerNotificationChannels()
    enqueueWork()
  }

  private fun enqueueWork() {
    NewPostsWorker.enqueue(workManager)
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
