package com.glints.lingoparents.ui.insight

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.glints.lingoparents.databinding.FragmentInsightListBinding
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.tabs.TabLayoutMediator

class InsightListFragment : Fragment() {

    private lateinit var binding: FragmentInsightListBinding
    private lateinit var viewModel: InsightListViewModel
    private lateinit var tokenPreferences: TokenPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInsightListBinding.inflate(inflater, container, false)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)

        initViews()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), CustomViewModelFactory(tokenPreferences, requireActivity()))[
                InsightListViewModel::class.java
        ]
    }

    private fun initViews(){
        binding.apply {
            vpInsight.apply {
                adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
                isUserInputEnabled = false
                val tabNames = arrayOf("All Insights", "Parenting", "Lifestyle")
                TabLayoutMediator(binding.tlInsightCategory, binding.vpInsight){tab,position ->
                    tab.text = tabNames[position]
                }.attach()
            }

            tfSearchInsight.apply {
                editText?.setOnEditorActionListener { textView, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        val query = textView.text.toString()
                        if (query.isBlank()) {
                            viewModel.sendBlankKeywordToInsightListFragment()
                        } else {
                            viewModel.sendKeywordToInsightListFragment(query)
                        }

                        val imm =
                            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(textView.windowToken, 0)
                    }
                    true
                }
            }
        }
    }
}