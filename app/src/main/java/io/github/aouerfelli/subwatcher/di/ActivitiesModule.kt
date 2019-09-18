package io.github.aouerfelli.subwatcher.di

import dagger.Module
import io.github.aouerfelli.subwatcher.ui.MainActivityModule

@Module(
    includes = [
        MainActivityModule::class
    ]
)
class ActivitiesModule
