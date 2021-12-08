package com.glints.lingoparents.ui.home.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.InsightSliderItem
import com.glints.lingoparents.data.model.response.DataItem
import com.glints.lingoparents.data.model.response.RecentInsightItem
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.base.PlutoViewHolder
import com.opensooq.pluto.listeners.OnItemClickListener

//ini orignal
//class InsightSliderAdapter(sliderItems: MutableList<InsightSliderItem>, onSliderItemClickListener: OnItemClickListener<InsightSliderItem>) :
//    PlutoAdapter<InsightSliderItem, InsightSliderAdapter.ViewHolder>(sliderItems, onSliderItemClickListener) {
//    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(parent, R.layout.carousel_item_insight)
//    }
//
//    class ViewHolder(parent: ViewGroup, itemLayoutId: Int) : PlutoViewHolder<InsightSliderItem>(parent, itemLayoutId) {
//        private var ivInsight: ImageView = getView(R.id.iv_insight)
//        private var tvInsight: TextView = getView(R.id.tv_insight)
//
//        override fun set(item: InsightSliderItem, position: Int) {
//            Glide.with(context).load(R.drawable.img_dummy_insight).into(ivInsight)
//            tvInsight.text = item.title
//        }
//
//    }
//
//}

//class InsightSliderAdapter(sliderItems: MutableList<MessageItem>, onSliderItemClickListener: OnItemClickListener<MessageItem>) :
//    PlutoAdapter<MessageItem, InsightSliderAdapter.ViewHolder>(sliderItems, onSliderItemClickListener) {
//    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(parent, R.layout.carousel_item_insight)
//    }
//
//    class ViewHolder(parent: ViewGroup, itemLayoutId: Int) : PlutoViewHolder<MessageItem>(parent, itemLayoutId) {
//        private var ivInsight: ImageView = getView(R.id.iv_insight)
//        private var tvInsight: TextView = getView(R.id.tv_insight)
//
//        override fun set(item: MessageItem, position: Int) {
//            if (item.cover == null) {
//                Glide.with(context).load(R.drawable.img_dummy_insight).into(ivInsight)
//            } else {
//                Glide.with(context).load(item.cover).into(ivInsight)
//            }
//            tvInsight.text = item.title
//        }
//
//    }
//
//}

//ini coba mutable diubah jadi list
class InsightSliderAdapter(
    sliderItems: MutableList<RecentInsightItem>,
    onSliderItemClickListener: OnItemClickListener<RecentInsightItem>
) :
    PlutoAdapter<RecentInsightItem, InsightSliderAdapter.ViewHolder>(
        sliderItems,
        onSliderItemClickListener
    ) {
    private val insight = ArrayList<RecentInsightItem>()
    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent, R.layout.carousel_item_insight)
    }

    class ViewHolder(parent: ViewGroup, itemLayoutId: Int) :
        PlutoViewHolder<RecentInsightItem>(parent, itemLayoutId) {
        private var ivInsight: ImageView = getView(R.id.iv_insight)
        private var tvInsight: TextView = getView(R.id.tv_insight)

        override fun set(item: RecentInsightItem, position: Int) {
            if (item.cover == null) {
                Glide.with(context).load(R.drawable.img_dummy_insight).into(ivInsight)

            }
            Glide.with(context).load(item.cover).into(ivInsight)
            tvInsight.text = item.title
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: MutableList<RecentInsightItem>) {
        insight.clear()
        insight.addAll(list)
        notifyDataSetChanged()
    }


}
////tanda stash