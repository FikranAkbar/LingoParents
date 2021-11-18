package com.glints.lingoparents.ui.login

import android.content.Context
import android.content.Intent
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
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentLoginBinding
import com.glints.lingoparents.ui.dashboard.DashboardActivity
import com.glints.lingoparents.utils.AuthFormValidator
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect


class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentLoginBinding.bind(view)

        setRegisterTextClickListener()
        setTermsPrivacyTextClickListener()

        binding.apply {
            mbtnLogin.setOnClickListener {
                if (vLoadingProgress.visibility == View.GONE) {
                    viewModel.onLoginButtonClick(
                        tilEmail.editText?.text.toString(),
                        tilPassword.editText?.text.toString()
                    )
                }
                closeKeyboard()
            }
            mbtnForgetPassword.setOnClickListener {
                viewModel.onForgotPasswordButtonClick()
            }
            tilEmail.editText?.setText("calvinsan123@gmail.com")
            tilPassword.editText?.setText("calvin123")
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loginEvent.collect { event ->
                when (event) {
                    is LoginViewModel.LoginEvent.TryToLoginUser -> {
                        binding.apply {
                            AuthFormValidator.apply {
                                hideFieldError(arrayListOf(tilEmail, tilPassword))

                                val email = event.email
                                val password = event.password

                                if (isValidEmail(email) &&
                                    isValidPassword(password)
                                ) {
                                    viewModel.loginUserByEmailPassword(email, password)
                                } else {
                                    if (!isValidEmail(email)) {
                                        showFieldError(tilEmail, EMAIL_WRONG_FORMAT_ERROR)
                                    }
                                    if (!isValidPassword(password)) {
                                        showFieldError(tilPassword, PASSWORD_EMPTY_ERROR)
                                    }
                                }
                            }
                        }
                    }
                    is LoginViewModel.LoginEvent.Loading -> {
                        showLoading(true)
                    }
                    is LoginViewModel.LoginEvent.Success -> {
                        showLoading(false)
                        val intent = Intent(
                            this@LoginFragment.requireContext(),
                            DashboardActivity::class.java
                        )
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    is LoginViewModel.LoginEvent.Error -> {
                        showLoading(false)
                        event.message.let {
                            if (it.contains("email", ignoreCase = true)) {
                                AuthFormValidator.showFieldError(binding.tilEmail, it)
                            } else if (it.contains("password", ignoreCase = true)) {
                                AuthFormValidator.showFieldError(binding.tilPassword, it)
                            }
                        }
                    }
                    is LoginViewModel.LoginEvent.NavigateToForgotPassword -> {
                        val action =
                            LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
                        findNavController().navigate(action)
                    }
                    is LoginViewModel.LoginEvent.NavigateToRegister -> {
                        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                        findNavController().navigate(action)
                    }
                    is LoginViewModel.LoginEvent.ShowSnackBarMessage -> {
                        Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        setFragmentResultListener("register_user") { _, bundle ->
            val result = bundle.getInt("register_user")
            viewModel.onRegisterUserSuccessful(result)
        }
    }

    private fun setRegisterTextClickListener() {
        val newText = getString(R.string.new_to_lingoparents)
        val ss = SpannableString(newText)

        val registerNowClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                viewModel.onRegisterButtonClick()
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
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun showLoading(bool: Boolean) {
        binding.apply {
            when (bool) {
                true -> {
                    vLoadingBackground.visibility = View.VISIBLE
                    vLoadingProgress.visibility = View.VISIBLE
                }
                else -> {
                    vLoadingBackground.visibility = View.GONE
                    vLoadingProgress.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}