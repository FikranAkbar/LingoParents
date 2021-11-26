package com.glints.lingoparents.ui.liveevent.detail

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentLiveEventDetailBinding
import com.glints.lingoparents.ui.dashboard.DashboardActivity

class LiveEventDetailFragment : Fragment(R.layout.fragment_live_event_detail) {
    private lateinit var binding: FragmentLiveEventDetailBinding
    private val viewModel: LiveEventDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLiveEventDetailBinding.inflate(inflater)

        binding.ivBackButton.setOnClickListener{
            findNavController().popBackStack()
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
        object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        return binding.root
    }
}