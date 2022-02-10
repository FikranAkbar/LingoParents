package com.glints.lingoparents.ui.accountsetting.linkedaccount

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.InviteChildResponse
import com.glints.lingoparents.databinding.FragmentLinkedAccountBinding
import com.glints.lingoparents.ui.accountsetting.linkedaccount.codeinvitation.ChildrenCodeInvitationFragment
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.glints.lingoparents.utils.interfaces.OnInviteChildrenSuccess
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collect

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

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: LinkedAccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinkedAccountBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(
            requireActivity(),
            CustomViewModelFactory(tokenPreferences, requireActivity())
        ) [LinkedAccountViewModel::class.java]

        initViewPager()

        binding.apply {
            mbtnAddChild.setOnClickListener {
                val dialog = ChildrenCodeInvitationFragment(this@LinkedAccountFragment)
                dialog.show(childFragmentManager, "Children Code Invitation")
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getParentId().observe(viewLifecycleOwner) { parentId ->
            viewModel.getParentCode(parentId.toInt())
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.parentCodeEvent.collect { event ->
                when(event) {
                    is LinkedAccountViewModel.ParentCodeEvent.Loading -> {}
                    is LinkedAccountViewModel.ParentCodeEvent.Error -> {
                        binding.mbtnParentCode.text = getString(R.string.failed_to_get_the_code)
                    }
                    is LinkedAccountViewModel.ParentCodeEvent.Success -> {
                        binding.mbtnParentCode.apply {
                            text = event.data
                            setOnClickListener {
                                copyTextToClipboard((it as MaterialButton).text.toString())
                            }
                        }
                    }
                }
            }
        }
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

    private fun copyTextToClipboard(text: String) {
        val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(requireContext(), "Parent referral code copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}