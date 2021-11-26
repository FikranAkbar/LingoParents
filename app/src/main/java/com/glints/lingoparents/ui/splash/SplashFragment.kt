package com.glints.lingoparents.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentSplashBinding
import com.glints.lingoparents.ui.dashboard.DashboardActivity
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import kotlinx.coroutines.flow.collect

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: SplashViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSplashBinding.bind(view)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                SplashViewModel::class.java
        ]

        viewModel.isAccessTokenExist().observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                viewModel.sendNavigateToAuthScreenEvent()
            }
            else if (it.isNotEmpty()) {
                viewModel.sendNavigateToHomeScreenEvent()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.splashEvent.collect { event ->
                when (event) {
                    SplashViewModel.SplashEvent.NavigateToAuthScreen -> {
                        val action = SplashFragmentDirections.actionGlobalLoginFragment()
                        findNavController().navigate(action)
                    }
                    SplashViewModel.SplashEvent.NavigateToHomeScreen -> {
                        val intent = Intent(this@SplashFragment.requireContext(), DashboardActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
            }
        }
    }
}