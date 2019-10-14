package io.github.aouerfelli.subwatcher.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.aouerfelli.subwatcher.repository.SubredditRepository
import io.github.aouerfelli.subwatcher.util.asEventLiveData
import kotlinx.coroutines.launch

class MainViewModel @AssistedInject constructor(
    private val repository: SubredditRepository,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    @AssistedInject.Factory
    interface Factory {
        fun create(state: SavedStateHandle): MainViewModel
    }

    val subredditList = repository.subreddits.asLiveData(viewModelScope.coroutineContext)

    val resultState = repository.states.asEventLiveData(viewModelScope.coroutineContext)

    fun refresh() = viewModelScope.launch {
        repository.refreshSubreddits()
    }

    fun add(subredditName: String) = viewModelScope.launch {
        repository.addSubreddit(subredditName)
    }
}
