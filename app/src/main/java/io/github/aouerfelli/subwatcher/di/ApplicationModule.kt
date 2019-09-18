package io.github.aouerfelli.subwatcher.di

import dagger.Module
import io.github.aouerfelli.subwatcher.ui.MainActivityModule
import io.github.aouerfelli.subwatcher.ui.main.MainModule

@Module(
    includes = [
        MainActivityModule::class,
        MainModule::class
    ]
)
class ApplicationModule
