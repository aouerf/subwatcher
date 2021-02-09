package com.aouerfelli.subwatcher.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// TODO: Unwrap

@JsonClass(generateAdapter = true)
data class AboutSubreddit(
  @Json(name = "data") val data: AboutSubredditData
)

@JsonClass(generateAdapter = true)
data class AboutSubredditData(
  @Json(name = "display_name") val displayName: String,
  @Json(name = "icon_img") val iconImageUrl: String?
)

@JsonClass(generateAdapter = true)
data class Posts(
  @Json(name = "data") val data: PostsData
)

@JsonClass(generateAdapter = true)
data class PostsData(
  @Json(name = "children") val children: List<Post>
)

@JsonClass(generateAdapter = true)
data class Post(
  @Json(name = "data") val data: PostData
)

@JsonClass(generateAdapter = true)
data class PostData(
  @Json(name = "created_utc") val createdUtc: Long
)
