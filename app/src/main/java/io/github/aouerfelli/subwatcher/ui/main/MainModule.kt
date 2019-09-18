package io.github.aouerfelli.subwatcher.ui.main

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment
}
