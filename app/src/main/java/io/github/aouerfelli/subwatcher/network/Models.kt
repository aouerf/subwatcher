package io.github.aouerfelli.subwatcher.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AboutSubreddit(
    @Json(name = "data") val data: AboutSubredditData
)

@JsonClass(generateAdapter = true)
data class AboutSubredditData(
    @Json(name = "display_name") val displayName: String,
    @Json(name = "icon_img") val iconImageUrl: String
)
