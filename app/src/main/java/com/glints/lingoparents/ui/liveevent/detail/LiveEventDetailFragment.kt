package com.glints.lingoparents.ui.liveevent.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentLiveEventDetailBinding
import kotlinx.coroutines.flow.collect

class LiveEventDetailFragment : Fragment(R.layout.fragment_live_event_detail) {
    private lateinit var binding: FragmentLiveEventDetailBinding
    private val viewModel: LiveEventDetailViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLiveEventDetailBinding.inflate(inflater)

        binding.apply {
            ivBackButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        viewModel.id.observe(viewLifecycleOwner) {
            viewModel.getLiveEventDetailById(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.liveEventDetailEvent.collect { event ->
                when (event) {
                    is LiveEventDetailViewModel.LiveEventDetailEvent.Success -> {
                        showLoading(false)
                        binding.apply {
                            event.result.apply {
                                Glide.with(requireContext())
                                    .load(R.drawable.img_no_image)
                                    .into(ivDetailEventPoster)

                                cover?.let {
                                    Glide.with(requireContext())
                                        .load(it)
                                        .into(ivDetailEventPoster)
                                }

                                tvDetailEventTitle.text = title
                                tvDateAndTimeContent.text = "$date, $started_at"
                                tvPriceContentNumber.text = price

                                Glide.with(requireContext())
                                    .load(speaker_photo)
                                    .into(ivPhotoContent)

                                tvSpeakerName.text = speaker
                                tvSpeakerProfession.text = speaker_profession
                                tvSpeakerCompany.text = speaker_company
                                tvDescriptionContent.text = description
                            }
                        }
                    }
                    is LiveEventDetailViewModel.LiveEventDetailEvent.Loading -> {
                        showLoading(true)
                    }
                    is LiveEventDetailViewModel.LiveEventDetailEvent.Error -> {

                    }
                }
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })

        return binding.root
    }

    private fun showLoading(bool: Boolean) {
        binding.apply {
            if (bool) {
                shimmerLayout.visibility = View.VISIBLE
                mainContent.visibility = View.GONE
            } else {
                shimmerLayout.visibility = View.GONE
                mainContent.visibility = View.VISIBLE
            }
        }
    }
}