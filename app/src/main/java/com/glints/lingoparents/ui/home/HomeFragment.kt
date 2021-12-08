package com.glints.lingoparents.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.LiveEventSliderItem
import com.glints.lingoparents.data.model.response.AllEventItem
import com.glints.lingoparents.data.model.response.DataItem
import com.glints.lingoparents.data.model.response.RecentInsightItem
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
    var parentIdValue = -1
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: HomeViewModel

    private lateinit var liveEventSliderAdapter: LiveEventSliderAdapter
    private lateinit var childrenAdapter: ChildrenAdapter
    private lateinit var insightSliderAdapter: InsightSliderAdapter

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

        val recentInsightPlaceholder = mutableListOf(
            RecentInsightItem(
                "title",
                "content",
                "cover",
                1,


                ),
        )
        insightSliderAdapter = InsightSliderAdapter(
            recentInsightPlaceholder,
            object : OnItemClickListener<RecentInsightItem> {
                override fun onItemClicked(item: RecentInsightItem?, position: Int) {

                }
            }
        )
        val allEventPlaceholder = mutableListOf(
            AllEventItem("date", "title", "cover","speaker photo", 50000, -1)
        )
        liveEventSliderAdapter = LiveEventSliderAdapter(
            allEventPlaceholder,
            object : OnItemClickListener<AllEventItem> {
                override fun onItemClicked(item: AllEventItem?, position: Int) {

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

        viewModel.getAccessParentId().observe(viewLifecycleOwner) { parentId ->
            parentIdValue = parentId
        }

        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
            viewModel.getRecentInsight(HomeViewModel.INSIGHT_TYPE)
            viewModel.getAllEvent(HomeViewModel.EVENT_TYPE)
//            viewModel.getStudentList("he", 4)
            viewModel.getStudentList(HomeViewModel.STUDENTLIST_TYPE, 3)
        }


        binding.apply {
            rvChildren.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                childrenAdapter = ChildrenAdapter(this@HomeFragment)
                adapter = childrenAdapter
                isVerticalScrollBarEnabled = false
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.studentList.collect { event ->
                when (event) {
                    is HomeViewModel.StudentList.Loading -> {
                        showEmptyStudentList(true)
                    }
                    is HomeViewModel.StudentList.Success -> {
                        Log.d("studn", event.list.toString())
                        //Toast.makeText(context, "hahahahahaha", Toast.LENGTH_LONG).show()
                        childrenAdapter.submitList(event.list)
                        showEmptyStudentList(false)
                    }
                    is HomeViewModel.StudentList.Error -> {
                        showEmptyStudentList(false)
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
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.allEvent.collect { event ->
                when (event) {
                    is HomeViewModel.AllEvent.Loading -> {
                        //showEmptyStudentList(true)
                        showEmptyInsight(true)
                    }
                    is HomeViewModel.AllEvent.Success -> {
                        //Log.d("dfdf", event.list.toString())
                        Toast.makeText(context, "INI BISA NIHHHHHH", Toast.LENGTH_SHORT).show()
                        //childrenAdapter.submitList(event.list)
                        //showEmptyInsight(false)
                        liveEventSliderAdapter.submitList(event.list)
                        liveEventSliderAdapter = LiveEventSliderAdapter(
                            event.list,
                            object : OnItemClickListener<AllEventItem> {
                                override fun onItemClicked(item: AllEventItem?, position: Int) {

                                }
                            }
                        )
                        binding.sliderLiveEvent.apply {
                            create(liveEventSliderAdapter, lifecycle = lifecycle)
                            setOnSlideChangeListener(object : OnSlideChangeListener {
                                override fun onSlideChange(
                                    adapter: PlutoAdapter<*, *>,
                                    position: Int
                                ) {

                                }
                            })
                        }
                        //showEmptyStudentList(false)
                    }
                    is HomeViewModel.AllEvent.Error -> {
                        //showEmptyStudentList(false)
                    }

                }

            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.recentInsight.collect { event ->
                when (event) {
                    is HomeViewModel.RecentInsight.Loading -> {
                        //showEmptyStudentList(true)
                        showEmptyInsight(true)
                    }
                    is HomeViewModel.RecentInsight.Success -> {
                        Log.d("dfdf", event.list.toString())
                        //Toast.makeText(context, "INI BISA NIHHHHHH", Toast.LENGTH_SHORT).show()
                        //childrenAdapter.submitList(event.list)
                        showEmptyInsight(false)
                        insightSliderAdapter.submitList(event.list)
                        insightSliderAdapter = InsightSliderAdapter(
                            event.list,
                            object : OnItemClickListener<RecentInsightItem> {
                                override fun onItemClicked(
                                    item: RecentInsightItem?,
                                    position: Int
                                ) {

                                }
                            }
                        )
                        binding.sliderInsight.apply {
                            create(insightSliderAdapter, lifecycle = lifecycle)
                            setOnSlideChangeListener(object : OnSlideChangeListener {
                                override fun onSlideChange(
                                    adapter: PlutoAdapter<*, *>,
                                    position: Int
                                ) {

                                }
                            })
                        }
                        //showEmptyStudentList(false)
                    }
                    is HomeViewModel.RecentInsight.Error -> {
                        //showEmptyStudentList(false)
                    }

                }

            }

        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showEmptyInsight(bool: Boolean) {
        binding.apply {
            if (bool) {
                sliderInsight.visibility = View.GONE
            } else {
                sliderInsight.visibility = View.VISIBLE
            }
        }
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