package io.github.aouerfelli.subwatcher.database

import android.annotation.SuppressLint
import android.content.Context
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import io.github.aouerfelli.subwatcher.Database
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.repository.SubredditIconImage
import io.github.aouerfelli.subwatcher.repository.SubredditName
import java.time.Instant
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
      iconImageAdapter = object : ColumnAdapter<SubredditIconImage, ByteArray> {
        override fun decode(databaseValue: ByteArray) = SubredditIconImage(databaseValue)
        override fun encode(value: SubredditIconImage) = value.image
      },
      // Core library desugaring handles Instant on API < 26
      lastPostedAdapter = @SuppressLint("NewApi") object : ColumnAdapter<Instant, Long> {
        override fun decode(databaseValue: Long) = Instant.ofEpochSecond(databaseValue)
        override fun encode(value: Instant) = value.epochSecond
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
