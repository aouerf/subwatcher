package io.github.aouerfelli.subwatcher.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.repository.Result
import io.github.aouerfelli.subwatcher.repository.SubredditName
import io.github.aouerfelli.subwatcher.repository.SubredditRepository
import io.github.aouerfelli.subwatcher.util.MutableReactiveEvent
import io.github.aouerfelli.subwatcher.util.asImmutable
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class MainViewModel @AssistedInject constructor(
  private val repository: SubredditRepository,
  @Assisted private val handle: SavedStateHandle
) : ViewModel() {

  @AssistedInject.Factory
  interface Factory {
    fun create(handle: SavedStateHandle): MainViewModel
  }

  val subredditList = repository.subreddits

  private val _isLoading = ConflatedBroadcastChannel(false)
  val isLoading = _isLoading.asFlow()

  private val _addedSubreddit = MutableReactiveEvent<Pair<SubredditName, Result<Subreddit>>>()
  val addedSubreddit = _addedSubreddit.asImmutable()

  private val _deletedSubreddit = MutableReactiveEvent<Result<Subreddit>>()
  val deletedSubreddit = _deletedSubreddit.asImmutable()

  private val _refreshedSubreddits = MutableReactiveEvent<Result<Nothing>>()
  val refreshedSubreddits = _refreshedSubreddits.asImmutable()

  private inline fun load(crossinline load: suspend () -> Unit) {
    _isLoading.offer(true)
    viewModelScope.launch {
      load()
    }.invokeOnCompletion {
      val noJobsRunning = viewModelScope.coroutineContext[Job]?.children?.none() != false
      if (noJobsRunning) {
        _isLoading.offer(false)
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
    repository.updateLastPosted(subreddit)
  }
}
