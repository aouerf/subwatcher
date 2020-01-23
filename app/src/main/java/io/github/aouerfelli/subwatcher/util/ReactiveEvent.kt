package io.github.aouerfelli.subwatcher.util

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

open class ReactiveEvent<T> protected constructor(
  value: T?,
  private val key: String?,
  private val handle: SavedStateHandle?
) {

  private val channel = ConflatedBroadcastChannel<T?>()
  val flow = channel.asFlow()

  open var value: T?
    get() = channel.value
    protected set(value) {
      channel.offer(value)
      if (handle != null && key != null) {
        handle[key] = value
      }
    }

  init {
    @Suppress("LeakingThis")
    this.value = value
  }

  fun clear() {
    value = null
  }

  inline fun observeOn(scope: CoroutineScope, crossinline action: suspend (T) -> Unit) {
    flow
      .onEach { if (it != null) action(it) }
      .launchIn(scope)
  }
}

class MutableReactiveEvent<T> private constructor(
  value: T?,
  key: String?,
  handle: SavedStateHandle?
) : ReactiveEvent<T>(value, key, handle) {

  constructor(value: T? = null) : this(value, null, null)

  constructor(key: String, handle: SavedStateHandle) : this(handle[key], key, handle)

  override var value: T?
    get() = super.value
    public set(value) {
      super.value = value
    }
}
