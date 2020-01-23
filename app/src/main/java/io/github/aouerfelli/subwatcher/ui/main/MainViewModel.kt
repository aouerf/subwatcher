package io.github.aouerfelli.subwatcher.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.repository.Result
import io.github.aouerfelli.subwatcher.repository.SubredditRepository
import io.github.aouerfelli.subwatcher.util.MutableReactiveEvent
import io.github.aouerfelli.subwatcher.util.asImmutable
import kotlinx.coroutines.launch

class MainViewModel @AssistedInject constructor(
  private val repository: SubredditRepository,
  @Assisted private val handle: SavedStateHandle
) : ViewModel() {

  @AssistedInject.Factory
  interface Factory {
    fun create(state: SavedStateHandle): MainViewModel
  }

  val subredditList = repository.subreddits

  private val _isLoading = MutableReactiveEvent(false, "loading", handle)
  val isLoading get() = _isLoading.asImmutable()

  private val _addedSubreddit = MutableReactiveEvent<Pair<String, Result<Subreddit>>>("added", handle)
  val addedSubreddit = _addedSubreddit.asImmutable()

  private val _deletedSubreddit = MutableReactiveEvent<Result<Subreddit>>("deleted", handle)
  val deletedSubreddit = _deletedSubreddit.asImmutable()

  private val _refreshedSubreddits = MutableReactiveEvent<Result<Nothing>>("refreshed", handle)
  val refreshedSubreddits = _refreshedSubreddits.asImmutable()

  private inline fun load(crossinline load: suspend () -> Unit) = viewModelScope.launch {
    _isLoading.value = true
    load()
    _isLoading.value = false
  }

  fun refresh() {
    load {
      _refreshedSubreddits.value = repository.refreshSubreddits()
    }
  }

  fun add(subredditName: String) {
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
}
