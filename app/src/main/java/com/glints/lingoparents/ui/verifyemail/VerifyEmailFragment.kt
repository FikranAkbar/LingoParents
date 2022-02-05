package com.glints.lingoparents.ui.verifyemail

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentVerifyEmailBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class VerifyEmailFragment : Fragment(R.layout.fragment_verify_email) {

    private var _binding: FragmentVerifyEmailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VerifyEmailViewModel by viewModels()


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentVerifyEmailBinding.bind(view)

        val token = arguments?.getString("token") as String
        val id = arguments?.getString("id") as String

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.verifyEmailEvent.collect { event ->
                when(event) {
                    is VerifyEmailViewModel.VerifyEmailEvent.Error -> {
                        showErrorSnackbar(event.message)
                        delay(200)
                    }
                    is VerifyEmailViewModel.VerifyEmailEvent.Success -> {
                        showSuccessSnackbar(event.message)
                        delay(200)
                    }
                }

                val action = VerifyEmailFragmentDirections.actionGlobalLoginFragment()
                findNavController().navigate(action)
            }
        }

        CoroutineScope(Dispatchers.Unconfined).launch {
            var dotCount = 0
            val defaultText = binding.tvVerifyEmail.text.toString()
            val currentTitle = binding.tvVerifyEmail

            viewModel.verifyUserEmail(token, id)

            repeat(15) {
                delay(500)

                if (dotCount >= 3) {
                    dotCount = 0
                    withContext(Dispatchers.Main) {
                        currentTitle.text = defaultText
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        currentTitle.text = currentTitle.text.toString() + "."
                    }
                    dotCount++
                }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}