package io.github.aouerfelli.subwatcher.di

import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import io.github.aouerfelli.subwatcher.Database
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.SubwatcherApplication
import io.github.aouerfelli.subwatcher.repository.SubredditId
import io.github.aouerfelli.subwatcher.repository.SubredditName
import io.github.aouerfelli.subwatcher.util.EncodedImage
import javax.inject.Singleton

@Module
object DatabaseModule {

    private const val DB_NAME = "subwatcher.db"

    @JvmStatic
    @Provides
    @Singleton
    fun provideSqliteDriver(application: SubwatcherApplication): AndroidSqliteDriver {
        return AndroidSqliteDriver(
            schema = Database.Schema,
            context = application,
            name = DB_NAME
        )
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideDatabase(sqliteDriver: AndroidSqliteDriver): Database {
        val subredditAdapter = Subreddit.Adapter(
            idAdapter = object : ColumnAdapter<SubredditId, String> {
                override fun decode(databaseValue: String) = SubredditId(databaseValue)
                override fun encode(value: SubredditId) = value.value
            },
            nameAdapter = object : ColumnAdapter<SubredditName, String> {
                override fun decode(databaseValue: String) = SubredditName(databaseValue)
                override fun encode(value: SubredditName) = value.value
            },
            iconImageAdapter = object : ColumnAdapter<EncodedImage, ByteArray> {
                override fun decode(databaseValue: ByteArray) = EncodedImage.encode(databaseValue)
                override fun encode(value: EncodedImage) = value.decode()
            }
        )

        return Database(sqliteDriver, subredditAdapter = subredditAdapter)
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideSubredditQueries(database: Database) = database.subredditEntityQueries
}
