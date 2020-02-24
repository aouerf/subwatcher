package io.github.aouerfelli.subwatcher.util.extensions

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend inline fun <T> Iterable<T>.forEachAsync(crossinline action: suspend (T) -> Unit) {
  coroutineScope {
    map { element ->
      async { action(element) }
    }.awaitAll()
  }
}
