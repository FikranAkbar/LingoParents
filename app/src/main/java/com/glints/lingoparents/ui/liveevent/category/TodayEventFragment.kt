package com.glints.lingoparents.ui.liveevent.category

import android.content.Context
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
import com.glints.lingoparents.databinding.FragmentTodayEventBinding
import com.glints.lingoparents.ui.liveevent.LiveEventListFragmentDirections
import com.glints.lingoparents.ui.liveevent.LiveEventListViewModel
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.NoInternetAccessOrErrorListener
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class TodayEventFragment : Fragment(R.layout.fragment_today_event),
    LiveEventListAdapter.OnItemClickCallback {
    private var _binding: FragmentTodayEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var liveEventListAdapter: LiveEventListAdapter
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: TodayLiveEventViewModel

    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayEventBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(
            this,
            CustomViewModelFactory(tokenPreferences, this, arguments)
        )[
                TodayLiveEventViewModel::class.java
        ]

        binding.apply {
            rvTodayEvent.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                liveEventListAdapter = LiveEventListAdapter(
                    this@TodayEventFragment,
                    TodayLiveEventViewModel.TODAY_TYPE)
                adapter = liveEventListAdapter
            }
        }

        viewModel.loadTodayLiveEventList()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.todayLiveEventListEvent.collect { event ->
                when (event) {
                    is TodayLiveEventViewModel.TodayLiveEventListEvent.Loading -> {
                        showLoading(true)
                        showEmptyWarning(false)
                    }
                    is TodayLiveEventViewModel.TodayLiveEventListEvent.Success -> {
                        liveEventListAdapter.submitList(event.list)
                        showLoading(false)
                    }
                    is TodayLiveEventViewModel.TodayLiveEventListEvent.Error -> {
                        liveEventListAdapter.submitList(listOf())
                        showLoading(false)
                        showEmptyWarning(true)

                        if (event.message.lowercase() != "not found")
                            noInternetAccessOrErrorHandler.onNoInternetAccessOrError(event.message)
                    }
                    is TodayLiveEventViewModel.TodayLiveEventListEvent.NavigateToDetailLiveEventFragment -> {
                        val action =
                            LiveEventListFragmentDirections.actionLiveEventListFragmentToLiveEventDetailFragment(
                                event.id
                            )
                        Log.d("IDEvent", event.id.toString())
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            noInternetAccessOrErrorHandler = context as NoInternetAccessOrErrorListener
        } catch (e: ClassCastException) {
            println("DEBUG: $context must be implement NoInternetAccessOrErrorListener")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClicked(item: LiveEventListResponse.LiveEventItemResponse) {
        viewModel.onTodayLiveEventItemClick(item.id)
        Log.d("IDEvent", item.id.toString())
    }

    @Subscribe(sticky = true)
    fun onBlankQuerySent(event: LiveEventListViewModel.LiveEventListEvent.SendBlankQueryToEventListFragment) {
        viewModel.loadTodayLiveEventList()
    }

    @Subscribe(sticky = true)
    fun onSearchViewDoneEditing(event: LiveEventListViewModel.LiveEventListEvent.SendQueryToEventListFragment) {
        viewModel.searchTodayLiveEventList(event.query)
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