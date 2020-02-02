package io.github.aouerfelli.subwatcher.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dev.zacsweers.moshisealed.annotations.DefaultObject
import dev.zacsweers.moshisealed.annotations.TypeLabel

// FIXME: Reddit response does not have a label that can be used to determine the type of response
@JsonClass(generateAdapter = true, generator = "sealed:")
sealed class AboutSubreddit {

  @TypeLabel("")
  @JsonClass(generateAdapter = true)
  data class Success(
    @Json(name = "data") val data: AboutSubredditData
  ) : AboutSubreddit()

  @TypeLabel("")
  @JsonClass(generateAdapter = true)
  data class Failure(
    @Json(name = "message") val message: String,
    @Json(name = "error") val error: Int
  ) : AboutSubreddit()

  // TODO: Remove and add @DefaultNull to AboutSubreddit?
  // TODO: No need for catching JsonDataException with this?
  @DefaultObject
  object Unknown : AboutSubreddit()
}

@JsonClass(generateAdapter = true)
data class AboutSubredditData(
  @Json(name = "id") val id: String,
  @Json(name = "display_name") val displayName: String,
  @Json(name = "icon_img") val iconImageUrl: String?
)
