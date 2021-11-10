package com.glints.lingoparents.ui.course

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentCourseBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CourseFragment : Fragment(R.layout.fragment_course) {
    private var binding: FragmentCourseBinding? = null
    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCourseBinding.bind(view)
        val sectionsPagerAdapter = CourseSectionsPagerAdapter(requireActivity() as AppCompatActivity)
        val viewPager: ViewPager2 = binding!!.vpCourse
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding!!.tabsCourse
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }
}