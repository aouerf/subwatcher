package com.aouerfelli.subwatcher

import android.os.StrictMode
import androidx.work.Configuration
import androidx.work.WorkManager
import com.aouerfelli.subwatcher.util.DebugLogcatTree
import com.aouerfelli.subwatcher.util.registerNotificationChannels
import com.aouerfelli.subwatcher.work.NewPostsWorker
import dagger.Lazy
import dagger.android.support.DaggerApplication
import timber.log.Timber
import javax.inject.Inject

class SubwatcherApplication : DaggerApplication(), Configuration.Provider {

  @Inject
  lateinit var workManagerConfig: Configuration

  // WorkManager needs to be injected lazily to allow on-demand initialization to work properly
  @Inject
  lateinit var workManager: Lazy<WorkManager>

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
