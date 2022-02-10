package com.glints.lingoparents.ui.accountsetting.linkedaccount.codeinvitation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentChildrenCodeInvitationBinding
import com.glints.lingoparents.ui.dashboard.hideKeyboard
import com.glints.lingoparents.utils.*
import com.glints.lingoparents.utils.interfaces.ApiCallSuccessCallback
import com.glints.lingoparents.utils.interfaces.NoInternetAccessOrErrorListener
import com.glints.lingoparents.utils.interfaces.OnInviteChildrenSuccess
import kotlinx.coroutines.flow.collect

class ChildrenCodeInvitationFragment(private val listener: OnInviteChildrenSuccess) : DialogFragment() {

    private lateinit var binding: FragmentChildrenCodeInvitationBinding
    private lateinit var viewModel: ChildrenCodeInvitationViewModel
    private lateinit var tokenPreferences: TokenPreferences

    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener
    private lateinit var apiCallSuccessCallback: ApiCallSuccessCallback

    private var relation = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChildrenCodeInvitationBinding.inflate(inflater, container, false)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(
            requireActivity(),
            CustomViewModelFactory(tokenPreferences, requireActivity())
        )[ChildrenCodeInvitationViewModel::class.java]

        initView()

        return binding.root
    }

    @SuppressLint("SetTextI18n", "NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.searchChildrenEvent.collect { event ->
                when (event) {
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.LoadingGetChildren -> {
                        showLoading(true)
                        requireActivity().hideKeyboard()
                        relation = ""
                    }
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.SuccessGetChildren -> {
                        showLoading(false)
                        event.result.apply {
                            binding.apply {
                                messageCodeInvitation.text = ""
                                rlCodeInvitation.visibility = View.VISIBLE
                                photo.let { ivChildren.load(it) }
                                tvUsernameChildren.text = fullname
                                tvChildrenAge.text = "$age years old"

                                inviteButton.isClickable = false
                                inviteButton.setBackgroundColor(resources.getColor(R.color.primary_orange_disable_button,
                                    null))

                                rgParentRelationship.clearCheck()
                                rgParentRelationship.setOnCheckedChangeListener { _, id ->
                                    when (id) {
                                        R.id.rb_father -> {
                                            relation = "Father"
                                        }
                                        R.id.rb_mother -> {
                                            relation = "Mother"
                                        }
                                        R.id.rb_guardian -> {
                                            relation = "Guardian"
                                        }
                                        R.id.rb_other -> {
                                            relation = "Other"
                                        }
                                        else -> {
                                            relation = ""
                                        }
                                    }

                                    if (relation.isNotEmpty()) {
                                        inviteButton.setOnClickListener {
                                            viewModel.inviteChild(viewModel.parentId,
                                                referral_code,
                                                relation)
                                        }
                                        inviteButton.setBackgroundColor(resources.getColor(R.color.primary_orange_variant,
                                            null))
                                    } else {
                                        inviteButton.isClickable = false
                                        inviteButton.setBackgroundColor(resources.getColor(R.color.primary_orange_disable_button,
                                            null))
                                    }
                                }
                            }
                        }
                    }
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.LoadingInvite -> {
                        showLoading(true)
                    }
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.SuccessInvite -> {
                        (this@ChildrenCodeInvitationFragment as DialogFragment).dialog?.cancel()
                        apiCallSuccessCallback.onApiCallSuccessCallback(event.message)
                        listener.onInviteChildrenSuccess()
                    }
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.ErrorGetChildren -> {
                        showLoading(false)
                        binding.messageCodeInvitation.text = event.message
                    }
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.ErrorInvite -> {
                        showLoading(false)
                        binding.messageCodeInvitation.text = event.message
                        if (event.message.lowercase().contains(getString(R.string.incorrectly_entered_parent_relationship))) {
                            binding.messageCodeInvitation.text = getString(R.string.parent_has_already_invited)
                        }
                    }
                }
            }
        }
    }

    private fun initView() {
        binding.apply {
            viewModel.getParentId().observe(viewLifecycleOwner) { parentId ->
                viewModel.parentId = parentId.toInt()
                searchButton.setOnClickListener {
                    viewModel.searchChildUsingStudentCode(parentId.toInt(),
                        tfSearchInsight.editText!!.text.toString())
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

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        window!!.setLayout(width,
            ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            noInternetAccessOrErrorHandler = context as NoInternetAccessOrErrorListener
            apiCallSuccessCallback = context as ApiCallSuccessCallback
        } catch (e: ClassCastException) {
            println("DEBUG: $context must be implement NoInternetAccessOrErrorListener")
        }
    }
}