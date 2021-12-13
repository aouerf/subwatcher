package com.aouerfelli.subwatcher.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface NotifyStateFlow<T : Any> : StateFlow<T?> {
  fun clear()
}

private class ImmutableNotifyStateFlow<T : Any>(flow: NotifyStateFlow<T>) : NotifyStateFlow<T> by flow

class MutableNotifyStateFlow<T : Any> private constructor(private val flow: MutableStateFlow<T?>) :
  NotifyStateFlow<T>, MutableStateFlow<T?> by flow {

  constructor() : this(MutableStateFlow(null))

  override fun clear() {
    value = null
  }
}

fun <T : Any> MutableNotifyStateFlow<T>.asEventStateFlow(): NotifyStateFlow<T> =
  ImmutableNotifyStateFlow(this)
