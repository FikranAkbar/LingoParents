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
import com.glints.lingoparents.databinding.FragmentCompletedEventBinding
import com.glints.lingoparents.ui.liveevent.LiveEventListFragmentDirections
import com.glints.lingoparents.ui.liveevent.LiveEventListViewModel
import kotlinx.coroutines.flow.collect

class CompletedEventFragment : Fragment(R.layout.fragment_completed_event),
    LiveEventListAdapter.OnItemClickCallback {
    private var _binding: FragmentCompletedEventBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LiveEventListViewModel by viewModels()
    private lateinit var liveEventListAdapter: LiveEventListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedEventBinding.inflate(inflater)

        binding.apply {
            rvCompletedEvent.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                liveEventListAdapter = LiveEventListAdapter(this@CompletedEventFragment)
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

        viewModel.loadTodayLiveEventList(LiveEventListViewModel.COMPLETED_TYPE)

        lifecycleScope.launchWhenStarted {
            viewModel.completedLiveEventListEvent.collect { event ->
                when (event) {
                    is LiveEventListViewModel.CompletedLiveEventListEvent.Loading -> {
                        showLoading(true)
                    }
                    is LiveEventListViewModel.CompletedLiveEventListEvent.Success -> {
                        liveEventListAdapter.submitList(event.list)
                        showLoading(false)
                    }
                    is LiveEventListViewModel.CompletedLiveEventListEvent.Error -> {

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
                rvCompletedEvent.visibility = View.GONE
                shimmerLayout.visibility = View.VISIBLE
            } else {
                rvCompletedEvent.visibility = View.VISIBLE
                shimmerLayout.visibility = View.GONE
            }
        }
    }
}