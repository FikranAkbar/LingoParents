package com.glints.lingoparents.ui.accountsetting.linkedaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.InviteChildResponse
import com.glints.lingoparents.databinding.FragmentLinkedAccountBinding
import com.glints.lingoparents.ui.accountsetting.linkedaccount.codeinvitation.ChildrenCodeInvitationFragment
import com.glints.lingoparents.utils.interfaces.OnInviteChildrenSuccess
import com.google.android.material.tabs.TabLayoutMediator

class LinkedAccountFragment : Fragment(R.layout.fragment_linked_account), OnInviteChildrenSuccess {
    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.invited,
            R.string.requesuted
        )
    }

    private var _binding: FragmentLinkedAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinkedAccountBinding.inflate(inflater)

        initViewPager()

        binding.mbtnAddChild.setOnClickListener {
            val dialog = ChildrenCodeInvitationFragment(this)
            dialog.show(childFragmentManager, "Children Code Invitation")
        }
        return binding.root
    }

    private fun initViewPager() {
        val linkedAccountSectionsPagerAdapter = LinkedAccountSectionsPagerAdapter(requireActivity() as AppCompatActivity)
        val viewPager2 = binding.viewPagerLinkedAccount
        viewPager2.adapter = linkedAccountSectionsPagerAdapter

        val tabs = binding.tabLinkedAccount
        TabLayoutMediator(tabs, viewPager2) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onInviteChildrenSuccess() {
        initViewPager()
    }
}