package com.aouerfelli.subwatcher

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import coil.ImageLoader
import com.aouerfelli.subwatcher.database.Database
import com.aouerfelli.subwatcher.database.DatabaseModule
import com.aouerfelli.subwatcher.network.NetworkDetails
import com.aouerfelli.subwatcher.network.NetworkModule
import com.aouerfelli.subwatcher.network.RedditService
import com.aouerfelli.subwatcher.util.CoroutineDispatchers
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.HttpUrl.Companion.toHttpUrl
import javax.inject.Singleton

@Module(
  includes = [
    NetworkModule::class,
    DatabaseModule::class
  ]
)
@InstallIn(SingletonComponent::class)
interface ApplicationModule {

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
    fun provideNetworkDetails(@ApplicationContext context: Context): NetworkDetails {
      return NetworkDetails(
        baseUrl = RedditService.baseUrl.toHttpUrl(),
        cacheDir = context.cacheDir
      )
    }

    @Provides
    fun provideSqlDriver(@ApplicationContext context: Context): SqlDriver {
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
    fun provideWorkManager(@ApplicationContext context: Context) = WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
      return ImageLoader.Builder(context)
        .build()
    }
  }
}
