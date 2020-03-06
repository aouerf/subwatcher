package com.aouerfelli.subwatcher

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.Configuration
import androidx.work.WorkManager
import coil.ImageLoaderBuilder
import com.aouerfelli.subwatcher.broadcast.BroadcastReceiversModule
import com.aouerfelli.subwatcher.ui.MainModule
import com.aouerfelli.subwatcher.work.SubwatcherWorkerFactory
import com.aouerfelli.subwatcher.work.WorkersModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
  includes = [
    MainModule::class,
    WorkersModule::class,
    BroadcastReceiversModule::class
  ]
)
abstract class ApplicationModule {

  @Binds
  abstract fun bindContext(application: SubwatcherApplication): Context

  companion object {

    @Provides
    fun provideProcessLifecycleCoroutineScope(): LifecycleCoroutineScope {
      return ProcessLifecycleOwner.get().lifecycleScope
    }

    @Provides
    @Singleton
    fun provideWorkManager(context: Context) = WorkManager.getInstance(context)

    @Provides
    fun provideWorkConfiguration(workerFactory: SubwatcherWorkerFactory): Configuration {
      return Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()
    }

    @Provides
    @Singleton
    fun provideImageLoader(context: Context) = ImageLoaderBuilder(context).build()
  }
}