package io.github.aouerfelli.subwatcher.repository

import io.github.aouerfelli.subwatcher.util.EncodedImage

inline class SubredditId(val value: String)

inline class SubredditName(val value: String) {
    override fun toString() = "r/$value"
}

data class Subreddit(
    val id: SubredditId,
    val name: SubredditName,
    val iconImage: EncodedImage?
) {
    override fun toString() = name.toString()
}
