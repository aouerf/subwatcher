package io.github.aouerfelli.subwatcher.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get

inline fun <reified VM : ViewModel> Fragment.provideViewModel(
    crossinline provider: (SavedStateHandle) -> VM
): VM {
    val factory = object : AbstractSavedStateViewModelFactory(this, arguments) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            @Suppress("UNCHECKED_CAST")
            return provider(handle) as T
        }
    }
    return ViewModelProvider(this, factory).get()
}
