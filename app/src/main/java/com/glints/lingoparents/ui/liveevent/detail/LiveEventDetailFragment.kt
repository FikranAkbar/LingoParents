package com.glints.lingoparents.ui.liveevent.detail

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.imageLoader
import coil.load
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FormRegisterEventBinding
import com.glints.lingoparents.databinding.FragmentLiveEventDetailBinding
import com.glints.lingoparents.utils.*
import com.google.android.material.snackbar.Snackbar
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.snap.Gopay
import com.midtrans.sdk.corekit.models.snap.Shopeepay
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import kotlinx.coroutines.flow.collect

class LiveEventDetailFragment : Fragment(R.layout.fragment_live_event_detail),
    TransactionFinishedCallback {
    private lateinit var binding: FragmentLiveEventDetailBinding
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: LiveEventDetailViewModel
    private val paymentMethodItems = listOf<String>("Cash", "BRI", "BNI", "BCA", "GoPay", "OVO")

    private var id_user: Int? = null
    private var id_event: Int? = null
    private var eventType: String? = null

    private var fullname: String? = null
    private var phoneNumber: String? = null
    private var email: String? = null

    private var attendance_time_event: String? = null

    private var idUser_createValue: Int? = null
    private var total_price: Int? = null
    private var voucherCode: String? = null
    private var paymentMethod: String? = null

    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

    @SuppressLint("SetTextI18n", "NewApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLiveEventDetailBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(
            this,
            CustomViewModelFactory(tokenPreferences, this, eventId = arguments?.get("id") as Int)
        )[
                LiveEventDetailViewModel::class.java
        ]

        initMidtransSdk()

        binding.apply {
            ivBackButton.setOnClickListener {
                findNavController().popBackStack()
            }
            mbtnRegister.setOnClickListener {
                //showDialog(inflater)
                MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
                MidtransSDK.getInstance().startPaymentUiFlow(requireContext())
            }
            arguments?.get("category")?.apply {
                eventType = this as String?
                if (this == "completed") {
                    mbtnRegister.visibility = View.INVISIBLE
                    tvEventWasFinished.visibility = View.VISIBLE
                }
            }
        }

        viewModel.getParentProfile()

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
                                println("Event Detail: $this")
                                if (eventType != "completed") {
                                    Trx_event_participants?.find { it.id_user == id_user }?.let {
                                        mbtnRegister.visibility = View.INVISIBLE
                                        tvUserHasBeenRegistered.visibility = View.VISIBLE
                                    }
                                }

                                tvDetailEventTitle.text = title
                                tvDateAndTimeContent.text = "$date, $started_at"
                                tvPriceContentNumber.text = price

                                val imageLoader = requireContext().imageLoader

                                cover?.let {
                                    ivDetailEventPoster.load(it, imageLoader)
                                }

                                speaker_photo?.let {
                                    ivPhotoContent.load(it, imageLoader)
                                }



                                tvSpeakerName.text = speaker
                                tvSpeakerProfession.text = speaker_profession
                                tvSpeakerCompany.text = speaker_company
                                tvDescriptionContent.text = description
                                attendance_time_event = started_at
                                total_price = price.toInt()
                                idUser_createValue = idUser_create
                            }
                        }
                    }
                    is LiveEventDetailViewModel.LiveEventDetailEvent.SuccessGetProfile -> {
                        event.parentProfile.apply {
                            fullname = "$firstname $lastname"
                            phoneNumber = phone
                            this@LiveEventDetailFragment.email = this.email
                        }
                    }
                    is LiveEventDetailViewModel.LiveEventDetailEvent.RegisterClick -> {
                        val phoneNumber = event.phone
                        val voucherCode = event.voucherCode
                        val fullname = event.fullname
                        val email = event.email
                        val paymentMethod = event.paymentMethod
                        val status = "yes"

                        viewModel.registerLiveEvent(
                            total_price!!,
                            phoneNumber,
                            voucherCode,
                            id_user!!,
                            id_event!!,
                            fullname,
                            attendance_time_event!!,
                            email,
                            "yes",
                            paymentMethod,
                            idUser_createValue!!,
                            status
                        )


                    }
                    is LiveEventDetailViewModel.LiveEventDetailEvent.RegisterSuccess -> {
                        Snackbar.make(
                            requireView(),
                            "Register Live Event Success",
                            Snackbar.LENGTH_LONG
                        )
                            .setBackgroundTint(resources.getColor(R.color.success_color, null))
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .show()

                        binding.apply {
                            mbtnRegister.visibility = View.INVISIBLE
                            tvUserHasBeenRegistered.visibility = View.VISIBLE
                        }
                    }
                    is LiveEventDetailViewModel.LiveEventDetailEvent.Loading -> {
                        showLoading(true)
                    }
                    is LiveEventDetailViewModel.LiveEventDetailEvent.Error -> {
                        noInternetAccessOrErrorHandler.onNoInternetAccessOrError(getString(R.string.default_error_message))
                        showLoading(false)
                    }

                    is LiveEventDetailViewModel.LiveEventDetailEvent.RegisterError -> {
                        Snackbar.make(
                            requireView(),
                            "Failed To Register Live Event",
                            Snackbar.LENGTH_SHORT
                        )
                            .setBackgroundTint(Color.parseColor("#F03738"))
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .show()
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
            shimmerLayout.isVisible = bool
            mainContent.isVisible = !bool
        }
    }

    private fun showDialog(inflater: LayoutInflater) {
        val formRegister = Dialog(requireActivity())
        val formRegisterEventBinding: FormRegisterEventBinding =
            FormRegisterEventBinding.inflate(inflater)


        formRegister.apply {
            fun closeDialog() {
                dismiss()
                viewModel.getLiveEventDetailById(viewModel.getCurrentEventId())

            }

            formRegisterEventBinding.apply {
                tfEmail.editText?.setText(email)
                tfFullName.editText?.setText(fullname)
                tfPhoneNumber.editText?.setText(phoneNumber)
                val adapter =
                    ArrayAdapter(context, R.layout.item_payment_method, paymentMethodItems)
                (tfPaymentMethod.editText as AutoCompleteTextView).setAdapter(adapter)
                ivBackButton.setOnClickListener {
                    closeDialog()
                }
                mbtnRegister.setOnClickListener {
                    fullname = tfFullName.editText?.text.toString()
                    phoneNumber = tfPhoneNumber.editText?.text.toString()
                    email = tfEmail.editText?.text.toString()
                    voucherCode = tfVoucherCode.editText?.text.toString()
                    paymentMethod = tfPaymentMethod.editText?.text.toString()
                    AuthFormValidator.apply {
                        hideFieldError(
                            arrayListOf(
                                tfFullName,
                                tfEmail,
                                tfPhoneNumber,
                                tfVoucherCode,
                                tfPaymentMethod
                            )
                        )
                        if (isValidEmail(email) && isValidField(fullname) && isValidPhoneNumber(
                                phoneNumber
                            ) && isValidField(voucherCode) && isValidField(
                                paymentMethod
                            )
                        ) {
                            viewModel.onRegisterButtonClick(
                                tfFullName.editText?.text.toString(),
                                tfEmail.editText?.text.toString(),
                                tfPhoneNumber.editText?.text.toString(),
                                tfVoucherCode.editText?.text.toString(),
                                tfPaymentMethod.editText?.text.toString()
                            )
                            closeDialog()
                        } else {
                            if (!isValidEmail(email)) {
                                showFieldError(tfEmail, EMAIL_WRONG_FORMAT_ERROR)
                            }
                            if (!isValidField(fullname)) {
                                showFieldError(tfFullName, EMPTY_FIELD_ERROR)
                            }
                            if (!isValidPhoneNumber(phoneNumber)) {
                                showFieldError(tfPhoneNumber, PHONENUMBER_ERROR)
                            }
                            if (!isValidField(paymentMethod)) {
                                showFieldError(tfPaymentMethod, EMPTY_FIELD_ERROR)
                            }
                            if (!isValidField(voucherCode)) {
                                showFieldError(tfVoucherCode, EMPTY_FIELD_ERROR)
                            }
                        }


                    }
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

    private fun initTransactionRequest(): TransactionRequest {
        val transactionRequestNew =
            TransactionRequest(System.currentTimeMillis().toString() + "", 49900.0)
        transactionRequestNew.customerDetails = initCustomerDetails()
        transactionRequestNew.gopay = Gopay("mysamplesdk:://midtrans")
        transactionRequestNew.shopeepay = Shopeepay("mysamplesdk:://midtrans")
        return transactionRequestNew
    }

    private fun initCustomerDetails(): CustomerDetails{
        val mCustomerDetails = CustomerDetails()
        mCustomerDetails.phone = "085156283106"
        mCustomerDetails.firstName = "Fikran Akbar"
        mCustomerDetails.email = "mail@mail.com"
        mCustomerDetails.customerIdentifier = "mail@mail.com"
        return mCustomerDetails
    }

    private fun initMidtransSdk() {
        val clientKey = MidtransSdkConfig.MERCHANT_CLIENT_KEY
        val baseUrl = MidtransSdkConfig.MERCHANT_BASE_CHECKOUT_URL
        val sdkFlowUiBuilder = SdkUIFlowBuilder.init()
            .setClientKey(clientKey)
            .setContext(requireContext())
            .setTransactionFinishedCallback(this)
            .setMerchantBaseUrl(baseUrl)
            .setUIkitCustomSetting(uiKitCustomSetting())
            .enableLog(true)
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .setLanguage("en")
        sdkFlowUiBuilder.buildSDK()
    }

    private fun uiKitCustomSetting(): UIKitCustomSetting {
        val uIKitCustomSetting = UIKitCustomSetting()
        uIKitCustomSetting.isSkipCustomerDetailsPages = true
        uIKitCustomSetting.isShowPaymentStatus = true
        return uIKitCustomSetting
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            noInternetAccessOrErrorHandler = context as NoInternetAccessOrErrorListener
        } catch (e: ClassCastException) {
            println("DEBUG: $context must be implement NoInternetAccessOrErrorListener")
        }
    }

    override fun onTransactionFinished(result: TransactionResult) {
        if (result.response != null) {
            when (result.status) {
                TransactionResult.STATUS_SUCCESS -> Toast.makeText(requireContext(), "Transaction Finished. ID: " + result.response.transactionId, Toast.LENGTH_LONG).show()
                TransactionResult.STATUS_PENDING -> Toast.makeText(requireContext(), "Transaction Pending. ID: " + result.response.transactionId, Toast.LENGTH_LONG).show()
                TransactionResult.STATUS_FAILED -> Toast.makeText(requireContext(), "Transaction Failed. ID: " + result.response.transactionId.toString() + ". Message: " + result.response.statusMessage, Toast.LENGTH_LONG).show()
            }
            result.response.validationMessages
        } else if (result.isTransactionCanceled) {
            Toast.makeText(requireContext(), "Transaction Canceled", Toast.LENGTH_LONG).show()
        } else {
            if (result.status.equals(TransactionResult.STATUS_INVALID, true)) {
                Toast.makeText(requireContext(), "Transaction Invalid", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Transaction Finished with failure.", Toast.LENGTH_LONG).show()
            }
        }
    }
}