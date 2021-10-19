package com.glints.lingoparents.ui.forgotpassword

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.glints.lingoparents.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelProvider.NewInstanceFactory()
        )[
                ForgotPasswordViewModel::class.java
        ]
    }
}