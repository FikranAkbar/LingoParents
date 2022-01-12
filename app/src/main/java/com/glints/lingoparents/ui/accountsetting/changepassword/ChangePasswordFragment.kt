package com.glints.lingoparents.ui.accountsetting.changepassword

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentChangePasswordBinding
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.NoInternetAccessOrErrorListener
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: PasswordSettingViewModel

    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                PasswordSettingViewModel::class.java
        ]
        binding.apply {
            mbtnSave.setOnClickListener {
                viewModel.onSaveButtonClick(
                    tfCurrentPassword.editText?.text.toString(),
                    tfNewPassword.editText?.text.toString(),
                    tfConfirmPassword.editText?.text.toString(),
                )

            }
            mbtnCancel.setOnClickListener {
                clearPasswordValue()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.passwordSettingEvent.collect { event ->
                when (event) {

                    is PasswordSettingViewModel.PasswordSettingEvent.TryToChangePassword -> {


                        binding.apply {
                            val currentPassword = event.currentPassword
                            val newPassword = event.newPassword
                            val confirmPassword = event.confirmPassword

                            if (currentPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                                if (currentPassword.length >= 8 && newPassword.length >= 8 && confirmPassword.length >= 8) {
                                    if (newPassword == confirmPassword) {
                                        if (currentPassword != newPassword) {
                                            viewModel.changePassword(
                                                currentPassword,
                                                newPassword,
                                                confirmPassword
                                            )
                                            //viewModel.savePassword(binding.tfNewPassword.editText?.text.toString())
                                            //viewModel.savePassword(event.newPassword)
                                        } else {
                                            Snackbar.make(
                                                requireView(),
                                                "New password value must be different with current password value",
                                                Snackbar.LENGTH_SHORT
                                            )
                                                .setBackgroundTint(Color.parseColor("#F03738"))
                                                .setTextColor(Color.parseColor("#FFFFFF"))
                                                .show()
                                        }
                                    } else {
                                        Snackbar.make(
                                            requireView(),
                                            "New password value must be same with confimation password's",
                                            Snackbar.LENGTH_SHORT
                                        )
                                            .setBackgroundTint(Color.parseColor("#F03738"))
                                            .setTextColor(Color.parseColor("#FFFFFF"))
                                            .show()

                                    }
                                } else {
                                    Snackbar.make(
                                        requireView(),
                                        "Minimum character for a password is 8",
                                        Snackbar.LENGTH_SHORT
                                    )
                                        .setBackgroundTint(Color.parseColor("#F03738"))
                                        .setTextColor(Color.parseColor("#FFFFFF"))
                                        .show()

                                }


                            } else {
                                Snackbar.make(
                                    requireView(),
                                    "Field(s) must be filled out",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .setBackgroundTint(Color.parseColor("#F03738"))
                                    .setTextColor(Color.parseColor("#FFFFFF"))
                                    .show()

                            }


                        }


                    }
                    is PasswordSettingViewModel.PasswordSettingEvent.Success -> {
                        Snackbar.make(
                            requireView(),
                            "Change Password Success",
                            Snackbar.LENGTH_LONG
                        )
                            .setBackgroundTint(Color.parseColor("#42ba96"))
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .show()

                        clearPasswordValue()

                    }
                    is PasswordSettingViewModel.PasswordSettingEvent.Error -> {
                        noInternetAccessOrErrorHandler.onNoInternetAccessOrError(event.message)
                    }
                    PasswordSettingViewModel.PasswordSettingEvent.Loading -> {

                    }
                }
            }
        }
        return binding.root
    }

    private fun clearPasswordValue() {
        binding.apply {
            tfCurrentPassword.editText?.setText("")
            tfNewPassword.editText?.setText("")
            tfConfirmPassword.editText?.setText("")
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


