package io.github.aouerfelli.subwatcher

import android.os.StrictMode
import dagger.android.support.DaggerApplication
import timber.log.LogcatTree
import timber.log.Timber

class SubwatcherApplication : DaggerApplication() {

  override fun applicationInjector() = DaggerAppComponent.factory().create(this)

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(LogcatTree())
      setupStrictMode()
    }
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
