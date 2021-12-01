package com.glints.lingoparents.ui.insight.category

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.glints.lingoparents.data.model.response.AllInsightsListResponse
import com.glints.lingoparents.databinding.ItemInsightBinding

class CategoriesAdapter(private val listener: OnItemClickCallback)
    : RecyclerView.Adapter<CategoriesAdapter.AdapterHolder>(){

    private val dataList = ArrayList<AllInsightsListResponse.Data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesAdapter.AdapterHolder {
        return AdapterHolder(ItemInsightBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: CategoriesAdapter.AdapterHolder, position: Int) {
        holder.bind(dataList[position], holder)
    }

    override fun getItemCount(): Int = dataList.size

    inner class AdapterHolder(private val binding: ItemInsightBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: AllInsightsListResponse.Data, holder: AdapterHolder){
            holder.itemView.setOnClickListener {
                listener.onItemClicked(item)
            }
            binding.apply {
                ivInsight.load(item.cover)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(item: AllInsightsListResponse.Data)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<AllInsightsListResponse.Data>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }
}