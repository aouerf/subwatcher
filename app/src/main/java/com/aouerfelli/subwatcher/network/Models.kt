package com.aouerfelli.subwatcher.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: Unwrap

@Serializable
data class AboutSubreddit(
  @SerialName("data") val data: AboutSubredditData
)

@Serializable
data class AboutSubredditData(
  @SerialName("display_name") val displayName: String,
  @SerialName("icon_img") val iconImageUrl: String?
)

@Serializable
data class Posts(
  @SerialName("data") val data: PostsData
)

@Serializable
data class PostsData(
  @SerialName("children") val children: List<Post>
)

@Serializable
data class Post(
  @SerialName("data") val data: PostData
)

@Serializable
data class PostData(
  // Has to be double because of https://github.com/Kotlin/kotlinx.serialization/issues/1653
  @SerialName("created_utc") val createdUtc: Double
)
