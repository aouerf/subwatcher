package com.aouerfelli.subwatcher.repository

import com.aouerfelli.subwatcher.database.Subreddit
import com.aouerfelli.subwatcher.database.SubredditQueries
import com.aouerfelli.subwatcher.network.Post
import com.aouerfelli.subwatcher.network.PostData
import com.aouerfelli.subwatcher.network.Posts
import com.aouerfelli.subwatcher.network.PostsData
import com.aouerfelli.subwatcher.network.RedditService
import com.aouerfelli.subwatcher.util.CoroutineDispatchers
import com.aouerfelli.subwatcher.util.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SubredditRepositoryTest {

  @get:Rule
  val coroutineTestRule = CoroutineTestRule()

  private val api = mockk<RedditService>()
  private val db = mockk<SubredditQueries>()

  private lateinit var repository: SubredditRepository

  @Before
  fun setUp() {
    repository = SubredditRepository(
      api = api,
      db = db,
      coroutineDispatchers = object : CoroutineDispatchers {
        override val default = coroutineTestRule.dispatcher
        override val unconfined = coroutineTestRule.dispatcher
        override val io = coroutineTestRule.dispatcher
      }
    )
  }

  @Test
  fun `check for no subreddit posts`() = runTest {
    val subreddit = Subreddit(
      name = SubredditName("subreddit"),
      iconUrl = null,
      lastPosted = null
    )
    val response = Posts(PostsData(emptyList()))
    coEvery { api.getNewPosts(subreddit.name.name) } returns response
    val result = repository.checkForNewerPosts(subreddit)
    assertEquals(0u, result?.first)
    assertEquals(0u, result?.second)
  }

  @Test
  fun `check for new subreddit posts`() = runTest {
    val subreddit = Subreddit(
      name = SubredditName("subreddit"),
      iconUrl = null,
      lastPosted = null
    )
    val response = Posts(PostsData(List(10) { Post(PostData(it.toLong())) }))
    coEvery { api.getNewPosts(subreddit.name.name) } returns response
    val result = repository.checkForNewerPosts(subreddit)
    assertEquals(response.data.children.size.toUInt(), result?.first)
    assertEquals(response.data.children.size.toUInt(), result?.second)
  }

  @Test
  fun `check for no new subreddit posts`() = runTest {
    val createdUtc = 123456789L
    val subreddit = Subreddit(
      name = SubredditName("subreddit"),
      iconUrl = null,
      lastPosted = SubredditLastPosted(createdUtc)
    )
    val response = Posts(PostsData(listOf(Post(PostData(createdUtc)))))
    coEvery { api.getNewPosts(subreddit.name.name) } returns response
    val result = repository.checkForNewerPosts(subreddit)
    assertEquals(0u, result?.first)
    assertEquals(response.data.children.size.toUInt(), result?.second)
  }

  @Test
  fun `check for newer subreddit posts`() = runTest {
    val epochSeconds = 123456789L
    val subreddit = Subreddit(
      name = SubredditName("subreddit"),
      iconUrl = null,
      lastPosted = SubredditLastPosted(epochSeconds)
    )
    val response = Posts(PostsData(List(5) { Post(PostData(it * epochSeconds)) }))
    coEvery { api.getNewPosts(subreddit.name.name) } returns response
    val result = repository.checkForNewerPosts(subreddit)
    assertEquals(response.data.children.size.toUInt() - 2u, result?.first)
    assertEquals(response.data.children.size.toUInt(), result?.second)
  }

  @Test
  fun `check for all new subreddit posts`() = runTest {
    val epochSeconds = 123456789L
    val subreddit = Subreddit(
      name = SubredditName("subreddit"),
      iconUrl = null,
      lastPosted = SubredditLastPosted(epochSeconds)
    )
    val response = Posts(PostsData(List(5) { Post(PostData(epochSeconds + it + 1)) }))
    coEvery { api.getNewPosts(subreddit.name.name) } returns response
    val result = repository.checkForNewerPosts(subreddit)
    assertEquals(response.data.children.size.toUInt(), result?.first)
    assertEquals(response.data.children.size.toUInt(), result?.second)
  }
}
