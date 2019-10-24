package io.github.aouerfelli.subwatcher.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, observer: (T) -> Unit) {
    liveData.observe(this, observer)
}

inline fun <T> LifecycleOwner.observeNotNull(
    liveData: LiveData<T?>,
    crossinline observer: (T) -> Unit
) {
    liveData.observe(this) { it?.let(observer) }
}

fun <T> Flow<T>.asEventLiveData(
    context: CoroutineContext = EmptyCoroutineContext
) = liveData(context) {
    collect { value ->
        value?.let { emit(it) }
        emit(null)
    }
}
