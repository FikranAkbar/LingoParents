package com.glints.lingoparents.ui.insight.detail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.InsightCommentItem
import com.glints.lingoparents.databinding.ItemInsightCommentBinding
import java.util.ArrayList

class CommentsAdapter : RecyclerView.Adapter<CommentsAdapter.AdapterHolder>() {
    private val dataList = ArrayList<InsightCommentItem>()

    inner class AdapterHolder(private val binding: ItemInsightCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InsightCommentItem, holder: AdapterHolder) {
            binding.apply {
                Glide.with(holder.itemView.context).load(R.drawable.ic_user_avatar_female).into(ivInsightComment)
                tvUsernameComment.text = item.name
                tvInsightCommentBody.text = item.comments
            }
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

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<InsightCommentItem>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }
}