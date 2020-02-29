package io.github.aouerfelli.subwatcher.work.newposts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleCoroutineScope
import dagger.android.DaggerBroadcastReceiver
import io.github.aouerfelli.subwatcher.repository.SubredditName
import io.github.aouerfelli.subwatcher.repository.SubredditRepository
import io.github.aouerfelli.subwatcher.repository.asUrl
import io.github.aouerfelli.subwatcher.util.extensions.goAsync
import io.github.aouerfelli.subwatcher.util.extensions.launch
import javax.inject.Inject
import timber.log.Timber
import timber.log.warn

// TODO: Move to more appropriate location
class ViewSubredditBroadcastReceiver : DaggerBroadcastReceiver() {

  companion object {
    fun createIntent(context: Context, subredditName: SubredditName): Intent {
      return Intent(context, ViewSubredditBroadcastReceiver::class.java)
        // This is to make the intent unique, as extras aren't taken into account for PendingIntents
        // TODO: When minSdk 29 becomes viable, replace action with identifier
        .setAction(subredditName.name)
    }
  }

  @Inject
  lateinit var repository: SubredditRepository

  @Inject
  lateinit var processLifecycleCoroutineScope: LifecycleCoroutineScope

  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    goAsync(processLifecycleCoroutineScope) {
      val timber = Timber.tagged(this::class.java.simpleName)

      val subredditName = intent.action?.let(::SubredditName)
      if (subredditName == null) {
        timber.warn { "No subreddit name was provided." }
        return@goAsync
      }

      subredditName.asUrl().launch(context, startNewTask = context !is Activity)

      val subreddit = repository.getSubreddit(subredditName)
      if (subreddit == null) {
        timber.warn { "Subreddit ${subredditName.name} is not in database." }
        return@goAsync
      }
      repository.updateLastPosted(subreddit)
    }
  }
}
