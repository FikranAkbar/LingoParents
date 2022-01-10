package com.glints.lingoparents.ui.progress.learning.assignment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.QuestionItem
import com.glints.lingoparents.databinding.FragmentAssignmentBinding
import com.glints.lingoparents.ui.progress.adapter.QuestionAdapter
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.NoInternetAccessOrErrorListener
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect

class AssignmentFragment : Fragment(R.layout.fragment_assignment) {
    private var _binding: FragmentAssignmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var questionAdapter: QuestionAdapter

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: AssignmentViewModel

    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAssignmentBinding.bind(view)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(
            this, CustomViewModelFactory(
                tokenPreferences, this,
                studentId = arguments?.get(AssignmentViewModel.STUDENT_ID_KEY) as Int,
                sessionId = arguments?.get(AssignmentViewModel.SESSION_ID_KEY) as Int
            )
        )[
                AssignmentViewModel::class.java
        ]

        viewModel.getSessionDetailBySessionId()

        binding.apply {
            ivBackButton.setOnClickListener {
                findNavController().popBackStack()
            }
            tvBack.setOnClickListener {
                findNavController().popBackStack()
            }
            rvQuestion.apply {
                setHasFixedSize(true)
                showRecyclerList()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.assignmentEvent.collect { event ->
                when (event) {
                    is AssignmentViewModel.AssignmentEvent.Loading -> {

                    }
                    is AssignmentViewModel.AssignmentEvent.Success -> {
                        val data = event.result
                        data.apply {
                            binding.apply {
                                coverFlag?.let {
                                    ivFlag.load(it)
                                }
                                tvAssignmentLevel.text =
                                    "${sessionLevel ?: "No Level"} - ${sessionSublevel ?: "No Sub Level"}"
                                tvTutorNameValue.text = tutorName ?: "No name"
                                tvSessionScheduleValue.text = sessionSchedule ?: "-"
                                tvAttendanceValue.text = attendance ?: "-"
                                tvStudentScoreValue.text = (studentScore ?: 0).toString()
                                tvAssignmentFeedbackValue.text =
                                    assignmentFeedback ?: "No assignment feedback!"
                                tvSessionFeedbackValue.text =
                                    sessionFeedback ?: "No session feedback!"
                                tvNotesValue.text = notes ?: "No notes!"
                            }
                        }
                    }
                    is AssignmentViewModel.AssignmentEvent.Error -> {
                        noInternetAccessOrErrorHandler.onNoInternetAccessOrError(event.message)
                    }
                }
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
    }

    private fun showRecyclerList() {
        binding.rvQuestion.apply {
            layoutManager =
                LinearLayoutManager(activity)
            val listQuestionAdapter = QuestionAdapter()
            adapter = listQuestionAdapter
            listQuestionAdapter.setOnItemClickCallback(object :
                QuestionAdapter.OnItemClickCallback {
                override fun onItemClicked(question: QuestionItem) {}
            })
        }
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

}