package com.glints.lingoparents.ui.insight.detail.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.glints.lingoparents.data.model.response.InsightDetailResponse
import com.glints.lingoparents.databinding.ItemInsightCommentBinding

class CommentsAdapter(private val listener: OnItemClickCallback) :
    RecyclerView.Adapter<CommentsAdapter.AdapterHolder>() {
    private val dataList = ArrayList<InsightDetailResponse.MasterComment>()
    private lateinit var rvChild: RecyclerView

    inner class AdapterHolder(private val binding: ItemInsightCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: InsightDetailResponse.MasterComment, holder: AdapterHolder) {
            binding.apply {
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
                    btnReplyComment.visibility = View.VISIBLE
                    btnReplyComment.setOnClickListener {
                        listener.onReplyCommentClicked(
                            item,
                            tfReplyComment.editText?.text.toString()
                        )
                        tfReplyComment.editText?.setText("")
                    }
                }

                if (item.replies > 0) tvShowReplyComment.visibility = View.VISIBLE
                tvShowReplyComment.text = "Show $totalReplies Replies"
                tvShowReplyComment.setOnClickListener {
                    if (rvCommentReply.visibility == View.GONE) {
                        rvCommentReply.visibility = View.VISIBLE
                        tvShowReplyComment.text = "Hide Replies"
                        rvChild = rvCommentReply
                        listener.onShowCommentRepliesClicked(item)
                    } else if (rvCommentReply.visibility == View.VISIBLE) {
                        rvCommentReply.visibility = View.GONE
                        tvShowReplyComment.text = "Show $totalReplies Replies"
                    }
                }
            }
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
        return AdapterHolder(
            ItemInsightCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
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
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<InsightDetailResponse.MasterComment>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }
}