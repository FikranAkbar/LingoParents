package com.glints.lingoparents.ui.insight.category

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.ActivityDashboardBinding.inflate
import com.glints.lingoparents.databinding.FragmentAllInsightBinding
import com.glints.lingoparents.databinding.FragmentLifestyleInsightBinding

class LifestyleInsightFragment : Fragment() {

    private lateinit var binding: FragmentLifestyleInsightBinding
    private val viewModel: CategoriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLifestyleInsightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(LifestyleInsightViewModel::class.java)
        // TODO: Use the ViewModel
    }

}