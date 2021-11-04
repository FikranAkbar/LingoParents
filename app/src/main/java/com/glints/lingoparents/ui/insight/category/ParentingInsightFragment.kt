package com.glints.lingoparents.ui.insight.category

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.glints.lingoparents.R

class ParentingInsightFragment : Fragment() {

    companion object {
        fun newInstance() = ParentingInsightFragment()
    }

    private lateinit var viewModel: ParentingInsightViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_parenting_insight, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ParentingInsightViewModel::class.java)
        // TODO: Use the ViewModel
    }

}