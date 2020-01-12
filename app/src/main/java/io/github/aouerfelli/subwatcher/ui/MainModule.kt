package io.github.aouerfelli.subwatcher.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.github.aouerfelli.subwatcher.ui.main.MainFragment

@Module
abstract class MainModule {

  @ContributesAndroidInjector
  abstract fun contributeMainFragment(): MainFragment
}
