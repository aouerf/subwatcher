package io.github.aouerfelli.subwatcher.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dev.chrisbanes.insetter.doOnApplyWindowInsets
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
import io.github.aouerfelli.subwatcher.util.observeOn
import io.github.aouerfelli.subwatcher.util.onSwipe
import io.github.aouerfelli.subwatcher.util.setThemeColorScheme
import io.github.aouerfelli.subwatcher.util.toAndroidString
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

  private var snackbar: Snackbar? = null
  private var snackbarDismissCallback: Snackbar.Callback? = null

  private inline fun setSnackbar(value: Snackbar?, crossinline onFinish: () -> Unit = { }) {
    val validDismissEvent = BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE
    snackbarDismissCallback?.onDismissed(snackbar, validDismissEvent)

    value ?: return

    snackbarDismissCallback = object : Snackbar.Callback() {
      override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
        transientBottomBar ?: return
        if (event != DISMISS_EVENT_MANUAL && event != DISMISS_EVENT_CONSECUTIVE) {
          onFinish()
        }
        snackbar = null
        snackbarDismissCallback = null
      }
    }
    snackbar = value.apply {
      addCallback(snackbarDismissCallback)
      anchorView = binding?.addSubredditButton
      show()
    }
  }

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
    binding.subredditList.doOnApplyWindowInsets { view, insets, initialState ->
      view.updatePadding(
        left = insets.systemWindowInsetLeft + initialState.paddings.left,
        top = insets.systemWindowInsetTop + initialState.paddings.top,
        right = insets.systemWindowInsetRight + initialState.paddings.right,
        bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
      )
    }

    binding.subredditsRefresh.setThemeColorScheme()
    binding.subredditsRefresh.setOnRefreshListener {
      viewModel.refresh()
    }

    binding.addSubredditButton.setOnLongClickListener {
      if (BuildConfig.DEBUG) {
        viewModel.add("random")
        true
      } else {
        false
      }
    }
    binding.addSubredditButton.doOnApplyWindowInsets { view, insets, initialState ->
      view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        updateMargins(
          left = insets.systemWindowInsetLeft + initialState.margins.left,
          top = insets.systemWindowInsetTop + initialState.margins.top,
          right = insets.systemWindowInsetRight + initialState.margins.right,
          bottom = insets.systemWindowInsetBottom + initialState.margins.bottom
        )
      }
    }

    viewModel.subredditList
      .onEach { subredditListAdapter.submitList(it) }
      .launchIn(viewLifecycleOwner.lifecycleScope)
    viewModel.isLoading
      .onEach { binding.subredditsRefresh.isRefreshing = it }
      .launchIn(viewLifecycleOwner.lifecycleScope)
    viewModel.refreshedSubreddits.observeOn(viewLifecycleOwner, ::onSubredditsRefreshed)
    viewModel.addedSubreddit.observeOn(viewLifecycleOwner, ::onSubredditAdded)
    viewModel.deletedSubreddit.observeOn(viewLifecycleOwner, ::onSubredditDeleted)
  }

  private inline fun onError(result: Result.Error, crossinline onHandled: () -> Unit) {
    @StringRes val stringRes = when (result) {
      Result.Error.ConnectionError -> R.string.no_connection
      Result.Error.NetworkError -> R.string.server_unreachable
    }
    val snackbar = binding?.root?.makeSnackbar(stringRes.toAndroidString())
    setSnackbar(snackbar, onHandled)
  }

  private fun onSubredditsRefreshed(result: Result<Nothing>) {
    when (result) {
      is Result.Success.Empty -> Unit
      is Result.Error -> onError(result, viewModel.refreshedSubreddits::clear)
      else -> Timber.warn { "Refreshed subreddits result $result is not handled." }
    }
  }

  private fun onSubredditAdded(nameAndResult: Pair<String, Result<Subreddit>>) {
    val (name, result) = nameAndResult

    fun onSuccess(subreddit: Subreddit) {
      val snackbar = binding?.root?.makeSnackbar(
        getString(R.string.added_subreddit, subreddit.name.name).toAndroidString(),
        R.string.action_view.toAndroidString(),
        length = SnackbarLength.LONG
      ) {
        context?.let(subreddit.name.asUrl()::launch)
      }
      setSnackbar(snackbar, viewModel.addedSubreddit::clear)
    }

    fun onFailure(failure: Result.Failure) {
      @StringRes val stringRes = when (failure) {
        Result.Failure.NetworkFailure -> R.string.added_subreddit_does_not_exist
        Result.Failure.DatabaseFailure -> R.string.added_subreddit_exists
      }
      val string = getString(stringRes, name).toAndroidString()
      val snackbar = binding?.root?.makeSnackbar(string)
      setSnackbar(snackbar, viewModel.addedSubreddit::clear)
    }

    when (result) {
      is Result.Success -> onSuccess(result.data)
      is Result.Failure -> onFailure(result)
      is Result.Error -> onError(result, viewModel.addedSubreddit::clear)
      else -> Timber.warn { "Add subreddit result $result is not handled." }
    }
  }

  private fun onSubredditDeleted(result: Result<Subreddit>) {
    fun onSuccess(subreddit: Subreddit) {
      val snackbar = binding?.root?.makeSnackbar(
        getString(R.string.deleted_subreddit, subreddit.name.name).toAndroidString(),
        R.string.action_undo.toAndroidString(),
        length = SnackbarLength.LONG
      ) {
        viewModel.add(subreddit)
      }
      setSnackbar(snackbar, viewModel.deletedSubreddit::clear)
    }

    when (result) {
      is Result.Success -> onSuccess(result.data)
      else -> Timber.warn { "Delete subreddit result $result is not handled." }
    }
  }
}
