package com.glints.lingoparents.ui.liveevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentLiveEventListBinding
import com.google.android.material.tabs.TabLayoutMediator

class LiveEventListFragment : Fragment(R.layout.fragment_live_event_list) {

    private lateinit var binding: FragmentLiveEventListBinding
    private val viewModel: LiveEventListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLiveEventListBinding.inflate(inflater)

        binding.apply {
            vpLiveEvent.apply {
                adapter = LiveEventViewPagerAdapter(requireActivity() as AppCompatActivity)
                isUserInputEnabled = false
                val tabNames = arrayOf("Today", "Upcoming", "Completed")
                TabLayoutMediator(tlLiveEventCategory, this) { tab, position ->
                    tab.text = tabNames[position]
                }.attach()
            }
        }

        return binding.root
    }
}