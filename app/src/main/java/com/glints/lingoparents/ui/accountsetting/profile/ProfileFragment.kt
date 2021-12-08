package com.glints.lingoparents.ui.accountsetting.profile

import android.content.Intent
import android.graphics.Color
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
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    //amin
    private var emailValue: String? = null


    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                ProfileViewModel::class.java
        ]
        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
            viewModel.getParentProfile(accessToken)
        }
        viewModel.getAccessEmail().observe(viewLifecycleOwner) { email ->
            emailValue = email
        }

        binding.apply {
            mbtnEdit.setOnClickListener {
                enterEditState()
            }
            mbtnCancel.setOnClickListener {
                exitEditState()
            }
            mbtnSave.setOnClickListener {
                viewModel.onSaveButtonClick(
                    tfFirstName.editText?.text.toString(),
                    tfLastName.editText?.text.toString(),
                    tfAddress.editText?.text.toString(),
                    tfPhoneNumber.editText?.text.toString()
                )
                exitEditState()
                //binding.fragmentProfile.invalidate()
            }
            mbtnLogout.setOnClickListener {
                viewModel.onLogOutButtonClick()
            }
        }
        //amin
        lifecycleScope.launchWhenStarted {
            viewModel.profileEvent.collect { event ->
                when (event) {
                    ProfileViewModel.ProfileEvent.NavigateToAuthScreen -> {
                        val intent =
                            Intent(this@ProfileFragment.requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    //amin
                    is ProfileViewModel.ProfileEvent.Success -> {
                        binding.apply {
                            event.parentProfile.apply {
                                tvFirstNameContent.text = firstname
                                tvLastNameContent.text = lastname
                                tvEmailContent.text = emailValue
                                tvAddressContent.text = address
                                tvPhoneNumberContent.text = phone
                            }
                        }
                    }
                    is ProfileViewModel.ProfileEvent.TryToEditProfile -> {
                        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
                            viewModel.editParentProfile(
                                accessToken,
                                event.firstname,
                                event.lastname,
                                event.address,
                                event.phone
                            )
                        }
                    }
                    is ProfileViewModel.ProfileEvent.EditSuccess -> {
                        Snackbar.make(requireView(), "Edit Profile Success", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.parseColor("#42ba96"))
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .show()
                        //findNavController().navigate(R.id.accountSettingFragment)
                        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
                            viewModel.getParentProfile(accessToken)
                        }


                    }
                    is ProfileViewModel.ProfileEvent.Error -> {
                        Snackbar.make(requireView(), "Error", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.parseColor("#FF0000"))
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .show()
                    }
                    is ProfileViewModel.ProfileEvent.Loading -> {

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
                tfAddress.editText?.setText(tvAddressContent.text)
                tfFirstName.visibility = this
                tfFirstName.editText?.setText(tvFirstNameContent.text)
                tfLastName.visibility = this
                tfLastName.editText?.setText(tvLastNameContent.text)
                tfPhoneNumber.visibility = this
                tfPhoneNumber.editText?.setText(tvPhoneNumberContent.text)
                mbtnSave.visibility = this
                mbtnCancel.visibility = this
            }
            View.INVISIBLE.apply {
                tvAddressContent.visibility = this
                tvFirstNameContent.visibility = this
                tvLastNameContent.visibility = this
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
                tvFirstNameContent.visibility = this
                tvLastNameContent.visibility = this
                tvPhoneNumberContent.visibility = this
                mbtnLogout.visibility = this
                mbtnEdit.visibility = this
            }
            View.INVISIBLE.apply {
                tfAddress.visibility = this
                tfAddress.editText?.setText("")
                tfFirstName.visibility = this
                tfFirstName.editText?.setText("")
                tfLastName.visibility = this
                tfLastName.editText?.setText("")
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