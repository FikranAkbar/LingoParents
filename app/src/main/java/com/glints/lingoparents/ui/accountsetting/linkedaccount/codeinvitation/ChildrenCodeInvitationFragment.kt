package com.glints.lingoparents.ui.accountsetting.linkedaccount.codeinvitation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
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
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.NoInternetAccessOrErrorListener
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.snackbar.Snackbar
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
                        relation = ""
                    }
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.SuccessGetChildren -> {
                        showLoading(false)
                        requireActivity().hideKeyboard()
                        event.result.apply {
                            binding.apply {
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
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.SuccessInvite -> {
                        (this@ChildrenCodeInvitationFragment as DialogFragment).dialog?.cancel()
                        showSuccessSnackbar(event.message)
                    }
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.ErrorGetChildren -> {
                        showLoading(false)
                        binding.messageCodeInvitation.text = event.message
                    }
                    is ChildrenCodeInvitationViewModel.SearchChildrenCodeInvitationEvent.ErrorInvite ->
                        showErrorSnackbar(event.message)
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

    private fun showSuccessSnackbar(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(resources.getColor(R.color.success_color, null))
                .setTextColor(Color.WHITE)
                .show()
        } else {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.GREEN)
                .setTextColor(Color.WHITE)
                .show()
        }
    }

    private fun showErrorSnackbar(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Snackbar.make(binding.root,
                message,
                Snackbar.LENGTH_SHORT)
                .setBackgroundTint(resources.getColor(R.color.error_color, null))
                .setTextColor(Color.WHITE)
                .show()
        } else {
            Snackbar.make(binding.root,
                message,
                Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.RED)
                .setTextColor(Color.WHITE)
                .show()
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
        } catch (e: ClassCastException) {
            println("DEBUG: $context must be implement NoInternetAccessOrErrorListener")
        }
    }
}