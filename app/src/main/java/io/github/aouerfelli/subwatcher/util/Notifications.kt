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
import io.github.aouerfelli.subwatcher.ui.MainActivity
import io.github.aouerfelli.subwatcher.work.newposts.ViewSubredditBroadcastReceiver

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

  val notificationManager = NotificationManagerCompat.from(this)
  channelsData.map { (id, title, description, importance) ->
    NotificationChannel(id.toString(), getString(title), importance).apply {
      this.description = description?.let(::getString)
    }
  }.forEach(notificationManager::createNotificationChannel)
}

// This is a suspend function because of the large icon loading with Coil
suspend fun Context.notifyNewSubredditPosts(
  subreddits: List<Pair<Subreddit, Pair<UInt, UInt>>>,
  imageLoader: ImageLoader
) {
  if (subreddits.isEmpty()) {
    return
  }

  val channelId = NotificationChannelId.NEW_POSTS.toString()

  fun buildNewSubredditPostsSummaryNotification(): Notification? {
    if (Build.VERSION.SDK_INT < 24) {
      // If bundle notifications are not available then don't use a summary notification
      return null
    }

    // TODO: Cancel summary notification when there are no more notifications

    // The bundle is hidden when there is only a single notification. so there is no need to worry
    // about the singular form.
    val summaryText = getString(R.string.notify_new_subreddit_posts_summary_text, subreddits.size)
    val style = NotificationCompat.InboxStyle()
      .setSummaryText(summaryText)

    val mainActivityIntent = Intent(this, MainActivity::class.java)
    val contentIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0)

    return NotificationCompat.Builder(this, channelId)
      .setSmallIcon(R.drawable.ic_reddit_mark)
      .setGroup(NotificationId.NEW_POSTS.toString())
      .setGroupSummary(true)
      .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
      .setStyle(style)
      .setContentIntent(contentIntent)
      .build()
  }

  // This request code makes each PendingIntent unique by incrementing it for every notification.
  // TODO: Make it more explicit
  var requestCode = 0
  suspend fun buildNewSubredditPostsNotification(
    details: Pair<Subreddit, Pair<UInt, UInt>>,
    imageLoader: ImageLoader
  ): Notification {
    val (subreddit, postsAmount) = details
    val (unreadPostsAmount, totalPostsAmount) = postsAmount

    val contentTitle = getString(R.string.notify_new_subreddit_posts_title, subreddit.name.name)
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

    val intent = ViewSubredditBroadcastReceiver.createIntent(this, subreddit.name)
    val contentIntent = PendingIntent.getBroadcast(this, requestCode++, intent, 0)

    return NotificationCompat.Builder(this, channelId)
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

  val notificationManager = NotificationManagerCompat.from(this)

  val notificationId = NotificationId.NEW_POSTS.ordinal

  val notifications = subreddits.map { buildNewSubredditPostsNotification(it, imageLoader) }

  val summaryNotification = buildNewSubredditPostsSummaryNotification()
  if (summaryNotification != null) {
    notificationManager.notify(notificationId, summaryNotification)
  }

  subreddits.map(Pair<Subreddit, *>::first).zip(notifications)
    .forEach { (subreddit, notification) ->
      // Use the subreddit name as the tag to generate a unique identifier for each subreddit of
      // this type of notification.
      notificationManager.notify(subreddit.name.name, notificationId, notification)
    }
}
