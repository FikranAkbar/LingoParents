package com.glints.lingoparents.ui.accountsetting

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.PickImageContractOptions
import com.canhub.cropper.options
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentAccountSettingBinding
import com.glints.lingoparents.databinding.ItemProfilePictureDialogBinding
import com.glints.lingoparents.ui.accountsetting.profile.ProfileViewModel
import com.glints.lingoparents.ui.dashboard.DashboardActivity
import com.glints.lingoparents.utils.*
import com.glints.lingoparents.utils.interfaces.NoInternetAccessOrErrorListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collect
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File

class AccountSettingFragment : Fragment(R.layout.fragment_account_setting) {
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: AccountSettingViewModel
    private lateinit var dialogBinding: ItemProfilePictureDialogBinding
    private lateinit var latestPhoto: Uri
    private lateinit var noInternetAccessOrErrorHandler: NoInternetAccessOrErrorListener

    private lateinit var customDialog: AlertDialog

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            latestPhoto = result.uriContent!!
            val uriFilePath = result.getUriFilePath(requireContext())!! // optional usage
            val file = File(uriFilePath)
            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val photo: MultipartBody.Part =
                MultipartBody.Part.createFormData("photo", file.name, requestFile)
            viewModel.onCroppedImage(photo)
        } else {
            val exception = result.error
        }
    }

    private fun startCrop() {
        // start picker to get image for cropping and then use the image in cropping activity
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
                setAspectRatio(1, 1)
                setCropShape(CropImageView.CropShape.OVAL)
                setImagePickerContractOptions(
                    PickImageContractOptions(includeGallery = true, includeCamera = true)
                )

            }
        )

    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.account_setting_tab_text_1,
            R.string.account_setting_tab_text_2,
            R.string.account_setting_tab_text_3
        )
    }

    private var _binding: FragmentAccountSettingBinding? = null
    private val binding get() = _binding!!


    override fun onStart() {
        super.onStart()
        (activity as DashboardActivity).showBottomNav(false)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                AccountSettingViewModel::class.java
        ]
        viewModel.loadingState()

        _binding = FragmentAccountSettingBinding.inflate(inflater)

        val sectionsPagerAdapter =
            AccountSettingSectionsPagerAdapter(requireActivity() as AppCompatActivity)
        val viewPager2 = binding.viewPagerAccountSetting
        viewPager2.isUserInputEnabled = false
        viewPager2.adapter = sectionsPagerAdapter
        val tabs = binding.tabAccountSetting
        TabLayoutMediator(tabs, viewPager2) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        val root = tabs.getChildAt(0)
        if (root is LinearLayout) {
            root.apply {
                showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                val drawable = GradientDrawable()
                drawable.apply {
                    setColor(ContextCompat.getColor(requireContext(), R.color.teal_700))
                    setSize(4, 1)
                }
                dividerPadding = 18
                dividerDrawable = drawable
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    (activity as DashboardActivity).showBottomNav(true)
                    findNavController().popBackStack()
                }
            })


        binding.apply {
            ivBackButton.setOnClickListener {
                (activity as DashboardActivity).showBottomNav(true)
                findNavController().popBackStack()
            }
            ivProfilePicture.setOnClickListener {
                showProfilePictureDialog()
            }

        }
        lifecycleScope.launchWhenStarted {
            viewModel.accountSettingEvent.collect { event ->
                when (event) {
                    is AccountSettingViewModel.AccountSetting.Loading -> {
                        showLoading(true)
                    }
                    is AccountSettingViewModel.AccountSetting.TryToUploadPhoto -> {
                        viewModel.uploadPhoto(
                            event.photo)
                    }
                    is AccountSettingViewModel.AccountSetting.UploadPhotoSuccess -> {
                        latestPhoto.let {
                            binding.ivProfilePicture.setImageURI(it)
                        }
                        Snackbar.make(requireView(), "Change Avatar Success", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.parseColor("#42ba96"))
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .show()
                    }
                    is AccountSettingViewModel.AccountSetting.Error -> {
                        noInternetAccessOrErrorHandler.onNoInternetAccessOrError(event.message)
                    }
                }
            }
        }

        return binding.root
    }

    @Subscribe
    fun getNameAndPhoto(event: ProfileViewModel.EventBusActionToAccountSetting.SendParentData) {
        showLoading(false)
        binding.apply {
            event.parentProfile.apply {
                val name = "$firstname $lastname"
                tvParent.text = name
                if (photo != null) {
                    ivProfilePicture.load(photo)
                }
            }
        }
    }

    private fun showProfilePictureDialog() {
        dialogBinding = ItemProfilePictureDialogBinding.inflate(
            LayoutInflater.from(requireContext()), null, false)
        customDialog = AlertDialog.Builder(requireContext(), 0).create()

        customDialog.apply {
            setView(dialogBinding.root)
            setCancelable(true)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }.show()

        dialogBinding.tvUpdate.setOnClickListener {
            startCrop()
            customDialog.dismiss()
        }
    }

    private fun showLoading(boolean: Boolean) {
        binding.apply {
            ivProfilePicture.isVisible = !boolean
            tvHello.isVisible = !boolean
            tvParent.isVisible = !boolean
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