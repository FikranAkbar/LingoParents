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
import com.glints.lingoparents.databinding.FragmentAllInsightBinding
import com.glints.lingoparents.ui.insight.InsightListFragmentDirections
import com.glints.lingoparents.ui.insight.InsightListViewModel
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class AllInsightFragment : Fragment(), CategoriesAdapter.OnItemClickCallback {

    private var _binding: FragmentAllInsightBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InsightListViewModel
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var insightListAdapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllInsightBinding.inflate(inflater, container, false)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)

        binding.rvAllInsight.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            insightListAdapter = CategoriesAdapter(this@AllInsightFragment)
            adapter = insightListAdapter
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProvider(requireActivity(), CustomViewModelFactory(tokenPreferences, requireActivity(), arguments))[
                    InsightListViewModel::class.java
            ]

        viewModel.loadInsightList(InsightListViewModel.ALL_TAG)

        lifecycleScope.launchWhenStarted {
            viewModel.allInsightList.collect { insight ->
                when (insight) {
                    is InsightListViewModel.AllInsightList.Loading -> {
                        showLoading(true)
                        showEmptyWarning(false)
                    }
                    is InsightListViewModel.AllInsightList.Success -> {
                        insightListAdapter.submitList(insight.list)
                        showLoading(false)
                        if (insight.list.isEmpty()) {
                            showEmptyWarning(true)
                        } else {
                            showEmptyWarning(false)
                        }
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

    @Subscribe
    fun onBlankKeywordSent(insight: InsightListViewModel.InsightSearchList.SendBlankKeywordToInsightListFragment){
        viewModel.loadInsightList(InsightListViewModel.ALL_TAG)
        EventBus.getDefault().removeStickyEvent(insight)
    }

    @Subscribe
    fun onKeywordSent(insight: InsightListViewModel.InsightSearchList.SendKeywordToInsightListFragment){
        viewModel.getInsightSearchList(InsightListViewModel.ALL_TAG, insight.keyword)
        EventBus.getDefault().removeStickyEvent(insight)
    }

    private fun showLoading(b: Boolean) {
        binding.apply {
            if (b) {
                rvAllInsight.visibility = View.GONE
                shimmerLayout.visibility = View.VISIBLE
            } else {
                rvAllInsight.visibility = View.VISIBLE
                shimmerLayout.visibility = View.GONE
            }
        }
    }

    private fun showEmptyWarning(b: Boolean) {
        binding.apply {
            if (b) {
                rvAllInsight.visibility = View.GONE
                ivNoInsight.visibility = View.VISIBLE
                tvNoInsight.visibility = View.VISIBLE
            } else {
                ivNoInsight.visibility = View.GONE
                tvNoInsight.visibility = View.GONE
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
        viewModel.onAllInsightItemClick(item.id)
    }

}