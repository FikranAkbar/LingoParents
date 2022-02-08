package com.glints.lingoparents.ui.accountsetting.linkedaccount.category

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.response.LinkedAccountsResponse
import com.glints.lingoparents.databinding.FragmentLinkedAccountListBinding
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class LinkedAccountListFragment(private val listType: String) :
    Fragment(R.layout.fragment_linked_account),
    LinkedAccountListAdapter.OnItemClickCallback {
    companion object {
        const val INVITED_TYPE = "Invited"
        const val REQUESTED_TYPE = "Requested"
    }

    private var _binding: FragmentLinkedAccountListBinding? = null
    private val binding get() = _binding!!

    private lateinit var linkedAccountListAdapter: LinkedAccountListAdapter
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: LinkedAccountListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLinkedAccountListBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                LinkedAccountListViewModel::class.java
        ]

        binding.rvLinkedAccountList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            linkedAccountListAdapter =
                LinkedAccountListAdapter(this@LinkedAccountListFragment, listType)
            adapter = linkedAccountListAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getParentId().observe(viewLifecycleOwner) { parentId ->
            viewModel.parentId = parentId

            if (listType == REQUESTED_TYPE) {
                viewModel.getListOfRequestedLinkedAccount(viewModel.parentId)
            } else if (listType == INVITED_TYPE) {
                viewModel.getListOfInvitedLinkedAccount(viewModel.parentId)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.linkedAccountListEvent.collect { event ->
                when (event) {
                    is LinkedAccountListViewModel.LinkedAccountListEvent.ErrorAction -> {
                        showErrorSnackbar(event.message)
                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.ErrorGetInvitedList -> {
                        showEmptyContent()
                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.ErrorGetRequestedList -> {
                        showEmptyContent()
                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.LoadingAction -> {}
                    is LinkedAccountListViewModel.LinkedAccountListEvent.LoadingGetInvitedList -> {
                        showLoading()
                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.LoadingGetRequestedList -> {
                        showLoading()
                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.SuccessAction -> {
                        linkedAccountListAdapter.differ.submitList(
                            linkedAccountListAdapter.differ.currentList.filter {
                                it.id_student.toInt() != event.result.id_student
                            }
                        )

                        showSuccessSnackbar(event.message)

                        if (linkedAccountListAdapter.differ.currentList.isEmpty()) {
                            showEmptyContent()
                        }
                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.SuccessGetInvitedList -> {
                        val result = event.result

                        if (result.isNotEmpty()) {
                            showMainContent()
                            linkedAccountListAdapter.submitList(event.result)
                        } else {
                            showEmptyContent()
                        }
                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.SuccessGetRequestedList -> {
                        val result = event.result

                        if (result.isNotEmpty()) {
                            showMainContent()
                            linkedAccountListAdapter.submitList(event.result)
                        } else {
                            showEmptyContent()
                        }
                    }
                }
            }
        }
    }

    private fun showMainContent() {
        binding.apply {
            rvLinkedAccountList.visibility = View.VISIBLE
            shimmerLayout.visibility = View.GONE
            ivNoChildren.visibility = View.GONE
            tvNoChildren.visibility = View.GONE
        }
    }

    private fun showLoading() {
        binding.apply {
            rvLinkedAccountList.visibility = View.GONE
            shimmerLayout.visibility = View.VISIBLE
            ivNoChildren.visibility = View.GONE
            tvNoChildren.visibility = View.GONE
        }
    }

    private fun showEmptyContent() {
        binding.apply {
            rvLinkedAccountList.visibility = View.GONE
            shimmerLayout.visibility = View.GONE
            ivNoChildren.visibility = View.VISIBLE
            tvNoChildren.visibility = View.VISIBLE
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
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(resources.getColor(R.color.success_color, null))
                .setTextColor(Color.WHITE)
                .show()
        } else {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.GREEN)
                .setTextColor(Color.WHITE)
                .show()
        }
    }

    override fun onCancelClicked(item: LinkedAccountsResponse.ChildrenData) {
        viewModel.doActionWithLinkingAccount(viewModel.parentId.toInt(),
            item.id_student.toInt(),
            LinkedAccountListViewModel.CANCEL_ACTION)
    }

    override fun onAcceptClicked(item: LinkedAccountsResponse.ChildrenData) {
        viewModel.doActionWithLinkingAccount(viewModel.parentId.toInt(),
            item.id_student.toInt(),
            LinkedAccountListViewModel.ACCEPT_ACTION)
    }

    override fun onDeclineClicked(item: LinkedAccountsResponse.ChildrenData) {
        viewModel.doActionWithLinkingAccount(viewModel.parentId.toInt(),
            item.id_student.toInt(),
            LinkedAccountListViewModel.DECLINE_ACTION)
    }
}