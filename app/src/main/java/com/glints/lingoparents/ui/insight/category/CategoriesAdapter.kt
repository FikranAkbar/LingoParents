package com.glints.lingoparents.ui.insight.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.InsightSliderItem
import com.glints.lingoparents.databinding.ItemInsightBinding

class CategoriesAdapter(private val listener: OnItemClickCallback, private val dataList: MutableList<InsightSliderItem>)
    : RecyclerView.Adapter<CategoriesAdapter.AdapterHolder>(){
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

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class AdapterHolder(private val binding: ItemInsightBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(insightSliderItem: InsightSliderItem, holder: AdapterHolder){
            holder.itemView.setOnClickListener {
                listener.onItemClicked(insightSliderItem)
            }
            binding.apply {
                Glide.with(holder.itemView.context).load(R.drawable.img_dummy_insight_card).into(ivInsight)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(insightSliderItem: InsightSliderItem)
    }
}