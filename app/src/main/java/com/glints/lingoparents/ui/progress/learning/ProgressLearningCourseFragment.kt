package com.glints.lingoparents.ui.progress.learning

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import android.view.Gravity
import android.graphics.Color
import android.util.TypedValue
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.SessionItem
import com.glints.lingoparents.databinding.FragmentProgressLearningCourseBinding
import com.glints.lingoparents.ui.progress.adapter.SessionAdapter
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.card.MaterialCardView

class ProgressLearningCourseFragment : Fragment(R.layout.fragment_progress_learning_course) {
    companion object {
        private val DUMMY_SESSION_ATTENDANCE = listOf(
            true,
            false,
            true,
            false,
            true
        )
    }

    private lateinit var rvSession: RecyclerView
    private val list = ArrayList<SessionItem>()

    private var _binding: FragmentProgressLearningCourseBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: ProgressLearningCourseViewModel

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProgressLearningCourseBinding.bind(view)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                ProgressLearningCourseViewModel::class.java
        ]

        binding.apply {
            sessionNumber.apply {
                for (i: Int in 1..10) {
                    addView(makeNewCellForSessionNumber(i))
                }
            }
            sessionAttendance.apply {
                for (i: Int in 0..9) {
                    try {
                        addView(makeNewCellForSessionAttendance(DUMMY_SESSION_ATTENDANCE[i]))
                    } catch (e: IndexOutOfBoundsException) {
                        addView(makeNewCellForSessionAttendance(null))
                    }
                }
            }
            expandable.apply {
                val arrowImage = parentLayout.findViewById<ImageView>(R.id.iv_coursearrow)
                parentLayout.setOnClickListener {
                    if (!isExpanded) {
                        expand()
                        arrowImage.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24))
                    } else {
                        collapse()
                        arrowImage.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24))
                    }
                }
            }
        }

        rvSession = binding.rvSession
        rvSession.setHasFixedSize(true)
        showRecyclerList()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun makeNewCellForSessionNumber(num: Int): MaterialCardView {
        val sessionNumberCell = MaterialCardView(context)
        sessionNumberCell.apply {
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, 30f.toDips().toInt(), 1f)
            minimumWidth = 30f.toDips().toInt()
            radius = 0f
            strokeWidth = 4
            strokeColor = Color.parseColor("#C2C9D1")
            addView(makeTextView(num))
        }
        return sessionNumberCell
    }

    private fun makeNewCellForSessionAttendance(isAttend: Boolean?): MaterialCardView {
        val sessionAttendanceCell = MaterialCardView(context)
        sessionAttendanceCell.apply {
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, 30f.toDips().toInt(), 1f)
            minimumWidth = 30f.toDips().toInt()
            radius = 0f
            strokeWidth = 4
            strokeColor = Color.parseColor("#C2C9D1")
            addView(makeView(isAttend))
        }
        return sessionAttendanceCell
    }

    private fun makeTextView(num: Int): TextView {
        val sessionNumberContent = TextView(context)
        sessionNumberContent.apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            text = num.toString()
            gravity = Gravity.CENTER_VERTICAL
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            setPadding(4, 4, 4, 4)
        }
        return sessionNumberContent
    }

    private fun makeView(isAttend: Boolean?): View {
        val sessionAttendanceContent = View(context)
        sessionAttendanceContent.apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setBackgroundColor(
                when (isAttend) {
                    true -> Color.parseColor("#ceefce")
                    false -> Color.parseColor("#fbcdcd")
                    else -> Color.WHITE
                }
            )
            setPadding(4, 4, 4, 4)
        }
        return sessionAttendanceContent
    }

    private fun Float.toDips() =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, resources.displayMetrics)

    //rv
    private val listSession: ArrayList<SessionItem>
        get() {
            val dataSession = resources.getStringArray(R.array.session_name)
            val dataShortDesc = resources.getStringArray(R.array.session_shortdesc)
            val dataDesc = resources.getStringArray(R.array.session_desc)
            val dataScore = resources.getStringArray(R.array.session_score)
            val listSession = ArrayList<SessionItem>()
            for (i in dataSession.indices) {
                val session = SessionItem(
                    dataSession[i],
                    dataShortDesc[i],
                    dataDesc[i],
                    dataScore[i],
                )
                listSession.add(session)
            }
            return listSession
        }

    private fun showRecyclerList() {
        rvSession.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val listSessionAdapter = SessionAdapter(list)
        rvSession.adapter = listSessionAdapter
        listSessionAdapter.setOnItemClickCallback(object : SessionAdapter.OnItemClickCallback {
            override fun onItemClicked(session: SessionItem) {
                findNavController().navigate(R.id.action_progressLearningCourseFragment_to_assignmentFragment)
            }
        })

        list.clear()
        list.addAll(listSession)
    }
}