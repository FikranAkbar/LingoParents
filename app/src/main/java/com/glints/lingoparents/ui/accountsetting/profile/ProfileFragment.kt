package com.glints.lingoparents.ui.accountsetting.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding : FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)

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
}