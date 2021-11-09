package com.glints.lingoparents.ui.liveevent.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.LiveEventItem
import com.glints.lingoparents.databinding.FragmentCompletedEventBinding
import com.glints.lingoparents.ui.liveevent.LiveEventListViewModel

class CompletedEventFragment : Fragment(R.layout.fragment_completed_event),
    LiveEventListAdapter.OnItemClickCallback {
    private lateinit var binding: FragmentCompletedEventBinding
    private val viewModel: LiveEventListViewModel by activityViewModels()
    private lateinit var liveEventListAdapter: LiveEventListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompletedEventBinding.inflate(inflater)

        binding.apply {
            rvCompletedEvent.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                liveEventListAdapter = LiveEventListAdapter(this@CompletedEventFragment)
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

    }
}