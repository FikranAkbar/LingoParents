package com.glints.lingoparents.ui.login

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect


class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentLoginBinding.bind(view)

        setRegisterTextClickListener()
        setTermsPrivacyTextClickListener()

        binding.apply {
            mbtnLogin.setOnClickListener {
                viewModel.onLoginButtonClick(
                    tilEmail.editText?.text.toString(),
                    tilPassword.editText?.text.toString())

                closeKeyboard()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loginEvent.collect { event ->
                when(event) {
                    is LoginViewModel.LoginEvent.Error -> {

                    }
                    is LoginViewModel.LoginEvent.Loading -> {

                    }
                    is LoginViewModel.LoginEvent.NavigateToForgotPassword -> {

                    }
                    is LoginViewModel.LoginEvent.NavigateToRegister -> {

                    }
                    is LoginViewModel.LoginEvent.Success -> {
                        Snackbar.make(binding.root, event.result, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setRegisterTextClickListener() {
        val newText = getString(R.string.new_to_lingoparents)
        val ss = SpannableString(newText)

        val registerNowClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(activity, "Register Clicked", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE
                ds.isUnderlineText = false
            }
        }

        ss.setSpan(registerNowClickableSpan, 21, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvNewToLingoparents.apply {
            text = ss
            movementMethod = LinkMovementMethod.getInstance()
        }

    }

    private fun setTermsPrivacyTextClickListener() {
        val newText = getString(R.string.terms_privacy_bottom_text)
        val ss = SpannableString(newText)

        val termClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                Toast.makeText(activity, "Terms Clicked", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE
                ds.isUnderlineText = false
            }
        }

        val privacyClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                Toast.makeText(activity, "Privacy Clicked", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE
                ds.isUnderlineText = false
            }
        }

        ss.setSpan(termClickableSpan, 47, 66, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(privacyClickableSpan, 71, 84, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvTermsPrivacyBottomText.apply {
            text = ss
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun closeKeyboard() {
        requireActivity().apply {
            val view = currentFocus
            if (view != null) {
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
}