package com.glints.lingoparents.ui.register

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentRegisterBinding.bind(view)

        binding.apply {
            mbtnLogin.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}