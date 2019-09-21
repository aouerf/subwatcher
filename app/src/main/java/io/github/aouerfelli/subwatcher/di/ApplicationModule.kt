package io.github.aouerfelli.subwatcher.di

import dagger.Module
import io.github.aouerfelli.subwatcher.network.NetworkModule
import io.github.aouerfelli.subwatcher.ui.main.MainModule

@Module(
    includes = [
        NetworkModule::class,
        MainModule::class
    ]
)
class ApplicationModule
