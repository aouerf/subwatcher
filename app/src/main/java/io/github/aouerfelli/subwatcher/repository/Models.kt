package io.github.aouerfelli.subwatcher.repository

import androidx.core.net.toUri
import io.github.aouerfelli.subwatcher.util.ImageBlob

const val redditBaseUrl = "https://www.reddit.com"

inline class SubredditName(val name: String)
typealias SubredditIconImage = ImageBlob
// TODO: De-inlined because of https://github.com/cashapp/sqldelight/issues/1203#issuecomment-487438538
data class SubredditLastPosted(val epochSeconds: Long)

fun SubredditName.asUrl() = "$redditBaseUrl/r/$name/new".toUri()

operator fun SubredditLastPosted.compareTo(other: SubredditLastPosted): Int {
  return epochSeconds.compareTo(other.epochSeconds)
}
