package com.glints.lingoparents.ui.liveevent.category

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.glints.lingoparents.data.model.response.LiveEventListResponse
import com.glints.lingoparents.databinding.ItemLiveEventBinding

class LiveEventListAdapter(
    private val listener: OnItemClickCallback,
    private val category: String,
) :
    RecyclerView.Adapter<LiveEventListAdapter.CustomViewHolder>() {

    private val liveEventList = ArrayList<LiveEventListResponse.LiveEventItemResponse>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<LiveEventListResponse.LiveEventItemResponse>) {
        liveEventList.clear()
        liveEventList.addAll(list)
        notifyDataSetChanged()
    }

    inner class CustomViewHolder(
        private val itemLiveEventBinding: ItemLiveEventBinding,
        private val category: String,
    ) :
        RecyclerView.ViewHolder(itemLiveEventBinding.root) {
        fun bind(
            holder: CustomViewHolder,
            item: LiveEventListResponse.LiveEventItemResponse,
        ) {
            holder.itemView.setOnClickListener {
                listener.onItemClicked(item)
            }

            itemLiveEventBinding.apply {
                tvLiveEventTitle.text = item.title
                tvLiveEventDate.text = item.date

                ivImage.load(item.speaker_photo)

                detailText.text =
                    if (category.lowercase() == "completed") "Completed" else "Show more"

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val itemLiveEventBinding =
            ItemLiveEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(itemLiveEventBinding, category)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val liveEventItem = liveEventList[position]
        holder.bind(holder, liveEventItem)
    }

    override fun getItemCount(): Int = liveEventList.size

    interface OnItemClickCallback {
        fun onItemClicked(item: LiveEventListResponse.LiveEventItemResponse)
    }
}