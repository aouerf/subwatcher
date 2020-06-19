package com.aouerfelli.subwatcher.ui

import com.aouerfelli.subwatcher.ui.main.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainModule {

  @ContributesAndroidInjector
  abstract fun contributeMainFragment(): MainFragment
}
