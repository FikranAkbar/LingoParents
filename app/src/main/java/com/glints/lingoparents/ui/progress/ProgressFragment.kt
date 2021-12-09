package com.glints.lingoparents.ui.progress

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.CourseItem
import com.glints.lingoparents.databinding.FragmentProgressBinding
import com.glints.lingoparents.ui.course.AllCoursesFragmentDirections
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProgressBinding.bind(view)

        initViewPager()
        initSpinner()
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

    private fun initSpinner() {
        val spinner = binding.spStudents
        val list = listOf(
            "Molly Potts",
            "Molly Potts Molly Potts",
            "Molly Potts Molly"
        )
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_student, list)
        spinner.adapter = arrayAdapter

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}