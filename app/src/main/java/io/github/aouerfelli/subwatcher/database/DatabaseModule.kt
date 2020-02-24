package io.github.aouerfelli.subwatcher.database

import android.content.Context
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import io.github.aouerfelli.subwatcher.Database
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.repository.SubredditIconUrl
import io.github.aouerfelli.subwatcher.repository.SubredditLastPosted
import io.github.aouerfelli.subwatcher.repository.SubredditName
import javax.inject.Singleton

@Module
object DatabaseModule {

  private const val DB_NAME = "subwatcher.db"

  @Provides
  @Singleton
  fun provideDatabase(context: Context): Database {
    val subredditAdapter = Subreddit.Adapter(
      nameAdapter = object : ColumnAdapter<SubredditName, String> {
        override fun decode(databaseValue: String) = SubredditName(databaseValue)
        override fun encode(value: SubredditName) = value.name
      },
      iconUrlAdapter = object : ColumnAdapter<SubredditIconUrl, String> {
        override fun decode(databaseValue: String) = SubredditIconUrl(databaseValue)
        override fun encode(value: SubredditIconUrl) = value.url
      },
      lastPostedAdapter = object : ColumnAdapter<SubredditLastPosted, Long> {
        override fun decode(databaseValue: Long) = SubredditLastPosted(databaseValue)
        override fun encode(value: SubredditLastPosted) = value.epochSeconds
      }
    )

    val sqliteDriver = AndroidSqliteDriver(
      schema = Database.Schema,
      context = context,
      name = DB_NAME
    )

    return Database(sqliteDriver, subredditAdapter = subredditAdapter)
  }

  @Provides
  fun provideSubredditQueries(database: Database) = database.subredditEntityQueries
}
