package com.glints.lingoparents.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.glints.lingoparents.databinding.ActivityHomeBinding
import com.glints.lingoparents.viewmodel.ViewModelFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
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
                    HomeViewModel::class.java
        ]
    }
}