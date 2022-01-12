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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.InsightDetailResponse
import com.glints.lingoparents.databinding.ItemInsightCommentBinding
import com.glints.lingoparents.ui.insight.detail.DetailInsightFragment

class CommentsAdapter(private val listener: OnItemClickCallback, private val context: Context) :
    RecyclerView.Adapter<CommentsAdapter.AdapterHolder>() {
    private lateinit var rvChild: RecyclerView
    private var parentId: Int = 0

    private val diffUtilCallback = object :
        DiffUtil.ItemCallback<InsightDetailResponse.MasterComment>() {
        override fun areItemsTheSame(
            oldItem: InsightDetailResponse.MasterComment,
            newItem: InsightDetailResponse.MasterComment,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: InsightDetailResponse.MasterComment,
            newItem: InsightDetailResponse.MasterComment,
        ): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this, diffUtilCallback)

    inner class AdapterHolder(private val binding: ItemInsightCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: InsightDetailResponse.MasterComment) {
            binding.apply {
                if (parentId == item.id_user)
                    hideTextView(true)
                else hideTextView(false)

                ivComment.load(item.Master_user.Master_parent.photo)
                tvUsernameComment.text =
                    item.Master_user.Master_parent.firstname + " " + item.Master_user.Master_parent.lastname
                tvCommentBody.text = item.comment
                tvLikeComment.text = item.total_like.toString()
                tvDislikeComment.text = item.total_dislike.toString()

                tvLikeComment.setOnClickListener {
                    listener.onLikeCommentClicked(item)
                }

                tvDislikeComment.setOnClickListener {
                    listener.onDislikeCommentClicked(item)
                }

                tvReportComment.setOnClickListener {
                    showReportDialog(context, item, item.id)
                }

                tvReplyComment.setOnClickListener {
                    tfReplyComment.visibility = View.VISIBLE
                    tfReplyComment.requestFocus()
                    btnReplyComment.visibility = View.VISIBLE
                    "Reply".also { btnReplyComment.text = it }

                    btnReplyComment.setOnClickListener {
                        if (TextUtils.isEmpty(tfReplyComment.editText?.text)) {
                            tfReplyComment.requestFocus()
                            tfReplyComment.error = "Please enter your comment"
                        } else {
                            listener.onReplyCommentClicked(
                                item,
                                tfReplyComment.editText?.text.toString()
                            )
                            tfReplyComment.editText?.setText("")
                        }
                    }
                }

                if (item.replies > 0) tvShowReplyComment.visibility = View.VISIBLE
                tvShowReplyComment.text = "Show ${item.replies} Replies"
                tvShowReplyComment.setOnClickListener {
                    if (rvCommentReply.visibility == View.GONE) {
                        rvCommentReply.visibility = View.VISIBLE
                        tvShowReplyComment.text = "Hide Replies"
                        rvChild = rvCommentReply
                        listener.onShowCommentRepliesClicked(item)
                    } else if (rvCommentReply.visibility == View.VISIBLE) {
                        rvCommentReply.visibility = View.GONE
                        tvShowReplyComment.text = "Show ${item.replies} Replies"
                    }
                }

                tvDeleteComment.setOnClickListener {
                    listener.onDeleteCommentClicked(item, item.id)
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
            item: InsightDetailResponse.MasterComment,
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

    fun showCommentReplies(_adapter: CommentRepliesAdapter) {
        rvChild.apply {
            visibility = View.VISIBLE
            setHasFixedSize(true)

            val linearLayoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            layoutManager = linearLayoutManager
            adapter = _adapter
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

    interface OnItemClickCallback : CommentRepliesAdapter.OnItemClickCallback {
        fun onReportCommentClicked(
            item: InsightDetailResponse.MasterComment,
            id: Int,
            report_comment: String,
        )

        fun onLikeCommentClicked(item: InsightDetailResponse.MasterComment)
        fun onDislikeCommentClicked(item: InsightDetailResponse.MasterComment)
        fun onReplyCommentClicked(item: InsightDetailResponse.MasterComment, comment: String)
        fun onShowCommentRepliesClicked(item: InsightDetailResponse.MasterComment)
        fun onDeleteCommentClicked(item: InsightDetailResponse.MasterComment, id: Int)
        fun onUpdateCommentClicked(item: InsightDetailResponse.MasterComment, comment: String)
    }

    fun submitList(list: List<InsightDetailResponse.MasterComment>) {
        differ.submitList(list)
    }

    fun submitParentId(id: Int) {
        parentId = id
    }
}