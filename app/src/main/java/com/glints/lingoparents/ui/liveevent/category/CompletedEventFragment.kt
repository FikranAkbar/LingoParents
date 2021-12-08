package com.glints.lingoparents.ui.liveevent.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.LiveEventListResponse
import com.glints.lingoparents.databinding.FragmentCompletedEventBinding
import com.glints.lingoparents.ui.liveevent.LiveEventListFragmentDirections
import com.glints.lingoparents.ui.liveevent.LiveEventListViewModel
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CompletedEventFragment : Fragment(R.layout.fragment_completed_event),
    LiveEventListAdapter.OnItemClickCallback {
    private var _binding: FragmentCompletedEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var liveEventListAdapter: LiveEventListAdapter
    private lateinit var viewModel: CompletedLiveEventViewModel
    private lateinit var tokenPreferences: TokenPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedEventBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel =
            ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this, arguments))[
                    CompletedLiveEventViewModel::class.java
            ]

        binding.apply {
            rvCompletedEvent.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                liveEventListAdapter = LiveEventListAdapter(this@CompletedEventFragment)
                adapter = liveEventListAdapter
            }
        }

        viewModel.loadCompletedLiveEventList()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.completedLiveEventListEvent.collect { event ->
                when (event) {
                    is CompletedLiveEventViewModel.CompletedLiveEventListEvent.Loading -> {
                        showLoading(true)
                        showEmptyWarning(false)
                    }
                    is CompletedLiveEventViewModel.CompletedLiveEventListEvent.Success -> {
                        liveEventListAdapter.submitList(event.list)
                        showLoading(false)
                    }
                    is CompletedLiveEventViewModel.CompletedLiveEventListEvent.Error -> {
                        liveEventListAdapter.submitList(listOf())
                        showLoading(false)
                        showEmptyWarning(true)
                    }
                    is CompletedLiveEventViewModel.CompletedLiveEventListEvent.NavigateToDetailLiveEventFragment -> {
                        val action =
                            LiveEventListFragmentDirections.actionLiveEventListFragmentToLiveEventDetailFragment(
                                event.id
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClicked(item: LiveEventListResponse.LiveEventItemResponse) {
        viewModel.onCompletedLiveEventItemClick(item.id)
        Log.d("IDEvent", item.id.toString())
    }

    @Subscribe
    fun onBlankQuerySent(event: LiveEventListViewModel.LiveEventListEvent.SendBlankQueryToEventListFragment) {
        viewModel.loadCompletedLiveEventList()
    }

    @Subscribe
    fun onQuerySent(event: LiveEventListViewModel.LiveEventListEvent.SendQueryToEventListFragment) {
        viewModel.searchCompletedLiveEventList(event.query)
    }

    private fun showLoading(bool: Boolean) {
        binding.apply {
            if (bool) {
                rvCompletedEvent.visibility = View.GONE
                shimmerLayout.visibility = View.VISIBLE
            } else {
                rvCompletedEvent.visibility = View.VISIBLE
                shimmerLayout.visibility = View.GONE
            }
        }
    }

    private fun showEmptyWarning(bool: Boolean) {
        binding.apply {
            if (bool) {
                ivNoEvent.visibility = View.VISIBLE
                tvNoEvent.visibility = View.VISIBLE
            } else {
                ivNoEvent.visibility = View.GONE
                tvNoEvent.visibility = View.GONE
            }
        }
    }
}