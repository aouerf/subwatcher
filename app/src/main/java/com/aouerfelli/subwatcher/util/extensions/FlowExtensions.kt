package com.aouerfelli.subwatcher.util.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

inline fun <T : Any> Flow<T?>.observe(
  lifecycleOwner: LifecycleOwner,
  crossinline action: suspend (T) -> Unit
) = this
  .flowWithLifecycle(lifecycleOwner.lifecycle)
  .onEach { if (it != null) action(it) }
  .launchIn(lifecycleOwner.lifecycleScope)
