package com.glints.lingoparents.ui.accountsetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentAccountSettingBinding
import com.google.android.material.tabs.TabLayoutMediator

class AccountSettingFragment : Fragment(R.layout.fragment_account_setting) {

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.account_setting_tab_text_1,
            R.string.account_setting_tab_text_2
        )
    }

    private lateinit var binding: FragmentAccountSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountSettingBinding.inflate(inflater)

        val sectionsPagerAdapter = SectionsPagerAdapter(requireActivity() as AppCompatActivity)
        val viewPager2 = binding.viewPagerAccountSetting
        viewPager2.adapter = sectionsPagerAdapter
        val tabs = binding.tabAccountSetting
        TabLayoutMediator(tabs, viewPager2) { tab, position ->
            tab.text
        }.attach()

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}