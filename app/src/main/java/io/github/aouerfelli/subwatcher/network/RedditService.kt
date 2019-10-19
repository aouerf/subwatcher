package io.github.aouerfelli.subwatcher.network

import com.squareup.moshi.JsonDataException
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException

interface RedditService {

    @GET("/r/{subreddit}/about.json")
    suspend fun getAboutSubreddit(@Path("subreddit") subreddit: String): AboutSubreddit
}

suspend fun <T : Any> RedditService.fetch(request: suspend RedditService.() -> T): Response<T> {
    return try {
        val response = request()
        Response.Success(response)
    } catch (e: HttpException) {
        Response.Failure.Fetch(e.code())
    } catch (e: JsonDataException) {
        Response.Failure.Parse
    } catch (e: IOException) {
        Response.Error
    }
}
