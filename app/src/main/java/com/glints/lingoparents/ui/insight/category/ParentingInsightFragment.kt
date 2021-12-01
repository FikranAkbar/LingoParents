package com.glints.lingoparents.ui.insight.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.data.model.response.AllInsightsListResponse
import com.glints.lingoparents.databinding.FragmentParentingInsightBinding
import com.glints.lingoparents.ui.insight.InsightListFragmentDirections
import com.glints.lingoparents.ui.insight.InsightListViewModel
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect

class ParentingInsightFragment : Fragment(), CategoriesAdapter.OnItemClickCallback {

    private var _binding: FragmentParentingInsightBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InsightListViewModel
    private lateinit var tokenPreferences: TokenPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParentingInsightBinding.inflate(inflater, container, false)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)

        binding.rvParentingInsight.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = CategoriesAdapter(this@ParentingInsightFragment)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this, arguments))[
                    InsightListViewModel::class.java
            ]

        viewModel.getAccessToken().observe(viewLifecycleOwner){ accessToken ->
            viewModel.loadInsightList(InsightListViewModel.PARENTING_TAG, accessToken)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.parentingInsightList.collect { insight ->
                when(insight){
                    is InsightListViewModel.ParentingInsightList.Loading -> {
                        showLoading(true)
                        showEmptyWarning(false)
                    }
                    is InsightListViewModel.ParentingInsightList.Success -> {
                        CategoriesAdapter(this@ParentingInsightFragment).submitList(insight.list)
                        showEmptyWarning(false)
                    }
                    is InsightListViewModel.ParentingInsightList.Error -> {
                        showLoading(false)
                        showEmptyWarning(true)
                    }
                    is InsightListViewModel.ParentingInsightList.NavigateToDetailInsightFragment -> {
                        val action = InsightListFragmentDirections
                            .actionInsightListFragmentToDetailInsightFragment(insight.id)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun showLoading(b: Boolean) {
        binding.apply {
            if (b) {
                rvParentingInsight.visibility = View.GONE
            } else {
                rvParentingInsight.visibility = View.VISIBLE
            }
        }
    }

    private fun  showEmptyWarning(b: Boolean){

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClicked(item: AllInsightsListResponse.Message) {
        val action = InsightListFragmentDirections.actionInsightListFragmentToDetailInsightFragment()
        findNavController().navigate(action)
    }

}