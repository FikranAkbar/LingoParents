package com.glints.lingoparents.ui.course

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.CourseItem
import com.glints.lingoparents.databinding.FragmentAllCoursesBinding
import com.glints.lingoparents.ui.course.adapter.CourseAdapter

class AllCoursesFragment : Fragment(R.layout.fragment_all_courses) {
    private lateinit var rvCourse: RecyclerView
    private val list = ArrayList<CourseItem>()
    private var binding: FragmentAllCoursesBinding? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAllCoursesBinding.bind(view)
        rvCourse = binding!!.rvCourse
        rvCourse.setHasFixedSize(true)
        list.addAll(listCourse)
        showRecyclerList()
        Log.d("Coba", list.toString())
        //binding!!.iddarixml
        //binding!!.textView2.setText("hahahaha")
    }

    private val listCourse: ArrayList<CourseItem>
        get() {
            val dataName = resources.getStringArray(R.array.data_course)
            val dataDesc1 = resources.getStringArray(R.array.data_desccourse1)
            val dataDesc2 = resources.getStringArray(R.array.data_desccourse2)
            val dataDesc3 = resources.getStringArray(R.array.data_desccourse3)
            val dataCard1 = resources.obtainTypedArray(R.array.data_coursecardphoto1)
            val dataCard2 = resources.obtainTypedArray(R.array.data_coursecardphoto2)
            val dataCard3 = resources.obtainTypedArray(R.array.data_coursecardphoto3)
            val dataPhoto = resources.obtainTypedArray(R.array.data_coursephoto)
            val listCourse = ArrayList<CourseItem>()
            for (i in dataName.indices) {
                val course = CourseItem(
                    dataName[i],
                    dataDesc1[i],
                    dataDesc2[i],
                    dataDesc3[i],
                    dataCard1.getResourceId(i, -1),
                    dataCard2.getResourceId(i, -1),
                    dataCard3.getResourceId(i, -1),
                    dataPhoto.getResourceId(i, -1)
                )
                listCourse.add(course)
            }
            return listCourse
        }

    private fun showRecyclerList() {
        rvCourse.layoutManager = LinearLayoutManager(activity)
        val listCourseAdapter = CourseAdapter(list)
        rvCourse.adapter = listCourseAdapter

        listCourseAdapter.setOnItemClickCallback(object : CourseAdapter.OnItemClickCallback {
            override fun onItemClicked(course: CourseItem) {
                //Toast.makeText(context, "Kamu memilih " + course.name, Toast.LENGTH_SHORT).show()
                val toDetailCourseFragment =
                    AllCoursesFragmentDirections.actionAllCoursesFragmentToDetailCourseFragment(
                        course
                    )
                findNavController().navigate(toDetailCourseFragment)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}