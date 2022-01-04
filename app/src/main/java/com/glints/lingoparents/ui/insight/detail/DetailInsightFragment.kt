package com.glints.lingoparents.ui.insight.detail

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
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
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.GetCommentRepliesResponse
import com.glints.lingoparents.data.model.response.InsightDetailResponse
import com.glints.lingoparents.databinding.FragmentDetailInsightBinding
import com.glints.lingoparents.ui.insight.detail.adapter.CommentRepliesAdapter
import com.glints.lingoparents.ui.insight.detail.adapter.CommentsAdapter
import com.glints.lingoparents.utils.CustomViewModelFactory
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

    companion object {
        val report_body = arrayOf("Spam", "Harassment", "Rules Violation", "Other")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDetailInsightBinding.inflate(inflater, container, false)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)

        initViews()

        binding.rvInsightComment.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            commentsAdapter = CommentsAdapter(this@DetailInsightFragment, requireContext())
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
                                    SimpleDateFormat("EEE, MMM d", Locale.getDefault())
                                        .format(date)
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
                            "Add comment successfully",
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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getCommentReplies.collect { insight ->
                when (insight) {
                    is DetailInsightViewModel.GetCommentReplies.Loading -> {
                    }
                    is DetailInsightViewModel.GetCommentReplies.Success -> {
                        commentRepliesAdapter =
                            CommentRepliesAdapter(this@DetailInsightFragment, requireContext())
                        commentRepliesAdapter.submitList(insight.list)
                        commentsAdapter.showCommentReplies(commentRepliesAdapter)
                    }
                    is DetailInsightViewModel.GetCommentReplies.Error -> {
                        Snackbar.make(requireView(), insight.message, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.parseColor("#FF0000"))
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.reportInsight.collect { insight ->
                when (insight) {
                    is DetailInsightViewModel.ReportInsight.Loading -> {

                    }
                    is DetailInsightViewModel.ReportInsight.Success -> {
                        Snackbar.make(
                            requireView(),
                            "Reported successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is DetailInsightViewModel.ReportInsight.Error -> {
                        Snackbar.make(
                            requireView(),
                            "Something went wrong...",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.deleteComment.collect { insight ->
                when (insight) {
                    is DetailInsightViewModel.DeleteComment.Loading -> {

                    }
                    is DetailInsightViewModel.DeleteComment.Success -> {
                        Snackbar.make(
                            requireView(),
                            insight.result.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is DetailInsightViewModel.DeleteComment.Error -> {
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
            viewModel.updateComment.collect { insight ->
                when (insight) {
                    is DetailInsightViewModel.UpdateComment.Loading -> {

                    }
                    is DetailInsightViewModel.UpdateComment.Success -> {
                        Snackbar.make(
                            requireView(),
                            insight.result.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is DetailInsightViewModel.UpdateComment.Error -> {
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

    private fun initViews() {
        binding.apply {
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


    fun String.getDateWithServerTimeStamp(): Date? {
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