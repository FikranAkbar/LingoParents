package com.glints.lingoparents.ui.insight.detail.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.glints.lingoparents.data.model.response.GetCommentRepliesResponse
import com.glints.lingoparents.databinding.ItemInsightCommentBinding

class CommentRepliesAdapter(private val listener: OnItemClickCallback) :
    RecyclerView.Adapter<CommentRepliesAdapter.AdapterHolder>() {
    private val dataList = ArrayList<GetCommentRepliesResponse.Message>()

    inner class AdapterHolder(private val binding: ItemInsightCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetCommentRepliesResponse.Message, holder: AdapterHolder) {
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
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(rvCommentReply.context)
                        adapter = commentRepliesAdapter
                    }
                    listener.onShowCommentRepliesClicked(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentRepliesAdapter.AdapterHolder {
        return AdapterHolder(
            ItemInsightCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentRepliesAdapter.AdapterHolder, position: Int) {
        holder.bind(dataList[position], holder)
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