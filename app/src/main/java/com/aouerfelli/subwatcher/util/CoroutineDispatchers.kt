package com.aouerfelli.subwatcher.util

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineDispatchers {
  val default: CoroutineDispatcher
  val main: CoroutineDispatcher
  val unconfined: CoroutineDispatcher
  val io: CoroutineDispatcher
}
