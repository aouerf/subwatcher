package io.github.aouerfelli.subwatcher.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import dagger.android.support.DaggerFragment
import io.github.aouerfelli.subwatcher.R
import io.github.aouerfelli.subwatcher.databinding.MainFragmentBinding
import io.github.aouerfelli.subwatcher.repository.State
import io.github.aouerfelli.subwatcher.util.makeSnackbar
import io.github.aouerfelli.subwatcher.util.observe
import io.github.aouerfelli.subwatcher.util.provideViewModel
import io.github.aouerfelli.subwatcher.util.setThemeColorScheme
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
        viewLifecycleOwner.observe(mainViewModel.resultState) { state ->
            binding.subredditsRefresh.isRefreshing = state == State.LOADING

            @StringRes val errorStringRes = when (state) {
                State.CONNECTION_ERROR -> R.string.no_connection
                State.NETWORK_FAILURE -> R.string.network_failure
                State.DATABASE_FAILURE -> R.string.database_failure
                else -> null
            }
            errorStringRes?.let { binding.root.makeSnackbar(it) }
        }
    }
}
