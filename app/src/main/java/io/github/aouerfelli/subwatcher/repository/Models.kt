package io.github.aouerfelli.subwatcher.repository

import androidx.core.net.toUri

inline class SubredditId(val value: String)

inline class SubredditName(val value: String) {

    companion object {
        const val baseUrl = "https://www.reddit.com"

        val random = SubredditName("random")
    }

    override fun toString() = "r/$value"

    fun asUrl() = "$baseUrl/$this".toUri()
}
