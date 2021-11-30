package com.glints.lingoparents.ui.login

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentLoginBinding
import com.glints.lingoparents.ui.dashboard.DashboardActivity
import com.glints.lingoparents.utils.AuthFormValidator
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class LoginFragment : Fragment(R.layout.fragment_login) {

    companion object {
        private const val TAG = "LoginFragment"
        private const val RC_GOOGLE_SIGN_IN = 9001
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: LoginViewModel

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentLoginBinding.bind(view)

        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                LoginViewModel::class.java
        ]

        setRegisterTextClickListener()
        setTermsPrivacyTextClickListener()
        setGoogleSignInClient()

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
            mbtnLoginWithGoogle.setOnClickListener {
                viewModel.onLoginWithGoogleClick()
            }
            tilEmail.editText?.setText("fikran7561@gmail.com")
            tilPassword.editText?.setText("fikran7561")
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
                    is LoginViewModel.LoginEvent.TryToLoginWithGoogle -> {
                        val signInIntent = googleSignInClient.signInIntent
                        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
                    }
                    is LoginViewModel.LoginEvent.LoginWithGoogleSuccess -> {
                        Snackbar.make(binding.root, event.account.email as CharSequence, Snackbar.LENGTH_SHORT).show()
                    }
                    is LoginViewModel.LoginEvent.LoginWithGoogleFailure -> {
                        Snackbar.make(binding.root, event.errorMessage, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        setFragmentResultListener("register_user") { _, bundle ->
            val result = bundle.getInt("register_user")
            viewModel.onRegisterUserSuccessful(result)
        }
    }

    private fun setGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            val account = task?.getResult(ApiException::class.java)
            viewModel.onLoginWithGoogleSuccessful(account as GoogleSignInAccount)
        } catch (e: ApiException) {
            viewModel.onLoginWithGoogleFailure("signInResult:failed code" + e.statusCode)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}