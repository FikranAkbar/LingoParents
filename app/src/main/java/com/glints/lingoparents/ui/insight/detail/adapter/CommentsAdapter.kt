package com.glints.lingoparents.ui.insight.detail.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.glints.lingoparents.data.model.response.GetCommentRepliesResponse
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

                tvDeleteComment.setOnClickListener {
                    listener.onDeleteCommentClicked(item ,item.id)
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
        fun onDeleteCommentClicked(item: InsightDetailResponse.MasterComment, id: Int)

        override fun onLikeCommentClicked(item: GetCommentRepliesResponse.Message) {
            onLikeCommentClicked(item)
        }

        override fun onDislikeCommentClicked(item: GetCommentRepliesResponse.Message) {
            onDislikeCommentClicked(item)
        }

        override fun onReplyCommentClicked(
            item: GetCommentRepliesResponse.Message,
            comment: String
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

    fun hideTextView(binding: ItemInsightCommentBinding, b: Boolean){
        binding.apply {
            if (b){
                tvDeleteComment.visibility = View.VISIBLE
                tvUpdateComment.visibility = View.VISIBLE
            }
            else{
                tvDeleteComment.visibility = View.GONE
                tvUpdateComment.visibility = View.GONE
            }
        }
    }
}