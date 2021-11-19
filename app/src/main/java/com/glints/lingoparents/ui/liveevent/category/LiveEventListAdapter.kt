package com.glints.lingoparents.ui.liveevent.category

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glints.lingoparents.data.model.LiveEventItem
import com.glints.lingoparents.databinding.ItemLiveEventBinding

class LiveEventListAdapter(private val listener: OnItemClickCallback) :
    RecyclerView.Adapter<LiveEventListAdapter.CustomViewHolder>() {

    private val liveEventList = ArrayList<LiveEventItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<LiveEventItem>) {
        liveEventList.clear()
        liveEventList.addAll(list)
        notifyDataSetChanged()
    }

    inner class CustomViewHolder(private val itemLiveEventBinding: ItemLiveEventBinding) :
        RecyclerView.ViewHolder(itemLiveEventBinding.root) {
        fun bind(holder: CustomViewHolder, item: LiveEventItem) {
            holder.itemView.setOnClickListener {
                listener.onItemClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val itemLiveEventBinding =
            ItemLiveEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(itemLiveEventBinding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val liveEventItem = liveEventList[position]
        holder.bind(holder, liveEventItem)
    }

    override fun getItemCount(): Int = liveEventList.size

    interface OnItemClickCallback {
        fun onItemClicked(item: LiveEventItem)
    }
}