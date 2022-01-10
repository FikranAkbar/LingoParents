package com.glints.lingoparents.ui.progress

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentProgressBinding
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.NoInternetAccessOrErrorListener
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collect


class ProgressFragment : Fragment(R.layout.fragment_progress) {
    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_progress_text_1,
            R.string.tab_progress_text_2
        )
        private const val SELECTED_SPINNER_ITEM = "selected_spinner_item"
        private const val SELECTED_CHILDREN_FROM_HOME = "children_name"
    }

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectedChildrenNameFromHome: String
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: ProgressViewModel

    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProgressBinding.bind(view)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                ProgressViewModel::class.java
        ]

        selectedChildrenNameFromHome = arguments?.getString(SELECTED_CHILDREN_FROM_HOME) ?: ""
        arguments?.clear()

        viewModel.getParentId().observe(viewLifecycleOwner) { parentId ->
            viewModel.getStudentListByParentId(parentId)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.progressEvent.collect { event ->
                when (event) {
                    is ProgressViewModel.ProgressEvent.Loading -> {
                        initBlankSpinner()
                    }
                    is ProgressViewModel.ProgressEvent.Success -> {
                        initViewPager()

                        binding.apply {
                            ivNoChildren.visibility = View.GONE
                            tvNoChildren.visibility = View.GONE
                        }

                        viewModel.makeMapFromStudentList(event.result)
                        val firstStudentId = event.result[0].student_id!!
                        viewModel.saveSelectedStudentId(firstStudentId)

                        val eventBusActionToStudentProfile =
                            ProgressViewModel.EventBusActionToStudentProfile.SendStudentId(
                                firstStudentId
                            )
                        viewModel.sendEventToProfileFragment(eventBusActionToStudentProfile)

                        val eventBusActionToStudentLearningProgress =
                            ProgressViewModel.EventBusActionToStudentLearningProgress.SendStudentId(
                                firstStudentId
                            )
                        viewModel.sendStickyEventToLearningProgressFragment(
                            eventBusActionToStudentLearningProgress
                        )
                    }
                    is ProgressViewModel.ProgressEvent.Error -> {
                        removeViewPager()

                        binding.apply {
                            ivNoChildren.visibility = View.VISIBLE
                            tvNoChildren.visibility = View.VISIBLE
                        }

                        if (event.message.lowercase() != "no data available")
                            noInternetAccessOrErrorHandler.onNoInternetAccessOrError(event.message)
                    }
                    is ProgressViewModel.ProgressEvent.NameListGenerated -> {
                        initSpinner(event.result)
                    }
                }
            }
        }
    }

    private fun initViewPager() {
        val sectionsPagerAdapter = ProgressSectionPagerAdapter(activity as AppCompatActivity)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        viewPager.isUserInputEnabled = false
        val tabs: TabLayout = binding.tlLiveEventCategory
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    private fun removeViewPager() {
        binding.viewPager.apply {
            removeAllViews()
        }
    }

    private fun initSpinner(map: Map<String, Int>) {
        val spinner = binding.spStudents
        val nameList = map.keys.toList()
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_student, nameList)
        spinner.adapter = arrayAdapter
        if (selectedChildrenNameFromHome.isNotBlank()) {
            val childrenIndex = nameList.indexOf(selectedChildrenNameFromHome)
            spinner.setSelection(childrenIndex)
            selectedChildrenNameFromHome = ""
        }
        else {
            spinner.setSelection(viewModel.lastSelectedSpinnerItem)
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position) as String
                Log.d("MAP:", map.toString())
                Log.d("SELECTEDITEM:", selectedItem)
                viewModel.lastSelectedSpinnerItem = position
                if (selectedItem != "No Students") {
                    val studentId = map[selectedItem]!!
                    viewModel.saveSelectedStudentId(studentId)

                    val eventBusActionToProfileFragment =
                        ProgressViewModel.EventBusActionToStudentProfile.SendStudentId(studentId)
                    viewModel.sendEventToProfileFragment(eventBusActionToProfileFragment)
                    val eventBusActionToLearningProgressFragment =
                        ProgressViewModel.EventBusActionToStudentLearningProgress.SendStudentId(
                            studentId
                        )
                    viewModel.sendStickyEventToLearningProgressFragment(
                        eventBusActionToLearningProgressFragment
                    )
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun initBlankSpinner() {
        val spinner = binding.spStudents
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_student, listOf("No Students"))
        spinner.adapter = arrayAdapter
        spinner.isClickable = false
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        binding.spStudents.setSelection(viewModel.lastSelectedSpinnerItem)
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
        println("Apakah hancur ?")
    }
}