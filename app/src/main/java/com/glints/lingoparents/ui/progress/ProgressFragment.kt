package com.glints.lingoparents.ui.progress

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentProgressBinding


class ProgressFragment : Fragment(R.layout.fragment_progress) {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProgressBinding.bind(view)

        //val dataCourse = DetailCourseFragmentArgs.fromBundle(arguments as Bundle).course
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}