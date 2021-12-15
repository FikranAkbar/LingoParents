package com.glints.lingoparents.ui.insight.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.glints.lingoparents.data.model.response.InsightDetailResponse
import com.glints.lingoparents.databinding.FragmentDetailInsightBinding
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class DetailInsightFragment : Fragment(), CommentsAdapter.OnItemClickCallback {
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
            commentsAdapter = CommentsAdapter(this@DetailInsightFragment)
            adapter = commentsAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(
                this,
                CustomViewModelFactory(
                    tokenPreferences,
                    this,
                    insightId = arguments?.get("id") as Int
                )
            )[DetailInsightViewModel::class.java]

        viewModel.loadInsightDetail(viewModel.getCurrentInsightId())

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.insightDetail.collect { insight ->
                when (insight) {
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
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.likeDislikeInsight.collect { insight ->
                when (insight) {
                    is DetailInsightViewModel.LikeDislikeInsight.Success -> {
                        Snackbar.make(
                            binding.root,
                            insight.result.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is DetailInsightViewModel.LikeDislikeInsight.Loading -> {
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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.createComment.collect { insight ->
                when (insight) {
                    is DetailInsightViewModel.CreateComment.Success -> {
                        Snackbar.make(
                            requireView(),
                            "Comment Created Successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()

                        binding.apply {
                            tfInsightComment.editText?.setText("")
                        }
                    }
                    is DetailInsightViewModel.CreateComment.Loading -> {

                    }
                    is DetailInsightViewModel.CreateComment.Error -> {
                        Snackbar.make(requireView(), "Something's Wrong", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.parseColor("#FF0000"))
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .show()
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

    private fun initViews() {
        binding.apply {
            ivBackButton.setOnClickListener {
                findNavController().popBackStack()
            }
            tvInsightAddComment.setOnClickListener {
                binding.tfInsightComment.visibility = View.VISIBLE
                binding.btnComment.visibility = View.VISIBLE
            }
            tvInsightLike.setOnClickListener {
                viewModel.sendLikeRequest(
                    viewModel.getCurrentInsightId(),
                    DetailInsightViewModel.INSIGHT_TYPE
                )
            }
            tvInsightDislike.setOnClickListener {
                viewModel.sendDislikeRequest(
                    viewModel.getCurrentInsightId(),
                    DetailInsightViewModel.INSIGHT_TYPE
                )
            }
            btnComment.setOnClickListener {
                viewModel.createComment(
                    viewModel.getCurrentInsightId(),
                    DetailInsightViewModel.INSIGHT_TYPE,
                    tfInsightComment.editText?.text.toString()
                )
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
    }

    override fun onLikeCommentClicked(item: InsightDetailResponse.MasterComment) {
        viewModel.sendLikeRequest(
            item.id,
            DetailInsightViewModel.COMMENT_TYPE
        )
    }

    override fun onDislikeCommentClicked(item: InsightDetailResponse.MasterComment) {
        viewModel.sendDislikeRequest(
            item.id,
            DetailInsightViewModel.COMMENT_TYPE
        )
    }

    override fun onReplyCommentClicked(item: InsightDetailResponse.MasterComment, comment: String) {
        viewModel.createComment(
            item.id,
            DetailInsightViewModel.COMMENT_TYPE,
            comment
        )
    }
}