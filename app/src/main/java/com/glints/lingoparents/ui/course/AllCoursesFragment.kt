package com.glints.lingoparents.ui.course

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.AllCoursesResponse
import com.glints.lingoparents.databinding.FragmentAllCoursesBinding
import com.glints.lingoparents.ui.course.adapter.CourseAdapter
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.interfaces.NoInternetAccessOrErrorListener
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect

class AllCoursesFragment : Fragment(R.layout.fragment_all_courses),
    CourseAdapter.OnItemClickCallback {
    private var _binding: FragmentAllCoursesBinding? = null
    private val binding get() = _binding!!

    private lateinit var courseAdapter: CourseAdapter
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: AllCoursesViewModel

    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAllCoursesBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel =
            ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this, arguments))[
                    AllCoursesViewModel::class.java
            ]

        binding.apply {
            rvCourse.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                courseAdapter = CourseAdapter(this@AllCoursesFragment)
                adapter = courseAdapter
            }
        }

        viewModel.getAllCourses()

        lifecycleScope.launchWhenStarted {
            viewModel.allCoursesEvent.collect { event ->
                when (event) {
                    is AllCoursesViewModel.AllCoursesEvent.Loading -> {
                        showLoading(true)
                        showEmptyWarning(false)
                    }
                    is AllCoursesViewModel.AllCoursesEvent.Success -> {
                        courseAdapter.submitList(event.list)
                        showLoading(false)
                    }
                    is AllCoursesViewModel.AllCoursesEvent.Error -> {
                        showLoading(false)
                        showEmptyWarning(true)
                        noInternetAccessOrErrorHandler.onNoInternetAccessOrError(event.message)
                    }
                    is AllCoursesViewModel.AllCoursesEvent.NavigateToDetailCourseFragment -> {
                        val action =
                            AllCoursesFragmentDirections.actionAllCoursesFragmentToDetailCourseFragment(
                                event.id
                            )
                        Log.d("IDEvent", event.id.toString())
                        findNavController().navigate(action)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            noInternetAccessOrErrorHandler = context as NoInternetAccessOrErrorListener
        } catch (e: ClassCastException) {
            println("DEBUG: $context must be implement NoInternetAccessOrErrorListener")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClicked(item: AllCoursesResponse.CourseItemResponse) {
        viewModel.courseItemClick(item.id)
    }

    private fun showLoading(bool: Boolean) {
        binding.apply {
            rvCourse.isVisible = !bool
            shimmerLayout.isVisible = bool
        }
    }

    private fun showEmptyWarning(bool: Boolean) {
        binding.apply {
            ivNoCourse.isVisible = bool
            tvNoCourse.isVisible = bool
        }
    }
}