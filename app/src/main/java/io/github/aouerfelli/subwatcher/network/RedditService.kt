package io.github.aouerfelli.subwatcher.network

import retrofit2.http.GET
import retrofit2.http.Path

interface RedditService {

    @GET("/r/{subreddit}/about.json")
    suspend fun getAboutSubreddit(@Path("subreddit") subreddit: String): AboutSubreddit
}
