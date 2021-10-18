package com.glints.lingoparents.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.glints.lingoparents.R
import com.glints.lingoparents.viewmodel.ViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var factory: ViewModelFactory
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

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
                SplashViewModel::class.java
        ]
    }
}