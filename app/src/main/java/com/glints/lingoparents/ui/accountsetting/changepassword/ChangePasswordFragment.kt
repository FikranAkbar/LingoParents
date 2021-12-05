package com.glints.lingoparents.ui.accountsetting.changepassword

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentChangePasswordBinding
import com.glints.lingoparents.ui.MainActivity
import com.glints.lingoparents.ui.accountsetting.profile.ProfileViewModel
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: PasswordSettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                PasswordSettingViewModel::class.java
        ]
        //amin
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
                findNavController().navigate(R.id.accountSettingFragment)

            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.passwordSettingEvent.collect { event ->
                when (event) {
                    //amin
                    is PasswordSettingViewModel.PasswordSettingEvent.TryToChangePassword -> {
                        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
//                            binding.apply {
//                                val currentPassword = event.currentPassword
//                                val newPassword = event.newPassword
//                                val confirmPassword = event.confirmPassword
//                                if (currentPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
//                                    if (currentPassword.length >= 6 && newPassword.length >= 6 && confirmPassword.length >= 6) {
//                                        if (newPassword.equals(confirmPassword)) {
//                                            viewModel.changePassword(
//                                                accessToken,
//                                                currentPassword,
//                                                newPassword,
//                                                confirmPassword
//                                            )
//                                        } else {
//                                            Toast.makeText(context, "password ga sama", Toast.LENGTH_SHORT).show()
//
//                                        }
//
//                                    }
//
//                                } else {
//                                    Toast.makeText(context, "d", Toast.LENGTH_SHORT).show()
//                                }
//                            }
                            viewModel.changePassword(
                                accessToken,
                                event.currentPassword,
                                event.newPassword,
                                event.confirmPassword
                            )
                        }

                    }
                    is PasswordSettingViewModel.PasswordSettingEvent.Success -> {
                        clearPasswordValue()
                        findNavController().navigate(R.id.accountSettingFragment)
                        Snackbar.make(
                            requireView(),
                            "Change Password Success",
                            Snackbar.LENGTH_LONG
                        )
                            .setBackgroundTint(Color.parseColor("#42ba96"))
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .show()


                    }
                    is PasswordSettingViewModel.PasswordSettingEvent.Error -> {
                        Snackbar.make(requireView(), "Error", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.parseColor("#FF9494"))
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .show()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


