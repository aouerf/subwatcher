package io.github.aouerfelli.subwatcher.util

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.api.get
import io.github.aouerfelli.subwatcher.R
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.broadcast.ViewSubredditBroadcastReceiver
import io.github.aouerfelli.subwatcher.repository.asUri

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
  val notificationId = NotificationId.NEW_POSTS.ordinal
  val notificationManager = NotificationManagerCompat.from(this)
  notificationManager.cancelAll()

  val notifications = subreddits.map { (subreddit, newPostsAmount) ->
    val (unreadPostsAmount, totalPostsAmount) = newPostsAmount

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
    val contentIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

    // TODO: Add inbox-style to preview new post titles
    NotificationCompat.Builder(this, channelId)
      .setContentTitle(contentTitle)
      .setContentText(contentText)
      .setSmallIcon(R.drawable.ic_stat_name)
      .setLargeIcon(largeIcon)
      .setContentIntent(contentIntent)
      .setAutoCancel(true)
      .build()
  }

  subreddits.map(Pair<Subreddit, *>::first).zip(notifications)
    .forEach { (subreddit, notification) ->
      // Use the subreddit name as the tag to generate a unique identifier for each subreddit of
      // this type of notification.
      notificationManager.notify(subreddit.name.name, notificationId, notification)
    }
}
