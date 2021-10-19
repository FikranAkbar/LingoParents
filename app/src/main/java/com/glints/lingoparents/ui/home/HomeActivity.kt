package com.glints.lingoparents.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.glints.lingoparents.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelProvider.NewInstanceFactory()
        )[
                HomeViewModel::class.java
        ]
    }
}