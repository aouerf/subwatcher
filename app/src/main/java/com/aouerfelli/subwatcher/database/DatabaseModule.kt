package com.aouerfelli.subwatcher.database

import com.aouerfelli.subwatcher.repository.SubredditIconUrl
import com.aouerfelli.subwatcher.repository.SubredditLastPosted
import com.aouerfelli.subwatcher.repository.SubredditName
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import javax.inject.Singleton

@Module
@DisableInstallInCheck
object DatabaseModule {

  @Provides
  @Singleton
  fun provideDatabase(sqliteDriver: SqlDriver): Database {
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

    return Database(sqliteDriver, subredditAdapter = subredditAdapter)
  }

  @Provides
  fun provideSubredditQueries(database: Database) = database.subredditQueries
}
