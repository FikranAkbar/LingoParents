package com.glints.lingoparents.ui.insight.detail.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.InsightCommentItem
import com.glints.lingoparents.databinding.ItemInsightCommentBinding
import com.glints.lingoparents.ui.insight.detail.DetailInsightFragment

class CommentRepliesAdapter(
    private val listener: OnItemClickCallback,
    private val context: Context,
) :
    RecyclerView.Adapter<CommentRepliesAdapter.ChildAdapterHolder>() {
    private var parentId: Int = 0

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
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this, diffUtilCallback)

    inner class ChildAdapterHolder(private val binding: ItemInsightCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        private fun initBinding(
            item: InsightCommentItem,
            binding: ItemInsightCommentBinding,
        ) {
            binding.apply {
                if (parentId == item.idUser)
                    hideTextView(true)
                else hideTextView(false)

                item.photo?.let {
                    ivComment.load(it)
                }

                tvUsernameComment.text = item.name
                tvCommentBody.text = item.comment
                tvCommentLike.text = item.totalLike.toString()
                tvCommentDislike.text = item.totalDislike.toString()
                tvCommentLike.setOnClickListener {
                    listener.onLikeCommentClicked(item)
                }

                tvCommentDislike.setOnClickListener {
                    listener.onDislikeCommentClicked(item)
                }

                tvReportComment.setOnClickListener {
                    showReportDialog(context, item, item.idComment)
                }

                tvReplyComment.setOnClickListener {
                    tfReplyComment.visibility = View.VISIBLE
                    btnReplyComment.visibility = View.VISIBLE
                    btnReplyComment.setOnClickListener {
                        listener.onReplyCommentClicked(
                            item,
                            tfReplyComment.editText?.text.toString()
                        )
                        tfReplyComment.editText?.setText("")
                    }
                }

                tvDeleteComment.setOnClickListener {
                    listener.onDeleteCommentClicked(item, item.idComment)
                }

                tvUpdateComment.setOnClickListener {
                    tfReplyComment.visibility = View.VISIBLE
                    tfReplyComment.requestFocus()
                    btnReplyComment.visibility = View.VISIBLE
                    "Update".also { btnReplyComment.text = it }

                    btnReplyComment.setOnClickListener {
                        if (TextUtils.isEmpty(tfReplyComment.editText?.text)) {
                            tfReplyComment.requestFocus()
                            tfReplyComment.error = "Please enter your comment"
                        } else {
                            listener.onUpdateCommentClicked(
                                item,
                                tfReplyComment.editText?.text.toString()
                            )
                            tfReplyComment.editText?.setText("")
                        }
                    }
                }

                if (item.totalReply > 0) createNestedComment(binding, item)
            }
        }

        fun bind(item: InsightCommentItem) = initBinding(item, binding)

        @SuppressLint("SetTextI18n")
        private fun createNestedComment(
            binding: ItemInsightCommentBinding,
            item: InsightCommentItem,
        ) {
            binding.apply {
                if (item.totalReply > 0) tvShowReplyComment.visibility = View.VISIBLE
                tvShowReplyComment.text = "Show ${item.totalReply} Replies"
                tvShowReplyComment.setOnClickListener {
                    if (llReplies.visibility == View.GONE) {
                        llReplies.isVisible = true
                        tvShowReplyComment.text = "Hide Replies"
                        val newBinding = ItemInsightCommentBinding.inflate(
                            LayoutInflater.from(binding.root.context)
                        )
                        initBinding(item, newBinding)
                        llReplies.addView(newBinding.root)
                        listener.onShowCommentRepliesClicked(item)
                    } else {
                        tvShowReplyComment.text = "Show ${item.totalReply} Replies"
                        llReplies.removeAllViews()
                        llReplies.isVisible = false
                    }
                }
            }
        }

        private fun hideTextView(b: Boolean) {
            binding.apply {
                tvDeleteComment.isVisible = b
                tvUpdateComment.isVisible = b
            }
        }

        private fun showReportDialog(
            context: Context,
            item: InsightCommentItem,
            id: Int,
        ) {
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
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CommentRepliesAdapter.ChildAdapterHolder {
        return ChildAdapterHolder(
            ItemInsightCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentRepliesAdapter.ChildAdapterHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    interface OnItemClickCallback {
        fun onReportCommentClicked(
            item: InsightCommentItem,
            id: Int,
            report_comment: String,
        )

        fun onLikeCommentClicked(item: InsightCommentItem)
        fun onDislikeCommentClicked(item: InsightCommentItem)
        fun onReplyCommentClicked(item: InsightCommentItem, comment: String)
        fun onShowCommentRepliesClicked(item: InsightCommentItem)
        fun onDeleteCommentClicked(item: InsightCommentItem, id: Int)
        fun onUpdateCommentClicked(item: InsightCommentItem, comment: String)
    }

    fun submitList(list: List<InsightCommentItem>) {
        differ.submitList(list)
    }
}