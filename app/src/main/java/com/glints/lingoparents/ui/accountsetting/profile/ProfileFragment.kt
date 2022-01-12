package com.glints.lingoparents.ui.accountsetting.profile

import android.content.Context
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
import com.glints.lingoparents.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: ProfileViewModel

    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

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

        viewModel.getParentProfile()

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
            }
            mbtnLogout.setOnClickListener {
                viewModel.onLogOutButtonClick()
            }
        }

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
                                tvEmailContent.text = email
                            }
                        }
                    }
                    is ProfileViewModel.ProfileEvent.TryToEditProfile -> {
                        binding.apply {
                            val firstname = tfFirstName.editText?.text.toString()
                            val lastname = tfLastName.editText?.text.toString()
                            val address = tfAddress.editText?.text.toString()
                            val phonenumber = tfPhoneNumber.editText?.text.toString()
                            AuthFormValidator.apply {
                                hideFieldError(
                                    arrayListOf(
                                        tfFirstName,
                                        tfLastName,
                                        tfEmail,
                                        tfAddress,
                                        tfPhoneNumber
                                    )
                                )
                                if (isValidField(firstname) && isValidField(lastname) && isValidField(
                                        address
                                    ) && isValidPhoneNumber(phonenumber)
                                ) {
                                    viewModel.getAccessToken()
                                        .observe(viewLifecycleOwner) { accessToken ->
                                            viewModel.editParentProfile(
                                                accessToken,
                                                event.firstname,
                                                event.lastname,
                                                event.address,
                                                event.phone
                                            )
                                        }
                                    exitEditState()

                                } else {
                                    if (!isValidField(firstname)) {
                                        showFieldError(tfFirstName, EMPTY_FIELD_ERROR)
                                    }
                                    if (!isValidField(lastname)) {
                                        showFieldError(tfLastName, EMPTY_FIELD_ERROR)
                                    }
                                    if (!isValidField(address)) {
                                        showFieldError(tfAddress, EMPTY_FIELD_ERROR)
                                    }
                                    if (!isValidPhoneNumber(phonenumber)) {
                                        showFieldError(tfPhoneNumber, PHONENUMBER_ERROR)
                                    }
                                }
                            }
                        }

                    }
                    is ProfileViewModel.ProfileEvent.EditSuccess -> {
                        Snackbar.make(requireView(), "Edit Profile Success", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.parseColor("#42ba96"))
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .show()

                        viewModel.getParentProfile()
                    }
                    is ProfileViewModel.ProfileEvent.SendToAccountSetting -> {
                        val eventBusActionToAccountSetting =
                            ProfileViewModel.EventBusActionToAccountSetting.SendParentData(event.parentProfile)
                        viewModel.sendParentDataToAccountSettingFragment(
                            eventBusActionToAccountSetting
                        )

                    }
                    is ProfileViewModel.ProfileEvent.Error -> {
                        noInternetAccessOrErrorHandler.onNoInternetAccessOrError(event.message)
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
                tfEmail.visibility = this
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
            AuthFormValidator.apply {
                hideFieldError(
                    arrayListOf(
                        tfFirstName,
                        tfLastName,
                        tfEmail,
                        tfAddress,
                        tfPhoneNumber
                    )
                )


                View.VISIBLE.apply {
                    tvAddressContent.visibility = this
                    tvFirstNameContent.visibility = this
                    tvLastNameContent.visibility = this
                    tvPhoneNumberContent.visibility = this
                    mbtnLogout.visibility = this
                    mbtnEdit.visibility = this
                }
                View.INVISIBLE.apply {
                    tfEmail.visibility = this
                    tfEmail.editText?.setText("")
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