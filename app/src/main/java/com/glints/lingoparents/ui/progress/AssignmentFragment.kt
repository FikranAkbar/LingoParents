package com.glints.lingoparents.ui.progress

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentAssignmentBinding

class AssignmentFragment : Fragment(R.layout.fragment_assignment) {

    private var _binding: FragmentAssignmentBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAssignmentBinding.bind(view)


    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}