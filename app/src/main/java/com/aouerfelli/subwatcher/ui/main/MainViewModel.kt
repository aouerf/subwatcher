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
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel @AssistedInject constructor(
  private val repository: SubredditRepository,
  private val processLifecycleScope: LifecycleCoroutineScope,
  @Assisted private val handle: SavedStateHandle
) : ViewModel() {

  @AssistedInject.Factory
  interface Factory {
    fun create(handle: SavedStateHandle): MainViewModel
  }

  val subredditList = repository.subreddits

  private val _isLoading = MutableEventStream(false)
  val isLoading = _isLoading.asImmutable()

  private val _addedSubreddit = MutableEventStream<Pair<SubredditName, Result<Subreddit>>>()
  val addedSubreddit = _addedSubreddit.asImmutable()

  private val _deletedSubreddit = MutableEventStream<Result<Subreddit>>()
  val deletedSubreddit = _deletedSubreddit.asImmutable()

  private val _refreshedSubreddits = MutableEventStream<Result<Nothing>>()
  val refreshedSubreddits = _refreshedSubreddits.asImmutable()

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
