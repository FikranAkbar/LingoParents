package com.glints.lingoparents.ui.insight.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.glints.lingoparents.databinding.FragmentDetailInsightBinding

class DetailInsightFragment : Fragment() {

    private lateinit var binding: FragmentDetailInsightBinding
    private lateinit var viewModel: DetailInsightViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailInsightBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[DetailInsightViewModel::class.java]
        // TODO: Use the ViewModel
    }

    private fun initViews(){
        binding.tvInsightAddComment.setOnClickListener {
            binding.tfInsightComment.visibility = View.VISIBLE
            binding.btnComment.visibility = View.VISIBLE
        }
    }
}