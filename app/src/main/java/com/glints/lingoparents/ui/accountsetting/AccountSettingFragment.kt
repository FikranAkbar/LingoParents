package com.glints.lingoparents.ui.accountsetting

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentAccountSettingBinding
import com.glints.lingoparents.ui.dashboard.DashboardActivity
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
}