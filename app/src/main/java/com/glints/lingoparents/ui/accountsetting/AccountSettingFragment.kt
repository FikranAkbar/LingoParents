package com.glints.lingoparents.ui.accountsetting

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.ParentProfileResponse
import com.glints.lingoparents.databinding.FragmentAccountSettingBinding
import com.glints.lingoparents.ui.dashboard.DashboardActivity
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountSettingFragment : Fragment(R.layout.fragment_account_setting) {
    //class AccountSettingFragment(private val tokenPreferences: TokenPreferences) : Fragment(R.layout.fragment_account_setting) {
    private lateinit var tokenPreferences: TokenPreferences

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.account_setting_tab_text_1,
            R.string.account_setting_tab_text_2
        )
    }

    private var _binding: FragmentAccountSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //amin
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        _binding = FragmentAccountSettingBinding.inflate(inflater)

        val sectionsPagerAdapter =
            ProgressSectionsPagerAdapter(requireActivity() as AppCompatActivity)
        val viewPager2 = binding.viewPagerAccountSetting
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
                    findNavController().popBackStack()
                    (activity as DashboardActivity).showBottomNav(true)
                }
            })

        (activity as DashboardActivity).showBottomNav(false)

        binding.apply {
            cvBackButton.setOnClickListener {
                findNavController().popBackStack()
                (activity as DashboardActivity).showBottomNav(true)
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}