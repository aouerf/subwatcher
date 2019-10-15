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
import io.github.aouerfelli.subwatcher.repository.State
import io.github.aouerfelli.subwatcher.util.SnackbarLength
import io.github.aouerfelli.subwatcher.util.makeSnackbar
import io.github.aouerfelli.subwatcher.util.observe
import io.github.aouerfelli.subwatcher.util.observeNotNull
import io.github.aouerfelli.subwatcher.util.onSwipe
import io.github.aouerfelli.subwatcher.util.provideViewModel
import io.github.aouerfelli.subwatcher.util.setThemeColorScheme
import io.github.aouerfelli.subwatcher.util.toAndroidString
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

        viewLifecycleOwner.observe(mainViewModel.subredditList, subredditListAdapter::submitList)
        viewLifecycleOwner.observeNotNull(mainViewModel.deletedSubreddit, ::onSubredditDeleted)
        viewLifecycleOwner.observeNotNull(mainViewModel.resultState, ::handleResultState)
    }

    private fun onSubredditDeleted(subreddit: Subreddit) {
        binding.root.makeSnackbar(
            getString(R.string.deleted_subreddit, subreddit.name.value).toAndroidString(),
            R.string.action_undo.toAndroidString(),
            length = SnackbarLength.LONG
        ) {
            mainViewModel.add(subreddit)
        }
    }

    private fun handleResultState(state: State) {
        binding.subredditsRefresh.isRefreshing = state == State.LOADING

        @StringRes val errorStringRes = when (state) {
            State.CONNECTION_ERROR -> R.string.no_connection
            State.NETWORK_FAILURE -> R.string.network_failure
            State.DATABASE_FAILURE -> R.string.database_failure
            else -> null
        }
        if (errorStringRes != null) {
            binding.root.makeSnackbar(errorStringRes.toAndroidString())
        }
    }
}
