package com.glints.lingoparents.ui.progress

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentProgressProfileBinding

class ProgressProfileFragment : Fragment(R.layout.fragment_progress_profile) {

    private var _binding: FragmentProgressProfileBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProgressProfileBinding.bind(view)
        binding.ivCharacterBadge.setOnClickListener {
            Toast.makeText(context,"clicked",Toast.LENGTH_SHORT).show()
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}