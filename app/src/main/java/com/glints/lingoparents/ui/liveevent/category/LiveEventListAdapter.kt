package com.glints.lingoparents.ui.liveevent.category

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.glints.lingoparents.data.model.response.LiveEventListResponse
import com.glints.lingoparents.databinding.ItemLiveEventBinding

class LiveEventListAdapter(
    private val listener: OnItemClickCallback,
    private val category: String,
) :
    RecyclerView.Adapter<LiveEventListAdapter.CustomViewHolder>() {

    private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<LiveEventListResponse.LiveEventItemResponse>() {
        override fun areItemsTheSame(
            oldItem: LiveEventListResponse.LiveEventItemResponse,
            newItem: LiveEventListResponse.LiveEventItemResponse,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: LiveEventListResponse.LiveEventItemResponse,
            newItem: LiveEventListResponse.LiveEventItemResponse,
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    fun submitList(list: List<LiveEventListResponse.LiveEventItemResponse>) {
        differ.submitList(list)
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
        holder.bind(holder, differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    interface OnItemClickCallback {
        fun onItemClicked(item: LiveEventListResponse.LiveEventItemResponse)
    }
}