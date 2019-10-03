package io.github.aouerfelli.subwatcher.ui.main

import androidx.lifecycle.*
import io.github.aouerfelli.subwatcher.repository.SubredditRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repository: SubredditRepository) : ViewModel() {

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
