package com.aouerfelli.subwatcher.ui.main

import android.os.Bundle
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.Adapter
import coil.ImageLoader
import com.aouerfelli.subwatcher.BuildConfig
import com.aouerfelli.subwatcher.R
import com.aouerfelli.subwatcher.database.Subreddit
import com.aouerfelli.subwatcher.databinding.MainFragmentBinding
import com.aouerfelli.subwatcher.repository.Result
import com.aouerfelli.subwatcher.repository.SubredditName
import com.aouerfelli.subwatcher.repository.asUrl
import com.aouerfelli.subwatcher.ui.ViewBindingFragment
import com.aouerfelli.subwatcher.util.EventSnackbar
import com.aouerfelli.subwatcher.util.SnackbarLength
import com.aouerfelli.subwatcher.util.extensions.launch
import com.aouerfelli.subwatcher.util.extensions.onSwipe
import com.aouerfelli.subwatcher.util.extensions.setThemeColorScheme
import com.aouerfelli.subwatcher.util.makeSnackbar
import com.aouerfelli.subwatcher.util.observe
import com.aouerfelli.subwatcher.util.toAndroidString
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : ViewBindingFragment<MainFragmentBinding>(MainFragmentBinding::inflate) {

  companion object {
    const val ADD_SUBREDDIT_REQUEST_KEY = "add_subreddit"
  }

  private val viewModel: MainViewModel by viewModels()

  @Inject
  lateinit var imageLoader: ImageLoader
  private lateinit var subredditListAdapter: SubredditListAdapter

  private val eventSnackbar = EventSnackbar()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    childFragmentManager.setFragmentResultListener(ADD_SUBREDDIT_REQUEST_KEY, this) { _, bundle ->
      val subredditName = bundle.getString(AddSubredditDialogFragment.SUBREDDIT_NAME_KEY)
      if (subredditName != null) {
        viewModel.add(SubredditName(subredditName))
      }
    }
  }

  override fun onBindingCreated(binding: MainFragmentBinding, savedInstanceState: Bundle?) {
    subredditListAdapter = SubredditListAdapter(imageLoader) { subreddit, viewContext ->
      subreddit.name.asUrl().launch(viewContext)
      viewModel.updateLastPosted(subreddit)
    }
    subredditListAdapter.stateRestorationPolicy = Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    binding.subredditList.adapter = subredditListAdapter
    binding.subredditList.onSwipe { viewHolder, _ ->
      val position = viewHolder.bindingAdapterPosition
      val item = subredditListAdapter.currentList[position]
      viewModel.delete(item)
    }
    binding.subredditList.applyInsetter {
      type(navigationBars = true, statusBars = true) {
        padding()
      }
    }

    binding.subredditsRefresh.setThemeColorScheme()
    binding.subredditsRefresh.setOnRefreshListener {
      viewModel.refresh()
    }

    binding.addSubredditButton.setOnClickListener {
      val dialogFragment = AddSubredditDialogFragment()
      dialogFragment.show(childFragmentManager, dialogFragment.tag)
    }
    binding.addSubredditButton.setOnLongClickListener {
      if (BuildConfig.DEBUG) {
        viewModel.add(SubredditName("random"))
        true
      } else {
        false
      }
    }
    binding.addSubredditButton.applyInsetter {
      type(tappableElement = true) {
        margin()
      }
    }

    viewModel.subredditList
      .onEach { list ->
        binding.emptyStateContainer.isGone = list.isNotEmpty()
        subredditListAdapter.submitList(list)
        binding.subredditsRefresh.isEnabled = list.isNotEmpty()
      }
      .launchIn(viewLifecycleOwner.lifecycleScope)
    viewModel.isLoading
      .onEach { binding.subredditsRefresh.isRefreshing = it }
      .launchIn(viewLifecycleOwner.lifecycleScope)
    viewModel.refreshedSubreddits.observe(viewLifecycleOwner, ::onSubredditsRefreshed)
    viewModel.addedSubreddit.observe(viewLifecycleOwner, ::onSubredditAdded)
    viewModel.deletedSubreddit.observe(viewLifecycleOwner, ::onSubredditDeleted)
  }

  private inline fun onError(result: Result.Error, crossinline onHandled: () -> Unit) {
    val stringRes = when (result) {
      Result.Error.ConnectionError -> R.string.no_connection
      Result.Error.NetworkError -> R.string.server_unreachable
    }
    val snackbar = binding?.root?.makeSnackbar(stringRes.toAndroidString())
      ?.setAnchorView(binding?.addSubredditButton)
    eventSnackbar.set(snackbar) { onHandled() }
  }

  private fun onSubredditsRefreshed(result: Result<Nothing>) {
    when (result) {
      is Result.Success.Empty -> Unit
      is Result.Error -> onError(result, viewModel.refreshedSubreddits::clear)
      else -> Timber.w("Refreshed subreddits result $result is not handled.")
    }
  }

  private fun onSubredditAdded(nameAndResult: Pair<SubredditName, Result<Subreddit>>) {
    val (name, result) = nameAndResult

    fun onSuccess(subreddit: Subreddit) {
      val snackbar = binding?.root?.makeSnackbar(
        getString(R.string.added_subreddit, subreddit.name.name).toAndroidString(),
        R.string.action_view.toAndroidString(),
        length = SnackbarLength.LONG
      ) {
        context?.let(subreddit.name.asUrl()::launch)
      }?.setAnchorView(binding?.addSubredditButton)
      eventSnackbar.set(snackbar, viewModel.addedSubreddit::clear)
    }

    fun onFailure(failure: Result.Failure) {
      val stringRes = when (failure) {
        Result.Failure.NetworkFailure -> R.string.added_subreddit_does_not_exist
        Result.Failure.DatabaseFailure -> R.string.added_subreddit_exists
      }
      val string = getString(stringRes, name.name).toAndroidString()
      val snackbar = binding?.root?.makeSnackbar(string)?.setAnchorView(binding?.addSubredditButton)
      eventSnackbar.set(snackbar, viewModel.addedSubreddit::clear)
    }

    when (result) {
      is Result.Success -> onSuccess(result.data)
      is Result.Failure -> onFailure(result)
      is Result.Error -> onError(result, viewModel.addedSubreddit::clear)
      else -> Timber.w("Add subreddit result $result is not handled.")
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
      }?.setAnchorView(binding?.addSubredditButton)
      eventSnackbar.set(snackbar, viewModel.deletedSubreddit::clear)
    }

    when (result) {
      is Result.Success -> onSuccess(result.data)
      else -> Timber.w("Delete subreddit result $result is not handled.")
    }
  }
}
