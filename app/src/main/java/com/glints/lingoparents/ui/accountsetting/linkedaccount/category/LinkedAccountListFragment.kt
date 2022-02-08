package com.glints.lingoparents.ui.accountsetting.linkedaccount.category

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
import kotlinx.coroutines.flow.collect

class LinkedAccountListFragment(private val listType: String) : Fragment(R.layout.fragment_linked_account),
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
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinkedAccountListBinding.inflate(inflater)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                LinkedAccountListViewModel::class.java
        ]

        binding.rvLinkedAccountList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            linkedAccountListAdapter = LinkedAccountListAdapter(this@LinkedAccountListFragment, listType)
            adapter = linkedAccountListAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getParentId().observe(viewLifecycleOwner) { parentId ->
            viewModel.parentId = parentId

            if (listType == REQUESTED_TYPE) {
                viewModel.getListOfInvitedLinkedAccount(viewModel.parentId)
                println("LIST FRAGMENT TYPE: INI REQUESTED TYPE")
            } else if (listType == INVITED_TYPE) {
                viewModel.getListOfInvitedLinkedAccount(viewModel.parentId)
                println("LIST FRAGMENT TYPE: INI INVITED TYPE")
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.linkedAccountListEvent.collect { event ->
                when(event) {
                    is LinkedAccountListViewModel.LinkedAccountListEvent.ErrorAction -> {

                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.ErrorGetInvitedList -> {

                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.ErrorGetRequestedList -> {

                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.LoadingAction -> {

                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.LoadingGetInvitedList -> {

                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.LoadingGetRequestedList -> {

                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.SuccessAction -> {

                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.SuccessGetInvitedList -> {
                        linkedAccountListAdapter.submitList(event.result)
                    }
                    is LinkedAccountListViewModel.LinkedAccountListEvent.SuccessGetRequestedList -> {
                        linkedAccountListAdapter.submitList(event.result)
                    }
                }
            }
        }
    }

    override fun onItemClicked(item: LinkedAccountsResponse.ChildrenData) {

    }
}