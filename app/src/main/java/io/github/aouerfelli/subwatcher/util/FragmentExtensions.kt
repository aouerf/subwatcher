package io.github.aouerfelli.subwatcher.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

inline fun <reified VM : ViewModel> Fragment.provideViewModel(
    crossinline provider: (SavedStateHandle) -> VM
) = viewModels<VM> {
    object : AbstractSavedStateViewModelFactory(this, arguments) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            @Suppress("UNCHECKED_CAST")
            return provider(handle) as T
        }
    }
}
