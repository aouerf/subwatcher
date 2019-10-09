package io.github.aouerfelli.subwatcher.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.aouerfelli.subwatcher.repository.SubredditRepository
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

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing

    fun refresh() = viewModelScope.launch {
        _isRefreshing.value = true
        repository.refreshSubreddits()
        _isRefreshing.value = false
    }

    fun add(subredditName: String) = viewModelScope.launch {
        repository.addSubreddit(subredditName)
    }
}
