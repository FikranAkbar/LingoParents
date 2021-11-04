package com.glints.lingoparents.ui.insight.category

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.glints.lingoparents.R

class AllInsightFragment : Fragment() {

    companion object {
        fun newInstance() = AllInsightFragment()
    }

    private lateinit var viewModel: AllInsightViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_insight, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AllInsightViewModel::class.java)
        // TODO: Use the ViewModel
    }

}