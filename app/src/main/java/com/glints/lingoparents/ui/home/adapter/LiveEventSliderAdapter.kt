package com.glints.lingoparents.ui.home.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.AllEventItem
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.base.PlutoViewHolder
import com.opensooq.pluto.listeners.OnItemClickListener

class LiveEventSliderAdapter(
    sliderItems: MutableList<AllEventItem>,
    onSliderItemClickListener: OnItemClickListener<AllEventItem>
) :
    PlutoAdapter<AllEventItem, LiveEventSliderAdapter.ViewHolder>(
        sliderItems,
        onSliderItemClickListener
    ) {
    private val event = ArrayList<AllEventItem>()
    override fun getViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(parent, R.layout.carousel_item_live_event)
    }

    class ViewHolder(parent: ViewGroup, itemLayoutId: Int) :
        PlutoViewHolder<AllEventItem>(parent, itemLayoutId) {
        private val liveEventPoster: ImageView = getView(R.id.iv_live_event_poster)
        private val liveEventTitle: TextView = getView(R.id.tv_live_event_title)
        private val liveEventPrice: TextView = getView(R.id.tv_live_event_price)
        private val liveEventDate: TextView = getView(R.id.tv_live_event_date)

        override fun set(item: AllEventItem, position: Int) {
            if (item.cover == null) {
                liveEventPoster.load(R.drawable.img_dummy_live_event)
            }
            liveEventPoster.load(item.cover)
            liveEventTitle.text = item.title
            liveEventPrice.text = item.price.toString()
            liveEventDate.text = item.date
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: MutableList<AllEventItem>) {
        event.clear()
        event.addAll(list)
        notifyDataSetChanged()
    }

}