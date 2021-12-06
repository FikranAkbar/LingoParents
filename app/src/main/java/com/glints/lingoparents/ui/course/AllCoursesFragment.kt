package com.glints.lingoparents.ui.course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.CourseItem
import com.glints.lingoparents.data.model.response.AllCoursesResponse
import com.glints.lingoparents.databinding.FragmentAllCoursesBinding
import com.glints.lingoparents.databinding.FragmentTodayEventBinding
import com.glints.lingoparents.ui.course.adapter.CourseAdapter
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect

//blm connect api
//class AllCoursesFragment : Fragment(R.layout.fragment_all_courses) {
//    private lateinit var rvCourse: RecyclerView
//    private val list = ArrayList<CourseItem>()
//    private var _binding: FragmentAllCoursesBinding? = null
//    private val binding get() = _binding!!
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        _binding = FragmentAllCoursesBinding.bind(view)
//        rvCourse = binding!!.rvCourse
//        rvCourse.setHasFixedSize(true)
////        list.clear()
////        list.addAll(listCourse)
//        showRecyclerList()
//        Log.d("Coba", list.toString())
//        //binding!!.iddarixml
//        //binding!!.textView2.setText("hahahaha")
//    }
//
//    private val listCourse: ArrayList<CourseItem>
//        get() {
//            val dataName = resources.getStringArray(R.array.data_course)
//            val dataDesc1 = resources.getStringArray(R.array.data_desccourse1)
//            val dataDesc2 = resources.getStringArray(R.array.data_desccourse2)
//            val dataDesc3 = resources.getStringArray(R.array.data_desccourse3)
//            val dataCard1 = resources.obtainTypedArray(R.array.data_coursecardphoto1)
//            val dataCard2 = resources.obtainTypedArray(R.array.data_coursecardphoto2)
//            val dataCard3 = resources.obtainTypedArray(R.array.data_coursecardphoto3)
//            val dataPhoto = resources.obtainTypedArray(R.array.data_coursephoto)
//            val listCourse = ArrayList<CourseItem>()
//            for (i in dataName.indices) {
//                val course = CourseItem(
//                    dataName[i],
//                    dataDesc1[i],
//                    dataDesc2[i],
//                    dataDesc3[i],
//                    dataCard1.getResourceId(i, -1),
//                    dataCard2.getResourceId(i, -1),
//                    dataCard3.getResourceId(i, -1),
//                    dataPhoto.getResourceId(i, -1)
//                )
//                listCourse.add(course)
//            }
//            return listCourse
//        }
//
//    private fun showRecyclerList() {
//        rvCourse.layoutManager = LinearLayoutManager(activity)
//        val listCourseAdapter = CourseAdapter(list)
//        rvCourse.adapter = listCourseAdapter
//
//        listCourseAdapter.setOnItemClickCallback(object : CourseAdapter.OnItemClickCallback {
//            override fun onItemClicked(course: CourseItem) {
//                //Toast.makeText(context, "Kamu memilih " + course.name, Toast.LENGTH_SHORT).show()
//                goToDetail(course)
//            }
//        })
//        list.clear()
//        list.addAll(listCourse)
//        listCourseAdapter.notifyDataSetChanged()
//    }
//
//    private fun goToDetail(course: CourseItem) {
//        val toDetailCourseFragment =
//            AllCoursesFragmentDirections.actionAllCoursesFragmentToDetailCourseFragment(
//                course
//            )
//        findNavController().navigate(toDetailCourseFragment)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }
//}

//ini yang kalo udah connect pi
class AllCoursesFragment : Fragment(R.layout.fragment_all_courses),
    CourseAdapter.OnItemClickCallback {
    private var _binding: FragmentAllCoursesBinding? = null
    private val binding get() = _binding!!

    private lateinit var courseAdapter: CourseAdapter
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: AllCoursesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllCoursesBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel =
            ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this, arguments))[
                    AllCoursesViewModel::class.java
            ]
        //amin
//        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences))[
//                AllCoursesViewModel::class.java
//
//        ]

        binding.apply {
            rvCourse.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                courseAdapter = CourseAdapter(this@AllCoursesFragment)
                adapter = courseAdapter
            }
        }

        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
            viewModel.getAllCourses(accessToken)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.allCoursesEvent.collect { event ->
                when (event) {
                    is AllCoursesViewModel.AllCoursesEvent.Loading -> {
                        showLoading(true)
//                        showEmptyWarning(false)
                    }
                    is AllCoursesViewModel.AllCoursesEvent.Success -> {
                        courseAdapter.submitList(event.list)
                        showLoading(false)
                    }
                    is AllCoursesViewModel.AllCoursesEvent.Error -> {
                        showLoading(false)
//                        showEmptyWarning(true)
                    }
//                    is AllCoursesViewModel.AllCoursesEvent.NavigateToDetailLiveEventFragment -> {
//                        val action =
//                            LiveEventListFragmentDirections.actionLiveEventListFragmentToLiveEventDetailFragment(event.id)
//                        Log.d("IDEvent", event.id.toString())
//                        findNavController().navigate(action)
//                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClicked(item: AllCoursesResponse.CourseItemResponse) {
//        viewModel.onTodayLiveEventItemClick(item.id)
//        Log.d("IDEvent", item.id.toString())
        Toast.makeText(context, "id: ${item.id}, course: ${item.title}", Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(bool: Boolean) {
        binding.apply {
            if (bool) {
                rvCourse.visibility = View.GONE
//                shimmerLayout.visibility = View.VISIBLE
            } else {
                rvCourse.visibility = View.VISIBLE
//                shimmerLayout.visibility = View.GONE
            }
        }
    }

//    private fun showEmptyWarning(bool: Boolean) {
//        binding.apply {
//            if (bool) {
//                ivNoEvent.visibility = View.VISIBLE
//                tvNoEvent.visibility = View.VISIBLE
//            } else {
//                ivNoEvent.visibility = View.GONE
//                tvNoEvent.visibility = View.GONE
//            }
//        }
//    }
}