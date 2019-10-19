package io.github.aouerfelli.subwatcher.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import dagger.android.support.DaggerFragment
import io.github.aouerfelli.subwatcher.R
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.databinding.MainFragmentBinding
import io.github.aouerfelli.subwatcher.repository.Result
import io.github.aouerfelli.subwatcher.util.SnackbarLength
import io.github.aouerfelli.subwatcher.util.launch
import io.github.aouerfelli.subwatcher.util.makeSnackbar
import io.github.aouerfelli.subwatcher.util.observe
import io.github.aouerfelli.subwatcher.util.observeNotNull
import io.github.aouerfelli.subwatcher.util.onSwipe
import io.github.aouerfelli.subwatcher.util.provideViewModel
import io.github.aouerfelli.subwatcher.util.setThemeColorScheme
import io.github.aouerfelli.subwatcher.util.toAndroidString
import timber.log.Timber
import timber.log.warn
import javax.inject.Inject

class MainFragment : DaggerFragment() {

    private lateinit var binding: MainFragmentBinding

    private lateinit var subredditListAdapter: SubredditListAdapter

    @Inject
    lateinit var mainViewModelFactory: MainViewModel.Factory
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subredditListAdapter = SubredditListAdapter()
        binding.subredditList.adapter = subredditListAdapter
        binding.subredditList.onSwipe { viewHolder, _ ->
            val position = viewHolder.adapterPosition
            val item = subredditListAdapter.currentList[position]
            mainViewModel.delete(item)
        }

        binding.subredditsRefresh.setThemeColorScheme()
        binding.subredditsRefresh.setOnRefreshListener {
            mainViewModel.refresh()
        }

        binding.addSubredditButton.setOnClickListener {
            mainViewModel.add("random")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainViewModel = provideViewModel(mainViewModelFactory::create)
        with(viewLifecycleOwner) {
            observe(mainViewModel.subredditList, subredditListAdapter::submitList)
            observe(mainViewModel.isLoading, binding.subredditsRefresh::setRefreshing)
            observeNotNull(mainViewModel.refreshedSubreddits, ::onSubredditsRefreshed)
            observeNotNull(mainViewModel.addedSubreddit, ::onSubredditAdded)
            observeNotNull(mainViewModel.deletedSubreddit, ::onSubredditDeleted)
        }
    }

    private fun onError(result: Result.Error) {
        @StringRes val stringRes = when (result) {
            Result.Error.ConnectionError -> R.string.no_connection
            Result.Error.NetworkError -> R.string.server_unreachable
        }
        binding.root.makeSnackbar(stringRes.toAndroidString())
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
            binding.root.makeSnackbar(
                getString(R.string.added_subreddit, subreddit.name.value).toAndroidString(),
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
            binding.root.makeSnackbar(string)
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
            binding.root.makeSnackbar(
                getString(R.string.deleted_subreddit, subreddit.name.value).toAndroidString(),
                R.string.action_undo.toAndroidString(),
                length = SnackbarLength.LONG
            ) {
                mainViewModel.add(subreddit)
            }
        }

        when (result) {
            is Result.Success -> onSuccess(result.data)
            else -> Timber.warn { "Delete subreddit result $result is not handled." }
        }
    }
}
