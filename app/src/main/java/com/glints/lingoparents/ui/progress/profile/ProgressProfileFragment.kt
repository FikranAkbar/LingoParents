package com.glints.lingoparents.ui.progress.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
                        showLoading(true)
                    }
                    is ProgressProfileViewModel.ProgressProfileEvent.Success -> {
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
    fun collectStudentIdEvent(event: ProgressViewModel.EventBusAction.SendStudentId) {
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

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}