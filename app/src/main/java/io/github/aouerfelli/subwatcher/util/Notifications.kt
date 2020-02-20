package io.github.aouerfelli.subwatcher.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.aouerfelli.subwatcher.R
import io.github.aouerfelli.subwatcher.repository.SubredditName
import io.github.aouerfelli.subwatcher.repository.asUrl
import io.github.aouerfelli.subwatcher.util.extensions.buildCustomTabsIntent

private enum class NotificationId {
  NEW_SUBREDDIT_POSTS
}

private enum class NotificationChannelId {
  NEW_SUBREDDIT_POSTS
}

private data class NotificationChannelData(
  val id: NotificationChannelId,
  val title: AndroidString,
  val description: AndroidString? = null,
  val importance: Int = NotificationManagerCompat.IMPORTANCE_DEFAULT
)

private val channelsData = listOf(
  NotificationChannelData(
    NotificationChannelId.NEW_SUBREDDIT_POSTS,
    // TODO: String resources
    "New subreddit posts".toAndroidString(),
    "Shows the number of new posts for a subreddit since the last check.".toAndroidString()
  )
)

fun Context.registerNotificationChannels() {
  if (Build.VERSION.SDK_INT < 26) {
    return
  }
  val notificationManager = NotificationManagerCompat.from(applicationContext)
  channelsData.map { (id, title, description, importance) ->
    NotificationChannel(id.toString(), getString(title), importance).apply {
      this.description = description?.let(::getString)
    }
  }.forEach(notificationManager::createNotificationChannel)
}

fun Context.launchNewSubredditPostsNotification(
  subredditName: SubredditName,
  unreadPostsAmount: UInt
): Notification {
  val notificationManager = NotificationManagerCompat.from(applicationContext)
  val customTabsIntent = subredditName.asUrl().buildCustomTabsIntent()
  val contentIntent = PendingIntent.getActivity(
    applicationContext, 0, customTabsIntent.intent, 0, customTabsIntent.startAnimationBundle
  )
  val notification =
    NotificationCompat.Builder(applicationContext, NotificationChannelId.NEW_SUBREDDIT_POSTS.toString())
      .setSmallIcon(R.drawable.ic_reddit_mark)
      .setContentTitle("r/${subredditName.name}")
      .setContentText("$unreadPostsAmount new posts") // TODO: String resource
      .setContentIntent(contentIntent)
      .setAutoCancel(true)
      .build()
  notificationManager.notify(subredditName.name, NotificationId.NEW_SUBREDDIT_POSTS.ordinal, notification)
  return notification
}
