package io.github.aouerfelli.subwatcher.repository

import androidx.core.net.toUri
import io.github.aouerfelli.subwatcher.util.ImageBlob

const val redditBaseUrl = "https://www.reddit.com"

inline class SubredditName(val name: String)
typealias SubredditIcon = ImageBlob

fun SubredditName.asUrl() = "$redditBaseUrl/r/$name/new".toUri()
