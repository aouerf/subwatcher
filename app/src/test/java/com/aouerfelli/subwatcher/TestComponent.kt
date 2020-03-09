package com.aouerfelli.subwatcher

import com.aouerfelli.subwatcher.database.DatabaseModule
import com.aouerfelli.subwatcher.repository.SubredditRepositoryTest
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    TestModule::class,
    DatabaseModule::class
  ]
)
interface TestComponent {

  @Component.Factory
  interface Factory {
    fun create(@BindsInstance coroutineDispatcher: CoroutineDispatcher): TestComponent
  }

  fun inject(subredditRepositoryTest: SubredditRepositoryTest)
}
