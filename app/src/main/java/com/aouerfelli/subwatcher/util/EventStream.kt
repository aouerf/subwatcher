package com.aouerfelli.subwatcher.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ObsoleteCoroutinesApi::class)
open class EventStream<T : Any> protected constructor(
  value: T? = null,
  private val handler: Pair<SavedStateHandle, SavedStateHandler<T>>? = null
) {

  // TODO: SharedFlow
  private val channel = ConflatedBroadcastChannel<T?>()
  @Suppress("DEPRECATION")
  val flow = channel.asFlow()

  open var value: T?
    get() = channel.value
    protected set(value) {
      channel.trySend(value)
      handler?.let { (handle, key) -> handle[key] = value }
    }

  init {
    @Suppress("LeakingThis")
    this.value = value
  }

  fun clear() {
    value = null
  }
}

class MutableEventStream<T : Any> private constructor(
  value: T?,
  handler: Pair<SavedStateHandle, SavedStateHandler<T>>?
) : EventStream<T>(value, handler) {

  constructor(value: T? = null) : this(value, null)

  constructor(handle: SavedStateHandle, key: SavedStateHandler<T>) : this(
    handle[key],
    handle to key
  )

  companion object {
    operator fun <T : Any> invoke(
      value: T,
      handle: SavedStateHandle,
      key: SavedStateHandler<T>
    ): MutableEventStream<T> {
      handle[key] = value
      return MutableEventStream(value, handle to key)
    }
  }

  override var value: T?
    get() = super.value
    public set(value) {
      super.value = value
    }
}

fun <T : Any> MutableEventStream<T>.asImmutable(): EventStream<T> = this

inline fun <T : Any> EventStream<T>.observe(
  scope: CoroutineScope,
  crossinline action: (T) -> Unit
) {
  flow
    .onEach { if (it != null) action(it) }
    .launchIn(scope)
}

inline fun <T : Any> EventStream<T>.observe(
  owner: LifecycleOwner,
  crossinline action: (T) -> Unit
) {
  observe(owner.lifecycleScope, action)
}

sealed class SavedStateHandler<T : Any> {

  data class Key<T : Any>(val key: String) : SavedStateHandler<T>()
  data class Delegate<T : Any>(
    val get: (SavedStateHandle) -> T?,
    val set: (SavedStateHandle, T?) -> Unit
  ) : SavedStateHandler<T>()

  companion object {
    operator fun <T : Any> invoke(key: String) = Key<T>(key)
    operator fun <T : Any> invoke(
      get: (SavedStateHandle) -> T?,
      set: (SavedStateHandle, T?) -> Unit
    ) = Delegate(get, set)
  }
}

private operator fun <T : Any> SavedStateHandle.get(key: SavedStateHandler<T>): T? {
  return when (key) {
    is SavedStateHandler.Key -> get(key.key)
    is SavedStateHandler.Delegate -> key.get(this)
  }
}

private operator fun <T : Any> SavedStateHandle.set(key: SavedStateHandler<T>, value: T?) {
  when (key) {
    is SavedStateHandler.Key -> set(key.key, value)
    is SavedStateHandler.Delegate -> key.set(this, value)
  }
}
