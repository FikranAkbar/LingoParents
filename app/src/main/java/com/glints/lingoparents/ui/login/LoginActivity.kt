package com.glints.lingoparents.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.glints.lingoparents.databinding.ActivityLoginBinding
import com.glints.lingoparents.ui.forgotpassword.ForgotPasswordViewModel
import com.glints.lingoparents.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModelFactory()
        setViewModel()
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
}