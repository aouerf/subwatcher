package io.github.aouerfelli.subwatcher.repository

import androidx.core.net.toUri
import io.github.aouerfelli.subwatcher.network.NetworkModule

inline class SubredditId(val value: String)

inline class SubredditName(val value: String) {
    override fun toString() = "r/$value"

    fun asUrl() = "${NetworkModule.BASE_URL}/$this".toUri()
}
