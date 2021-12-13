package com.glints.lingoparents.ui.insight.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.data.model.response.AllInsightsListResponse
import com.glints.lingoparents.databinding.FragmentLifestyleInsightBinding
import com.glints.lingoparents.ui.insight.InsightListFragmentDirections
import com.glints.lingoparents.ui.insight.InsightListViewModel
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LifestyleInsightFragment : Fragment(), CategoriesAdapter.OnItemClickCallback {

    private var _binding: FragmentLifestyleInsightBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InsightListViewModel
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var insightListAdapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLifestyleInsightBinding.inflate(inflater, container, false)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)

        binding.rvLifestyleInsight.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            insightListAdapter = CategoriesAdapter(this@LifestyleInsightFragment)
            adapter = insightListAdapter
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProvider(requireParentFragment(), CustomViewModelFactory(tokenPreferences, requireParentFragment(), arguments))[
                    InsightListViewModel::class.java
            ]

        viewModel.loadInsightList(InsightListViewModel.LIFESTYLE_TAG)

        lifecycleScope.launchWhenStarted {
            viewModel.lifestyleInsightList.collect { insight ->
                when (insight) {
                    is InsightListViewModel.LifestyleInsightList.Loading -> {
                        showLoading(true)
                        showEmptyWarning(false)
                    }
                    is InsightListViewModel.LifestyleInsightList.Success -> {
                        insightListAdapter.submitList(insight.list)
                        showLoading(false)
                        if (insight.list.isEmpty()) {
                            showEmptyWarning(true)
                        } else {
                            showEmptyWarning(false)
                        }
                    }
                    is InsightListViewModel.LifestyleInsightList.Error -> {
                        showLoading(false)
                        showEmptyWarning(true)
                    }
                    is InsightListViewModel.LifestyleInsightList.NavigateToDetailInsightFragment -> {
                        val action = InsightListFragmentDirections
                            .actionInsightListFragmentToDetailInsightFragment(insight.id)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    @Subscribe
    fun onBlankKeywordSent(){
        viewModel.loadInsightList(InsightListViewModel.LIFESTYLE_TAG)
    }

    @Subscribe
    fun onKeywordSent(insight: InsightListViewModel.InsightSearchList.SendKeywordToInsightListFragment){
        viewModel.getInsightSearchList(InsightListViewModel.LIFESTYLE_TAG, insight.keyword)
    }

    private fun showLoading(b: Boolean) {
        binding.apply {
            if (b) {
                rvLifestyleInsight.visibility = View.GONE
                shimmerLayout.visibility = View.VISIBLE
            } else {
                rvLifestyleInsight.visibility = View.VISIBLE
                shimmerLayout.visibility = View.GONE
            }
        }
    }

    private fun showEmptyWarning(b: Boolean) {
        binding.apply {
            if (b) {
                rvLifestyleInsight.visibility = View.GONE
                ivNoLifestyleInsight.visibility = View.VISIBLE
                tvNoLifestyleInsight.visibility = View.VISIBLE
            } else {
                ivNoLifestyleInsight.visibility = View.GONE
                tvNoLifestyleInsight.visibility = View.GONE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClicked(item: AllInsightsListResponse.Message) {
        viewModel.onLifestyleInsightItemClick(item.id)
    }

}