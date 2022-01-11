package com.glints.lingoparents.ui.insight.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.glints.lingoparents.data.model.response.AllInsightsListResponse
import com.glints.lingoparents.databinding.ItemInsightBinding

class CategoriesAdapter(private val listener: OnItemClickCallback) :
    RecyclerView.Adapter<CategoriesAdapter.AdapterHolder>() {

    private val diffUtilCallback = object :
        DiffUtil.ItemCallback<AllInsightsListResponse.Message>() {
        override fun areItemsTheSame(
            oldItem: AllInsightsListResponse.Message,
            newItem: AllInsightsListResponse.Message,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AllInsightsListResponse.Message,
            newItem: AllInsightsListResponse.Message,
        ): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this, diffUtilCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CategoriesAdapter.AdapterHolder {
        return AdapterHolder(ItemInsightBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: CategoriesAdapter.AdapterHolder, position: Int) {
        holder.bind(differ.currentList[position], holder)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class AdapterHolder(private val binding: ItemInsightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AllInsightsListResponse.Message, holder: AdapterHolder) {
            holder.itemView.setOnClickListener {
                listener.onItemClicked(item)
            }
            binding.apply {
                ivInsight.load(item.cover)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(item: AllInsightsListResponse.Message)
    }

    fun submitList(list: List<AllInsightsListResponse.Message>) {
        differ.submitList(list)
    }
}