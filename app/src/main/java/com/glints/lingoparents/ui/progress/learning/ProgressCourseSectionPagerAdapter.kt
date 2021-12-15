package com.glints.lingoparents.ui.progress.learning

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.glints.lingoparents.data.model.response.CourseListByStudentIdResponse

class ProgressCourseSectionPagerAdapter(
    activity: AppCompatActivity,
    private val courseList: List<CourseListByStudentIdResponse.DataItem>,
    private val studentId: Int
) : FragmentStateAdapter(activity) {
    override fun getItemCount() = courseList.size

    override fun createFragment(position: Int): Fragment {
        val fragment = ProgressLearningCourseFragment()
        val bundle = Bundle().also { b ->
            b.putInt(ProgressLearningCourseViewModel.COURSE_ID_KEY, courseList[position].idCourse!!)
            b.putInt(ProgressLearningCourseViewModel.STUDENT_ID_KEY, studentId)
        }
        fragment.arguments = bundle
        return fragment
    }
}