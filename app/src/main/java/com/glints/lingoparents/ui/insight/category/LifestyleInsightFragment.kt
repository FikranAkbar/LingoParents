package com.glints.lingoparents.ui.insight.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.data.model.InsightSliderItem
import com.glints.lingoparents.databinding.FragmentLifestyleInsightBinding
import com.glints.lingoparents.ui.insight.InsightListFragmentDirections
import com.glints.lingoparents.ui.insight.InsightListViewModel
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore

class LifestyleInsightFragment : Fragment(), CategoriesAdapter.OnItemClickCallback {

    private var _binding: FragmentLifestyleInsightBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InsightListViewModel
    private lateinit var tokenPreferences: TokenPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLifestyleInsightBinding.inflate(inflater, container, false)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)

        binding.rvLifestyleInsight.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)

            adapter = CategoriesAdapter(
                this@LifestyleInsightFragment,
                mutableListOf(
                    InsightSliderItem("", ""),
                    InsightSliderItem("", ""),
                    InsightSliderItem("", ""),
                    InsightSliderItem("", ""),
                    InsightSliderItem("", ""),
                    InsightSliderItem("", "")
                )
            )
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this, arguments))[
                InsightListViewModel::class.java
        ]
        // TODO: Use the ViewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClicked(insightSliderItem: InsightSliderItem) {
        val action = InsightListFragmentDirections.actionInsightListFragmentToDetailInsightFragment()
        findNavController().navigate(action)
    }

}