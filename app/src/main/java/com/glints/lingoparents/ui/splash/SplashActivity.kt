package com.glints.lingoparents.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.glints.lingoparents.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setViewModel()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelProvider.NewInstanceFactory())[
                SplashViewModel::class.java
        ]
    }
}