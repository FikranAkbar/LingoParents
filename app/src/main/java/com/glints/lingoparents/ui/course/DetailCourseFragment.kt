package com.glints.lingoparents.ui.course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.AllCoursesResponse
import com.glints.lingoparents.data.model.response.TrxCourseCardsItem
import com.glints.lingoparents.databinding.FragmentAllCoursesBinding
import com.glints.lingoparents.databinding.FragmentDetailCourseBinding
import com.glints.lingoparents.ui.course.adapter.CourseAdapter
import com.glints.lingoparents.ui.course.adapter.DetailCourseAdapter
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect

//class DetailCourseFragment : Fragment(R.layout.fragment_detail_course) {
//
//    private var _binding: FragmentDetailCourseBinding? = null
//    private val binding get() = _binding!!
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        _binding = FragmentDetailCourseBinding.bind(view)
//        val courseId = DetailCourseFragmentArgs.fromBundle(arguments as Bundle).id
//        binding.tvCourse.text = courseId.toString()
////        val dataCourse = DetailCourseFragmentArgs.fromBundle(arguments as Bundle).course
////        binding.tvCourse.text = dataCourse.name
////        binding.tvCourse1.text = dataCourse.desc1
////        binding.tvCourse2.text = dataCourse.desc2
////        binding.tvCourse3.text = dataCourse.desc3
////        binding.ivCourseCard1.setImageResource(dataCourse.card1)
////        binding.ivCourseCard2.setImageResource(dataCourse.card2)
////        binding.ivCourseCard3.setImageResource(dataCourse.card3)
//        binding.cvBackButton.setOnClickListener {
//            findNavController().popBackStack()
//            //findNavController().navigate(DetailCourseFragmentDirections.actionDetailCourseFragmentToAllCoursesFragment())
//        }
//        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
//            object: OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    findNavController().popBackStack()
//                }
//            })
//    }
//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }
//}

class DetailCourseFragment : Fragment(R.layout.fragment_detail_course),
    DetailCourseAdapter.OnItemClickCallback {
    private var _binding: FragmentDetailCourseBinding? = null
    private val binding get() = _binding!!

    private lateinit var detailCourseAdapter: DetailCourseAdapter
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: DetailCourseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailCourseBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
//        viewModel =
//            ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this, arguments))[
//                    AllCoursesViewModel::class.java
//            ]
        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
            viewModel.getCourseDetailById(viewModel.getCurrentCourseId(), accessToken)
        }
//        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
//            viewModel.getCourseDetailById(//idyangdioper,accessToken)
//        }

        //amin
//        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences))[
//                AllCoursesViewModel::class.java
//
//        ]

        binding.apply {
            rvDetailCourse.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                detailCourseAdapter = DetailCourseAdapter(this@DetailCourseFragment)
                adapter = detailCourseAdapter
            }
        }


        lifecycleScope.launchWhenStarted {
            viewModel.courseDetail.collect { event ->
                when (event) {
                    is DetailCourseViewModel.CourseDetail.Loading -> {
                        //showLoading(true)
                        //showEmptyWarning(false)
                    }
                    is DetailCourseViewModel.CourseDetail.Success -> {
                        detailCourseAdapter.submitList(event.result)
                        //showLoading(false)
                    }
                    is DetailCourseViewModel.CourseDetail.Error -> {
//                        showLoading(false)
//                        showEmptyWarning(true)
                    }
//                    is AllCoursesViewModel.AllCoursesEvent.NavigateToDetailCourseFragment -> {
//                        val action =
//                            AllCoursesFragmentDirections.actionAllCoursesFragmentToDetailCourseFragment(
//                                event.id
//                            )
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

    //    override fun onItemClicked(item: AllCoursesResponse.CourseItemResponse) {
//        //navigation
//        //tanda stashhhhhhhhhh
//        viewModel.courseItemClick(item.id)
//        //Toast.makeText(context, "id: ${item.id}, course: ${item.title}", Toast.LENGTH_SHORT).show()
//    }
    override fun onItemClicked(item: TrxCourseCardsItem) {
        //navigation
        //tanda stashhhhhhhhhh
        //Toast.makeText(context, "id: ${item.id}, course: ${item.title}", Toast.LENGTH_SHORT).show()
    }

//    private fun showLoading(bool: Boolean) {
//        binding.apply {
//            if (bool) {
//                rvCourse.visibility = View.GONE
//                shimmerLayout.visibility = View.VISIBLE
//            } else {
//                rvCourse.visibility = View.VISIBLE
//                shimmerLayout.visibility = View.GONE
//            }
//        }
//    }

//    private fun showEmptyWarning(bool: Boolean) {
//        binding.apply {
//            if (bool) {
//                ivNoCourse.visibility = View.VISIBLE
//                tvNoCourse.visibility = View.VISIBLE
//            } else {
//                ivNoCourse.visibility = View.GONE
//                tvNoCourse.visibility = View.GONE
//            }
//        }
//    }
}