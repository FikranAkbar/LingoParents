package com.glints.lingoparents.ui.course

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.TrxCourseCardsItem
import com.glints.lingoparents.databinding.FragmentDetailCourseBinding
import com.glints.lingoparents.ui.course.adapter.DetailCourseAdapter
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.NoInternetAccessOrErrorListener
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect

class DetailCourseFragment : Fragment(R.layout.fragment_detail_course),
    DetailCourseAdapter.OnItemClickCallback {
    private var _binding: FragmentDetailCourseBinding? = null
    private val binding get() = _binding!!

    private lateinit var detailCourseAdapter: DetailCourseAdapter
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: DetailCourseViewModel

    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailCourseBinding.inflate(inflater)
        binding.cvBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })


        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(
            this,
            CustomViewModelFactory(tokenPreferences, this, eventId = arguments?.get("id") as Int)
        )[
                DetailCourseViewModel::class.java
        ]
        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
            viewModel.getCourseDetailById(viewModel.getCurrentCourseId(), accessToken)
        }

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
                        showLoading(true)
                        showEmptyWarning(false)
                    }
                    is DetailCourseViewModel.CourseDetail.Success -> {
                        detailCourseAdapter.submitList(event.result)
                        binding.apply {
                            tvCourse.text = event.courseTitle
                        }
                        showLoading(false)
                    }
                    is DetailCourseViewModel.CourseDetail.Error -> {
                        showLoading(false)
                        showEmptyWarning(true)
                        noInternetAccessOrErrorHandler.onNoInternetAccessOrError(getString(R.string.default_error_message))
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

    override fun onItemClicked(item: TrxCourseCardsItem) {
    }

    private fun showLoading(bool: Boolean) {
        binding.apply {
            if (bool) {
                tvCourse.visibility = View.GONE
                rvDetailCourse.visibility = View.GONE
                shimmerLayout.visibility = View.VISIBLE
            } else {
                tvCourse.visibility = View.VISIBLE
                rvDetailCourse.visibility = View.VISIBLE
                shimmerLayout.visibility = View.GONE
            }
        }
    }

    private fun showEmptyWarning(bool: Boolean) {
        binding.apply {
            if (bool) {
                cvBackButton.visibility = View.INVISIBLE
                rvDetailCourse.visibility = View.INVISIBLE
                tvCourse.visibility = View.INVISIBLE
                ivNoDetailCourse.visibility = View.VISIBLE
                tvNoDetailCourse.visibility = View.VISIBLE
            } else {
                cvBackButton.visibility = View.VISIBLE
                rvDetailCourse.visibility = View.VISIBLE
                tvCourse.visibility = View.VISIBLE
                ivNoDetailCourse.visibility = View.GONE
                tvNoDetailCourse.visibility = View.GONE
            }
        }
    }
}