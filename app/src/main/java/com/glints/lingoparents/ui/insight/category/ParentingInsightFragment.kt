package com.glints.lingoparents.ui.insight.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.data.model.InsightSliderItem
import com.glints.lingoparents.databinding.FragmentParentingInsightBinding
import com.glints.lingoparents.ui.insight.InsightListFragmentDirections

class ParentingInsightFragment : Fragment(), CategoriesAdapter.OnItemClickCallback {

    private lateinit var binding: FragmentParentingInsightBinding
    private val viewModel: CategoriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParentingInsightBinding.inflate(inflater, container, false)

        binding.rvParentingInsight.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)

            adapter = CategoriesAdapter(
                this@ParentingInsightFragment,
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
//        viewModel = ViewModelProvider(this).get(ParentingInsightViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onItemClicked(insightSliderItem: InsightSliderItem) {
        val action = InsightListFragmentDirections.actionInsightListFragmentToDetailInsightFragment()
        findNavController().navigate(action)
    }

}