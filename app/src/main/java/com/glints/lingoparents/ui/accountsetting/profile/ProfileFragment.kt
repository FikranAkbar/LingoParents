package com.glints.lingoparents.ui.accountsetting.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.dataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.glints.lingoparents.R
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.ParentProfileResponse
import com.glints.lingoparents.databinding.FragmentProfileBinding
import com.glints.lingoparents.ui.MainActivity
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment(R.layout.fragment_profile) {

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
                    is ProfileViewModel.ProfileEvent.Success -> {
                        binding.apply {
                            event.parentProfile.apply {
                                tvFirstNameContent.text = firstname
                                tvLastNameContent.text = lastname
                                tvAddressContent.text = address
                                tvPhoneNumberContent.text = phone
                            }
                        }
                    }
                    //amin
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
                }
            }
        }
        return binding.root
    }

    private fun enterEditState() {
        binding.apply {
            View.VISIBLE.apply {
                tfAddress.visibility = this
                tfFirstName.visibility = this
                tfLastName.visibility = this
                tfPhoneNumber.visibility = this
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