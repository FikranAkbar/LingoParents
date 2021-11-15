package com.glints.lingoparents.ui.insight.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.data.model.InsightCommentItem
import com.glints.lingoparents.databinding.FragmentDetailInsightBinding

class DetailInsightFragment : Fragment() {

    private lateinit var binding: FragmentDetailInsightBinding
    private lateinit var viewModel: DetailInsightViewModel
    private lateinit var commentsAdapter: CommentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailInsightBinding.inflate(inflater, container, false)

        initViews()

        binding.rvInsightComment.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            commentsAdapter = CommentsAdapter()
            adapter = commentsAdapter
            commentsAdapter.submitList(
                listOf(
                    InsightCommentItem("Camile Berger", "It's a good stuff", "@drawable/ic_user_avatar_female"),
                    InsightCommentItem("Luke Grandin", "It's a good stuff", "@drawable/ic_user_avatar_female"),
                    InsightCommentItem("Ethan Souffer", "It's a not bad stuff", "@drawable/ic_user_avatar_female")
                )
            )
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[DetailInsightViewModel::class.java]
        // TODO: Use the ViewModel
    }

    private fun initViews(){
        binding.ivBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvInsightAddComment.setOnClickListener {
            binding.tfInsightComment.visibility = View.VISIBLE
            binding.btnComment.visibility = View.VISIBLE
        }
    }
}