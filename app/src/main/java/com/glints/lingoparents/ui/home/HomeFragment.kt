package com.glints.lingoparents.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.InsightSliderItem
import com.glints.lingoparents.data.model.LiveEventSliderItem
import com.glints.lingoparents.data.model.response.DataItem
import com.glints.lingoparents.databinding.FragmentHomeBinding
import com.glints.lingoparents.ui.home.adapter.ChildrenAdapter
import com.glints.lingoparents.ui.home.adapter.InsightSliderAdapter
import com.glints.lingoparents.ui.home.adapter.LiveEventSliderAdapter
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.listeners.OnItemClickListener
import com.opensooq.pluto.listeners.OnSlideChangeListener
import kotlinx.coroutines.flow.collect

class HomeFragment : Fragment(R.layout.fragment_home), ChildrenAdapter.OnItemClickCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: HomeViewModel

    private lateinit var insightSliderAdapter: InsightSliderAdapter
    private lateinit var liveEventSliderAdapter: LiveEventSliderAdapter
    private lateinit var childrenAdapter: ChildrenAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel =
            ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this, arguments))[
                    HomeViewModel::class.java
            ]
        insightSliderAdapter = InsightSliderAdapter(
            mutableListOf(
                InsightSliderItem(
                    "7 Challenges of Parenting a Special Kid",
                    "@drawable/img_dummy_insight"
                ),
                InsightSliderItem(
                    "7 Challenges of Parenting a Special Kid",
                    "@drawable/img_dummy_insight"
                ),
                InsightSliderItem(
                    "7 Challenges of Parenting a Special Kid",
                    "@drawable/img_dummy_insight"
                ),
                InsightSliderItem(
                    "7 Challenges of Parenting a Special Kid",
                    "@drawable/img_dummy_insight"
                ),
                InsightSliderItem(
                    "7 Challenges of Parenting a Special Kid",
                    "@drawable/img_dummy_insight"
                ),
            ),
            object : OnItemClickListener<InsightSliderItem> {
                override fun onItemClicked(item: InsightSliderItem?, position: Int) {

                }
            }
        )

        binding.sliderInsight.apply {
            create(insightSliderAdapter, lifecycle = lifecycle)
            setOnSlideChangeListener(object : OnSlideChangeListener {
                override fun onSlideChange(adapter: PlutoAdapter<*, *>, position: Int) {

                }
            })
        }

        liveEventSliderAdapter = LiveEventSliderAdapter(
            mutableListOf(
                LiveEventSliderItem(
                    "Build Career for Gen Z",
                    "@drawable/img_dummy_live_event",
                    "999.000,00-",
                    "08 Oct 2021, 19:00"
                ),
                LiveEventSliderItem(
                    "Build Career for Gen Z",
                    "@drawable/img_dummy_live_event",
                    "999.000,00-",
                    "08 Oct 2021, 19:00"
                ),
                LiveEventSliderItem(
                    "Build Career for Gen Z",
                    "@drawable/img_dummy_live_event",
                    "999.000,00-",
                    "08 Oct 2021, 19:00"
                ),
                LiveEventSliderItem(
                    "Build Career for Gen Z",
                    "@drawable/img_dummy_live_event",
                    "999.000,00-",
                    "08 Oct 2021, 19:00"
                ),
                LiveEventSliderItem(
                    "Build Career for Gen Z",
                    "@drawable/img_dummy_live_event",
                    "999.000,00-",
                    "08 Oct 2021, 19:00"
                ),
            ),
            object : OnItemClickListener<LiveEventSliderItem> {
                override fun onItemClicked(item: LiveEventSliderItem?, position: Int) {

                }
            }
        )

        binding.sliderLiveEvent.apply {
            create(liveEventSliderAdapter, lifecycle = lifecycle)
            setOnSlideChangeListener(object : OnSlideChangeListener {
                override fun onSlideChange(adapter: PlutoAdapter<*, *>, position: Int) {

                }
            })
        }

        //MULAI AMIN
        val x = "46"
        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
            viewModel.getStudentList(x.toInt(), accessToken)
        }

        binding.apply {
            rvChildren.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                childrenAdapter = ChildrenAdapter(this@HomeFragment)
                adapter = childrenAdapter

//                childrenAdapter.submitList(
//                    listOf(
//                        ChildrenItem("", "", "", "", ""),
//                        ChildrenItem("", "", "", "", ""),
//                        ChildrenItem("", "", "", "", ""),
//                        ChildrenItem("", "", "", "", ""),
//                    )
//                )
                isVerticalScrollBarEnabled = false
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.studentList.collect { event ->
                when (event) {
                    is HomeViewModel.StudentList.Loading -> {
                        showEmptyStudentList(true)
//                        showEmptyWarning(false)
                    }
                    is HomeViewModel.StudentList.Success -> {
                        childrenAdapter.submitList(event.list)
                        showEmptyStudentList(false)
                    }
                    is HomeViewModel.StudentList.Error -> {
//                        Toast.makeText(context,"ERROR",Toast.LENGTH_LONG).show()
//                        Log.d("HAHAHA","error")

                        showEmptyStudentList(false)
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
    private fun showEmptyStudentList(bool: Boolean) {
        binding.apply {
            if (bool) {
                rvChildren.visibility = View.GONE
                ivNoStudentList.visibility = View.VISIBLE
                tvNoStudentList.visibility = View.VISIBLE
            } else {
                rvChildren.visibility = View.VISIBLE
                ivNoStudentList.visibility = View.GONE
                tvNoStudentList.visibility = View.GONE
            }
        }
    }

    override fun onItemClicked(children: DataItem) {
        Toast.makeText(context, children.studentId.toString(), Toast.LENGTH_SHORT).show()
    }


}

//class HomeFragment : Fragment(R.layout.fragment_home), ChildrenAdapter.OnItemClickCallback {
//
//    private val viewModel: HomeViewModel by viewModels()
//    private lateinit var insightSliderAdapter: InsightSliderAdapter
//    private lateinit var liveEventSliderAdapter: LiveEventSliderAdapter
//    private lateinit var childrenAdapter: ChildrenAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//
//        val binding: FragmentHomeBinding = FragmentHomeBinding.inflate(inflater)
//
//        insightSliderAdapter = InsightSliderAdapter(
//            mutableListOf(
//                InsightSliderItem("7 Challenges of Parenting a Special Kid", "@drawable/img_dummy_insight"),
//                InsightSliderItem("7 Challenges of Parenting a Special Kid", "@drawable/img_dummy_insight"),
//                InsightSliderItem("7 Challenges of Parenting a Special Kid", "@drawable/img_dummy_insight"),
//                InsightSliderItem("7 Challenges of Parenting a Special Kid", "@drawable/img_dummy_insight"),
//                InsightSliderItem("7 Challenges of Parenting a Special Kid", "@drawable/img_dummy_insight"),
//            ),
//            object : OnItemClickListener<InsightSliderItem> {
//                override fun onItemClicked(item: InsightSliderItem?, position: Int) {
//
//                }
//            }
//        )
//
//        binding.sliderInsight.apply {
//            create(insightSliderAdapter, lifecycle = lifecycle)
//            setOnSlideChangeListener(object : OnSlideChangeListener {
//                override fun onSlideChange(adapter: PlutoAdapter<*, *>, position: Int) {
//
//                }
//            })
//        }
//
//        liveEventSliderAdapter = LiveEventSliderAdapter(
//            mutableListOf(
//                LiveEventSliderItem("Build Career for Gen Z", "@drawable/img_dummy_live_event", "999.000,00-", "08 Oct 2021, 19:00"),
//                LiveEventSliderItem("Build Career for Gen Z", "@drawable/img_dummy_live_event", "999.000,00-", "08 Oct 2021, 19:00"),
//                LiveEventSliderItem("Build Career for Gen Z", "@drawable/img_dummy_live_event", "999.000,00-", "08 Oct 2021, 19:00"),
//                LiveEventSliderItem("Build Career for Gen Z", "@drawable/img_dummy_live_event", "999.000,00-", "08 Oct 2021, 19:00"),
//                LiveEventSliderItem("Build Career for Gen Z", "@drawable/img_dummy_live_event", "999.000,00-", "08 Oct 2021, 19:00"),
//            ),
//            object : OnItemClickListener<LiveEventSliderItem> {
//                override fun onItemClicked(item: LiveEventSliderItem?, position: Int) {
//
//                }
//            }
//        )
//
//        binding.sliderLiveEvent.apply {
//            create(liveEventSliderAdapter, lifecycle = lifecycle)
//            setOnSlideChangeListener(object : OnSlideChangeListener {
//                override fun onSlideChange(adapter: PlutoAdapter<*, *>, position: Int) {
//
//                }
//            })
//        }
//
//
//        binding.apply {
//            rvChildren.apply {
//                setHasFixedSize(true)
//                layoutManager = LinearLayoutManager(activity)
//                childrenAdapter = ChildrenAdapter(this@HomeFragment)
//                adapter = childrenAdapter
//
//                childrenAdapter.submitList(
//                    listOf(
//                        ChildrenItem("", "", "", "", ""),
//                        ChildrenItem("", "", "", "", ""),
//                        ChildrenItem("", "", "", "", ""),
//                        ChildrenItem("", "", "", "", ""),
//                    )
//                )
//                isVerticalScrollBarEnabled = false
//            }
//        }
//
//        return binding.root
//    }
//
//    override fun onItemClicked(children: ChildrenItem) {
//
//    }
//}