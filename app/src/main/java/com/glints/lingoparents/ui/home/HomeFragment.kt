package com.glints.lingoparents.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.ChildrenItem
import com.glints.lingoparents.data.model.InsightSliderItem
import com.glints.lingoparents.data.model.LiveEventSliderItem
import com.glints.lingoparents.databinding.FragmentHomeBinding
import com.glints.lingoparents.ui.home.adapter.ChildrenAdapter
import com.glints.lingoparents.ui.home.adapter.InsightSliderAdapter
import com.glints.lingoparents.ui.home.adapter.LiveEventSliderAdapter
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.listeners.OnItemClickListener
import com.opensooq.pluto.listeners.OnSlideChangeListener

class HomeFragment : Fragment(R.layout.fragment_home), ChildrenAdapter.OnItemClickCallback {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var insightSliderAdapter: InsightSliderAdapter
    private lateinit var liveEventSliderAdapter: LiveEventSliderAdapter
    private lateinit var childrenAdapter: ChildrenAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentHomeBinding = FragmentHomeBinding.inflate(inflater)

        insightSliderAdapter = InsightSliderAdapter(
            mutableListOf(
                InsightSliderItem("7 Challenges of Parenting a Special Kid", "@drawable/img_dummy_insight"),
                InsightSliderItem("7 Challenges of Parenting a Special Kid", "@drawable/img_dummy_insight"),
                InsightSliderItem("7 Challenges of Parenting a Special Kid", "@drawable/img_dummy_insight"),
                InsightSliderItem("7 Challenges of Parenting a Special Kid", "@drawable/img_dummy_insight"),
                InsightSliderItem("7 Challenges of Parenting a Special Kid", "@drawable/img_dummy_insight"),
            ),
            object : OnItemClickListener<InsightSliderItem> {
                override fun onItemClicked(item: InsightSliderItem?, position: Int) {

                }
            }
        )

        binding.sliderInsight.apply {
            create(insightSliderAdapter, lifecycle = lifecycle)
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
            setOnSlideChangeListener(object : OnSlideChangeListener {
                override fun onSlideChange(adapter: PlutoAdapter<*, *>, position: Int) {

                }
            })
        }


        binding.apply {
            rvChildren.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                childrenAdapter = ChildrenAdapter(this@HomeFragment)
                adapter = childrenAdapter

                childrenAdapter.submitList(
                    listOf(
                        ChildrenItem("", "", "", "", ""),
                        ChildrenItem("", "", "", "", ""),
                        ChildrenItem("", "", "", "", ""),
                        ChildrenItem("", "", "", "", ""),
                    )
                )
            }
        }

        return binding.root
    }

    override fun onItemClicked(children: ChildrenItem) {

    }
}