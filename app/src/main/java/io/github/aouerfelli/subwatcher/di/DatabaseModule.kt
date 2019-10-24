package io.github.aouerfelli.subwatcher.di

import android.content.Context
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import io.github.aouerfelli.subwatcher.Database
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.repository.SubredditId
import io.github.aouerfelli.subwatcher.repository.SubredditName
import io.github.aouerfelli.subwatcher.util.ImageBlob
import javax.inject.Singleton

@Module
object DatabaseModule {

    private const val DB_NAME = "subwatcher.db"

    @Provides
    @Singleton
    fun provideDatabase(context: Context): Database {
        val subredditAdapter = Subreddit.Adapter(
            idAdapter = object : ColumnAdapter<SubredditId, String> {
                override fun decode(databaseValue: String) = SubredditId(databaseValue)
                override fun encode(value: SubredditId) = value.value
            },
            nameAdapter = object : ColumnAdapter<SubredditName, String> {
                override fun decode(databaseValue: String) = SubredditName(databaseValue)
                override fun encode(value: SubredditName) = value.value
            },
            iconImageAdapter = object : ColumnAdapter<ImageBlob, ByteArray> {
                override fun decode(databaseValue: ByteArray) = ImageBlob(databaseValue)
                override fun encode(value: ImageBlob) = value.value
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
