package com.glints.lingoparents.ui.liveevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentLiveEventListBinding

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

        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}