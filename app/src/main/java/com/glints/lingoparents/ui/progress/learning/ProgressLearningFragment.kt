package com.glints.lingoparents.ui.progress.learning

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentProgressLearningBinding
import com.glints.lingoparents.ui.progress.ProgressViewModel
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

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

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: ProgressLearningViewModel

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProgressLearningBinding.bind(view)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                ProgressLearningViewModel::class.java
        ]

        initViewPager()
    }

    private fun initViewPager() {
        val tabIcons = arrayOf(R.drawable.ic_small_korea_flag, R.drawable.ic_small_germany_flag)
        val sectionsPagerAdapter = ProgressCourseSectionPagerAdapter(activity as AppCompatActivity)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.isUserInputEnabled = false
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tlCourseList
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        tabs.getTabAt(0)!!.setIcon(tabIcons[0])
        tabs.getTabAt(1)!!.setIcon(tabIcons[1])
    }

    @Subscribe(sticky = true)
    fun collectedEventStudentId(event: ProgressViewModel.EventBusActionToStudentLearningProgress.SendStudentId) {
        viewModel.getCourseListByStudentId(event.studentId)
        EventBus.getDefault().removeStickyEvent(event)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}