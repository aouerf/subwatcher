package com.aouerfelli.subwatcher.repository

import androidx.core.net.toUri
import com.aouerfelli.subwatcher.network.RedditService

inline class SubredditName(val name: String) : Comparable<SubredditName> {
  override fun compareTo(other: SubredditName) = name.compareTo(other.name, ignoreCase = true)
}

// TODO: De-inlined because of https://github.com/cashapp/sqldelight/issues/1203#issuecomment-487438538
data class SubredditIconUrl(val url: String)
data class SubredditLastPosted(val epochSeconds: Long)

val SubredditName.isValid: Boolean
  get() = name.matches("[A-Za-z0-9][A-Za-z0-9_]{2,20}".toRegex())

fun SubredditName.asUrl() = "${RedditService.baseUrl}/r/$name/new".toUri()

fun SubredditIconUrl.asUri() = url.toUri()

operator fun SubredditLastPosted?.compareTo(other: SubredditLastPosted?): Int {
  return compareValues(this?.epochSeconds, other?.epochSeconds)
}
