package io.github.aouerfelli.subwatcher.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import coil.ImageLoader
import io.github.aouerfelli.subwatcher.BuildConfig
import io.github.aouerfelli.subwatcher.R
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.databinding.MainFragmentBinding
import io.github.aouerfelli.subwatcher.repository.Result
import io.github.aouerfelli.subwatcher.repository.asUrl
import io.github.aouerfelli.subwatcher.ui.BaseFragment
import io.github.aouerfelli.subwatcher.util.SnackbarLength
import io.github.aouerfelli.subwatcher.util.launch
import io.github.aouerfelli.subwatcher.util.makeSnackbar
import io.github.aouerfelli.subwatcher.util.observe
import io.github.aouerfelli.subwatcher.util.observeNotNull
import io.github.aouerfelli.subwatcher.util.onSwipe
import io.github.aouerfelli.subwatcher.util.setThemeColorScheme
import io.github.aouerfelli.subwatcher.util.toAndroidString
import javax.inject.Inject
import timber.log.Timber
import timber.log.warn

class MainFragment : BaseFragment<MainFragmentBinding, MainViewModel>() {

  @Inject
  lateinit var viewModelFactory: MainViewModel.Factory
  override val viewModelClass = MainViewModel::class

  @Inject
  lateinit var imageLoader: ImageLoader
  private lateinit var subredditListAdapter: SubredditListAdapter

  override fun inflateView(
    inflater: LayoutInflater,
    root: ViewGroup?,
    attachToRoot: Boolean
  ): MainFragmentBinding = MainFragmentBinding.inflate(inflater, root, attachToRoot)

  override fun createViewModel(handle: SavedStateHandle) = viewModelFactory.create(handle)

  override fun onBindingCreated(binding: MainFragmentBinding, savedInstanceState: Bundle?) {
    subredditListAdapter = SubredditListAdapter(imageLoader)
    binding.subredditList.adapter = subredditListAdapter
    binding.subredditList.onSwipe { viewHolder, _ ->
      val position = viewHolder.adapterPosition
      val item = subredditListAdapter.currentList[position]
      viewModel.delete(item)
    }

    binding.subredditsRefresh.setThemeColorScheme()
    binding.subredditsRefresh.setOnRefreshListener {
      viewModel.refresh()
    }

    binding.addSubredditButton.setOnLongClickListener {
      if (BuildConfig.DEBUG) {
        viewModel.add("random")
        true
      } else false
    }

    with(viewLifecycleOwner) {
      observe(viewModel.subredditList, subredditListAdapter::submitList)
      observe(viewModel.isLoading, binding.subredditsRefresh::setRefreshing)
      observeNotNull(viewModel.refreshedSubreddits, ::onSubredditsRefreshed)
      observeNotNull(viewModel.addedSubreddit, ::onSubredditAdded)
      observeNotNull(viewModel.deletedSubreddit, ::onSubredditDeleted)
    }
  }

  private fun onError(result: Result.Error) {
    @StringRes val stringRes = when (result) {
      Result.Error.ConnectionError -> R.string.no_connection
      Result.Error.NetworkError -> R.string.server_unreachable
    }
    binding?.root?.makeSnackbar(stringRes.toAndroidString())
  }

  private fun onSubredditsRefreshed(result: Result<Nothing>) {
    when (result) {
      is Result.Success.Empty -> Unit
      is Result.Error -> onError(result)
      else -> Timber.warn { "Refreshed subreddits result $result is not handled." }
    }
  }

  private fun onSubredditAdded(nameAndResult: Pair<String, Result<Subreddit>>) {
    val (name, result) = nameAndResult

    fun onSuccess(subreddit: Subreddit) {
      binding?.root?.makeSnackbar(
        getString(R.string.added_subreddit, subreddit.name.name).toAndroidString(),
        R.string.action_view.toAndroidString(),
        length = SnackbarLength.LONG
      ) {
        context?.let(subreddit.name.asUrl()::launch)
      }
    }

    fun onFailure(failure: Result.Failure) {
      @StringRes val stringRes = when (failure) {
        Result.Failure.NetworkFailure -> R.string.added_subreddit_does_not_exist
        Result.Failure.DatabaseFailure -> R.string.added_subreddit_exists
      }
      val string = getString(stringRes, name).toAndroidString()
      binding?.root?.makeSnackbar(string)
    }

    when (result) {
      is Result.Success -> onSuccess(result.data)
      is Result.Failure -> onFailure(result)
      is Result.Error -> onError(result)
      else -> Timber.warn { "Add subreddit result $result is not handled." }
    }
  }

  private fun onSubredditDeleted(result: Result<Subreddit>) {
    fun onSuccess(subreddit: Subreddit) {
      binding?.root?.makeSnackbar(
        getString(R.string.deleted_subreddit, subreddit.name.name).toAndroidString(),
        R.string.action_undo.toAndroidString(),
        length = SnackbarLength.LONG
      ) {
        viewModel.add(subreddit)
      }
    }

    when (result) {
      is Result.Success -> onSuccess(result.data)
      else -> Timber.warn { "Delete subreddit result $result is not handled." }
    }
  }
}
