package com.glints.lingoparents.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.glints.lingoparents.databinding.ActivityLoginBinding
import com.glints.lingoparents.ui.forgotpassword.ForgotPasswordViewModel
import com.glints.lingoparents.viewmodel.ViewModelFactory
import com.glints.lingoparents.vo.Status

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var viewModel: LoginViewModel

    private var email: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModelFactory()
        setViewModel()
        loginUser(email, password)
    }

    private fun setViewModelFactory() {
        factory = ViewModelFactory.getInstance()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(
            this, factory)[
                    LoginViewModel::class.java
        ]
    }

    private fun loginUser(email: String, password: String) {
        viewModel.loginUser(email, password).observe(this, { userEntity ->
            if (userEntity != null) {
                when(userEntity.status) {
                    Status.SUCCESS -> {
                        // Login the user and go to home screen
                    }
                    Status.LOADING -> {
                        // Showing loading progress
                    }
                    Status.ERROR -> {
                        // Display the error (ex: with toast)
                    }
                }
            }
        })
    }
}