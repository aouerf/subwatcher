package com.aouerfelli.subwatcher.network

import com.aouerfelli.subwatcher.DaggerTestComponent
import com.aouerfelli.subwatcher.util.CoroutineTestRule
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RedditServiceTest {

  @get:Rule
  val coroutineTestRule = CoroutineTestRule()

  @Inject
  lateinit var redditService: RedditService

  @Inject
  lateinit var moshi: Moshi

  private val server = MockWebServer()

  @Before
  fun setUp() {
    server.start()
    DaggerTestComponent.factory().create(
      coroutineTestRule.dispatcher,
      server
    ).inject(this)
  }

  @Test
  fun `about subreddit serialization`() {
    val adapter = moshi.adapter(AboutSubreddit::class.java)
    assertEquals(aboutSubreddit, adapter.fromJson(aboutSubredditRaw))
  }

  @Test
  fun `new posts serialization`() {
    val adapter = moshi.adapter(Posts::class.java)
    assertEquals(newPosts, adapter.fromJson(newPostsRaw))
  }

  // FIXME: Using `runBlocking(coroutineTestRule.dispatcher)` instead of `coroutineTestRule.dispatcher.runBlockingTest` because of Retrofit

  @Test
  fun `about subreddit response 200`() = runBlocking(coroutineTestRule.dispatcher) {
    val mockResponse = MockResponse().setResponseCode(200).setBody(aboutSubredditRaw)
    server.enqueue(mockResponse)
    val response = redditService.fetch { getAboutSubreddit(subredditName) }
    assertEquals("/r/$subredditName/about.json", server.takeRequest(1, TimeUnit.SECONDS)?.path)
    assertEquals(Response.Success::class, response::class)
    response as Response.Success
    assertEquals(aboutSubreddit, response.body)
  }

  @Test
  fun `about subreddit malformed response body`() = runBlocking(coroutineTestRule.dispatcher) {
    val mockResponseBody = aboutSubredditRaw.replace("display_name", "name")
    val mockResponse = MockResponse().setResponseCode(200).setBody(mockResponseBody)
    server.enqueue(mockResponse)
    val response = redditService.fetch { getAboutSubreddit(subredditName) }
    assertEquals(Response.Failure.Parse::class, response::class)
  }

  @Test
  fun `new posts response 200`() = runBlocking(coroutineTestRule.dispatcher) {
    val mockResponse = MockResponse().setResponseCode(200).setBody(newPostsRaw)
    server.enqueue(mockResponse)
    val response = redditService.fetch { getNewPosts(subredditName) }
    assertEquals("/r/$subredditName/new.json", server.takeRequest(1, TimeUnit.SECONDS)?.path)
    assertEquals(Response.Success::class, response::class)
    response as Response.Success
    assertEquals(newPosts, response.body)
  }

  @Test
  fun `new posts malformed response body`() = runBlocking(coroutineTestRule.dispatcher) {
    val mockResponseBody = newPostsRaw.replace("children", "child")
    val mockResponse = MockResponse().setResponseCode(200).setBody(mockResponseBody)
    server.enqueue(mockResponse)
    val response = redditService.fetch { getNewPosts(subredditName) }
    assertEquals(Response.Failure.Parse::class, response::class)
  }

  @Test
  fun `response 503`() = runBlocking(coroutineTestRule.dispatcher) {
    val mockResponse = MockResponse().setResponseCode(503)
    server.enqueue(mockResponse)
    // Endpoint doesn't matter
    val response = redditService.fetch { getAboutSubreddit("") }
    assertEquals(Response.Failure.Fetch::class, response::class)
    response as Response.Failure.Fetch
    assertEquals(503, response.code)
  }

  @Test
  fun `response 404`() = runBlocking(coroutineTestRule.dispatcher) {
    val mockResponse = MockResponse().setResponseCode(404).setBody(
      """
      {
        "message": "Not Found",
        "error": 404
      }
      """.trimIndent()
    )
    server.enqueue(mockResponse)
    // Endpoint doesn't matter
    val response = redditService.fetch { getAboutSubreddit("") }
    assertEquals(Response.Failure.Parse::class, response::class)
  }

  @After
  fun tearDown() {
    server.shutdown()
  }

  companion object {
    private const val subredditName = "subreddit"

    private const val subredditIconImageUrl = "https://icon_img.png"
    private val aboutSubredditRaw =
      """
      {
        "kind": "t5",
        "data": {
          "id": "1a23b",
          "display_name": "$subredditName",
          "icon_img": "$subredditIconImageUrl",
          "display_name_prefixed": "r/$subredditName"
        }
      }
      """.trimIndent()
    val aboutSubreddit = AboutSubreddit(
      AboutSubredditData(subredditName, subredditIconImageUrl)
    )

    private const val newPostsCreatedUtc = 123456789L
    private val newPostsRaw =
      """
      {
        "kind": "Listing",
        "data": {
          "dist": 25,
          "children": [
            {
              "kind": "t3",
              "data": {
                "subreddit": "$subredditName",
                "created_utc": $newPostsCreatedUtc.0,
                "media": null
              }
            }
          ],
          "before": null
        }
      }
      """.trimIndent()
    private val newPosts = Posts(
      PostsData(
        listOf(
          Post(PostData(newPostsCreatedUtc))
        )
      )
    )
  }
}
