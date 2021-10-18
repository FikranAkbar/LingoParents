package com.glints.lingoparents.ui.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.glints.lingoparents.databinding.ActivityRegisterBinding
import com.glints.lingoparents.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModelFactory()
        setViewModel()
    }

    private fun setViewModelFactory() {
        factory = ViewModelFactory.getInstance()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(
            this, factory
        )[
                RegisterViewModel::class.java
        ]
    }
}