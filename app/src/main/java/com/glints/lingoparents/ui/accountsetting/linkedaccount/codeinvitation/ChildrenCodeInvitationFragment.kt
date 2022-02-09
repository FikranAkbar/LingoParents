package com.glints.lingoparents.ui.accountsetting.linkedaccount.codeinvitation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentChildrenCodeInvitationBinding
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.NoInternetAccessOrErrorListener
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect

class ChildrenCodeInvitationFragment : DialogFragment() {

    private lateinit var binding: FragmentChildrenCodeInvitationBinding
    private lateinit var viewModel: ChildrenCodeInvitationViewModel
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

    private var relation = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChildrenCodeInvitationBinding.inflate(inflater, container, false)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(
            requireActivity(),
            CustomViewModelFactory(tokenPreferences, requireActivity())
        )[ChildrenCodeInvitationViewModel::class.java]

        initView()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.searchChildrenEvent.collect { event ->
                when (event) {
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.LoadingGetChildren -> {
                        showLoading(true)
                    }
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.SuccessGetChildren -> {
                        showLoading(false)
                        addRadioGroup(true)
                        event.result.apply {
                            binding.apply {
                                rlCodeInvitation.visibility = View.VISIBLE
                                photo.let { ivChildren.load(it) }
                                tvUsernameChildren.text = fullname
                                tvChildrenAge.text = age.toString()

                                inviteButton.setOnClickListener {
                                    viewModel.getParentId()
                                        .observe(viewLifecycleOwner) { parentId ->
                                            viewModel.inviteChild(parentId.toInt(),
                                                referral_code,
                                                relation)
                                        }
                                }
                            }
                        }
                    }
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.SuccessInvite -> {
                        addRadioGroup(false)
                    }
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.ErrorGetChildren -> {
                        showLoading(false)
                        binding.messageCodeInvitation.text = event.message
                    }
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.ErrorInvite ->
                        noInternetAccessOrErrorHandler.onNoInternetAccessOrError(event.message)
                }
            }
        }
    }

    private fun initView() {
        binding.apply {
            viewModel.getParentId().observe(viewLifecycleOwner) { parentId ->
                searchButton.setOnClickListener {
                    viewModel.searchChildUsingStudentCode(parentId.toInt(),
                        tfSearchInsight.editText.toString())
                }
            }
        }
    }

    private fun showLoading(b: Boolean) {
        binding.apply {
            shimmerLayout.isVisible = b
            mainContent.isVisible = !b
        }
    }

    private fun addRadioGroup(b: Boolean) {
        val layoutSize = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)

        val radioFather = RadioButton(requireContext())
        radioFather.apply {
            layoutParams = layoutSize
            text = context.getString(R.string.father)
            id = R.id.radioButton_Father
        }

        val radioMother = RadioButton(requireContext())
        radioMother.apply {
            layoutParams = layoutSize
            text = context.getString(R.string.mother)
            id = R.id.radioButton_Mother
        }

        val radioGuardian = RadioButton(requireContext())
        radioGuardian.apply {
            layoutParams = layoutSize
            text = context.getString(R.string.guardian)
            id = R.id.radioButton_Guardian
        }

        val radioOther = RadioButton(requireContext())
        radioOther.apply {
            layoutParams = layoutSize
            text = context.getString(R.string.other)
            id = R.id.radioButton_Other
        }

        val radioGroup = RadioGroup(requireContext())
        radioGroup.addView(radioFather)
        radioGroup.addView(radioMother)
        radioGroup.addView(radioGuardian)
        radioGroup.addView(radioOther)
        binding.root.addView(radioGroup)

        radioGroup.setOnCheckedChangeListener { _, id ->
            if (id == 1) relation = getString(R.string.father)
            else if (id == 2) relation = getString(R.string.mother)
            else if (id == 3) relation = getString(R.string.guardian)
            else if (id == 4) relation = getString(R.string.other)
            else relation = ""
        }

        radioGroup.isVisible = b
    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        window!!.setLayout(width,
            ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}