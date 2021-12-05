package com.glints.lingoparents.ui.accountsetting.changepassword

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
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
    var passwordValue: String? = null
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
        viewModel.getAccessPassword().observe(viewLifecycleOwner) { password ->
            passwordValue = password
        }
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

                            binding.apply {
                                val currentPassword = event.currentPassword
                                val newPassword = event.newPassword
                                val confirmPassword = event.confirmPassword

//                                Log.d("test", "password datastore: ${passwordValue.toString()}")
//                                Log.d("test", "password current:$currentPassword")
//                                Log.d("test", "password baru:$newPassword")
                                if (currentPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                                    if (currentPassword.length >= 8 && newPassword.length >= 8 && confirmPassword.length >= 8) {
                                        if (newPassword.equals(confirmPassword)) {
                                            if (!(currentPassword == newPassword)) {
                                                viewModel.changePassword(
                                                    accessToken,
                                                    currentPassword,
                                                    newPassword,
                                                    confirmPassword
                                                )
                                                viewModel.savePassword(binding.tfNewPassword.editText?.text.toString())
                                                //viewModel.savePassword(event.newPassword)
                                            } else {
                                                Snackbar.make(
                                                    requireView(),
                                                    "New password must be different from the old one",
                                                    Snackbar.LENGTH_SHORT
                                                )
                                                    .setBackgroundTint(Color.parseColor("#FF0000"))
                                                    .setTextColor(Color.parseColor("#FFFFFF"))
                                                    .show()
                                            }
                                        } else {
                                            Snackbar.make(
                                                requireView(),
                                                "New password value must be same with confimation password's",
                                                Snackbar.LENGTH_SHORT
                                            )
                                                .setBackgroundTint(Color.parseColor("#FF0000"))
                                                .setTextColor(Color.parseColor("#FFFFFF"))
                                                .show()

                                        }
                                    } else {
                                        Snackbar.make(
                                            requireView(),
                                            "Minimum character for a password is 8",
                                            Snackbar.LENGTH_SHORT
                                        )
                                            .setBackgroundTint(Color.parseColor("#FF0000"))
                                            .setTextColor(Color.parseColor("#FFFFFF"))
                                            .show()

                                    }


                                } else {
                                    Snackbar.make(
                                        requireView(),
                                        "Field(s) must be filled out",
                                        Snackbar.LENGTH_SHORT
                                    )
                                        .setBackgroundTint(Color.parseColor("#FF0000"))
                                        .setTextColor(Color.parseColor("#FFFFFF"))
                                        .show()

                                }


                            }
                        }

                    }
                    is PasswordSettingViewModel.PasswordSettingEvent.Success -> {
                        Snackbar.make(
                            requireView(),
                            "Change Password Success",
                            Snackbar.LENGTH_SHORT
                        )
                            .setBackgroundTint(Color.parseColor("#42ba96"))
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .show()
                        //viewModel.savePassword(binding.tfConfirmPassword.editText?.text.toString())

                        //clearPasswordValue()

                    }
                    is PasswordSettingViewModel.PasswordSettingEvent.Error -> {
                        Snackbar.make(requireView(), "Wrong Password", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.parseColor("#FF0000"))
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


