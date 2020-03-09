package com.aouerfelli.subwatcher

import com.aouerfelli.subwatcher.util.CoroutineDispatchers
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
      override val main = coroutineDispatcher
      override val unconfined = coroutineDispatcher
      override val io = coroutineDispatcher
    }
  }
}
