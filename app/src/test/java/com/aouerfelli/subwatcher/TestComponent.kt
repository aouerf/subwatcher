package com.aouerfelli.subwatcher

import com.aouerfelli.subwatcher.database.DatabaseModule
import com.aouerfelli.subwatcher.network.NetworkModule
import com.aouerfelli.subwatcher.repository.SubredditRepositoryTest
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.mockwebserver.MockWebServer
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    TestModule::class,
    NetworkModule::class,
    DatabaseModule::class
  ]
)
interface TestComponent {

  @Component.Factory
  interface Factory {
    fun create(
      @BindsInstance coroutineDispatcher: CoroutineDispatcher,
      @BindsInstance mockWebServer: MockWebServer
    ): TestComponent
  }

  fun inject(subredditRepositoryTest: SubredditRepositoryTest)
}
