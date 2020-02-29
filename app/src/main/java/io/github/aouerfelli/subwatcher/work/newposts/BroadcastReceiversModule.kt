package io.github.aouerfelli.subwatcher.work.newposts

import dagger.Module
import dagger.android.ContributesAndroidInjector

// TODO: Move to more appropriate location
@Module
abstract class BroadcastReceiversModule {

  @ContributesAndroidInjector
  abstract fun contributeViewSubredditBroadcastReceiver(): ViewSubredditBroadcastReceiver
}
