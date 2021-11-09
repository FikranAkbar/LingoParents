package com.glints.lingoparents.ui.insight.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.glints.lingoparents.R

class DetailInsightFragment : Fragment() {

    companion object {
        fun newInstance() = DetailInsightFragment()
    }

    private lateinit var viewModel: DetailInsightViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.detail_insight_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DetailInsightViewModel::class.java)
        // TODO: Use the ViewModel
    }

}