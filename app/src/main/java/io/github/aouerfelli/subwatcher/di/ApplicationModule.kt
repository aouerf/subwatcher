package io.github.aouerfelli.subwatcher.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.aouerfelli.subwatcher.SubwatcherApplication
import io.github.aouerfelli.subwatcher.ui.main.MainModule

@Module(
    includes = [
        MainModule::class
    ]
)
class ApplicationModule {

    @Provides
    fun provideContext(application: SubwatcherApplication): Context = application.applicationContext
}
