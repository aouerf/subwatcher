package com.aouerfelli.subwatcher

import com.aouerfelli.subwatcher.network.NetworkDetails
import com.aouerfelli.subwatcher.util.CoroutineDispatchers
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver.Companion.IN_MEMORY
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.mockwebserver.MockWebServer
import javax.inject.Singleton

@Module
@DisableInstallInCheck
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
  fun provideNetworkDetails(mockWebServer: MockWebServer): NetworkDetails {
    return NetworkDetails(baseUrl = mockWebServer.url("/"))
  }

  @Provides
  fun provideSqlDriver(): SqlDriver {
    val driver = JdbcSqliteDriver(IN_MEMORY)
    Database.Schema.create(driver)
    return driver
  }
}
