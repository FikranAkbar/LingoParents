package com.glints.lingoparents.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.Insight
import com.glints.lingoparents.databinding.FragmentHomeBinding
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.listeners.OnItemClickListener
import com.opensooq.pluto.listeners.OnSlideChangeListener

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.bind(view)
        val pluto = binding.sliderInsight
        val adapter = InsightSliderAdapter(
            mutableListOf(
                Insight("TEST", "@drawable/img_dummy_insight"),
                Insight("TEST", "@drawable/img_dummy_insight"),
                Insight("TEST", "@drawable/img_dummy_insight"),
                Insight("TEST", "@drawable/img_dummy_insight"),
                Insight("TEST", "@drawable/img_dummy_insight"),
            ),
            object : OnItemClickListener<Insight> {
                override fun onItemClicked(item: Insight?, position: Int) {

                }
            }
        )

        pluto.create(adapter, lifecycle = lifecycle)
        pluto.setOnSlideChangeListener(object : OnSlideChangeListener {
            override fun onSlideChange(adapter: PlutoAdapter<*, *>, position: Int) {

            }
        })
    }
}