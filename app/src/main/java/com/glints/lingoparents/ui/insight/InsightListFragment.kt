package com.glints.lingoparents.ui.insight

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.glints.lingoparents.databinding.FragmentInsightListBinding
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class InsightListFragment : Fragment() {

    private lateinit var binding: FragmentInsightListBinding
    private lateinit var viewModel: InsightListViewModel
    private lateinit var tokenPreferences: TokenPreferences

    private var lastSelectedTabIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentInsightListBinding.inflate(inflater, container, false)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)

        initViews()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            CustomViewModelFactory(tokenPreferences, requireActivity())
        )[
                InsightListViewModel::class.java
        ]
    }

    private fun initViews() {
        binding.apply {
            vpInsight.apply {
                adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
                isUserInputEnabled = false
                val tabNames = arrayOf("All Insights", "Parenting", "Lifestyle")
                TabLayoutMediator(binding.tlInsightCategory, binding.vpInsight) { tab, position ->
                    tab.text = tabNames[position]
                }.attach()

                repeat(tabNames.count()) {
                    currentItem = it
                }

                currentItem = 0
            }

            tfSearchInsight.apply {
                editText?.setOnEditorActionListener { textView, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        val query = textView.text.toString()
                        viewModel.sendStickyKeywordToInsightListFragment(query)

                        val imm =
                            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(textView.windowToken, 0)
                    }
                    true
                }
            }

            tlInsightCategory.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        lastSelectedTabIndex = it.position
                    }
                }
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    tab?.let {
                        lastSelectedTabIndex = it.position
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    override fun onResume() {
        super.onResume()
        binding.tlInsightCategory.getTabAt(lastSelectedTabIndex)!!.select()
    }
}