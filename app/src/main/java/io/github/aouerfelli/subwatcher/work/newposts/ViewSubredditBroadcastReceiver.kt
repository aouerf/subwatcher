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
import timber.log.Timber
import timber.log.warn
import javax.inject.Inject

// TODO: Move to more appropriate location
// TODO: Replace with Activity to avoid creating a new task
class ViewSubredditBroadcastReceiver : DaggerBroadcastReceiver() {

  companion object {
    private const val SUBREDDIT_NAME_EXTRA = "subreddit_name"

    fun createIntent(context: Context, subredditName: SubredditName): Intent {
      return Intent(context, ViewSubredditBroadcastReceiver::class.java)
        .putExtra(SUBREDDIT_NAME_EXTRA, subredditName.name)
    }
  }

  @Inject
  lateinit var repository: SubredditRepository

  @Inject
  lateinit var processCoroutineScope: LifecycleCoroutineScope

  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    goAsync(processCoroutineScope) {
      val timber = Timber.tagged(this::class.java.simpleName)

      val subredditName = intent.getStringExtra(SUBREDDIT_NAME_EXTRA)?.let(::SubredditName)
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
