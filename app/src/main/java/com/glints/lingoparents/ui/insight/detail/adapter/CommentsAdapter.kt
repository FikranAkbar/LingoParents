package com.glints.lingoparents.ui.insight.detail.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.InsightCommentItem
import com.glints.lingoparents.databinding.ItemInsightCommentBinding
import com.glints.lingoparents.ui.dashboard.hideKeyboard
import com.glints.lingoparents.ui.dashboard.openKeyboard
import com.glints.lingoparents.ui.insight.detail.DetailInsightFragment
import kotlin.math.roundToInt

class CommentsAdapter(
    private val listener: OnItemClickCallback,
    private val context: Context,
    private var uniqueAdapterId: Double,
    var parentCommentListener: ParentCommentListener? = null,
) :
    RecyclerView.Adapter<CommentsAdapter.AdapterHolder>() {
    companion object {
        var parentId: Int = 0
    }

    private val diffUtilCallback = object :
        DiffUtil.ItemCallback<InsightCommentItem>() {
        override fun areItemsTheSame(
            oldItem: InsightCommentItem,
            newItem: InsightCommentItem,
        ): Boolean {
            return oldItem.idComment == newItem.idComment
        }

        override fun areContentsTheSame(
            oldItem: InsightCommentItem,
            newItem: InsightCommentItem,
        ): Boolean {
            return oldItem.comment == newItem.comment
        }
    }

    val differ = AsyncListDiffer(this, diffUtilCallback)

    inner class AdapterHolder(private val binding: ItemInsightCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val linearLayoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        private fun likeDislikeResponseToApiCall(resultMessage: String, id: Int) {
            binding.apply {
                var likeCount = tvLikeCount.text.toString().toInt()
                var dislikeCount = tvDislikeCount.text.toString().toInt()

                if (resultMessage.lowercase().contains("unlike")) {
                    ivCommentLike.clearColorFilter()

                    likeCount -= 1
                    tvLikeCount.text = likeCount.toString()

                    differ.submitList(differ.currentList.map {
                        if (it.idComment == id) {
                            it.copy(is_liked = 0, totalLike = it.totalLike - 1)
                        } else {
                            it.copy()
                        }
                    })
                } else if (resultMessage.lowercase().contains("undislike")) {
                    ivCommentDislike.clearColorFilter()

                    dislikeCount -= 1
                    tvDislikeCount.text = dislikeCount.toString()

                    differ.submitList(differ.currentList.map {
                        if (it.idComment == id) {
                            it.copy(is_disliked = 0, totalDislike = it.totalDislike - 1)
                        } else {
                            it.copy()
                        }
                    })
                } else if (resultMessage.lowercase().contains("dislike")) {
                    ivCommentDislike.setColorFilter(Color.BLUE)

                    dislikeCount += 1
                    tvDislikeCount.text = dislikeCount.toString()

                    if (ivCommentLike.colorFilter != null) {
                        ivCommentLike.clearColorFilter()

                        likeCount -= 1
                        tvLikeCount.text = likeCount.toString()

                        differ.submitList(differ.currentList.map {
                            if (it.idComment == id) {
                                it.copy(is_disliked = 1,
                                    is_liked = 0,
                                    totalDislike = it.totalDislike + 1,
                                    totalLike = it.totalLike - 1)
                            } else {
                                it.copy()
                            }
                        })
                    } else {
                        differ.submitList(differ.currentList.map {
                            if (it.idComment == id) {
                                it.copy(is_disliked = 1, totalDislike = it.totalDislike + 1)
                            } else {
                                it.copy()
                            }
                        })
                    }
                } else if (resultMessage.lowercase().contains("like")) {
                    ivCommentLike.setColorFilter(Color.BLUE)

                    likeCount += 1
                    tvLikeCount.text = likeCount.toString()

                    if (ivCommentDislike.colorFilter != null) {
                        ivCommentDislike.clearColorFilter()

                        dislikeCount -= 1
                        tvDislikeCount.text = dislikeCount.toString()

                        differ.submitList(differ.currentList.map {
                            if (it.idComment == id) {
                                it.copy(is_liked = 1, is_disliked = 0, totalLike = it.totalLike + 1, totalDislike = it.totalDislike - 1)
                            } else {
                                it.copy()
                            }
                        })
                    } else {
                        differ.submitList(differ.currentList.map {
                            if (it.idComment == id) {
                                it.copy(is_liked = 1, totalLike = it.totalLike + 1)
                            } else {
                                it.copy()
                            }
                        })
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: InsightCommentItem) {
            val onRvChildDifferListChangedListener =
                AsyncListDiffer.ListListener<InsightCommentItem> { _, currentList ->
                    if (currentList.size <= 0) {
                        binding.rvCommentReply.isVisible = false
                        binding.tvShowReplyComment.isVisible = false
                    }
                }

            binding.apply {
                //region Initialize views value
                if (parentId == item.idUser)
                    showDeleteAndUpdateTextView(true)
                else showDeleteAndUpdateTextView(false)

                item.photo?.let {
                    ivComment.load(it)
                }

                tvUsernameComment.text = item.name
                tvCommentBody.text = item.comment

                tvShowReplyComment.text = ""
                tvShowReplyComment.isVisible = false

                tvLikeCount.text = item.totalLike.toString()
                tvDislikeCount.text = item.totalDislike.toString()

                if (item.is_liked > 0) {
                    ivCommentLike.setColorFilter(Color.BLUE)
                } else ivCommentLike.clearColorFilter()

                if (item.is_disliked > 0) {
                    ivCommentDislike.setColorFilter(Color.BLUE)
                } else ivCommentDislike.clearColorFilter()

                rvCommentReply.apply {
                    setHasFixedSize(false)
                    isVisible = false

                    layoutManager = linearLayoutManager

                    if (item.totalReply > 0) {
                        rvCommentReply.isVisible = false
                        tvShowReplyComment.isVisible = true
                        tvShowReplyComment.text = "Show ${item.totalReply} Replies"
                        val newCommentsAdapter = createNewAdapter(item.idComment)
                        adapter = newCommentsAdapter

                        newCommentsAdapter.apply {
                            this.differ.removeListListener(onRvChildDifferListChangedListener)
                            this.differ.addListListener(onRvChildDifferListChangedListener)
                        }
                    }
                }
                //endregion

                //region Set like button onClickListener
                ivCommentLike.setOnClickListener {
                    listener.onLikeCommentClicked(
                        item,
                        ::likeDislikeResponseToApiCall
                    )
                }
                //endregion

                //region Set dislike button onClickListener
                ivCommentDislike.setOnClickListener {
                    listener.onDislikeCommentClicked(
                        item,
                        ::likeDislikeResponseToApiCall
                    )
                }
                //endregion

                //region Set report button onClickListener
                tvReportComment.setOnClickListener {
                    showReportDialog(context, item, item.idComment)
                }
                //endregion

                //region Set reply button onClickListener
                tvReplyComment.setOnClickListener {
                    if (btnReplyComment.text == "Update") {
                        btnReplyComment.text = "Reply"
                        tfReplyComment.editText?.setText("")
                        setPostCommentListener(item)
                    } else {
                        tfReplyComment.isVisible = !tfReplyComment.isVisible
                        btnReplyComment.isVisible = !btnReplyComment.isVisible

                        if (tvShowReplyComment.text.toString().lowercase().contains("show") &&
                            tfReplyComment.isVisible
                        ) {
                            rvCommentReply.visibility = View.VISIBLE
                            tvShowReplyComment.text = "Hide Replies"
                            listener.onShowCommentRepliesClicked(item, uniqueAdapterId, binding)
                        }

                        if (tfReplyComment.isVisible) {
                            tfReplyComment.requestFocus()
                            (context as Activity).openKeyboard()
                            "Reply".also { btnReplyComment.text = it }

                            setPostCommentListener(item)
                        } else {
                            (context as Activity).hideKeyboard()
                            btnReplyComment.text = ""
                            tfReplyComment.editText?.setText("")
                        }
                    }
                }
                //endregion

                //region Set show replies button onClickListener
                tvShowReplyComment.setOnClickListener {
                    if (!rvCommentReply.isVisible) {
                        rvCommentReply.isVisible = true

                        tvShowReplyComment.text = "Hide Replies"
                        listener.onShowCommentRepliesClicked(item, uniqueAdapterId, binding)
                    } else {
                        rvCommentReply.isVisible = false
                        val childRvDiffer =
                            (binding.rvCommentReply.adapter as CommentsAdapter).differ

                        if (childRvDiffer.currentList.size > 0) {
                            tvShowReplyComment.text =
                                "Show ${childRvDiffer.currentList.size} Replies"
                            tvShowReplyComment.isVisible = true
                        } else {
                            tvShowReplyComment.isVisible = false
                        }
                    }
                }
                //endregion

                //region Set delete button onClickListener
                tvDeleteComment.setOnClickListener {
                    parentCommentListener?.onChildCommentDelete()
                    listener.onDeleteCommentClicked(
                        item,
                        item.idComment,
                        uniqueAdapterId
                    )
                }
                //endregion

                //region Set update button onClickListener
                tvUpdateComment.setOnClickListener {
                    if (btnReplyComment.text == "Reply") {
                        btnReplyComment.text = "Update"
                        tfReplyComment.editText?.setText(item.comment)
                        tfReplyComment.editText?.selectAll()
                        setUpdateCommentListener(item)
                    } else {
                        tfReplyComment.isVisible = !tfReplyComment.isVisible
                        btnReplyComment.isVisible = !btnReplyComment.isVisible

                        if (tfReplyComment.isVisible) {
                            tfReplyComment.requestFocus()
                            (context as Activity).openKeyboard()
                            "Update".also { btnReplyComment.text = it }
                            tfReplyComment.editText?.setText(item.comment)
                            tfReplyComment.editText?.selectAll()

                            setUpdateCommentListener(item)
                        } else {
                            (context as Activity).hideKeyboard()
                            btnReplyComment.text = ""
                            tfReplyComment.editText?.setText("")
                        }
                    }
                }
                //endregion
            }
        }

        private fun showDeleteAndUpdateTextView(b: Boolean) {
            binding.apply {
                tvDeleteComment.isVisible = b
                tvUpdateComment.isVisible = b
            }
        }

        private fun showReportDialog(context: Context, item: InsightCommentItem, id: Int) {
            val builder = AlertDialog.Builder(context)
            var text = ""
            builder.apply {
                setCancelable(false)
                setTitle(R.string.report)
                setSingleChoiceItems(DetailInsightFragment.report_body, -1) { _, i ->
                    try {
                        text = DetailInsightFragment.report_body[i]
                    } catch (e: IllegalArgumentException) {
                        throw ClassCastException(e.toString())
                    }
                }
                setPositiveButton(R.string.report) { _, _ ->
                    listener.onReportCommentClicked(item, id, text)
                }
                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.create().show()
        }

        @SuppressLint("SetTextI18n")
        private fun setPostCommentListener(item: InsightCommentItem) {
            binding.apply {
                btnReplyComment.setOnClickListener {
                    if (TextUtils.isEmpty(tfReplyComment.editText?.text?.trim())) {
                        tfReplyComment.requestFocus()
                        tfReplyComment.error = "Please enter your comment"
                    } else {
                        if (rvCommentReply.adapter != null) {
                            listener.onReplyCommentClicked(
                                item,
                                tfReplyComment.editText?.text.toString().trim(),
                                (rvCommentReply.adapter as CommentsAdapter).getUniqueAdapterId()
                            )
                            (rvCommentReply.adapter as CommentsAdapter).assignParentCommentListener(
                                item.idComment,
                                this@CommentsAdapter)
                            item.totalReply = rvCommentReply.adapter!!.itemCount + 1
                        } else {
                            val newCommentsAdapter = createNewAdapter(item.idComment)
                            rvCommentReply.adapter = newCommentsAdapter

                            listener.onReplyCommentClicked(
                                item,
                                tfReplyComment.editText?.text.toString().trim(),
                                newCommentsAdapter.getUniqueAdapterId()
                            )

                            newCommentsAdapter.apply {

                                val onRvChildDifferListChangedListener =
                                    AsyncListDiffer.ListListener<InsightCommentItem> { _, currentList ->
                                        if (currentList.size <= 0) {
                                            binding.rvCommentReply.isVisible = false
                                            binding.tvShowReplyComment.isVisible = false
                                        }
                                    }

                                this.differ.removeListListener(onRvChildDifferListChangedListener)
                                this.differ.addListListener(onRvChildDifferListChangedListener)
                            }

                            item.totalReply += 1
                        }

                        tvShowReplyComment.text = "Hide Replies"
                        tvShowReplyComment.isVisible = true
                        rvCommentReply.isVisible = true

                        tfReplyComment.editText?.setText("")
                        tfReplyComment.isVisible = false
                        btnReplyComment.isVisible = false
                        (context as Activity).hideKeyboard()
                    }
                }
            }
        }

        private fun setUpdateCommentListener(item: InsightCommentItem) {
            binding.apply {
                btnReplyComment.setOnClickListener {
                    if (TextUtils.isEmpty(tfReplyComment.editText?.text.toString().trim())) {
                        tfReplyComment.requestFocus()
                        tfReplyComment.error = "Please enter your comment"
                    } else {
                        listener.onUpdateCommentClicked(
                            item,
                            tfReplyComment.editText?.text.toString(),
                            binding.tvCommentBody
                        )
                        tfReplyComment.editText?.setText("")
                        tfReplyComment.isVisible = false
                        btnReplyComment.isVisible = false
                        (context as Activity).hideKeyboard()
                    }
                }
            }
        }
    }

    fun showCommentReplies(_adapter: CommentsAdapter, binding: ItemInsightCommentBinding) {
        binding.rvCommentReply.apply {
            isVisible = true
            adapter = _adapter
            if (parentCommentListener == null) {
                this.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    setMargins(dpFormat(30), 0, 0, 0)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        return AdapterHolder(ItemInsightCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        )
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    interface ParentCommentListener {
        fun onChildCommentDelete()
    }

    interface OnItemClickCallback {
        fun onReportCommentClicked(
            item: InsightCommentItem,
            id: Int,
            report_comment: String,
        )

        fun onLikeCommentClicked(
            item: InsightCommentItem,
            uiResponseAfterApiCall: (responseMessage: String, id: Int) -> Unit,
        )

        fun onDislikeCommentClicked(
            item: InsightCommentItem,
            uiResponseAfterApiCall: (responseMessage: String, id: Int) -> Unit,
        )

        fun onReplyCommentClicked(
            item: InsightCommentItem,
            comment: String,
            uniqueAdapterId: Double,
        )

        fun onShowCommentRepliesClicked(
            item: InsightCommentItem,
            uniqueAdapterId: Double,
            binding: ItemInsightCommentBinding,
        )

        fun onDeleteCommentClicked(
            item: InsightCommentItem,
            id: Int,
            uniqueAdapterId: Double,
        )

        fun onUpdateCommentClicked(
            item: InsightCommentItem,
            comment: String,
            tvCommentBody: TextView,
        )
    }

    fun getUniqueAdapterId() = uniqueAdapterId

    fun submitList(list: List<InsightCommentItem>) {
        differ.submitList(list)
    }

    fun addNewCommentItem(item: InsightCommentItem) {
        val currentList = differ.currentList.map {
            it.copy()
        }
        differ.submitList(listOf(item) + currentList)
    }

    fun deleteCommentItem(item: InsightCommentItem) {
        val newList = differ.currentList.filter {
            it.idComment != item.idComment
        }

        differ.submitList(newList)
    }

    private fun createNewAdapter(itemCommentId: Int): CommentsAdapter {
        val uniqueAdapterId = DetailInsightFragment.randomGenerator.nextDouble()
        val newCommentsAdapter = CommentsAdapter(listener, context, uniqueAdapterId)
        DetailInsightFragment.commentAdapterMap[uniqueAdapterId] = newCommentsAdapter
        newCommentsAdapter.assignParentCommentListener(itemCommentId, this)

        return newCommentsAdapter
    }

    fun assignParentCommentListener(itemCommentId: Int, parentAdapter: CommentsAdapter) {
        this.parentCommentListener = object : ParentCommentListener {
            override fun onChildCommentDelete() {
                parentAdapter.differ.submitList(parentAdapter.differ.currentList.map {
                    if (it.idComment == itemCommentId) {
                        it.copy(totalReply = it.totalReply - 1)
                    } else {
                        it.copy()
                    }
                })
            }
        }
    }

    private fun dpFormat(dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

}