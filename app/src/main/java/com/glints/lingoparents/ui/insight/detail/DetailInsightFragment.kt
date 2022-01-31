package com.glints.lingoparents.ui.insight.detail

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.InsightCommentItem
import com.glints.lingoparents.databinding.FragmentDetailInsightBinding
import com.glints.lingoparents.databinding.ItemInsightCommentBinding
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

    companion object {
        val report_body = arrayOf("Spam", "Harassment", "Rules Violation", "Other")

        // Save all generated comment adapter and needs unique adapter id to access
        val commentAdapterMap: MutableMap<Double, CommentsAdapter> = mutableMapOf()

        // Only build random generator once
        val randomGenerator = Random
    }

    init {
        commentAdapterMap.clear()
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

        // only needed for mapping CreateCommentResponse to InsightCommentItem
        viewModel.getParentProfile()

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

                                if (is_liked > 0) {
                                    ivLike.setColorFilter(Color.BLUE)
                                } else {
                                    ivLike.clearColorFilter()
                                }

                                if (is_disliked > 0) {
                                    ivDislike.setColorFilter(Color.BLUE)
                                } else {
                                    ivDislike.clearColorFilter()
                                }
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
                        showSuccessSnackbar("Add comment successfully")

                        commentAdapterMap[insight.uniqueAdapterId]?.addNewCommentItem(insight.result)

                        //region Scroll recycler view to comment position after few delay
                        if (commentsAdapter.getUniqueAdapterId() == insight.uniqueAdapterId) {
                            CoroutineScope(Dispatchers.Unconfined).launch {
                                delay(200)
                                binding.rvInsightComment.smoothScrollToPosition(0)
                            }
                        }
                        //endregion

                        binding.apply {
                            tfInsightComment.editText?.setText("")
                        }
                    }
                    is DetailInsightViewModel.InsightAction.SuccessDeleteComment -> {
                        showSuccessSnackbar(insight.result.message)
                        commentAdapterMap[insight.uniqueAdapterId]?.apply {
                            deleteCommentItem(insight.item)
                        }
                    }
                    is DetailInsightViewModel.InsightAction.SuccessGetCommentReplies -> {
                        //region Generate CommentsAdapter
                        val uniqueAdapterId = randomGenerator.nextDouble()
                        val newCommentsAdapter = CommentsAdapter(this@DetailInsightFragment,
                            requireContext(),
                            uniqueAdapterId)
                        commentAdapterMap[uniqueAdapterId] = newCommentsAdapter
                        newCommentsAdapter.submitList(insight.list)
                        commentAdapterMap[insight.uniqueAdapterId]?.apply {
                            newCommentsAdapter.assignParentCommentListener(insight.itemCommentId,
                                this)
                            showCommentReplies(
                                newCommentsAdapter,
                                insight.binding)
                        }
                        //endregion

                        //region set adapter's differ list listener
                        val onRvChildDifferListListener =
                            AsyncListDiffer.ListListener<InsightCommentItem> { _, currentList ->
                                if (currentList.size <= 0) {
                                    insight.binding.apply {
                                        rvCommentReply.isVisible = false
                                        tvShowReplyComment.isVisible = false
                                    }
                                }
                            }

                        newCommentsAdapter.differ.apply {
                            removeListListener(onRvChildDifferListListener)
                            addListListener(onRvChildDifferListListener)
                        }
                        //endregion
                    }
                    is DetailInsightViewModel.InsightAction.SuccessLikeDislike -> {
                        insight.uiResponseAfterApiCall.invoke(insight.result.message, insight.id)
                    }
                    is DetailInsightViewModel.InsightAction.SuccessUpdateComment -> {
                        showSuccessSnackbar(insight.result.message)
                        insight.tvCommentBody.text = insight.comment
                    }
                    is DetailInsightViewModel.InsightAction.SuccessReport -> {
                        showSuccessSnackbar("Reported successfully")
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
                //region Generate CommentsAdapter
                val uniqueAdapterId = randomGenerator.nextDouble()
                commentsAdapter =
                    CommentsAdapter(this@DetailInsightFragment, requireContext(), uniqueAdapterId)
                commentAdapterMap[uniqueAdapterId] = commentsAdapter
                adapter = commentsAdapter
                //endregion
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
                    DetailInsightViewModel.INSIGHT_TYPE,
                    ::likeDislikeResponseToApiCall
                )
            }
            ivDislike.setOnClickListener {
                viewModel.sendDislikeRequest(
                    viewModel.getCurrentInsightId(),
                    DetailInsightViewModel.INSIGHT_TYPE,
                    ::likeDislikeResponseToApiCall
                )
            }

            btnComment.setOnClickListener {
                if (TextUtils.isEmpty(tfInsightComment.editText?.text)) {
                    tfInsightComment.requestFocus()
                    tfInsightComment.error = "Please enter your comment"
                } else {
                    viewModel.createComment(
                        viewModel.getCurrentInsightId(),
                        commentsAdapter.getUniqueAdapterId(),
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

    private fun showSuccessSnackbar(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(resources.getColor(R.color.success_color, null))
                .setTextColor(Color.WHITE)
                .show()
        } else {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.GREEN)
                .setTextColor(Color.WHITE)
                .show()
        }
    }

    private fun likeDislikeResponseToApiCall(resultMessage: String, id: Int) {
        binding.apply {

            var likeCount = tvInsightLike.text.toString().toInt()
            var dislikeCount = tvInsightDislike.text.toString().toInt()

            if (resultMessage.lowercase().contains("unlike")) {
                ivLike.clearColorFilter()

                likeCount -= 1
                tvInsightLike.text = likeCount.toString()

            } else if (resultMessage.lowercase().contains("undislike")) {
                ivDislike.clearColorFilter()

                dislikeCount -= 1
                tvInsightDislike.text = dislikeCount.toString()

            } else if (resultMessage.lowercase().contains("dislike")) {
                ivDislike.setColorFilter(Color.BLUE)

                dislikeCount += 1
                tvInsightDislike.text = dislikeCount.toString()

                if (ivLike.colorFilter != null) {
                    ivLike.clearColorFilter()

                    likeCount -= 1
                    tvInsightLike.text = likeCount.toString()
                }
            } else if (resultMessage.lowercase().contains("like")) {
                ivLike.setColorFilter(Color.BLUE)

                likeCount += 1
                tvInsightLike.text = likeCount.toString()

                if (ivDislike.colorFilter != null) {
                    ivDislike.clearColorFilter()

                    dislikeCount -= 1
                    tvInsightDislike.text = dislikeCount.toString()
                }

            }
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

    override fun onLikeCommentClicked(
        item: InsightCommentItem,
        uiResponseAfterApiCall: (responseMessage: String, id: Int) -> Unit,
    ) {
        viewModel.sendLikeRequest(
            item.idComment,
            DetailInsightViewModel.COMMENT_TYPE,
            uiResponseAfterApiCall
        )
    }

    override fun onDislikeCommentClicked(
        item: InsightCommentItem,
        uiResponseAfterApiCall: (responseMessage: String, id: Int) -> Unit,
    ) {
        viewModel.sendDislikeRequest(
            item.idComment,
            DetailInsightViewModel.COMMENT_TYPE,
            uiResponseAfterApiCall
        )
    }

    override fun onReplyCommentClicked(
        item: InsightCommentItem,
        comment: String,
        uniqueAdapterId: Double,
    ) {
        viewModel.createComment(
            item.idComment,
            uniqueAdapterId,
            DetailInsightViewModel.COMMENT_TYPE,
            comment
        )
    }

    override fun onShowCommentRepliesClicked(
        item: InsightCommentItem,
        uniqueAdapterId: Double,
        binding: ItemInsightCommentBinding,
    ) {
        viewModel.getCommentReplies(item.idComment, uniqueAdapterId, binding)
    }

    override fun onDeleteCommentClicked(
        item: InsightCommentItem,
        id: Int,
        uniqueAdapterId: Double,
    ) {
        viewModel.deleteComment(item, id, uniqueAdapterId)
    }

    override fun onUpdateCommentClicked(
        item: InsightCommentItem,
        comment: String,
        tvCommentBody: TextView,
    ) {
        viewModel.updateComment(item.idComment, comment, tvCommentBody)
    }
}