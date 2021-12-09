package com.glints.lingoparents.ui.progress.learning.assignment

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.QuestionItem
import com.glints.lingoparents.databinding.FragmentAssignmentBinding
import com.glints.lingoparents.ui.progress.adapter.QuestionAdapter

class AssignmentFragment : Fragment(R.layout.fragment_assignment) {
    private lateinit var rvQuestion: RecyclerView
    private val list = ArrayList<QuestionItem>()


    private var _binding: FragmentAssignmentBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAssignmentBinding.bind(view)
        binding.ivBackButton.setOnClickListener {
            findNavController().popBackStack()
            //findNavController().navigate(DetailCourseFragmentDirections.actionDetailCourseFragmentToAllCoursesFragment())
        }
        binding.tvBack.setOnClickListener {
            findNavController().popBackStack()
            //findNavController().navigate(DetailCourseFragmentDirections.actionDetailCourseFragmentToAllCoursesFragment())
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
        binding.tvTutorName.setText(
            Html.fromHtml(
                "<b>Tutor Name: </b>" + "“Eleanor Pena”"
            )
        )
        binding.tvSessionSchedule.setText(
            Html.fromHtml(
                "<b>Session Schedule: </b>" + "23/07/2021 04:00 PM"
            )
        )
        binding.tvAttendance.setText(
            Html.fromHtml(
                "<b>Attendance: </b>" + "Yes"
            )
        )
        binding.tvStudentScore.setText(
            Html.fromHtml(
                "<b>Student Score: </b>" + "90"
            )
        )
        binding.tvAssignmentFeedback.setText(
            Html.fromHtml(
                "<b>Assignment Feedback: </b>" + "“Student need deeply learn about time and date”"
            )
        )
        binding.tvSessionFeedback.setText(
            Html.fromHtml(
                "<b>Session Feedback: </b>" + "“Student active and on time attending the class”"
            )
        )
        binding.tvNotes.setText(
            Html.fromHtml(
                "<b>Notes: </b>" + "“Student active and on time attending the class”"
            )
        )

        rvQuestion = binding.rvQuestion
        rvQuestion.setHasFixedSize(true)
        showRecyclerList()

    }

    //rv
    private val listQuestion: ArrayList<QuestionItem>
        get() {
            val dataQuestionNumber = resources.getStringArray(R.array.question_number)
            val dataStudentAnswer = resources.getStringArray(R.array.question_studentanswer)
            val dataCorrectAnswer = resources.getStringArray(R.array.question_correctanswer)
            val listQuestion = ArrayList<QuestionItem>()
            for (i in dataQuestionNumber.indices) {
                val question = QuestionItem(
                    dataQuestionNumber[i],
                    dataStudentAnswer[i],
                    dataCorrectAnswer[i],
                )
                listQuestion.add(question)
            }
            return listQuestion
        }

    private fun showRecyclerList() {
        rvQuestion.layoutManager =
            LinearLayoutManager(activity)
        val listQuestionAdapter = QuestionAdapter(list)
        rvQuestion.adapter = listQuestionAdapter
        listQuestionAdapter.setOnItemClickCallback(object : QuestionAdapter.OnItemClickCallback {
            override fun onItemClicked(question: QuestionItem) {
//                Toast.makeText(context, "Kamu memilih " + question.questionNumber, Toast.LENGTH_SHORT)
//                    .show()
            }
        })

        list.clear()
        list.addAll(listQuestion)
        listQuestionAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}