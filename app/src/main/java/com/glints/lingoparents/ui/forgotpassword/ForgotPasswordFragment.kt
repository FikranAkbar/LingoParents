package com.glints.lingoparents.ui.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentForgotPasswordBinding
import kotlinx.coroutines.flow.collect

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentForgotPasswordBinding.bind(view)

        binding.apply {
            mbtnBackToLogin.setOnClickListener {
                viewModel.onBackToLoginButtonClick()
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )

        lifecycleScope.launchWhenStarted {
            viewModel.forgotPasswordEvent.collect { event ->
                when(event) {
                    is ForgotPasswordViewModel.ForgotPasswordEvent.NavigateBackToLogin -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}