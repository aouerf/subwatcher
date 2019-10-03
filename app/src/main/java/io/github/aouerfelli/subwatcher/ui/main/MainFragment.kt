package io.github.aouerfelli.subwatcher.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import io.github.aouerfelli.subwatcher.databinding.MainFragmentBinding
import io.github.aouerfelli.subwatcher.util.observe
import io.github.aouerfelli.subwatcher.util.setThemeColorScheme
import javax.inject.Inject

class MainFragment : DaggerFragment() {

    private lateinit var binding: MainFragmentBinding

    private lateinit var subredditListAdapter: SubredditListAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MainViewModel by viewModels { viewModelFactory }

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
            viewModel.refresh()
        }

        binding.addButton.setOnClickListener {
            viewModel.add("random")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewLifecycleOwner.observe(viewModel.subredditList, subredditListAdapter::submitList)
        viewLifecycleOwner.observe(viewModel.isRefreshing, binding.subredditsRefresh::setRefreshing)
    }
}
