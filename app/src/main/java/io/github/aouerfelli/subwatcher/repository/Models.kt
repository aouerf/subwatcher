package io.github.aouerfelli.subwatcher.repository

import androidx.core.net.toUri

const val redditBaseUrl = "https://www.reddit.com"

inline class SubredditName(val name: String) : Comparable<SubredditName> {
  override fun compareTo(other: SubredditName) = name.compareTo(other.name, ignoreCase = true)
}

// TODO: De-inlined because of https://github.com/cashapp/sqldelight/issues/1203#issuecomment-487438538
data class SubredditIconUrl(val url: String)

data class SubredditLastPosted(val epochSeconds: Long)

fun SubredditName.asUrl() = "$redditBaseUrl/r/$name/new".toUri()

fun SubredditIconUrl.asUri() = url.toUri()

operator fun SubredditLastPosted.compareTo(other: SubredditLastPosted): Int {
  return epochSeconds.compareTo(other.epochSeconds)
}
