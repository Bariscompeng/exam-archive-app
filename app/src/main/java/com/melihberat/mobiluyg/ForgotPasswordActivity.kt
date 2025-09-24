package com.melihberat.mobiluyg

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var tilEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnContinue: MaterialButton
    private lateinit var tvBackToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        tilEmail = findViewById(R.id.tilForgotEmail)
        etEmail = findViewById(R.id.etForgotEmail)
        btnContinue = findViewById(R.id.btnForgotContinue)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)

        btnContinue.isEnabled = false

        etEmail.doOnTextChanged { text, _, _, _ ->
            val email = text?.toString()?.trim().orEmpty()
            val valid = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
            tilEmail.error = if (!valid && email.isNotEmpty()) "Geçerli bir e-mail girin" else null
            btnContinue.isEnabled = valid
        }

        etEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                if (btnContinue.isEnabled) btnContinue.performClick()
                true
            } else false
        }

        btnContinue.setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            startActivity(Intent(this, OtpValidationActivity::class.java).apply {
                putExtra("EXTRA_EMAIL", email)
            })
        }


        tvBackToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
