package com.glints.lingoparents.ui.liveevent.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentLiveEventDetailBinding

class LiveEventDetailFragment : Fragment(R.layout.fragment_live_event_detail) {
    private lateinit var binding: FragmentLiveEventDetailBinding
    private val viewModel: LiveEventDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLiveEventDetailBinding.inflate(inflater)

        return binding.root
    }
}