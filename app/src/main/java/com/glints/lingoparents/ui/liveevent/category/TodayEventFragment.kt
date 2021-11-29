package com.glints.lingoparents.ui.liveevent.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.LiveEventListResponse
import com.glints.lingoparents.databinding.FragmentTodayEventBinding
import com.glints.lingoparents.ui.liveevent.LiveEventListFragmentDirections
import com.glints.lingoparents.ui.liveevent.LiveEventListViewModel
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect

class TodayEventFragment : Fragment(R.layout.fragment_today_event),
    LiveEventListAdapter.OnItemClickCallback {
    private var _binding: FragmentTodayEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var liveEventListAdapter: LiveEventListAdapter
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: LiveEventListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayEventBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this, arguments)) [
            LiveEventListViewModel::class.java
        ]

        binding.apply {
            rvTodayEvent.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                liveEventListAdapter = LiveEventListAdapter(this@TodayEventFragment)
                adapter = liveEventListAdapter
            }
        }

        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
            viewModel.loadTodayLiveEventList(LiveEventListViewModel.TODAY_TYPE, accessToken)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.todayLiveEventListEvent.collect { event ->
                when (event) {
                    is LiveEventListViewModel.TodayLiveEventListEvent.Loading -> {
                        showLoading(true)
                    }
                    is LiveEventListViewModel.TodayLiveEventListEvent.Success -> {
                        liveEventListAdapter.submitList(event.list)
                        showLoading(false)
                    }
                    is LiveEventListViewModel.TodayLiveEventListEvent.Error -> {
                        showLoading(false)
                    }
                    is LiveEventListViewModel.TodayLiveEventListEvent.NavigateToDetailLiveEventFragment -> {
                        val action =
                            LiveEventListFragmentDirections.actionLiveEventListFragmentToLiveEventDetailFragment(event.id)
                        Log.d("IDEvent", event.id.toString())
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
        Log.d("IDEvent", item.id.toString())
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