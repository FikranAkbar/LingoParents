package com.glints.lingoparents.ui.progress.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentProgressProfileBinding
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

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: ProgressProfileViewModel

    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

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

        binding.apply {
            ivCharacterBadge.setOnClickListener {
                showDialog()
            }
        }

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
                            binding.apply {
                                photo?.let {
                                    imageView.load(it)
                                }
                                tvProfileName.text = fullname
                                tvProfileLevel.text =
                                    "${level_name ?: "No Level"} - ${sublevel_name ?: "No Sub Level"}"
                                logo?.let {
                                    ivCharacterBadge.load(it)
                                }
                                tvPhoneNumberValue.text = phone
                                tvEmailValue.text = email
                                tvAddressValue.text = address
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
    }

    private fun showDialog() {
        val dialog = Dialog(activity as AppCompatActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.item_popup_character)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val closeIcon = dialog.findViewById(R.id.ivClose) as ImageView
        closeIcon.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @Subscribe
    fun collectStudentIdEvent(event: ProgressViewModel.EventBusActionToStudentProfile.SendStudentId) {
        val studentId = event.studentId
        viewModel.getStudentProfileByStudentId(studentId)
    }

    private fun showLoading(b: Boolean) {
        binding.apply {
            when (b) {
                true -> {
                    shimmerLayout.visibility = View.VISIBLE
                    mainContent.visibility = View.GONE
                }
                false -> {
                    shimmerLayout.visibility = View.GONE
                    mainContent.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showMainContent(b: Boolean) {
        binding.apply {
            when(b) {
                true -> {
                    cvEventContainer.visibility = View.VISIBLE
                    ivChildrenRelationRemoved.visibility = View.GONE
                    tvChildrenRelationRemoved.visibility = View.GONE
                }
                false -> {
                    cvEventContainer.visibility = View.GONE
                    ivChildrenRelationRemoved.visibility = View.VISIBLE
                    tvChildrenRelationRemoved.visibility = View.VISIBLE
                }
            }
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