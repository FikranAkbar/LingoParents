package com.glints.lingoparents.ui.progress

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.CourseItem
import com.glints.lingoparents.data.model.response.StudentListResponse
import com.glints.lingoparents.databinding.FragmentProgressBinding
import com.glints.lingoparents.ui.course.AllCoursesFragmentDirections
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collect


class ProgressFragment : Fragment(R.layout.fragment_progress) {
    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_progress_text_1,
            R.string.tab_progress_text_2
        )
    }

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: ProgressViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProgressBinding.bind(view)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                ProgressViewModel::class.java
        ]

        initViewPager()

        viewModel.getParentId().observe(viewLifecycleOwner) { parentId ->
            viewModel.getStudentListByParentId(parentId)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.progressEvent.collect { event ->
                when(event) {
                    is ProgressViewModel.ProgressEvent.Loading -> {

                    }
                    is ProgressViewModel.ProgressEvent.Success -> {
                        viewModel.makeMapFromStudentList(event.result)
                    }
                    is ProgressViewModel.ProgressEvent.Error -> {

                    }
                    is ProgressViewModel.ProgressEvent.NameListGenerated -> {
                        initSpinner(event.result)
                    }
                }
            }
        }
    }

    private fun initViewPager() {
        val sectionsPagerAdapter = ProgressSectionPagerAdapter(activity as AppCompatActivity)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        viewPager.isUserInputEnabled = false
        val tabs: TabLayout = binding.tlLiveEventCategory
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    private fun initSpinner(map: Map<String, Int>) {
        val spinner = binding.spStudents
        val nameList = map.keys.toList()
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_student, nameList)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position) as String
                Log.d("SELECTED", map[selectedItem].toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}