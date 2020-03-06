package com.aouerfelli.subwatcher.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.aouerfelli.subwatcher.ui.main.MainFragment

@Module
abstract class MainModule {

  @ContributesAndroidInjector
  abstract fun contributeMainFragment(): MainFragment
}
