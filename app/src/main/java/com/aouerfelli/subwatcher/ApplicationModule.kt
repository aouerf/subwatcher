package com.aouerfelli.subwatcher

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.Configuration
import androidx.work.WorkManager
import coil.ImageLoader
import com.aouerfelli.subwatcher.database.DatabaseModule
import com.aouerfelli.subwatcher.network.NetworkDetails
import com.aouerfelli.subwatcher.network.NetworkModule
import com.aouerfelli.subwatcher.network.RedditService
import com.aouerfelli.subwatcher.util.CoroutineDispatchers
import com.aouerfelli.subwatcher.work.SubwatcherWorkerFactory
import com.aouerfelli.subwatcher.work.WorkersModule
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.HttpUrl.Companion.toHttpUrl
import javax.inject.Singleton

@Module(includes = [
  WorkersModule::class,
  NetworkModule::class,
  DatabaseModule::class
])
@InstallIn(SingletonComponent::class)
interface ApplicationModule {

  // Can also be provided through @ApplicationContext, but this is more convenient
  @get:Binds
  val Application.bindContext: Context

  companion object {

    @Provides
    @Singleton
    fun provideCoroutineDispatchers(): CoroutineDispatchers {
      return object : CoroutineDispatchers {
        override val default = Dispatchers.Default
        override val unconfined = Dispatchers.Unconfined
        override val io = Dispatchers.IO
      }
    }

    @Provides
    fun provideNetworkDetails(context: Context): NetworkDetails {
      return NetworkDetails(
        baseUrl = RedditService.baseUrl.toHttpUrl(),
        cacheDir = context.cacheDir
      )
    }

    @Provides
    fun provideSqlDriver(context: Context): SqlDriver {
      return AndroidSqliteDriver(
        schema = Database.Schema,
        context = context,
        name = "subwatcher.db"
      )
    }

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
    fun provideImageLoader(context: Context) = ImageLoader.Builder(context).build()
  }
}
