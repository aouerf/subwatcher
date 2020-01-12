package io.github.aouerfelli.subwatcher

import android.content.Context
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import io.github.aouerfelli.subwatcher.ui.MainModule
import javax.inject.Singleton

@Module(includes = [MainModule::class])
object ApplicationModule {

  @Provides
  fun provideContext(application: SubwatcherApplication): Context = application

  @Provides
  @Singleton
  fun provideImageLoader(context: Context): ImageLoader = ImageLoader(context)
}
