package io.github.aouerfelli.subwatcher.repository

import androidx.core.net.toUri

const val redditBaseUrl = "https://www.reddit.com"

inline class SubredditId(val id: String)
inline class SubredditName(val name: String)

fun SubredditName.asUrl() = "$redditBaseUrl/r/$name".toUri()
