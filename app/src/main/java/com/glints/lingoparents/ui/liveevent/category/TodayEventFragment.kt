package com.glints.lingoparents.ui.liveevent.category

import android.os.Bundle
import android.util.Log
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
import com.glints.lingoparents.databinding.FragmentTodayEventBinding
import com.glints.lingoparents.ui.liveevent.LiveEventListFragmentDirections
import com.glints.lingoparents.ui.liveevent.LiveEventListViewModel
import kotlinx.coroutines.flow.collect

class TodayEventFragment : Fragment(R.layout.fragment_today_event),
    LiveEventListAdapter.OnItemClickCallback {
    private var _binding: FragmentTodayEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var liveEventListAdapter: LiveEventListAdapter
    private val viewModel: LiveEventListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayEventBinding.inflate(inflater)

        binding.apply {
            rvTodayEvent.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                liveEventListAdapter = LiveEventListAdapter(this@TodayEventFragment)
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

        viewModel.loadTodayLiveEventList(LiveEventListViewModel.TODAY_TYPE)

        lifecycleScope.launchWhenStarted {
            viewModel.todayLiveEventListEvent.collect { event ->
                when (event) {
                    is LiveEventListViewModel.TodayLiveEventListEvent.Loading -> {
                        showLoading(true)
                        Log.d("TEST", "MULAI LOADING LIVE EVENT TODAY")
                    }
                    is LiveEventListViewModel.TodayLiveEventListEvent.Success -> {
                        liveEventListAdapter.submitList(event.list)
                        showLoading(false)
                        Log.d("TEST", "SELESAI LOADING LIVE EVENT TODAY")
                    }
                    is LiveEventListViewModel.TodayLiveEventListEvent.Error -> {

                    }
                    is LiveEventListViewModel.TodayLiveEventListEvent.NavigateToDetailLiveEventFragment -> {
                        val action =
                            LiveEventListFragmentDirections.actionLiveEventListFragmentToLiveEventDetailFragment(event.id)
                        findNavController().navigate(action)
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
        viewModel.onTodayLiveEventItemClick(item.id)
    }

    private fun showLoading(bool: Boolean) {
        binding.apply {
            if (bool) {
                rvTodayEvent.visibility = View.GONE
                shimmerLayout.visibility = View.VISIBLE
            } else {
                rvTodayEvent.visibility = View.VISIBLE
                shimmerLayout.visibility = View.GONE
            }
        }
    }
}