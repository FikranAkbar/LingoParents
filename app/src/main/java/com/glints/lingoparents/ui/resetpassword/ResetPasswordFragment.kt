package com.glints.lingoparents.ui.resetpassword

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentResetPasswordBinding
import com.glints.lingoparents.utils.AuthFormValidator
import com.glints.lingoparents.utils.NoInternetAccessOrErrorListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResetPasswordViewModel by viewModels()

    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

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
                        Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
                    }
                    is ResetPasswordViewModel.ResetPasswordEvent.Error -> {
                        showLoading(false)
                        noInternetAccessOrErrorHandler.onNoInternetAccessOrError(getString(R.string.default_error_message))
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            noInternetAccessOrErrorHandler = context as NoInternetAccessOrErrorListener
        } catch (e: ClassCastException) {
            println("DEBUG: $context must be implement NoInternetAccessOrErrorListener")
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}