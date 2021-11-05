package com.glints.lingoparents.ui.insight

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.glints.lingoparents.databinding.FragmentInsightListBinding
import com.google.android.material.tabs.TabLayoutMediator

class InsightListFragment : Fragment() {

    private lateinit var binding: FragmentInsightListBinding
    private lateinit var viewModel: InsightListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInsightListBinding.inflate(inflater, container, false)

        binding.vpInsight.isUserInputEnabled = false

        initViews()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[InsightListViewModel::class.java]
        // TODO: Use the ViewModel
    }

    private fun initViews(){
        binding.vpInsight.adapter = ViewPagerAdapter(parentFragmentManager, lifecycle)

        val tabNames = arrayOf("All Insights", "Parenting", "Lifestyle")
        TabLayoutMediator(binding.tlInsightCategory, binding.vpInsight){tab,position ->
            tab.text = tabNames[position]
        }.attach()

    }
}