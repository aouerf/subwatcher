package com.aouerfelli.subwatcher.database

import android.content.Context
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import com.aouerfelli.subwatcher.Database
import com.aouerfelli.subwatcher.Subreddit
import com.aouerfelli.subwatcher.repository.SubredditIconUrl
import com.aouerfelli.subwatcher.repository.SubredditLastPosted
import com.aouerfelli.subwatcher.repository.SubredditName
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
