package com.glints.lingoparents.ui.progress.learning

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.CourseDetailByStudentIdResponse
import com.glints.lingoparents.databinding.FragmentProgressLearningCourseBinding
import com.glints.lingoparents.ui.progress.ProgressFragmentDirections
import com.glints.lingoparents.ui.progress.adapter.SessionAdapter
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.NoInternetAccessOrErrorListener
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.flow.collect
import kotlin.math.roundToInt

class ProgressLearningCourseFragment : Fragment(R.layout.fragment_progress_learning_course) {
    private var _binding: FragmentProgressLearningCourseBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionAdapter: SessionAdapter

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: ProgressLearningCourseViewModel

    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProgressLearningCourseBinding.bind(view)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(
            this, CustomViewModelFactory(
                tokenPreferences, this,
                studentId = arguments?.getInt(ProgressLearningCourseViewModel.STUDENT_ID_KEY),
                courseId =  arguments?.getInt(ProgressLearningCourseViewModel.COURSE_ID_KEY)
            )
        )[
                ProgressLearningCourseViewModel::class.java
        ]

        viewModel.getCourseDetailByStudentId()

        showRecyclerList()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.progressLearningCourseEvent.collect { event ->
                when (event) {
                    is ProgressLearningCourseViewModel.ProgressLearningCourseEvent.Loading -> {
                        showLoading(true)
                    }
                    is ProgressLearningCourseViewModel.ProgressLearningCourseEvent.Success -> {
                        val courseDetail = event.courseDetail
                        val sessionDetail = event.lastSessionDetail
                        binding.apply {
                            tvTutorname.apply {
                                text = "${sessionDetail.tutorName}"
                                TooltipCompat.setTooltipText(this, "${sessionDetail.tutorName}")
                            }
                            expandable.apply {
                                val arrowImage =
                                    parentLayout.findViewById<ImageView>(R.id.iv_coursearrow)
                                parentLayout.apply {
                                    findViewById<TextView>(R.id.tv_courselevel).text =
                                        "${sessionDetail.sessionLevel}-${sessionDetail.sessionSublevel}"
                                    findViewById<TextView>(R.id.tv_levelvalue).text =
                                        "${courseDetail.progress!!.times(100)}%"
                                    setOnClickListener {
                                        if (!isExpanded) {
                                            expand()
                                            arrowImage.setImageDrawable(
                                                resources.getDrawable(
                                                    R.drawable.ic_baseline_keyboard_arrow_up_24,
                                                    requireContext().theme
                                                )
                                            )
                                        } else {
                                            collapse()
                                            arrowImage.setImageDrawable(
                                                resources.getDrawable(
                                                    R.drawable.ic_baseline_keyboard_arrow_down_24,
                                                    requireContext().theme
                                                )
                                            )
                                        }
                                    }
                                    secondLayout.apply {
                                        findViewById<ProgressBar>(R.id.progressBar).progress =
                                            courseDetail.progress.times(100).roundToInt()
                                        findViewById<TextView>(R.id.tv_modulevalue).text =
                                            "${courseDetail.modulesToComplete} Modules"
                                        findViewById<TextView>(R.id.tv_hoursvalue).text =
                                            "${courseDetail.hoursToComplete} Hours"
                                        findViewById<TextView>(R.id.tv_learninghoursvalue).text =
                                            "${courseDetail.learningHours} Hours"
                                    }
                                }
                            }

                            tvScore.apply {
                                text = if (courseDetail.overallScore != 0) {
                                    setTextColor(Color.BLACK)
                                    courseDetail.overallScore.toString()
                                } else {
                                    setTextColor(Color.parseColor("#C2C9D1"))
                                    requireContext().getString(R.string.no_score_text)
                                }
                            }

                            sessionAdapter.apply {
                                submitList(
                                    courseDetail.sessions!!
                                )
                                setOnItemClickCallback(object : SessionAdapter.OnItemClickCallback {
                                    override fun onItemClicked(session: CourseDetailByStudentIdResponse.SessionsItem) {
                                        val action =
                                            ProgressFragmentDirections.actionProgressFragmentToAssignmentFragment(
                                                event.studentId,
                                                session.idSession!!
                                            )
                                        Log.d("ACTION:", "$action")
                                        findNavController().navigate(action)
                                    }
                                })
                            }

                            sessionNumber.apply {
                                for (i: Int in 1..10) {
                                    addView(makeNewCellForSessionNumber(i))
                                }
                            }

                            sessionAttendance.apply {
                                for (i: Int in 0..9) {
                                    try {
                                        addView(makeNewCellForSessionAttendance(courseDetail.sessions!![i].attendance == "Yes"))
                                    } catch (e: IndexOutOfBoundsException) {
                                        addView(makeNewCellForSessionAttendance(null))
                                    }
                                }
                            }
                        }

                        showLoading(false)
                    }
                    is ProgressLearningCourseViewModel.ProgressLearningCourseEvent.SuccessWithNoSession -> {
                        val courseDetail = event.courseDetail
                        binding.apply {
                            tvTutorname.apply {
                                text = "No Tutor"
                            }
                            expandable.apply {
                                val arrowImage =
                                    parentLayout.findViewById<ImageView>(R.id.iv_coursearrow)
                                parentLayout.apply {
                                    findViewById<TextView>(R.id.tv_courselevel).text =
                                        "No Level - No Sub Level"
                                    findViewById<TextView>(R.id.tv_levelvalue).text =
                                        "${courseDetail.progress!!.times(100)}%"
                                    setOnClickListener {
                                        if (!isExpanded) {
                                            expand()
                                            arrowImage.setImageDrawable(
                                                resources.getDrawable(
                                                    R.drawable.ic_baseline_keyboard_arrow_up_24,
                                                    requireContext().theme
                                                )
                                            )
                                        } else {
                                            collapse()
                                            arrowImage.setImageDrawable(
                                                resources.getDrawable(
                                                    R.drawable.ic_baseline_keyboard_arrow_down_24,
                                                    requireContext().theme
                                                )
                                            )
                                        }
                                    }
                                    secondLayout.apply {
                                        findViewById<ProgressBar>(R.id.progressBar).progress =
                                            courseDetail.progress.times(100).roundToInt()
                                        findViewById<TextView>(R.id.tv_modulevalue).text =
                                            "${courseDetail.modulesToComplete} Modules"
                                        findViewById<TextView>(R.id.tv_hoursvalue).text =
                                            "${courseDetail.hoursToComplete} Hours"
                                        findViewById<TextView>(R.id.tv_learninghoursvalue).text =
                                            "${courseDetail.learningHours} Hours"
                                    }
                                }
                            }

                            tvScore.apply {
                                text = if (courseDetail.overallScore != 0) {
                                    setTextColor(Color.BLACK)
                                    courseDetail.overallScore.toString()
                                } else {
                                    setTextColor(Color.parseColor("#C2C9D1"))
                                    requireContext().getString(R.string.no_score_text)
                                }
                            }

                            sessionAdapter.apply {
                                submitList(
                                    courseDetail.sessions!!
                                )
                                setOnItemClickCallback(object : SessionAdapter.OnItemClickCallback {
                                    override fun onItemClicked(session: CourseDetailByStudentIdResponse.SessionsItem) {}
                                })
                            }

                            sessionNumber.apply {
                                for (i: Int in 1..10) {
                                    addView(makeNewCellForSessionNumber(i))
                                }
                            }

                            sessionAttendance.apply {
                                for (i: Int in 0..9) {
                                    try {
                                        addView(makeNewCellForSessionAttendance(courseDetail.sessions!![i].attendance == "Yes"))
                                    } catch (e: IndexOutOfBoundsException) {
                                        addView(makeNewCellForSessionAttendance(null))
                                    }
                                }
                            }
                        }

                        showLoading(false)
                    }
                    is ProgressLearningCourseViewModel.ProgressLearningCourseEvent.Error -> {
                        showLoading(false)
                        if (!event.message.lowercase().contains("not joined any class")) {
                            noInternetAccessOrErrorHandler.onNoInternetAccessOrError(event.message)
                        }
                    }
                }
            }
        }
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

    private fun showRecyclerList() {
        binding.apply {
            rvSession.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(rvSession)
            sessionAdapter = SessionAdapter()
            rvSession.adapter = sessionAdapter
        }
    }

    private fun showLoading(b: Boolean) {
        binding.apply {
            when (b) {
                true -> {
                    View.VISIBLE.apply {
                        shimmerExpandable.visibility = this
                        shimmerRvSession.visibility = this
                        shimmerScore.visibility = this
                        shimmerTvTutorname.visibility = this
                    }
                }
                false -> {
                    View.GONE.apply {
                        shimmerExpandable.visibility = this
                        shimmerRvSession.visibility = this
                        shimmerScore.visibility = this
                        shimmerTvTutorname.visibility = this
                    }
                }
            }
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

}