package io.github.aouerfelli.subwatcher.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.api.get
import io.github.aouerfelli.subwatcher.R
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.repository.asUri
import io.github.aouerfelli.subwatcher.repository.asUrl
import io.github.aouerfelli.subwatcher.ui.MainActivity
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

suspend fun Context.notifyNewSubredditPosts(
  subreddits: List<Triple<Subreddit, UInt, UInt>>,
  imageLoader: ImageLoader
) {
  if (subreddits.isEmpty()) {
    return
  }

  val notificationManager = NotificationManagerCompat.from(applicationContext)

  val notificationId = NotificationId.NEW_POSTS.ordinal

  val notifications = subreddits.map { buildNewSubredditPostsNotification(it, imageLoader) }

  val summaryNotification = buildNewSubredditPostsSummaryNotification(notifications.size.toUInt())
  if (summaryNotification != null) {
    notificationManager.notify(notificationId, summaryNotification)
  }

  subreddits.map(Triple<Subreddit, UInt, UInt>::first).zip(notifications)
    .forEach { (subreddit, notification) ->
      // Use the subreddit name as the tag to generate a unique identifier for each subreddit of
      // this type of notification.
      notificationManager.notify(subreddit.name.name, notificationId, notification)
    }
}

private fun Context.buildNewSubredditPostsSummaryNotification(numberOfSubreddits: UInt): Notification? {
  if (Build.VERSION.SDK_INT < 24) {
    // If bundle notifications are not available then don't use a summary notification
    return null
  }

  val channelId = NotificationChannelId.NEW_POSTS.toString()

  // The bundle is hidden when there is only a single notification. so there is no need to worry
  // about the singular form.
  val summaryText = getString(R.string.notify_new_subreddit_posts_summary_text, numberOfSubreddits)
  val style = NotificationCompat.InboxStyle()
    .setSummaryText(summaryText)

  val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
  val contentIntent = PendingIntent.getActivity(applicationContext, 0, mainActivityIntent, 0)

  return NotificationCompat.Builder(applicationContext, channelId)
    .setSmallIcon(R.drawable.ic_reddit_mark)
    .setGroup(NotificationId.NEW_POSTS.toString())
    .setGroupSummary(true)
    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
    .setStyle(style)
    .setContentIntent(contentIntent)
    .build()
}

private suspend fun Context.buildNewSubredditPostsNotification(
  details: Triple<Subreddit, UInt, UInt>,
  imageLoader: ImageLoader
): Notification {
  val (subreddit, unreadPostsAmount, totalPostsAmount) = details

  val channelId = NotificationChannelId.NEW_POSTS.toString()
  val contentTitle = getString(R.string.notify_new_subreddit_posts_title, subreddit.name.name)
  // TODO: Add to existing counter if previous notification wasn't cancelled
  val contentTextRes = if (unreadPostsAmount < totalPostsAmount) {
    if (unreadPostsAmount == 1u) {
      R.string.notify_new_subreddit_posts_text_singular
    } else {
      R.string.notify_new_subreddit_posts_text
    }
  } else {
    R.string.notify_new_subreddit_posts_text_max
  }
  // If there is only one new post then the argument is ignored
  val contentText = getString(contentTextRes, unreadPostsAmount.toInt())
  val largeIcon = subreddit.iconUrl?.asUri()?.let { imageLoader.get(it) }?.toBitmap()
  val customTabsIntent = subreddit.name.asUrl().buildCustomTabsIntent()
  val contentIntent = PendingIntent.getActivity(
    applicationContext, 0, customTabsIntent.intent, 0, customTabsIntent.startAnimationBundle
  )

  return NotificationCompat.Builder(applicationContext, channelId)
    .setContentTitle(contentTitle)
    .setContentText(contentText)
    .setSmallIcon(R.drawable.ic_reddit_mark)
    .setLargeIcon(largeIcon)
    .setGroup(NotificationId.NEW_POSTS.toString())
    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
    .setContentIntent(contentIntent)
    .setAutoCancel(true)
    .build()
}
