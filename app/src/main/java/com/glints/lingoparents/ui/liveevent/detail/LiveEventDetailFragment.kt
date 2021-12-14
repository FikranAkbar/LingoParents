package com.glints.lingoparents.ui.liveevent.detail

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FormRegisterEventBinding
import com.glints.lingoparents.databinding.FragmentLiveEventDetailBinding
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class LiveEventDetailFragment : Fragment(R.layout.fragment_live_event_detail) {
    private lateinit var binding: FragmentLiveEventDetailBinding
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: LiveEventDetailViewModel

    //register live event
    private var id_user: Int? = null
    private var id_event: Int? = null

    //idevent
    private var fullname: String? = null
    private var phoneNumber: String? = null
    private var email: String? = null

    //attendance
    private var attendance_time_event: String? = null

    //iduser create
    private var total_price: Int? = null
    private var voucherCode: String? = null
    private var paymentMethod: String? = null
    private var status: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLiveEventDetailBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(
            this,
            CustomViewModelFactory(tokenPreferences, this, eventId = arguments?.get("id") as Int)
        )[
                LiveEventDetailViewModel::class.java
        ]

        binding.apply {
            ivBackButton.setOnClickListener {
                findNavController().popBackStack()
            }
            mbtnRegister.setOnClickListener {
                showDialog(inflater)
            }
        }
        //amin
        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
            viewModel.getParentProfile(accessToken)
        }

        viewModel.getAccessEmail().observe(viewLifecycleOwner) { userEmail ->
            email = userEmail
        }
        viewModel.getUserId().observe(viewLifecycleOwner) { userId ->
            id_user = userId.toInt()
        }
        id_event = viewModel.getCurrentEventId()
        viewModel.getLiveEventDetailById(viewModel.getCurrentEventId())

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.liveEventDetailEvent.collect { event ->
                when (event) {
                    is LiveEventDetailViewModel.LiveEventDetailEvent.Success -> {
                        showLoading(false)
                        binding.apply {
                            event.result.apply {
                                cover?.let {
                                    ivDetailEventPoster.load(it)
                                }

                                tvDetailEventTitle.text = title
                                tvDateAndTimeContent.text = "$date, $started_at"
                                tvPriceContentNumber.text = price

                                ivPhotoContent.load(speaker_photo)

                                tvSpeakerName.text = speaker
                                tvSpeakerProfession.text = speaker_profession
                                tvSpeakerCompany.text = speaker_company
                                tvDescriptionContent.text = description
                                //register live event
                                attendance_time_event = started_at
                                total_price = price.toInt()
                            }
                        }
                    }
                    is LiveEventDetailViewModel.LiveEventDetailEvent.SuccessGetProfile -> {
                        event.parentProfile.apply {
                            fullname = "$firstname $lastname"
                            phoneNumber = phone
                        }
                    }
                    is LiveEventDetailViewModel.LiveEventDetailEvent.RegisterClick -> {

                        viewModel.registerLiveEvent(
                            total_price!!,
                            phoneNumber!!,
                            "sdfds",
                            id_user!!,
                            id_event!!,
                            fullname!!,
                            attendance_time_event!!,
                            email!!,
                            "yes",
                            "cash",
                            1,
                            "success"
                        )
                    }
                    is LiveEventDetailViewModel.LiveEventDetailEvent.RegisterSuccess -> {
                        Snackbar.make(
                            requireView(),
                            "Register Live Event Success",
                            Snackbar.LENGTH_LONG
                        )
                            .setBackgroundTint(Color.parseColor("#42ba96"))
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .show()


                    }
                    is LiveEventDetailViewModel.LiveEventDetailEvent.Loading -> {
                        showLoading(true)
                    }
                    is LiveEventDetailViewModel.LiveEventDetailEvent.Error -> {
                        showLoading(false)
                    }
                }
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })

        return binding.root
    }

    private fun showLoading(bool: Boolean) {
        binding.apply {
            if (bool) {
                shimmerLayout.visibility = View.VISIBLE
                mainContent.visibility = View.GONE
            } else {
                shimmerLayout.visibility = View.GONE
                mainContent.visibility = View.VISIBLE
            }
        }
    }

    private fun showDialog(inflater: LayoutInflater) {
        val formRegisterEventBinding: FormRegisterEventBinding =
            FormRegisterEventBinding.inflate(inflater)


        val formRegister = Dialog(requireActivity())
        formRegister.apply {
            formRegisterEventBinding.apply {
                ivBackButton.setOnClickListener {
                    dismiss()
                }
                mbtnRegister.setOnClickListener {
//                    viewModel.onRegisterButtonClick(
//                        tfVoucherCode.editText?.text.toString(),
//                        tfPaymentMethod.editText?.text.toString()
//                    )
                    viewModel.registerLiveEvent(
                        total_price!!,
                        phoneNumber!!,
                        "sdfds",
                        1,
                        id_event!!,
                        fullname!!,
                        attendance_time_event!!,
                        email!!,
                        "yes",
                        "cash",
                        1,
                        "success"
                    )

                }

            }

            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(formRegisterEventBinding.root)
            show()

            val layoutParams = WindowManager.LayoutParams()
            layoutParams.apply {
                copyFrom(window?.attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
                window?.attributes = this
            }

            formRegisterEventBinding.root.apply {
                setPadding(30, 80, 30, 80)
            }
        }
    }
}