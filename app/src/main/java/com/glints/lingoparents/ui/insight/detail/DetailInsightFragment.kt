package com.glints.lingoparents.ui.insight.detail

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.InsightCommentItem
import com.glints.lingoparents.databinding.FragmentDetailInsightBinding
import com.glints.lingoparents.ui.dashboard.hideKeyboard
import com.glints.lingoparents.ui.dashboard.openKeyboard
import com.glints.lingoparents.ui.insight.detail.adapter.CommentsAdapter
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.NoInternetAccessOrErrorListener
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class DetailInsightFragment : Fragment(), CommentsAdapter.OnItemClickCallback {
    private lateinit var binding: FragmentDetailInsightBinding
    private lateinit var viewModel: DetailInsightViewModel
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

    private val randomGenerator = Random
    private val childCommentAdapterMap: MutableMap<Double, CommentsAdapter> = mutableMapOf()

    companion object {
        val report_body = arrayOf("Spam", "Harassment", "Rules Violation", "Other")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailInsightBinding.inflate(inflater, container, false)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)

        initViews()

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

        viewModel.getParentId().observe(viewLifecycleOwner) { parentId ->
            CommentsAdapter.parentId = parentId.toInt()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.insightDetail.collect { insight ->
                when (insight) {
                    is DetailInsightViewModel.InsightDetail.Success -> {
                        showLoading(false)
                        binding.apply {
                            insight.result.apply {
                                val date = createdAt.getDateWithServerTimeStamp()

                                cover.let {
                                    ivInsightDetail.load(it)
                                }
                                tvInsightTitle.text = title
                                tvInsightDate.text =
                                    SimpleDateFormat("d MMMM yyy", Locale.getDefault())
                                        .format(date!!)
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
                        noInternetAccessOrErrorHandler.onNoInternetAccessOrError(insight.message)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.actionInsight.collect { insight ->
                when (insight) {
                    is DetailInsightViewModel.InsightAction.SuccessCreateComment -> {
                        Snackbar.make(
                            requireView(),
                            "Add comment successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()

                        binding.apply {
                            tfInsightComment.editText?.setText("")
                        }
                    }
                    is DetailInsightViewModel.InsightAction.SuccessDeleteComment -> {
                        Snackbar.make(
                            requireView(),
                            insight.result.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is DetailInsightViewModel.InsightAction.SuccessGetCommentReplies -> {
                        val uniqueId = randomGenerator.nextDouble()
                        val newCommentsAdapter = CommentsAdapter(this@DetailInsightFragment, requireContext(), uniqueId)
                        childCommentAdapterMap[uniqueId] = newCommentsAdapter
                        newCommentsAdapter.submitList(insight.list)
                        childCommentAdapterMap[insight.uniqueId]?.showCommentReplies(newCommentsAdapter)
                    }
                    is DetailInsightViewModel.InsightAction.SuccessLikeDislike -> {
                        Snackbar.make(
                            binding.root,
                            insight.result.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is DetailInsightViewModel.InsightAction.SuccessUpdateComment -> {
                        Snackbar.make(
                            binding.root,
                            insight.result.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is DetailInsightViewModel.InsightAction.SuccessReport -> {
                        Snackbar.make(
                            requireView(),
                            "Reported successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is DetailInsightViewModel.InsightAction.Error -> {
                        noInternetAccessOrErrorHandler.onNoInternetAccessOrError(insight.message)
                    }
                }
            }
        }
    }

    private fun showLoading(b: Boolean) {
        binding.apply {
            shimmerLayout.isVisible = b
            mainContent.isVisible = !b
        }
    }

    private fun initViews() {
        binding.apply {
            rvInsightComment.apply {
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(requireContext())
                val uniqueId = randomGenerator.nextDouble()
                commentsAdapter = CommentsAdapter(this@DetailInsightFragment, requireContext(), uniqueId)
                childCommentAdapterMap[uniqueId] = commentsAdapter
                adapter = commentsAdapter
            }

            ivBackButton.setOnClickListener {
                findNavController().popBackStack()
            }
            tvInsightAddComment.setOnClickListener {
                tfInsightComment.isVisible = !tfInsightComment.isVisible
                btnComment.isVisible = !btnComment.isVisible
                if (tfInsightComment.isVisible) {
                    tfInsightComment.requestFocus()
                    requireActivity().openKeyboard()
                } else {
                    requireActivity().hideKeyboard()
                }
            }

            tvInsightReport.setOnClickListener {
                showReportDialog(requireContext())
            }

            ivLike.setOnClickListener {
                viewModel.sendLikeRequest(
                    viewModel.getCurrentInsightId(),
                    DetailInsightViewModel.INSIGHT_TYPE
                )
            }
            ivDislike.setOnClickListener {
                viewModel.sendDislikeRequest(
                    viewModel.getCurrentInsightId(),
                    DetailInsightViewModel.INSIGHT_TYPE
                )
            }

            btnComment.setOnClickListener {
                if (TextUtils.isEmpty(tfInsightComment.editText?.text)) {
                    tfInsightComment.requestFocus()
                    tfInsightComment.error = "Please enter your comment"
                } else {
                    viewModel.createComment(
                        viewModel.getCurrentInsightId(),
                        DetailInsightViewModel.INSIGHT_TYPE,
                        tfInsightComment.editText?.text.toString()
                    )
                    requireActivity().hideKeyboard()
                    tfInsightComment.isVisible = false
                    btnComment.isVisible = false
                }
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
    }

    private fun showReportDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        var text = ""
        builder.apply {
            setCancelable(false)
            setTitle(R.string.report)
            setSingleChoiceItems(report_body, -1) { _, i ->
                try {
                    text = report_body[i]
                } catch (e: IllegalArgumentException) {
                    throw ClassCastException(e.toString())
                }
            }
            setPositiveButton(R.string.report) { _, _ ->
                viewModel.reportInsight(viewModel.getCurrentInsightId().toString(),
                    DetailInsightViewModel.INSIGHT_TYPE,
                    text)
            }
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
        }

        builder.create().show()
    }


    private fun String.getDateWithServerTimeStamp(): Date? {
        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.getDefault()
        )
        return try {
            dateFormat.parse(this)
        } catch (e: ParseException) {
            null
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            noInternetAccessOrErrorHandler = context as NoInternetAccessOrErrorListener
        } catch (e: ClassCastException) {
            println("DEBUG: $context must be implement NoInternetAccessOrErrorListener")
        }
    }

    override fun onReportCommentClicked(
        item: InsightCommentItem,
        id: Int,
        report_comment: String,
    ) {
        viewModel.reportInsight(id.toString(), DetailInsightViewModel.COMMENT_TYPE, report_comment)
    }

    override fun onLikeCommentClicked(item: InsightCommentItem) {
        viewModel.sendLikeRequest(
            item.idComment,
            DetailInsightViewModel.COMMENT_TYPE
        )
    }

    override fun onDislikeCommentClicked(item: InsightCommentItem) {
        viewModel.sendDislikeRequest(
            item.idComment,
            DetailInsightViewModel.COMMENT_TYPE
        )
    }

    override fun onReplyCommentClicked(item: InsightCommentItem, comment: String) {
        viewModel.createComment(
            item.idComment,
            DetailInsightViewModel.COMMENT_TYPE,
            comment
        )
    }

    override fun onShowCommentRepliesClicked(item: InsightCommentItem, uniqueId: Double) {
        viewModel.getCommentReplies(item.idComment, uniqueId)
    }

    override fun onDeleteCommentClicked(item: InsightCommentItem, id: Int) {
        viewModel.deleteComment(id)
    }

    override fun onUpdateCommentClicked(
        item: InsightCommentItem,
        comment: String,
    ) {
        viewModel.updateComment(item.idComment, comment)
    }
}