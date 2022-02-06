package com.glints.lingoparents.ui.liveevent.detail

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
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
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import kotlinx.coroutines.flow.collect

class LiveEventDetailFragment : Fragment(R.layout.fragment_live_event_detail),
    TransactionFinishedCallback {
    private lateinit var binding: FragmentLiveEventDetailBinding
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: LiveEventDetailViewModel

    private var id_user: Int = 0
    private var id_event: Int = 0
    private var eventType: String? = null

    private var fullname: String? = null
    private var phoneNumber: String? = null
    private var email: String? = null

    private var attendance_time_event: String? = null

    private var idUser_createValue: Int? = null
    private var total_price: Int = 0
    private var voucherCode: String = ""
    private var paymentMethod: String = ""

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
                showDialog(inflater)
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
                                        mbtnJoinZoom.visibility = View.VISIBLE
                                    }
                                }

                                tvDetailEventTitle.text = title
                                tvDateAndTimeContent.text = "$date at $started_at"
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

                                mbtnJoinZoom.setOnClickListener {
                                    // TODO: Call backend API to verify payment
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(zoom_link))
                                    requireContext().startActivity(intent)
                                }
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
                        val nameParts = fullname.split(" ").toMutableList()
                        val firstName = nameParts[0]
                        nameParts.removeAt(0)
                        val lastName = nameParts.joinToString(" ")

                        val email = event.email

                        /*
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
                         */

                        viewModel.createOrderEvent(
                            firstName,
                            lastName,
                            email,
                            phoneNumber,
                            id_event,
                            id_user,
                            total_price,
                            voucherCode
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
                            mbtnJoinZoom.visibility = View.VISIBLE
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
                    is LiveEventDetailViewModel.LiveEventDetailEvent.CreateOrderEventSuccess -> {
                        MidtransSDK.getInstance()
                            .startPaymentUiFlow(requireContext(), event.snapToken)
                        //Snackbar.make(binding.root, "createOrderEventSuccess", Snackbar.LENGTH_SHORT).show()
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
                ivBackButton.setOnClickListener {
                    closeDialog()
                }

                mbtnRegister.setOnClickListener {
                    fullname = tfFullName.editText?.text.toString().trim()
                    phoneNumber = tfPhoneNumber.editText?.text.toString().trim()
                    email = tfEmail.editText?.text.toString().trim()
                    voucherCode = tfVoucherCode.editText?.text.toString().trim()
                    AuthFormValidator.apply {
                        hideFieldError(
                            arrayListOf(
                                tfFullName,
                                tfEmail,
                                tfPhoneNumber,
                                tfVoucherCode
                            )
                        )
                        if (isValidEmail(email) && isValidField(fullname) && isValidPhoneNumber(phoneNumber)
                        ) {
                            viewModel.onRegisterButtonClick(
                                fullname!!,
                                email!!,
                                phoneNumber!!,
                                voucherCode
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

    /*private fun initTransactionRequest(): TransactionRequest {
        val transactionRequestNew =
            TransactionRequest(System.currentTimeMillis().toString() + "", 49900.0)
        transactionRequestNew.customerDetails = initCustomerDetails()
        return transactionRequestNew
    }*/

    /*private fun initCustomerDetails(): CustomerDetails{
        val mCustomerDetails = CustomerDetails()
        mCustomerDetails.phone = "085156283106"
        mCustomerDetails.firstName = "Fikran Akbar"
        mCustomerDetails.email = "mail@mail.com"
        mCustomerDetails.customerIdentifier = "mail@mail.com"
        return mCustomerDetails
    }*/

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
            println("account Numbers: ${result.response.accountNumbers[0].bank} - ${result.response.accountNumbers[0].accountNumber}")
            when (result.status) {
                TransactionResult.STATUS_SUCCESS -> {
                    Toast.makeText(requireContext(),
                        "Transaction Finished. ID: " + result.response.transactionId,
                        Toast.LENGTH_LONG).show()

                    binding.apply {
                        mbtnRegister.visibility = View.INVISIBLE
                        mbtnJoinZoom.visibility = View.VISIBLE
                    }
                }
                TransactionResult.STATUS_PENDING -> {
                    showSuccessSnackbar("Transaction pending with Order Id: ${result.response.transactionId}")

                    binding.apply {
                        mbtnRegister.visibility = View.INVISIBLE
                        mbtnJoinZoom.visibility = View.VISIBLE
                    }
                }
                TransactionResult.STATUS_FAILED -> showErrorSnackbar("Transaction failed with Order Id: ${result.response.transactionId}")
            }
            result.response.validationMessages
        } else if (result.isTransactionCanceled) {
            showErrorSnackbar("Transaction canceled")
        } else {
            if (result.status.equals(TransactionResult.STATUS_INVALID, true)) {
                showErrorSnackbar("Transaction invalid")
            } else {
                showErrorSnackbar("Transaction finished with failure")
            }
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

    private fun showSuccessSnackbar(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(resources.getColor(R.color.success_color, null))
                .setTextColor(Color.WHITE)
                .show()
        } else {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.GREEN)
                .setTextColor(Color.WHITE)
                .show()
        }
    }
}