package com.aouerfelli.subwatcher.util.extensions

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend inline fun <T, R> Iterable<T>.mapAsync(crossinline transform: suspend (T) -> R): List<R> {
  return coroutineScope {
    map { element ->
      async { transform(element) }
    }.awaitAll()
  }
}

suspend inline fun <T> Iterable<T>.forEachAsync(crossinline action: suspend (T) -> Unit) {
  mapAsync(action)
}
