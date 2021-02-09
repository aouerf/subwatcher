package com.aouerfelli.subwatcher.repository

import com.aouerfelli.subwatcher.network.RedditService

inline class SubredditName(val name: String) : Comparable<SubredditName> {
  override fun compareTo(other: SubredditName) = name.compareTo(other.name, ignoreCase = true)
}
inline class SubredditIconUrl(val url: String)
inline class SubredditLastPosted(val epochSeconds: Long)

val SubredditName.isValid: Boolean
  get() = name.matches("[A-Za-z0-9][A-Za-z0-9_]{2,20}".toRegex())

fun SubredditName.asUrl() = "${RedditService.baseUrl}/r/$name/new"

operator fun SubredditLastPosted?.compareTo(other: SubredditLastPosted?): Int {
  return compareValues(this?.epochSeconds, other?.epochSeconds)
}
