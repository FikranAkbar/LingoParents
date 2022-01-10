package com.glints.lingoparents.ui.insight.category

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.data.model.response.AllInsightsListResponse
import com.glints.lingoparents.databinding.FragmentParentingInsightBinding
import com.glints.lingoparents.ui.insight.InsightListFragmentDirections
import com.glints.lingoparents.ui.insight.InsightListViewModel
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.NoInternetAccessOrErrorListener
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ParentingInsightFragment : Fragment(), CategoriesAdapter.OnItemClickCallback {

    private var _binding: FragmentParentingInsightBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InsightListViewModel
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var insightListAdapter: CategoriesAdapter
    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentParentingInsightBinding.inflate(inflater, container, false)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)

        binding.rvParentingInsight.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            insightListAdapter = CategoriesAdapter(this@ParentingInsightFragment)
            adapter = insightListAdapter
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProvider(
                requireActivity(),
                CustomViewModelFactory(tokenPreferences, requireActivity(), arguments)
            )[
                    InsightListViewModel::class.java
            ]

        viewModel.loadInsightList(InsightListViewModel.PARENTING_TAG)

        lifecycleScope.launchWhenStarted {
            viewModel.parentingInsightList.collect { insight ->
                when (insight) {
                    is InsightListViewModel.ParentingInsightList.Loading -> {
                        showLoading(true)
                        showEmptyWarning(false)
                    }
                    is InsightListViewModel.ParentingInsightList.Success -> {
                        insightListAdapter.submitList(insight.list)
                        showLoading(false)
                        if (insight.list.isEmpty()) {
                            showEmptyWarning(true)
                        } else {
                            showEmptyWarning(false)
                        }
                    }
                    is InsightListViewModel.ParentingInsightList.Error -> {
                        showLoading(false)
                        showEmptyWarning(true)
                        noInternetAccessOrErrorHandler.onNoInternetAccessOrError(insight.message)
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

    @Subscribe(sticky = true)
    fun onBlankKeywordSent(insight: InsightListViewModel.InsightSearchList.SendBlankKeywordToInsightListFragment) {
        viewModel.loadInsightList(InsightListViewModel.PARENTING_TAG)
    }

    @Subscribe(sticky = true)
    fun onKeywordSent(insight: InsightListViewModel.InsightSearchList.SendKeywordToInsightListFragment) {
        viewModel.getInsightSearchList(InsightListViewModel.PARENTING_TAG, insight.keyword)
    }

    private fun showLoading(b: Boolean) {
        binding.apply {
            shimmerLayout.isVisible = b
            rvParentingInsight.isVisible = !b
        }
    }

    private fun showEmptyWarning(b: Boolean) {
        binding.apply {
            rvParentingInsight.isVisible = !b
            ivNoParentingInsight.isVisible = b
            tvNoParentingInsight.isVisible = b
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            noInternetAccessOrErrorHandler = context as NoInternetAccessOrErrorListener
        } catch (e: ClassCastException) {
            println("DEBUG: $context must be implement NoInternetAccessOrErrorListener")
        }
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
        viewModel.onParentingItemClick(item.id)
    }

}