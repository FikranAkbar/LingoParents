package com.glints.lingoparents.ui.liveevent

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentLiveEventListBinding
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.tabs.TabLayoutMediator

class LiveEventListFragment : Fragment(R.layout.fragment_live_event_list) {

    private lateinit var binding: FragmentLiveEventListBinding
    private lateinit var viewModel: LiveEventListViewModel
    private lateinit var tokenPreferences: TokenPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLiveEventListBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(
            requireActivity(),
            CustomViewModelFactory(tokenPreferences, requireActivity())
        )[
                LiveEventListViewModel::class.java
        ]

        binding.apply {
            vpLiveEvent.apply {
                adapter = LiveEventViewPagerAdapter(requireActivity() as AppCompatActivity)
                isUserInputEnabled = false
                val tabNames = arrayOf("Live", "Upcoming", "Completed")
                TabLayoutMediator(tlLiveEventCategory, this) { tab, position ->
                    tab.text = tabNames[position]
                }.attach()
            }
            tfSearch.apply {
                editText?.setOnEditorActionListener { textView, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        val query = textView.text.toString()
                        if (query.isBlank()) {
                            viewModel.sendBlackQueryToLiveEventListFragment()
                        } else {
                            viewModel.sendQueryToLiveEventListFragment(query)
                        }

                        val imm =
                            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(textView.windowToken, 0)
                    }
                    true
                }
            }
        }

        return binding.root
    }
}