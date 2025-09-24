package com.melihberat.mobiluyg

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var tilFullName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilRegPassword: TextInputLayout
    private lateinit var etFullName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etRegPassword: TextInputEditText
    private lateinit var btnDoRegister: MaterialButton
    private lateinit var btnGoLogin: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        tilFullName = findViewById(R.id.tilFullName)
        tilEmail = findViewById(R.id.tilEmail)
        tilRegPassword = findViewById(R.id.tilRegPassword)
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etRegPassword = findViewById(R.id.etRegPassword)
        btnDoRegister = findViewById(R.id.btnDoRegister)
        btnGoLogin = findViewById(R.id.btnGoLogin)


        tilFullName.isEndIconVisible = false
        tilEmail.isEndIconVisible = false


        etFullName.doOnTextChanged { text, _, _, _ ->
            val ok = isValidName(text?.toString())
            tilFullName.error = if (!ok && !text.isNullOrEmpty()) "Ad soyad çok kısa" else null
            tilFullName.isEndIconVisible = ok
        }

        etEmail.doOnTextChanged { text, _, _, _ ->
            val email = text?.toString()?.trim().orEmpty()
            val ok = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
            tilEmail.error = if (!ok && email.isNotEmpty()) "Geçerli bir e-mail girin" else null
            tilEmail.isEndIconVisible = ok
        }


        btnDoRegister.setOnClickListener {
            val name = etFullName.text?.toString()?.trim().orEmpty()
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val pass = etRegPassword.text?.toString().orEmpty()

            var ok = true
            if (!isValidName(name)) {
                tilFullName.error = "Ad soyad zorunlu"
                ok = false
            } else tilFullName.error = null

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Geçerli e-mail zorunlu"
                ok = false
            } else tilEmail.error = null

            if (pass.length < 4) {
                tilRegPassword.error = "Şifre en az 4 karakter"
                ok = false
            } else tilRegPassword.error = null

            if (!ok) return@setOnClickListener


            Toast.makeText(this, "Kayıt başarılı (demo)", Toast.LENGTH_SHORT).show()


            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        btnGoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun isValidName(value: String?): Boolean {
        val v = value?.trim().orEmpty()

        return v.length >= 2
    }
}
