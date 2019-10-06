package io.github.aouerfelli.subwatcher.repository

inline class SubredditId(val value: String)

inline class SubredditName(val value: String) {
    override fun toString() = "r/$value"
}
