package com.glints.lingoparents.ui.register

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentRegisterBinding
import com.glints.lingoparents.utils.AuthFormValidator
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: RegisterViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentRegisterBinding.bind(view)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(
            tokenPreferences, this,
            googleIdToken = arguments?.getString(RegisterViewModel.GOOGLE_ID_TOKEN_KEY)
        ))[
                RegisterViewModel::class.java
        ]

        binding.apply {
            mbtnLogin.setOnClickListener {
                viewModel.onLoginButtonClick()
            }
            mbtnSubmit.setOnClickListener {
                viewModel.onSubmitButtonClick(
                    firstName = tilFirstName.editText?.text.toString(),
                    lastName = tilLastName.editText?.text.toString(),
                    email = tilEmail.editText?.text.toString(),
                    password = tilPassword.editText?.text.toString(),
                    phone = tilPhone.editText?.text.toString()
                )
            }
            tilFirstName.editText?.setText(viewModel.googleFirstName)
            tilLastName.editText?.setText(viewModel.googleLastName)
            tilEmail.editText?.setText(viewModel.googleEmail)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.registerEvent.collect { event ->
                when (event) {
                    is RegisterViewModel.RegisterEvent.NavigateBackToLogin -> {
                        findNavController().popBackStack()
                    }
                    is RegisterViewModel.RegisterEvent.NavigateBackWithResult -> {
                        setFragmentResult(
                            "register_user",
                            bundleOf("register_user" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is RegisterViewModel.RegisterEvent.TryToRegisterUser -> {
                        handleOnTryRegisterUser(event)
                    }
                    is RegisterViewModel.RegisterEvent.Loading -> {
                        showLoading(true)
                    }
                    is RegisterViewModel.RegisterEvent.Success -> {
                        showLoading(false)
                        viewModel.onRegisterSuccessful()
                    }
                    is RegisterViewModel.RegisterEvent.Error -> {
                        handleOnErrorRegisterUser(event)
                    }
                    is RegisterViewModel.RegisterEvent.LoginError -> {

                    }
                    RegisterViewModel.RegisterEvent.LoginSuccess -> {

                    }
                }
            }
        }
    }

    private fun handleOnTryRegisterUser(event: RegisterViewModel.RegisterEvent.TryToRegisterUser) {
        binding.apply {
            AuthFormValidator.apply {
                hideFieldError(
                    arrayListOf(
                        tilEmail,
                        tilFirstName,
                        tilLastName,
                        tilPassword,
                        tilPhone
                    )
                )

                val firstName = event.firstName
                val lastName = event.lastName
                val email = event.email
                val password = event.password
                val phone = event.phone

                if (isValidEmail(email) &&
                    isValidPassword(password) &&
                    isValidField(firstName) &&
                    isValidField(lastName) &&
                    isValidField(phone)
                ) {
                    viewModel.registerUser(
                        email,
                        password,
                        firstName,
                        lastName,
                        phone
                    )
                } else {
                    if (!isValidEmail(email)) {
                        showFieldError(tilEmail, EMAIL_WRONG_FORMAT_ERROR)
                    }
                    if (!isValidPassword(password)) {
                        showFieldError(tilPassword, PASSWORD_EMPTY_ERROR)
                    }
                    if (!isValidField(firstName)) {
                        showFieldError(tilFirstName, EMPTY_FIELD_ERROR)
                    }
                    if (!isValidField(lastName)) {
                        showFieldError(tilLastName, EMPTY_FIELD_ERROR)
                    }
                    if (!isValidField(phone)) {
                        showFieldError(tilPhone, EMPTY_FIELD_ERROR)
                    }
                }
            }
        }
    }

    private fun handleOnErrorRegisterUser(event: RegisterViewModel.RegisterEvent.Error) {
        showLoading(false)
        event.message.let {
            when {
                it.contains("firstname", false) -> {
                    AuthFormValidator.showFieldError(binding.tilFirstName, it)
                }
                it.contains("lastname", false) -> {
                    AuthFormValidator.showFieldError(binding.tilLastName, it)
                }
                it.contains("email", false) -> {
                    AuthFormValidator.showFieldError(binding.tilEmail, it)
                }
                it.contains("phone", false) -> {
                    AuthFormValidator.showFieldError(binding.tilPhone, it)
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(resources.getColor(R.color.error_color, null))
                            .setTextColor(Color.WHITE)
                            .show()
                    } else {
                        Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.RED)
                            .setTextColor(Color.WHITE)
                            .show()
                    }
                }
            }
        }
    }

    private fun showLoading(bool: Boolean) {
        binding.apply {
            vLoadingBackground.isVisible = bool
            vLoadingProgress.isVisible = bool
            mbtnSubmit.isClickable = !bool
            mbtnLoginWithGoogle.isClickable = !bool
            mbtnLogin.isClickable = !bool
            tilFirstName.isEnabled = !bool
            tilLastName.isEnabled = !bool
            tilEmail.isEnabled = !bool
            tilPassword.isEnabled = !bool
            tilPhone.isEnabled = !bool
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}