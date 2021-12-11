package com.aouerfelli.subwatcher.database

import com.aouerfelli.subwatcher.DaggerTestComponent
import com.aouerfelli.subwatcher.repository.SubredditIconUrl
import com.aouerfelli.subwatcher.repository.SubredditLastPosted
import com.aouerfelli.subwatcher.repository.SubredditName
import com.aouerfelli.subwatcher.util.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.AfterEach
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class SubredditEntityQueriesTest {

  @get:Rule
  val coroutineTestRule = CoroutineTestRule()

  @Inject
  lateinit var subredditEntityQueries: SubredditQueries

  @Before
  fun startUp() {
    DaggerTestComponent.factory().create(
      coroutineTestRule.dispatcher,
      MockWebServer() // Unused
    ).inject(this)
  }

  @AfterEach
  fun deleteAllSubreddits() {
    subredditEntityQueries.deleteAll()
  }

  @Test
  fun `select and delete all subreddits`() {
    assertEquals(emptyList<Subreddit>(), subredditEntityQueries.selectAll().executeAsList())
    subredditEntityQueries.insert(subreddit)
    assertEquals(listOf(subreddit), subredditEntityQueries.selectAll().executeAsList())
    subredditEntityQueries.deleteAll()
    assertEquals(emptyList<Subreddit>(), subredditEntityQueries.selectAll().executeAsList())
  }

  @Test
  fun `select and delete subreddit`() {
    assertEquals(null, subredditEntityQueries.select(subreddit.name).executeAsOneOrNull())
    subredditEntityQueries.insert(subreddit)
    assertEquals(subreddit, subredditEntityQueries.select(subreddit.name).executeAsOneOrNull())
    subredditEntityQueries.delete(subreddit.name)
    assertEquals(null, subredditEntityQueries.select(subreddit.name).executeAsOneOrNull())
  }

  @Test
  fun `insert duplicate key constraint failure`() {
    val subredditCopy = subreddit.copy(
      name = SubredditName(subreddit.name.name.uppercase()),
      iconUrl = null,
      lastPosted = null
    )
    subredditEntityQueries.insert(subreddit)
    assertThrows(java.sql.SQLException::class.java) { subredditEntityQueries.insert(subredditCopy) }
  }

  @Test
  fun `select all ordering`() {
    val subreddits = listOf(
      subreddit.copy(name = SubredditName("A")),
      subreddit.copy(name = SubredditName("b")),
      subreddit.copy(name = SubredditName("C"))
    )
    subreddits.asReversed().forEach(subredditEntityQueries::insert)
    assertEquals(subreddits, subredditEntityQueries.selectAll().executeAsList())
  }

  @Test
  fun `update existing`() {
    subredditEntityQueries.insert(subreddit)
    val updatedSubreddit = subreddit.copy(iconUrl = null, lastPosted = null)
    subredditEntityQueries.update(
      name = subreddit.name,
      iconUrl = updatedSubreddit.iconUrl,
      lastPosted = updatedSubreddit.lastPosted
    )
    assertEquals(updatedSubreddit, subredditEntityQueries.select(subreddit.name).executeAsOne())
  }

  @Test
  fun `update not inserted`() {
    subredditEntityQueries.update(name = subreddit.name, iconUrl = null, lastPosted = null)
    assertEquals(null, subredditEntityQueries.select(subreddit.name).executeAsOneOrNull())
  }

  companion object {
    private val subreddit = Subreddit(
      name = SubredditName("subreddit"),
      iconUrl = SubredditIconUrl("https://icon_img.png"),
      lastPosted = SubredditLastPosted(123456789)
    )
  }
}
