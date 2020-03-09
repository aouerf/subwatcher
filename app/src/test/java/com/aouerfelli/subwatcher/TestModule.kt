package com.aouerfelli.subwatcher

import com.aouerfelli.subwatcher.util.CoroutineDispatchers
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver.Companion.IN_MEMORY
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
object TestModule {

  @Provides
  @Singleton
  fun provideCoroutineDispatchers(coroutineDispatcher: CoroutineDispatcher): CoroutineDispatchers {
    return object : CoroutineDispatchers {
      override val default = coroutineDispatcher
      override val unconfined = coroutineDispatcher
      override val io = coroutineDispatcher
    }
  }

  @Provides
  fun provideSqlDriver(): SqlDriver = JdbcSqliteDriver(IN_MEMORY)
}
