package com.glints.lingoparents.ui.liveevent.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.LiveEventListResponse
import com.glints.lingoparents.databinding.FragmentUpcomingEventBinding
import com.glints.lingoparents.ui.liveevent.LiveEventListFragmentDirections
import com.glints.lingoparents.ui.liveevent.LiveEventListViewModel
import kotlinx.coroutines.flow.collect

class UpcomingEventFragment : Fragment(R.layout.fragment_upcoming_event),
    LiveEventListAdapter.OnItemClickCallback {
    private var _binding: FragmentUpcomingEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var liveEventListAdapter: LiveEventListAdapter
    private val viewModel: LiveEventListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingEventBinding.inflate(inflater)

        binding.apply {
            rvUpcomingEvent.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                liveEventListAdapter = LiveEventListAdapter(this@UpcomingEventFragment)
                adapter = liveEventListAdapter
                liveEventListAdapter.submitList(
                    listOf(
                        LiveEventListResponse.LiveEventItemResponse(1, "", "", ""),
                        LiveEventListResponse.LiveEventItemResponse(1, "", "", ""),
                        LiveEventListResponse.LiveEventItemResponse(1, "", "", ""),
                    )
                )
            }
        }

        viewModel.loadTodayLiveEventList(LiveEventListViewModel.UPCOMING_TYPE)

        lifecycleScope.launchWhenStarted {
            viewModel.upcomingLiveEventListEvent.collect { event ->
                when (event) {
                    is LiveEventListViewModel.UpcomingLiveEventListEvent.Loading -> {
                        showLoading(true)
                    }
                    is LiveEventListViewModel.UpcomingLiveEventListEvent.Success -> {
                        liveEventListAdapter.submitList(event.list)
                        showLoading(false)
                    }
                    is LiveEventListViewModel.UpcomingLiveEventListEvent.Error -> {

                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClicked(item: LiveEventListResponse.LiveEventItemResponse) {
        val action =
            LiveEventListFragmentDirections.actionLiveEventListFragmentToLiveEventDetailFragment()
        findNavController().navigate(action)
    }

    private fun showLoading(bool: Boolean) {
        binding.apply {
            if (bool) {
                rvUpcomingEvent.visibility = View.GONE
                shimmerLayout.visibility = View.VISIBLE
            } else {
                rvUpcomingEvent.visibility = View.VISIBLE
                shimmerLayout.visibility = View.GONE
            }
        }
    }
}