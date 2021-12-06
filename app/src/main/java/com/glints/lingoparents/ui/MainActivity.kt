package com.glints.lingoparents.ui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.ActivityMainBinding
import com.glints.lingoparents.ui.dashboard.DashboardViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val flag = intent.getIntExtra("flag", -1)
        if (flag == DashboardViewModel.TOKEN_EXPIRED_FLAG) {
            Snackbar.make(binding.root, "Token expired. Please re-login...", Snackbar.LENGTH_LONG).show()
        }
    }
}

const val REGISTER_USER_RESULT_OK = Activity.RESULT_FIRST_USER + 1