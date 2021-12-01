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
import com.glints.lingoparents.databinding.FragmentAllInsightBinding
import com.glints.lingoparents.ui.insight.InsightListFragmentDirections
import com.glints.lingoparents.ui.insight.InsightListViewModel
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect

class AllInsightFragment : Fragment(), CategoriesAdapter.OnItemClickCallback {

    private var _binding: FragmentAllInsightBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InsightListViewModel
    private lateinit var tokenPreferences: TokenPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllInsightBinding.inflate(inflater, container, false)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)

        binding.rvAllInsight.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = CategoriesAdapter(this@AllInsightFragment)
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
            viewModel.loadInsightList(InsightListViewModel.ALL_TAG, accessToken)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.allInsightList.collect { insight ->
                when(insight){
                    is InsightListViewModel.AllInsightList.Loading -> {
                        showLoading(true)
                        showEmptyWarning(false)
                    }
                    is InsightListViewModel.AllInsightList.Success -> {
                        CategoriesAdapter(this@AllInsightFragment).submitList(insight.list)
                        showEmptyWarning(false)
                    }
                    is InsightListViewModel.AllInsightList.Error -> {
                        showLoading(false)
                        showEmptyWarning(true)
                    }
                    is InsightListViewModel.AllInsightList.NavigateToDetailInsightFragment -> {
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
                rvAllInsight.visibility = View.GONE
            } else {
                rvAllInsight.visibility = View.VISIBLE
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
        viewModel.onAllInsightItemClick(item.id)
    }

}