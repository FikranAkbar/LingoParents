package com.glints.lingoparents.ui.insight.detail.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.glints.lingoparents.data.model.response.GetCommentRepliesResponse
import com.glints.lingoparents.data.model.response.InsightDetailResponse
import com.glints.lingoparents.databinding.ItemInsightCommentBinding
import java.util.*

class CommentsAdapter(private val listener: OnItemClickCallback) :
    RecyclerView.Adapter<CommentsAdapter.AdapterHolder>() {
    private val dataList = ArrayList<InsightDetailResponse.MasterComment>()
    private var parentId: Int = 0

    inner class AdapterHolder(private val binding: ItemInsightCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InsightDetailResponse.MasterComment, holder: AdapterHolder) {
            binding.apply {
                if (parentId == item.id_user)
                    hideTextView(true)
                else hideTextView(false)

                val firstname: String = item.Master_user.Master_parent.firstname
                val lastname: String = item.Master_user.Master_parent.lastname
                val totalReplies = item.replies

                Glide.with(holder.itemView.context).load(item.Master_user.Master_parent.photo)
                    .into(ivComment)
                "$firstname $lastname".also { tvUsernameComment.text = it }
                tvCommentBody.text = item.comment
                tvLikeComment.text = item.total_like.toString()
                tvDislikeComment.text = item.total_dislike.toString()

                tvLikeComment.setOnClickListener {
                    listener.onLikeCommentClicked(item)
                }

                tvDislikeComment.setOnClickListener {
                    listener.onDislikeCommentClicked(item)
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

                when {
                    totalReplies > 1 -> "Show $totalReplies replies".also {
                        tvShowReplyComment.text = it
                    }
                    totalReplies == 1 -> "Show $totalReplies reply".also {
                        tvShowReplyComment.text = it
                    }
                    else -> tvShowReplyComment.visibility = View.GONE
                }

                tvShowReplyComment.setOnClickListener {
                    val commentRepliesAdapter = CommentRepliesAdapter(listener)
                    rvCommentReply.apply {
                        visibility = View.VISIBLE
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(rvCommentReply.context)
                        adapter = commentRepliesAdapter
                    }
                    listener.onShowCommentRepliesClicked(item)
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        return AdapterHolder(ItemInsightCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        )
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
        holder.bind(dataList[position], holder)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnItemClickCallback : CommentRepliesAdapter.OnItemClickCallback {
        fun onLikeCommentClicked(item: InsightDetailResponse.MasterComment)
        fun onDislikeCommentClicked(item: InsightDetailResponse.MasterComment)
        fun onReplyCommentClicked(item: InsightDetailResponse.MasterComment, comment: String)
        fun onShowCommentRepliesClicked(item: InsightDetailResponse.MasterComment)
        fun onDeleteCommentClicked(item: InsightDetailResponse.MasterComment, id: Int)
        fun onUpdateCommentClicked(item: InsightDetailResponse.MasterComment, comment: String)

        override fun onLikeCommentClicked(item: GetCommentRepliesResponse.Message) {
            onLikeCommentClicked(item)
        }

        override fun onDislikeCommentClicked(item: GetCommentRepliesResponse.Message) {
            onDislikeCommentClicked(item)
        }

        override fun onReplyCommentClicked(
            item: GetCommentRepliesResponse.Message,
            comment: String,
        ) {
            onReplyCommentClicked(item, comment)
        }

        override fun onShowCommentRepliesClicked(item: GetCommentRepliesResponse.Message) {
            onShowCommentRepliesClicked(item)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<InsightDetailResponse.MasterComment>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun submitParentId(id: Int) {
        parentId = id
    }
}