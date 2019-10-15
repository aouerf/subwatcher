package io.github.aouerfelli.subwatcher.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.repository.SubredditRepository
import io.github.aouerfelli.subwatcher.util.asEventLiveData
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
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

    private val _deletedSubreddit = ConflatedBroadcastChannel<Subreddit>()
    val deletedSubreddit =
        _deletedSubreddit.asFlow().asEventLiveData(viewModelScope.coroutineContext)

    fun refresh() = viewModelScope.launch {
        repository.refreshSubreddits()
    }

    fun add(subredditName: String) = viewModelScope.launch {
        repository.addSubreddit(subredditName)
    }

    fun add(subreddit: Subreddit) = viewModelScope.launch {
        repository.addSubreddit(subreddit)
    }

    fun delete(subreddit: Subreddit) = viewModelScope.launch {
        repository.deleteSubreddit(subreddit)?.also(_deletedSubreddit::offer)
    }
}
