package com.glints.lingoparents.ui.accountsetting.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentProfileBinding
import com.glints.lingoparents.ui.MainActivity
import com.glints.lingoparents.ui.accountsetting.AccountSettingViewModel
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: AccountSettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                AccountSettingViewModel::class.java
        ]

        binding.apply {
            mbtnEdit.setOnClickListener {
                enterEditState()
            }
            mbtnCancel.setOnClickListener {
                exitEditState()
            }
            mbtnSave.setOnClickListener {
                exitEditState()
            }
            mbtnLogout.setOnClickListener {
                viewModel.onLogOutButtonClick()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.profileEvent.collect { event ->
                when (event) {
                    AccountSettingViewModel.ProfileEvent.NavigateToAuthScreen -> {
                        val intent =
                            Intent(this@ProfileFragment.requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
            }
        }

        return binding.root
    }

    private fun enterEditState() {
        binding.apply {
            View.VISIBLE.apply {
                tfAddress.visibility = this
                tfFullName.visibility = this
                tfPhoneNumber.visibility = this
                mbtnSave.visibility = this
                mbtnCancel.visibility = this
            }
            View.INVISIBLE.apply {
                tvAddressContent.visibility = this
                tvFullNameContent.visibility = this
                tvPhoneNumberContent.visibility = this
                mbtnLogout.visibility = this
                mbtnEdit.visibility = this
            }
        }
    }

    private fun exitEditState() {
        binding.apply {
            View.VISIBLE.apply {
                tvAddressContent.visibility = this
                tvFullNameContent.visibility = this
                tvPhoneNumberContent.visibility = this
                mbtnLogout.visibility = this
                mbtnEdit.visibility = this
            }
            View.INVISIBLE.apply {
                tfAddress.visibility = this
                tfAddress.editText?.setText("")
                tfFullName.visibility = this
                tfFullName.editText?.setText("")
                tfPhoneNumber.visibility = this
                tfPhoneNumber.editText?.setText("")
                mbtnSave.visibility = this
                mbtnCancel.visibility = this
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}