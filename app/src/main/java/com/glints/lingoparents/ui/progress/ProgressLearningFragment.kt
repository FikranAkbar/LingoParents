package com.glints.lingoparents.ui.progress

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentProgressLearningBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ProgressLearningFragment : Fragment(R.layout.fragment_progress_learning) {
    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_progress_course_list_text_1,
            R.string.tab_progress_course_list_text_2
        )
    }

    private var _binding: FragmentProgressLearningBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProgressLearningBinding.bind(view)
        //viewpager
        val sectionsPagerAdapter = ProgressCourseSectionPagerAdapter(activity as AppCompatActivity)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.isUserInputEnabled = false
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tlCourseList
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}