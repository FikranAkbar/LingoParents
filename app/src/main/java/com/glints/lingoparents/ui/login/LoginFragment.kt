package com.glints.lingoparents.ui.login

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.FragmentLoginBinding
import com.glints.lingoparents.ui.MainActivity

import android.R.string.no





class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentLoginBinding.bind(view)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[
                LoginViewModel::class.java
        ]

        setRegisterTextClickListener()
        setTermsPrivacyTextClickListener()
    }

    private fun setRegisterTextClickListener() {
        val newText = getString(R.string.new_to_lingoparents)
        val ss = SpannableString(newText)

        val registerNowClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(activity, "Register Clicked", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE
                ds.isUnderlineText = false
            }
        }

        ss.setSpan(registerNowClickableSpan, 21, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvNewToLingoparents.apply {
            text = ss
            movementMethod = LinkMovementMethod.getInstance()
        }

    }

    private fun setTermsPrivacyTextClickListener() {
        val newText = getString(R.string.terms_privacy_bottom_text)
        val ss = SpannableString(newText)

        val termClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                Toast.makeText(activity, "Terms Clicked", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE
                ds.isUnderlineText = false
            }
        }

        val privacyClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                Toast.makeText(activity, "Privacy Clicked", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE
                ds.isUnderlineText = false
            }
        }

        ss.setSpan(termClickableSpan, 47, 66, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(privacyClickableSpan, 71, 84, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvTermsPrivacyBottomText.apply {
            text = ss
            movementMethod = LinkMovementMethod.getInstance()
        }
    }
}