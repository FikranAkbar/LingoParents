package com.glints.lingoparents.ui.resetpassword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentResetPasswordBinding

class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResetPasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentResetPasswordBinding.bind(view)

        binding.apply {
            mbtnBackToLogin.setOnClickListener {

            }
            mbtnSubmit.setOnClickListener {

            }
        }
    }
}