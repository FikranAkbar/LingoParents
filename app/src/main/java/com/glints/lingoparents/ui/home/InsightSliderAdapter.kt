package com.glints.lingoparents.ui.home

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.InsightSliderItem
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.base.PlutoViewHolder
import com.opensooq.pluto.listeners.OnItemClickListener

class InsightSliderAdapter(sliderItems: MutableList<InsightSliderItem>, onSliderItemClickListener: OnItemClickListener<InsightSliderItem>) :
    PlutoAdapter<InsightSliderItem, InsightSliderAdapter.ViewHolder>(sliderItems, onSliderItemClickListener) {
    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent, R.layout.carousel_item_insight)
    }

    class ViewHolder(parent: ViewGroup, itemLayoutId: Int) : PlutoViewHolder<InsightSliderItem>(parent, itemLayoutId) {
        private var ivInsight: ImageView = getView(R.id.iv_insight)
        private var tvInsight: TextView = getView(R.id.tv_insight)

        override fun set(item: InsightSliderItem, position: Int) {
            Glide.with(context).load(R.drawable.img_dummy_insight).into(ivInsight)
            tvInsight.text = item.title
        }

    }

}