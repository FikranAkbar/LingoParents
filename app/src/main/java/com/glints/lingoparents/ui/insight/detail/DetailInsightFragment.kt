package com.glints.lingoparents.ui.insight.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.glints.lingoparents.databinding.FragmentDetailInsightBinding
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class DetailInsightFragment : Fragment() {
    private lateinit var binding: FragmentDetailInsightBinding
    private lateinit var viewModel: DetailInsightViewModel
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var tokenPreferences: TokenPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailInsightBinding.inflate(inflater, container, false)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)

        initViews()

        binding.rvInsightComment.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            commentsAdapter = CommentsAdapter()
            adapter = commentsAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(
                this,
                CustomViewModelFactory(tokenPreferences, this, insightId = arguments?.get("id") as Int)
            )[DetailInsightViewModel::class.java]

        viewModel.getAccessToken().observe(viewLifecycleOwner){ accessToken ->
            viewModel.loadInsightDetail(viewModel.getCurrentInsightId(), accessToken)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.insightDetail.collect { insight ->
                when(insight){
                    is DetailInsightViewModel.InsightDetail.Success -> {
                        showLoading(false)
                        binding.apply {
                            insight.result.apply {
                                cover.let {
                                    ivInsightDetail.load(it)
                                }
                                tvInsightTitle.text = title
                                tvInsightDate.text = createdAt
                                tvInsightBody.text = content
                                tvInsightLike.text = total_like.toString()
                                tvInsightDislike.text = total_dislike.toString()
                            }
                            Snackbar.make(binding.root, "Successfully fetch detail insight", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    is DetailInsightViewModel.InsightDetail.SuccessGetComment -> {
                        commentsAdapter.submitList(insight.list)
                    }
                    is DetailInsightViewModel.InsightDetail.Loading -> {
                        showLoading(true)
                    }
                    is DetailInsightViewModel.InsightDetail.Error -> {
                        showLoading(false)
                    }
                }
            }

            viewModel.likeDislikeInsight.collect { insight ->
                Snackbar.make(binding.root, "insight event collected", Snackbar.LENGTH_SHORT).show()
                when(insight){
                    is DetailInsightViewModel.LikeDislikeInsight.TryToLikeDislikeInsight -> {
                        Snackbar.make(binding.root, "Button clicked", Snackbar.LENGTH_SHORT).show()
                        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
                            when(insight.type) {
                                DetailInsightViewModel.INSIGHT_TYPE -> {
                                    when(insight.action) {
                                        DetailInsightViewModel.LIKE_ACTION -> {
                                            viewModel.insightDetailLike(insight.id, insight.type, accessToken)
                                        }
                                        DetailInsightViewModel.DISLIKE_ACTION -> {
                                            viewModel.insightDetailDislike(insight.id, insight.type, accessToken)
                                        }
                                    }
                                }
                                DetailInsightViewModel.COMMENT_TYPE -> {
                                    when(insight.action) {
                                        DetailInsightViewModel.LIKE_ACTION -> {

                                        }
                                        DetailInsightViewModel.DISLIKE_ACTION -> {

                                        }
                                    }
                                }
                            }
                        }
                    }
                    is DetailInsightViewModel.LikeDislikeInsight.Success -> {
                        Snackbar.make(
                            binding.root,
                            insight.result.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                        Log.d("LikeInsightFragment", "Success")
                    }
                    is DetailInsightViewModel.LikeDislikeInsight.Loading -> {
                        Log.d("LikeInsightFragment", "Loading")
                    }
                    is DetailInsightViewModel.LikeDislikeInsight.Error -> {
                        Snackbar.make(
                            requireView(),
                            insight.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun showLoading(b: Boolean) {
        binding.apply {
            if (b) {
                shimmerLayout.visibility = View.VISIBLE
                mainContent.visibility = View.GONE
            } else {
                shimmerLayout.visibility = View.GONE
                mainContent.visibility = View.VISIBLE
            }
        }
    }

    private fun initViews(){
        binding.apply{
            ivBackButton.setOnClickListener {
                findNavController().popBackStack()
            }
            tvInsightAddComment.setOnClickListener {
                binding.tfInsightComment.visibility = View.VISIBLE
                binding.btnComment.visibility = View.VISIBLE
            }
            tvInsightLike.setOnClickListener {
                DetailInsightViewModel.apply {
                    viewModel.onLikeDislikeOnClick(LIKE_ACTION, viewModel.getCurrentInsightId(), INSIGHT_TYPE)
                }
                //viewModel.onLikeDislikeOnClick(DetailInsightViewModel.LIKE_ACTION, viewModel.getCurrentInsightId(), DetailInsightViewModel.INSIGHT_TYPE)
                //viewModel.getAccessToken().observe(viewLifecycleOwner) {
                //   viewModel.sendLikeRequest(viewModel.getCurrentInsightId(), DetailInsightViewModel.INSIGHT_TYPE, it)
                //}
            }
            tvInsightDislike.setOnClickListener {
                viewModel.onLikeDislikeOnClick(
                    DetailInsightViewModel.DISLIKE_ACTION, viewModel.getCurrentInsightId(),
                    DetailInsightViewModel.INSIGHT_TYPE
                )
                Log.d("TEST", "prepare to send event")
                //viewModel.onLikeDislikeOnClick(DetailInsightViewModel.DISLIKE_ACTION, viewModel.getCurrentInsightId(), DetailInsightViewModel.INSIGHT_TYPE)
                //viewModel.getAccessToken().observe(viewLifecycleOwner) {
                //    viewModel.sendDislikeRequest(viewModel.getCurrentInsightId(), DetailInsightViewModel.INSIGHT_TYPE, it)
                //}
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
    }
}