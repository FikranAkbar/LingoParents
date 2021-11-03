package com.glints.lingoparents.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.InsightSliderItem
import com.glints.lingoparents.data.model.LiveEventSliderItem
import com.glints.lingoparents.databinding.FragmentHomeBinding
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.listeners.OnItemClickListener
import com.opensooq.pluto.listeners.OnSlideChangeListener

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var insightSliderAdapter: InsightSliderAdapter
    private lateinit var liveEventSliderAdapter: LiveEventSliderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.bind(view)
        insightSliderAdapter = InsightSliderAdapter(
            mutableListOf(
                InsightSliderItem("TEST", "@drawable/img_dummy_insight"),
                InsightSliderItem("TEST", "@drawable/img_dummy_insight"),
                InsightSliderItem("TEST", "@drawable/img_dummy_insight"),
                InsightSliderItem("TEST", "@drawable/img_dummy_insight"),
                InsightSliderItem("TEST", "@drawable/img_dummy_insight"),
            ),
            object : OnItemClickListener<InsightSliderItem> {
                override fun onItemClicked(item: InsightSliderItem?, position: Int) {

                }
            }
        )

        binding.sliderInsight.apply {
            create(insightSliderAdapter, lifecycle = lifecycle)
            setCustomIndicator(binding.sliderInsightIndicator)
            setOnSlideChangeListener(object : OnSlideChangeListener {
                override fun onSlideChange(adapter: PlutoAdapter<*, *>, position: Int) {

                }
            })
        }

        liveEventSliderAdapter = LiveEventSliderAdapter(
            mutableListOf(
                LiveEventSliderItem("Build Career for Gen Z", "@drawable/img_dummy_live_event", "999.000,00-", "08 Oct 2021, 19:00"),
                LiveEventSliderItem("Build Career for Gen Z", "@drawable/img_dummy_live_event", "999.000,00-", "08 Oct 2021, 19:00"),
                LiveEventSliderItem("Build Career for Gen Z", "@drawable/img_dummy_live_event", "999.000,00-", "08 Oct 2021, 19:00"),
                LiveEventSliderItem("Build Career for Gen Z", "@drawable/img_dummy_live_event", "999.000,00-", "08 Oct 2021, 19:00"),
                LiveEventSliderItem("Build Career for Gen Z", "@drawable/img_dummy_live_event", "999.000,00-", "08 Oct 2021, 19:00"),
            ),
            object : OnItemClickListener<LiveEventSliderItem> {
                override fun onItemClicked(item: LiveEventSliderItem?, position: Int) {

                }
            }
        )

        binding.sliderLiveEvent.apply {
            create(liveEventSliderAdapter, lifecycle = lifecycle)
            setCustomIndicator(binding.sliderLiveEventIndicator)
            setOnSlideChangeListener(object : OnSlideChangeListener {
                override fun onSlideChange(adapter: PlutoAdapter<*, *>, position: Int) {

                }
            })
        }
    }
}