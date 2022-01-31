package com.glints.lingoparents.ui.authentication

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.ActivityAuthenticationBinding
import com.glints.lingoparents.ui.dashboard.TOKEN_EXPIRED_FLAG
import com.google.android.material.snackbar.Snackbar

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val flag = intent.getIntExtra("flag", -1)
        if (flag == TOKEN_EXPIRED_FLAG) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Snackbar.make(binding.root,
                    getString(R.string.token_expired_error),
                    Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(resources.getColor(R.color.error_color, null))
                    .setTextColor(Color.WHITE)
                    .show()
            } else {
                Snackbar.make(binding.root,
                    getString(R.string.token_expired_error),
                    Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .setTextColor(Color.WHITE)
                    .show()
            }
        }
    }
}