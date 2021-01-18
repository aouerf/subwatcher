package com.aouerfelli.subwatcher.ui.main

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aouerfelli.subwatcher.Subreddit
import com.aouerfelli.subwatcher.repository.Result
import com.aouerfelli.subwatcher.repository.SubredditName
import com.aouerfelli.subwatcher.repository.SubredditRepository
import com.aouerfelli.subwatcher.util.MutableEventStream
import com.aouerfelli.subwatcher.util.asImmutable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val repository: SubredditRepository,
  private val processLifecycleScope: LifecycleCoroutineScope,
  private val handle: SavedStateHandle
) : ViewModel() {

  val subredditList = repository.getSubredditsFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> get() = _isLoading

  private val _addedSubreddit = MutableEventStream<Pair<SubredditName, Result<Subreddit>>>()
  val addedSubreddit get() = _addedSubreddit.asImmutable()

  private val _deletedSubreddit = MutableEventStream<Result<Subreddit>>()
  val deletedSubreddit get() = _deletedSubreddit.asImmutable()

  private val _refreshedSubreddits = MutableEventStream<Result<Nothing>>()
  val refreshedSubreddits get() = _refreshedSubreddits.asImmutable()

  private inline fun load(crossinline load: suspend () -> Unit) {
    _isLoading.value = true
    viewModelScope.launch {
      load()
    }.invokeOnCompletion {
      val noJobsRunning = viewModelScope.coroutineContext[Job]?.children?.none() != false
      if (noJobsRunning) {
        _isLoading.value = false
      }
    }
  }

  fun refresh() {
    load {
      _refreshedSubreddits.value = repository.refreshSubreddits()
    }
  }

  fun add(subredditName: SubredditName) {
    load {
      _addedSubreddit.value = subredditName to repository.addSubreddit(subredditName)
    }
  }

  fun add(subreddit: Subreddit) {
    load {
      repository.addSubreddit(subreddit)
    }
  }

  fun delete(subreddit: Subreddit) {
    load {
      _deletedSubreddit.value = repository.deleteSubreddit(subreddit)
    }
  }

  fun updateLastPosted(subreddit: Subreddit) {
    processLifecycleScope.launch {
      repository.updateLastPosted(subreddit)
    }
  }
}
