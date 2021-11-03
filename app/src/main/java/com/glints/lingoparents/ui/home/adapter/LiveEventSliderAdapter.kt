package com.glints.lingoparents.ui.home.adapter

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.LiveEventSliderItem
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.base.PlutoViewHolder
import com.opensooq.pluto.listeners.OnItemClickListener

class LiveEventSliderAdapter(
    sliderItems: MutableList<LiveEventSliderItem>,
    onSliderItemClickListener: OnItemClickListener<LiveEventSliderItem>
) :
    PlutoAdapter<LiveEventSliderItem, LiveEventSliderAdapter.ViewHolder>(
        sliderItems,
        onSliderItemClickListener
    ) {
    override fun getViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(parent, R.layout.carousel_item_live_event)
    }

    class ViewHolder(parent: ViewGroup, itemLayoutId: Int) :
        PlutoViewHolder<LiveEventSliderItem>(parent, itemLayoutId) {
        private val liveEventPoster: ImageView = getView(R.id.iv_live_event_poster)
        private val liveEventTitle: TextView = getView(R.id.tv_live_event_title)
        private val liveEventPrice: TextView = getView(R.id.tv_live_event_price)
        private val liveEventDate: TextView = getView(R.id.tv_live_event_date)

        override fun set(item: LiveEventSliderItem, position: Int) {
            Glide.with(context).load(R.drawable.img_dummy_live_event).into(liveEventPoster)
            liveEventTitle.text = item.title
            liveEventPrice.text = item.price
            liveEventDate.text = item.date
        }

    }

}