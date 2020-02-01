package io.github.aouerfelli.subwatcher

import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.github.aouerfelli.subwatcher.ui.MainModule
import io.github.aouerfelli.subwatcher.util.ByteArrayFetcher
import javax.inject.Singleton

@Module(includes = [MainModule::class])
abstract class ApplicationModule {

  @Binds
  abstract fun bindContext(application: SubwatcherApplication): Context

  companion object {

    @Provides
    @Singleton
    fun provideImageLoader(context: Context): ImageLoader {
      return ImageLoaderBuilder(context)
        .componentRegistry {
          add(ByteArrayFetcher())
        }
        .build()
    }
  }
}
