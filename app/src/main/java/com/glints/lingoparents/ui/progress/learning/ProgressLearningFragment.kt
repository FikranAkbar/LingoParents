package com.glints.lingoparents.ui.progress.learning

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.CourseListByStudentIdResponse
import com.glints.lingoparents.databinding.FragmentProgressLearningBinding
import com.glints.lingoparents.ui.progress.ProgressViewModel
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collect
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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.progressLearningEvent.collect { event ->
                when(event) {
                    is ProgressLearningViewModel.ProgressLearningEvent.Loading -> {

                    }
                    is ProgressLearningViewModel.ProgressLearningEvent.Success -> {
                        initViewPager(event.result)
                    }
                    is ProgressLearningViewModel.ProgressLearningEvent.Error -> {

                    }
                }
            }
        }
    }

    private fun initViewPager(courseList: List<CourseListByStudentIdResponse.DataItem>) {
        val sectionsPagerAdapter = ProgressCourseSectionPagerAdapter(activity as AppCompatActivity)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.isUserInputEnabled = false
        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = binding.tlCourseList

        if (courseList.isNotEmpty()) {
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.customView = createTabItemView(courseList[position], position)
            }.attach()
        }

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.view?.apply {
                    findViewById<TextView>(R.id.tv_flag)
                        ?.setTextColor(Color.BLACK)
                    findViewById<ImageView>(R.id.iv_flag)
                        ?.clearColorFilter()
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.apply {
                    findViewById<TextView>(R.id.tv_flag)
                        ?.setTextColor(Color.parseColor("#B8B8BA"))
                    findViewById<ImageView>(R.id.iv_flag)
                        ?.setColorFilter(Color.parseColor("#B8B8BA"), PorterDuff.Mode.ADD)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    @SuppressLint("InflateParams")
    private fun createTabItemView(courseData: CourseListByStudentIdResponse.DataItem, position: Int): View {
        val tabItem = LayoutInflater.from(requireContext()).inflate(R.layout.item_tab_course, null, false) as ConstraintLayout
        val tabLabel = tabItem.findViewById<TextView>(R.id.tv_flag)
        val tabFlag = tabItem.findViewById<ImageView>(R.id.iv_flag)

        tabLabel.text = courseData.title
        tabFlag.load(courseData.flag)

        if (position != 0) {
            tabLabel.setTextColor(Color.parseColor("#B8B8BA"))
            tabFlag.setColorFilter(Color.parseColor("#B8B8BA"), PorterDuff.Mode.ADD)
        }

        return tabItem
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