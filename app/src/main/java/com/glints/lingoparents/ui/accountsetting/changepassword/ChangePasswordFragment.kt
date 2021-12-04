package com.glints.lingoparents.ui.accountsetting.changepassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentChangePasswordBinding
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore

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
                viewModel.onSubmitButtonClick(
                    tfCurrentPassword.editText?.text.toString(),
                    tfNewPassword.editText?.text.toString(),
                    tfConfirmPassword.editText?.text.toString(),
                )
            }
        }
//        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
//            viewModel.changePassword()
//        }
        //viewcycle
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}