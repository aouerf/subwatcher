package io.github.aouerfelli.subwatcher

import android.annotation.SuppressLint
import android.os.StrictMode
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.android.support.DaggerApplication
import io.github.aouerfelli.subwatcher.util.registerNotificationChannels
import io.github.aouerfelli.subwatcher.work.NewPostsWorker
import timber.log.LogcatTree
import timber.log.Timber
import java.time.Duration
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
      Timber.plant(LogcatTree())
      setupStrictMode()
    }
    registerNotificationChannels()
    enqueueNewPostsWork()
  }

  @SuppressLint("NewApi") // Core library desugaring handles java.time backport
  private fun enqueueNewPostsWork() {
    val repeatInterval = Duration.ofHours(1)
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()
    val workRequest = PeriodicWorkRequestBuilder<NewPostsWorker>(repeatInterval)
      .setConstraints(constraints)
      .build()
    workManager.enqueueUniquePeriodicWork(
      NewPostsWorker.WORK_NAME,
      ExistingPeriodicWorkPolicy.REPLACE,
      workRequest
    )
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
