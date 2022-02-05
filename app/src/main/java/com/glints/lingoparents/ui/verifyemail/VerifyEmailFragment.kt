package com.glints.lingoparents.ui.verifyemail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentVerifyEmailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VerifyEmailFragment : Fragment(R.layout.fragment_verify_email) {

    private var _binding: FragmentVerifyEmailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VerifyEmailViewModel by viewModels()


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentVerifyEmailBinding.bind(view)

        CoroutineScope(Dispatchers.Unconfined).launch {
            var dotCount = 0
            val defaultText = binding.tvVerifyEmail.text.toString()
            while (true) {
                delay(500)

                binding.tvVerifyEmail.apply {

                    if (dotCount >= 3) {
                        dotCount = 0
                        this.text = defaultText
                    } else {
                        this.text = this.text.toString() + "."
                        dotCount++
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}