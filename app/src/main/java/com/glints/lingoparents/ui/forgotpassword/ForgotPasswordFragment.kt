package com.glints.lingoparents.ui.forgotpassword

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentForgotPasswordBinding
import com.glints.lingoparents.utils.AuthFormValidator
import com.glints.lingoparents.utils.interfaces.NoInternetAccessOrErrorListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ForgotPasswordViewModel by viewModels()

    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentForgotPasswordBinding.bind(view)

        binding.apply {
            mbtnBackToLogin.setOnClickListener {
                viewModel.onBackToLoginButtonClick()
            }
            mbtnSubmit.setOnClickListener {
                viewModel.onSubmitButtonClick(tilEmail.editText?.text.toString())
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
                when (event) {
                    is ForgotPasswordViewModel.ForgotPasswordEvent.NavigateBackToLogin -> {
                        findNavController().popBackStack()
                    }
                    is ForgotPasswordViewModel.ForgotPasswordEvent.TryToSubmitForgotPassword -> {
                        binding.apply {
                            AuthFormValidator.apply {
                                hideFieldError(tilEmail)

                                val email = event.email
                                if (isValidEmail(email)) {
                                    viewModel.sendForgotPasswordRequest(email)
                                } else {
                                    showFieldError(tilEmail, EMAIL_WRONG_FORMAT_ERROR)
                                }
                            }
                        }
                    }
                    is ForgotPasswordViewModel.ForgotPasswordEvent.Error -> {
                        showLoading(false)
                        if (event.message.contains("email not found", ignoreCase = true)) {
                            AuthFormValidator.showFieldError(binding.tilEmail, event.message)
                        } else {
                            noInternetAccessOrErrorHandler.onNoInternetAccessOrError(event.message)
                        }
                    }
                    is ForgotPasswordViewModel.ForgotPasswordEvent.Loading -> {
                        showLoading(true)
                    }
                    is ForgotPasswordViewModel.ForgotPasswordEvent.Success -> {
                        showLoading(false)
                        binding.tilEmail.editText?.setText("")
                        showSuccessSnackbar(event.message)
                    }
                }
            }
        }
    }

    private fun showSuccessSnackbar(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(resources.getColor(R.color.success_color, null))
                .setTextColor(Color.WHITE)

            val snackbarView = snackbar.view
            val snackbarTextView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)

            snackbarTextView.maxLines = 4
            snackbar.show()
        } else {
            val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.GREEN)
                .setTextColor(Color.WHITE)

            val snackbarView = snackbar.view
            val snackbarTextView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)

            snackbarTextView.maxLines = 4
            snackbar.show()
        }
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
        super.onDestroy()
        _binding = null
    }
}