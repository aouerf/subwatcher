package com.aouerfelli.subwatcher.broadcast

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastReceiversModule {

  @ContributesAndroidInjector
  abstract fun contributeViewSubredditBroadcastReceiver(): ViewSubredditBroadcastReceiver
}
