package com.aouerfelli.subwatcher.network

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException

interface RedditService {

  companion object {
    const val baseUrl = "https://www.reddit.com"
  }

  @GET("/r/{subreddit}/about.json")
  suspend fun getAboutSubreddit(@Path("subreddit") subreddit: String): AboutSubreddit

  @GET("/r/{subreddit}/new.json")
  suspend fun getNewPosts(@Path("subreddit") subreddit: String): Posts
}

suspend fun <T : Any> RedditService.fetch(request: suspend RedditService.() -> T): Response<T> {
  return try {
    val response = request()
    Response.Success(response)
  } catch (e: HttpException) {
    when (val code = e.code()) {
      in Response.Failure.Fetch.clientErrorRange -> Response.Failure.Parse
      // in Response.Failure.Fetch.serverErrorRange -> Response.Failure.Fetch(code)
      else -> Response.Failure.Fetch(code)
    }
  } catch (e: SerializationException) {
    Response.Failure.Parse
  } catch (e: IOException) {
    Response.Error
  }
}
