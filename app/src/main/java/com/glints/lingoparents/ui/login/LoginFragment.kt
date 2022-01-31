package com.glints.lingoparents.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentLoginBinding
import com.glints.lingoparents.ui.authentication.AuthenticationActivity
import com.glints.lingoparents.utils.AuthFormValidator
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: LoginViewModel

    private lateinit var googleSignInClient: GoogleSignInClient

    /**
     * Newer version of how to use startActivityForResult method, which is ActivityResultLauncher.
     * This object is specifically used to handle google sign in intent.
     */
    private val loginWithGoogleIntentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                handleSignInResult(task)
            }
        }

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
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loginEvent.collect { event ->
                when (event) {
                    is LoginViewModel.LoginEvent.TryToLoginUser -> {
                        handleOnTryToLoginUser(event)
                    }
                    is LoginViewModel.LoginEvent.Loading -> {
                        handleOnLoginLoading()
                    }
                    is LoginViewModel.LoginEvent.Success -> {
                        handleOnLoginSuccess()
                    }
                    is LoginViewModel.LoginEvent.Error -> {
                        handleOnLoginError(event)
                    }
                    is LoginViewModel.LoginEvent.NavigateToForgotPassword -> {
                        handleOnNavigateToForgotPassword()
                    }
                    is LoginViewModel.LoginEvent.NavigateToRegister -> {
                        handleOnNavigateToRegister()
                    }
                    is LoginViewModel.LoginEvent.TryToLoginWithGoogle -> {
                        handleOnTryToLoginWithGoogle()
                    }
                    is LoginViewModel.LoginEvent.LoginWithGoogleFailure -> {
                        handleOnLoginWithGoogleFailure(event)
                    }
                }
            }
        }
    }

    private fun handleOnTryToLoginUser(event: LoginViewModel.LoginEvent.TryToLoginUser) {
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

    private fun handleOnLoginLoading() {
        showLoading(true)
    }

    private fun handleOnLoginSuccess() {
        CoroutineScope(Dispatchers.Unconfined).launch {
            delay(100)

            val intent = Intent(
                this@LoginFragment.requireContext(),
                AuthenticationActivity::class.java
            )
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun handleOnLoginError(event: LoginViewModel.LoginEvent.Error) {
        showLoading(false)
        event.message.let { message ->
            when {
                message.contains("email", ignoreCase = true) -> {
                    AuthFormValidator.showFieldError(binding.tilEmail, message)
                }
                message.contains("password", ignoreCase = true) -> {
                    AuthFormValidator.showFieldError(binding.tilPassword, message)
                }
                else -> {
                    if (event.message.lowercase().contains("user not found")) {
                        event.idToken?.let { idToken ->
                            val action =
                                LoginFragmentDirections.actionLoginFragmentToRegisterFragment(
                                    idToken)
                            findNavController().navigate(action)
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Snackbar.make(binding.root,
                                event.message,
                                Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(resources.getColor(R.color.error_color,
                                    null))
                                .setTextColor(Color.WHITE)
                                .show()
                        } else {
                            Snackbar.make(binding.root,
                                event.message,
                                Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(Color.RED)
                                .setTextColor(Color.WHITE)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun handleOnNavigateToForgotPassword() {
        val action =
            LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
        findNavController().navigate(action)
    }

    private fun handleOnNavigateToRegister() {
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        findNavController().navigate(action)
    }

    private fun handleOnTryToLoginWithGoogle() {
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        loginWithGoogleIntentLauncher.launch(signInIntent)
    }

    private fun handleOnLoginWithGoogleFailure(event: LoginViewModel.LoginEvent.LoginWithGoogleFailure) {
        showErrorSnackbar(event.errorMessage)
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

    private fun showErrorSnackbar(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(resources.getColor(R.color.error_color, null))
                .setTextColor(Color.WHITE)
                .show()
        } else {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.RED)
                .setTextColor(Color.WHITE)
                .show()
        }
    }

    private fun setGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
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
                showSuccessSnackbar("Terms & Condition Clicked")
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE
                ds.isUnderlineText = false
            }
        }

        val privacyClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                showSuccessSnackbar("Privacy Policy Clicked")
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
            vLoadingBackground.isVisible = bool
            vLoadingProgress.isVisible = bool
            tilEmail.isEnabled = !bool
            tilPassword.isEnabled = !bool
            mbtnForgetPassword.isClickable = !bool
            mbtnLogin.isClickable = !bool
            mbtnLoginWithGoogle.isClickable = !bool
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            val account = task?.getResult(ApiException::class.java)
            viewModel.loginWithGoogleEmail(account!!.idToken!!)
        } catch (e: ApiException) {
            e.localizedMessage?.let {
                viewModel.onLoginWithGoogleFailure(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}