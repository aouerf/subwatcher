package io.github.aouerfelli.subwatcher.di

import dagger.Module
import io.github.aouerfelli.subwatcher.ui.main.MainModule

@Module(
    includes = [
        MainModule::class
    ]
)
class ApplicationModule
