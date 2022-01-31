package com.glints.lingoparents.ui.resetpassword

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentResetPasswordBinding
import com.glints.lingoparents.utils.AuthFormValidator
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResetPasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentResetPasswordBinding.bind(view)

        binding.apply {
            mbtnBackToLogin.setOnClickListener {
                viewModel.onBackToLoginButtonClick()
            }
            mbtnSubmit.setOnClickListener {
                viewModel.onSubmitButtonClick(
                    tilPassword.editText?.text.toString(),
                    tilConfirmPassword.editText?.text.toString()
                )
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val action = ResetPasswordFragmentDirections.actionGlobalLoginFragment()
                    findNavController().navigate(action)
                }
            })

        lifecycleScope.launchWhenStarted {
            viewModel.resetPasswordEvent.collect { event ->
                when (event) {
                    is ResetPasswordViewModel.ResetPasswordEvent.Loading -> {
                        showLoading(true)
                    }
                    is ResetPasswordViewModel.ResetPasswordEvent.Success -> {
                        showLoading(false)
                        showSuccessSnackbar(event.message)
                    }
                    is ResetPasswordViewModel.ResetPasswordEvent.Error -> {
                        showLoading(false)
                        if (event.message.lowercase().contains("invalid request")) {
                            showErrorSnackbar("Reset password token was expired or has been used")
                        } else {
                            showErrorSnackbar(event.message)
                        }
                    }
                    is ResetPasswordViewModel.ResetPasswordEvent.NavigateToLogin -> {
                        val action = ResetPasswordFragmentDirections.actionGlobalLoginFragment()
                        findNavController().navigate(action)
                    }
                    is ResetPasswordViewModel.ResetPasswordEvent.TryToSubmitResetPassword -> {
                        binding.apply {
                            AuthFormValidator.apply {
                                hideFieldError(arrayListOf(tilPassword, tilConfirmPassword))

                                val newPassword = event.newPassword
                                val confirmNewPassword = event.confirmNewPassword
                                if (isValidPassword(newPassword) &&
                                    isValidPassword(confirmNewPassword) &&
                                    newPassword == confirmNewPassword
                                ) {
                                    val token = arguments?.get("token") as String
                                    val id = arguments?.get("id") as String
                                    viewModel.resetPassword(
                                        token,
                                        id,
                                        newPassword,
                                        confirmNewPassword
                                    )
                                } else {
                                    if (!isValidPassword(newPassword)) {
                                        showFieldError(tilPassword, PASSWORD_EMPTY_ERROR)
                                    }
                                    if (!isValidPassword(confirmNewPassword)) {
                                        showFieldError(tilConfirmPassword, PASSWORD_EMPTY_ERROR)
                                    }
                                    if (newPassword != confirmNewPassword) {
                                        showFieldError(tilConfirmPassword,
                                            PASSWORD_DIFFERENCE_ERROR)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        val token = arguments?.get("token") as String
        val id = arguments?.get("id") as String
        Log.d("TEST", "$token $id")
    }

    private fun showLoading(bool: Boolean) {
        binding.apply {
            vLoadingBackground.isVisible = bool
            vLoadingProgress.isVisible = bool
        }
    }

    private fun showErrorSnackbar(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Snackbar.make(binding.root,
                message,
                Snackbar.LENGTH_SHORT)
                .setBackgroundTint(resources.getColor(R.color.error_color, null))
                .setTextColor(Color.WHITE)
                .show()
        } else {
            Snackbar.make(binding.root,
                message,
                Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.RED)
                .setTextColor(Color.WHITE)
                .show()
        }
    }

    private fun showSuccessSnackbar(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(resources.getColor(R.color.success_color, null))
                .setTextColor(Color.WHITE)
                .show()
        } else {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.GREEN)
                .setTextColor(Color.WHITE)
                .show()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}