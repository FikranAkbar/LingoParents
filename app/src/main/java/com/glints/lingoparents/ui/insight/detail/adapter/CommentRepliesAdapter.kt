package com.glints.lingoparents.ui.insight.detail.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.glints.lingoparents.data.model.response.GetCommentRepliesResponse
import com.glints.lingoparents.databinding.ItemInsightCommentBinding

open class CommentRepliesAdapter(private val listener: OnItemClickCallback) :
    RecyclerView.Adapter<CommentRepliesAdapter.ChildAdapterHolder>() {
    private val dataList = ArrayList<GetCommentRepliesResponse.Message>()

    inner class ChildAdapterHolder(private val binding: ItemInsightCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        private fun initBinding(
            item: GetCommentRepliesResponse.Message,
            binding: ItemInsightCommentBinding
        ) {
            binding.apply {
                val firstname: String = item.Master_user.Master_parent.firstname
                val lastname: String = item.Master_user.Master_parent.lastname

                ivComment.load(item.Master_user.Master_parent.photo)
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

                if (item.replies > 0) createNestedComment(binding, item)
            }
        }

        fun bind(item: GetCommentRepliesResponse.Message) = initBinding(item, binding)

        @SuppressLint("SetTextI18n")
        private fun createNestedComment(
            binding: ItemInsightCommentBinding,
            item: GetCommentRepliesResponse.Message
        ) {
            binding.apply {
                if (item.replies > 0) tvShowReplyComment.visibility = View.VISIBLE
                tvShowReplyComment.text = "Show ${item.replies} Replies"
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
                        tvShowReplyComment.text = "Show ${item.replies} Replies"
                        llReplies.removeAllViews()
                        llReplies.isVisible = false
                    }
                }
            }
            return
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
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
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    interface OnItemClickCallback {
        fun onLikeCommentClicked(item: GetCommentRepliesResponse.Message)
        fun onDislikeCommentClicked(item: GetCommentRepliesResponse.Message)
        fun onReplyCommentClicked(item: GetCommentRepliesResponse.Message, comment: String)
        fun onShowCommentRepliesClicked(item: GetCommentRepliesResponse.Message)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<GetCommentRepliesResponse.Message>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }
}