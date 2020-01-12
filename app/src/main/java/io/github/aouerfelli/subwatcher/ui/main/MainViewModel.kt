package io.github.aouerfelli.subwatcher.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.repository.Result
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

  private val coroutineContext
    get() = viewModelScope.coroutineContext

  val subredditList = repository.subreddits.asLiveData(coroutineContext)

  private val _isLoading = MutableLiveData(false)
  val isLoading: LiveData<Boolean>
    get() = _isLoading

  private val _addedSubreddit = ConflatedBroadcastChannel<Pair<String, Result<Subreddit>>>()
  val addedSubreddit = _addedSubreddit.asFlow().asEventLiveData(coroutineContext)

  private val _deletedSubreddit = ConflatedBroadcastChannel<Result<Subreddit>>()
  val deletedSubreddit = _deletedSubreddit.asFlow().asEventLiveData(coroutineContext)

  private val _refreshedSubreddits = ConflatedBroadcastChannel<Result<Nothing>>()
  val refreshedSubreddits = _refreshedSubreddits.asFlow().asEventLiveData(coroutineContext)

  private inline fun load(crossinline load: suspend () -> Unit) = viewModelScope.launch {
    _isLoading.value = true
    load()
    _isLoading.value = false
  }

  fun refresh() {
    load { repository.refreshSubreddits().also(_refreshedSubreddits::offer) }
  }

  fun add(subredditName: String) {
    load {
      repository.addSubreddit(subredditName).also {
        _addedSubreddit.offer(subredditName to it)
      }
    }
  }

  fun add(subreddit: Subreddit) {
    load { repository.addSubreddit(subreddit) }
  }

  fun delete(subreddit: Subreddit) {
    load { repository.deleteSubreddit(subreddit).also(_deletedSubreddit::offer) }
  }
}
