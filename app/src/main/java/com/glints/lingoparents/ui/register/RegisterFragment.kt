package com.glints.lingoparents.ui.register

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentRegisterBinding
import com.glints.lingoparents.ui.accountsetting.profile.ProfileFragment
import com.glints.lingoparents.utils.AuthFormValidator
import kotlinx.coroutines.flow.collect

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentRegisterBinding.bind(view)

        binding.apply {
            mbtnLogin.setOnClickListener {
                viewModel.onLoginButtonClick()
            }
            mbtnSubmit.setOnClickListener {
                viewModel.onSubmitButtonClick(
                    tilFirstName.editText?.text.toString(),
                    tilLastName.editText?.text.toString(),
                    tilEmail.editText?.text.toString(),
                    tilPassword.editText?.text.toString(),
                    tilPhone.editText?.text.toString()
                )
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
                    is RegisterViewModel.RegisterEvent.Loading -> {
                        showLoading(true)
                    }
                    is RegisterViewModel.RegisterEvent.Success -> {
                        showLoading(false)
                        viewModel.onRegisterSuccessful()
                    }
                    is RegisterViewModel.RegisterEvent.Error -> {
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
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(bool: Boolean) {
        binding.apply {
            when (bool) {
                true -> {
                    vLoadingBackground.visibility = View.VISIBLE
                    vLoadingProgress.visibility = View.VISIBLE
                }
                else -> {
                    vLoadingBackground.visibility = View.GONE
                    vLoadingProgress.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}