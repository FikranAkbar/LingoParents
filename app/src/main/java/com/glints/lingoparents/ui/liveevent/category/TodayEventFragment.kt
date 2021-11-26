package com.glints.lingoparents.ui.liveevent.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.LiveEventItem
import com.glints.lingoparents.databinding.FragmentTodayEventBinding
import com.glints.lingoparents.ui.liveevent.LiveEventListFragmentDirections
import com.glints.lingoparents.ui.liveevent.LiveEventListViewModel

class TodayEventFragment : Fragment(R.layout.fragment_today_event),
    LiveEventListAdapter.OnItemClickCallback {
    private lateinit var binding: FragmentTodayEventBinding
    private lateinit var liveEventListAdapter: LiveEventListAdapter
    private val viewModel: LiveEventListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodayEventBinding.inflate(inflater)

        binding.apply {
            rvTodayEvent.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                liveEventListAdapter = LiveEventListAdapter(this@TodayEventFragment)
                adapter = liveEventListAdapter
                liveEventListAdapter.submitList(
                    listOf(
                        LiveEventItem("", "", ""),
                        LiveEventItem("", "", ""),
                        LiveEventItem("", "", ""),
                        LiveEventItem("", "", ""),
                        LiveEventItem("", "", ""),
                        LiveEventItem("", "", ""),
                        LiveEventItem("", "", ""),
                    )
                )
            }
        }

        return binding.root
    }

    override fun onItemClicked(item: LiveEventItem) {
        val action = LiveEventListFragmentDirections.actionLiveEventListFragmentToLiveEventDetailFragment()
        findNavController().navigate(action)
    }
}