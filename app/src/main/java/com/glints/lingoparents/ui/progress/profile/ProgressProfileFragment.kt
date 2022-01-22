package com.glints.lingoparents.ui.progress.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentProgressProfileBinding
import com.glints.lingoparents.databinding.ItemPopupCharacterBinding
import com.glints.lingoparents.ui.progress.ProgressViewModel
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.NoInternetAccessOrErrorListener
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ProgressProfileFragment : Fragment(R.layout.fragment_progress_profile) {
    private var _binding: FragmentProgressProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialogBinding: ItemPopupCharacterBinding
    private lateinit var customDialog: AlertDialog

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: ProgressProfileViewModel

    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

    private var studentName = ""
    private var studentLevel = ""

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentProgressProfileBinding.bind(view)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                ProgressProfileViewModel::class.java
        ]

        showLoading(false)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.progressProfileEvent.collect { event ->
                when (event) {
                    is ProgressProfileViewModel.ProgressProfileEvent.Loading -> {
                        showMainContent(true)
                        showLoading(true)
                    }
                    is ProgressProfileViewModel.ProgressProfileEvent.Success -> {
                        showMainContent(true)
                        event.result.apply {
                            studentName = fullname!!
                            studentLevel =
                                "${level_name ?: "No Level"} - ${sublevel_name ?: "No Sub Level"}"
                            binding.apply {
                                photo?.let {
                                    imageView.load(it)
                                }
                                tvProfileName.text = studentName
                                tvProfileLevel.text = studentLevel
                                logo?.let {
                                    ivCharacterBadge.load(it)
                                }
                                tvPhoneNumberValue.text = phone
                                tvEmailValue.text = email
                                tvAddressValue.text = address

                                ivCharacterBadge.setOnClickListener {
                                    viewModel.getStudentCharacter(id_character!!)
                                    showDialog()
                                }
                            }
                        }
                        showLoading(false)
                    }
                    is ProgressProfileViewModel.ProgressProfileEvent.Error -> {
                        showLoading(false)
                        if (event.message.lowercase().contains("relation not found")) {
                            showMainContent(false)
                        } else {
                            showMainContent(false)
                            noInternetAccessOrErrorHandler.onNoInternetAccessOrError(event.message)
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.studentCharacterEvent.collect { event ->
                when (event) {
                    is ProgressProfileViewModel.StudentCharacterEvent.Loading -> {
                        showLoadingCharacter(true)
                    }
                    is ProgressProfileViewModel.StudentCharacterEvent.Success -> {
                        showLoadingCharacter(false)
                        event.result.apply {
                            dialogBinding.apply {
                                photo.let {
                                    ivCharacter.load(it)
                                }
                                tvName.text = studentName
                                tvCourseLevel.text = studentLevel
                                tvSuperheroName.text = character
                                tvPersonalityTraits.text = "Personality Traits: $personality_traits"
                                tvDesc.text = description
                            }
                        }
                    }
                    is ProgressProfileViewModel.StudentCharacterEvent.Error -> {
                        showLoadingCharacter(false)
                        customDialog.dismiss()
                        noInternetAccessOrErrorHandler.onNoInternetAccessOrError(event.message)
                    }
                }
            }
        }
    }

    private fun showDialog() {
        dialogBinding = ItemPopupCharacterBinding.inflate(
            LayoutInflater.from(requireContext()), null, false)
        customDialog = AlertDialog.Builder(requireContext(), 0).create()

        customDialog.apply {
            setView(dialogBinding.root)
            setCancelable(false)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }.show()

        dialogBinding.ivClose.setOnClickListener {
            customDialog.dismiss()
        }
    }

    private fun showLoadingCharacter(b: Boolean) {
        binding.apply {
            vLoadingBackground.isVisible = b
            vLoadingProgress.isVisible = b
        }
    }

    @Subscribe
    fun collectStudentIdEvent(event: ProgressViewModel.EventBusActionToStudentProfile.SendStudentId) {
        val studentId = event.studentId
        viewModel.getStudentProfileByStudentId(studentId)
    }

    private fun showLoading(b: Boolean) {
        binding.apply {
            shimmerLayout.isVisible = b
            mainContent.isVisible = !b
        }
    }

    private fun showMainContent(b: Boolean) {
        binding.apply {
            cvEventContainer.isVisible = b
            ivChildrenRelationRemoved.isVisible = !b
            tvChildrenRelationRemoved.isVisible = !b
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
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