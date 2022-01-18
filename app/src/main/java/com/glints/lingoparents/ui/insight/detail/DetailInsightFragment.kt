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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.GetCommentRepliesResponse
import com.glints.lingoparents.data.model.response.InsightDetailResponse
import com.glints.lingoparents.databinding.FragmentDetailInsightBinding
import com.glints.lingoparents.ui.insight.detail.adapter.CommentRepliesAdapter
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

class DetailInsightFragment : Fragment(), CommentsAdapter.OnItemClickCallback,
    CommentRepliesAdapter.OnItemClickCallback {
    private lateinit var binding: FragmentDetailInsightBinding
    private lateinit var viewModel: DetailInsightViewModel
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var commentRepliesAdapter: CommentRepliesAdapter
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

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
            commentsAdapter.submitParentId(parentId.toInt())
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
                        commentRepliesAdapter =
                            CommentRepliesAdapter(this@DetailInsightFragment, requireContext())
                        commentRepliesAdapter.submitList(insight.list)
                        commentsAdapter.showCommentReplies(commentRepliesAdapter)
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
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
                commentsAdapter = CommentsAdapter(this@DetailInsightFragment, requireContext())
                adapter = commentsAdapter
            }

            ivBackButton.setOnClickListener {
                findNavController().popBackStack()
            }
            tvInsightAddComment.setOnClickListener {
                tfInsightComment.visibility = View.VISIBLE
                tfInsightComment.requestFocus()
                btnComment.visibility = View.VISIBLE
            }

            tvInsightReport.setOnClickListener {
                showReportDialog(requireContext())
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
                if (TextUtils.isEmpty(tfInsightComment.editText?.text)) {
                    tfInsightComment.requestFocus()
                    tfInsightComment.error = "Please enter your comment"
                } else {
                    viewModel.createComment(
                        viewModel.getCurrentInsightId(),
                        DetailInsightViewModel.INSIGHT_TYPE,
                        tfInsightComment.editText?.text.toString()
                    )
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
        item: InsightDetailResponse.MasterComment,
        id: Int,
        report_comment: String,
    ) {
        viewModel.reportInsight(id.toString(), DetailInsightViewModel.COMMENT_TYPE, report_comment)
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

    override fun onShowCommentRepliesClicked(item: InsightDetailResponse.MasterComment) {
        viewModel.getCommentReplies(item.id)
    }

    override fun onDeleteCommentClicked(item: InsightDetailResponse.MasterComment, id: Int) {
        viewModel.deleteComment(id)
    }

    override fun onUpdateCommentClicked(
        item: InsightDetailResponse.MasterComment,
        comment: String,
    ) {
        viewModel.updateComment(item.id, comment)
    }

    override fun onReportCommentClicked(
        item: GetCommentRepliesResponse.Message,
        id: Int,
        report_comment: String,
    ) {
        viewModel.reportInsight(id.toString(), DetailInsightViewModel.COMMENT_TYPE, report_comment)
    }

    override fun onLikeCommentClicked(item: GetCommentRepliesResponse.Message) {
        viewModel.sendLikeRequest(
            item.id,
            DetailInsightViewModel.COMMENT_TYPE
        )
    }

    override fun onDislikeCommentClicked(item: GetCommentRepliesResponse.Message) {
        viewModel.sendDislikeRequest(
            item.id,
            DetailInsightViewModel.COMMENT_TYPE
        )
    }

    override fun onReplyCommentClicked(item: GetCommentRepliesResponse.Message, comment: String) {
        viewModel.createComment(
            item.id,
            DetailInsightViewModel.COMMENT_TYPE,
            comment
        )
    }

    override fun onShowCommentRepliesClicked(item: GetCommentRepliesResponse.Message) {
        viewModel.getCommentReplies(item.id)
    }

    override fun onDeleteCommentClicked(item: GetCommentRepliesResponse.Message, id: Int) {
        viewModel.deleteComment(id)
    }

    override fun onUpdateCommentClicked(item: GetCommentRepliesResponse.Message, comment: String) {
        viewModel.updateComment(item.id, comment)
    }
}