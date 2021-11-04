package com.glints.lingoparents.ui.insight

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.glints.lingoparents.databinding.FragmentInsightListBinding

class InsightListFragment : Fragment() {

    private lateinit var binding: FragmentInsightListBinding
    private lateinit var viewModel: InsightListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInsightListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[
                InsightListViewModel::class.java]

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InsightListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}