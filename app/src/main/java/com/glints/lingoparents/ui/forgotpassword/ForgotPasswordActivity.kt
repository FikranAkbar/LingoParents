package com.glints.lingoparents.ui.forgotpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.glints.lingoparents.databinding.ActivityForgotPasswordBinding
import com.glints.lingoparents.viewmodel.ViewModelFactory

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
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
                    ForgotPasswordViewModel::class.java
        ]
    }
}