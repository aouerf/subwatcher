package io.github.aouerfelli.subwatcher.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.aouerfelli.subwatcher.R
import io.github.aouerfelli.subwatcher.repository.SubredditName
import io.github.aouerfelli.subwatcher.repository.asUrl
import io.github.aouerfelli.subwatcher.util.extensions.buildCustomTabsIntent

private enum class NotificationId {
  NEW_POSTS
}

private enum class NotificationChannelId {
  NEW_POSTS
}

private data class NotificationChannelData(
  val id: NotificationChannelId,
  @StringRes val title: Int,
  @StringRes val description: Int? = null,
  val importance: Int = NotificationManagerCompat.IMPORTANCE_DEFAULT
)

private val channelsData = listOf(
  NotificationChannelData(
    NotificationChannelId.NEW_POSTS,
    R.string.notify_new_subreddit_posts_channel_title,
    R.string.notify_new_subreddit_posts_channel_desc
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

fun Context.notifyNewSubredditPosts(
  subredditName: SubredditName,
  unreadPostsAmount: UInt,
  totalPostsAmount: UInt
): Notification {
  val notificationManager = NotificationManagerCompat.from(applicationContext)

  val channelId = NotificationChannelId.NEW_POSTS.toString()
  val contentTitle = getString(R.string.notify_new_subreddit_posts_title, subredditName.name)
  val contentTextRes = if (unreadPostsAmount < totalPostsAmount) {
    if (unreadPostsAmount == 1u) {
      R.string.notify_new_subreddit_posts_text_singular
    } else {
      R.string.notify_new_subreddit_posts_text
    }
  } else {
    R.string.notify_new_subreddit_posts_text_max
  }
  val contentText = getString(contentTextRes, unreadPostsAmount.toInt())
  val customTabsIntent = subredditName.asUrl().buildCustomTabsIntent()
  val contentIntent = PendingIntent.getActivity(
    applicationContext, 0, customTabsIntent.intent, 0, customTabsIntent.startAnimationBundle
  )

  // TODO: Set large icon
  val notification = NotificationCompat.Builder(applicationContext, channelId)
    .setContentTitle(contentTitle)
    .setContentText(contentText)
    .setSmallIcon(R.drawable.ic_reddit_mark)
    .setGroup(NotificationId.NEW_POSTS.toString())
    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
    .setContentIntent(contentIntent)
    .setAutoCancel(true)
    .build()

  // Use the subreddit name as the tag to generate a unique identifier for each subreddit of this
  // type of notification.
  notificationManager.notify(
    subredditName.name,
    NotificationId.NEW_POSTS.ordinal,
    notification
  )

  return notification
}
