package com.melihberat.mobiluyg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

import com.melihberat.mobiluyg.Network.ApiClient
import com.melihberat.mobiluyg.Network.TokenStore
import com.melihberat.mobiluyg.Network.ApiResult
import com.melihberat.mobiluyg.Network.safeCall
import com.melihberat.mobiluyg.Network.model.LoginRequest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USERNAME = "EXTRA_USERNAME"
        const val EXTRA_ROLE = "EXTRA_ROLE"
        private const val KEY_REMEMBER = "REMEMBER"
        private const val PREFS = "app_prefs"
    }

    private lateinit var tilUsername: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var cbRemember: CheckBox
    private lateinit var tvForgot: TextView
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnRegister: MaterialButton

    private lateinit var toggleRole: MaterialButtonToggleGroup
    private lateinit var btnRoleUser: MaterialButton
    private lateinit var btnRoleAdmin: MaterialButton

    private var currentRole: String = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Oturum hatırlama
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        val remembered = prefs.getBoolean(KEY_REMEMBER, false)
        val savedUser = prefs.getString(EXTRA_USERNAME, null)
        val savedRole = prefs.getString(EXTRA_ROLE, null)

        if (remembered && !savedUser.isNullOrBlank() && !savedRole.isNullOrBlank()) {
            val intent = if (savedRole == "admin") {
                Intent(this, AdminHomeActivity::class.java)
            } else {
                Intent(this, HomeActivity::class.java)
            }.apply {
                putExtra(EXTRA_USERNAME, savedUser)
                putExtra(EXTRA_ROLE, savedRole)
            }
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        tilUsername = findViewById(R.id.tilUsername)
        tilPassword = findViewById(R.id.tilPassword)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        cbRemember = findViewById(R.id.cbRemember)
        tvForgot = findViewById(R.id.tvForgot)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        toggleRole = findViewById(R.id.toggleRole)
        btnRoleUser = findViewById(R.id.btnRoleUser)
        btnRoleAdmin = findViewById(R.id.btnRoleAdmin)

        cbRemember.isChecked = remembered

        etPassword.hint = null
        toggleRole.check(btnRoleUser.id)
        applyRoleUi("user")

        toggleRole.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            currentRole = if (checkedId == btnRoleAdmin.id) "admin" else "user"
            applyRoleUi(currentRole)
        }

        etPassword.imeOptions = EditorInfo.IME_ACTION_DONE
        etPassword.setOnEditorActionListener { _, actionId, event ->
            val imeDone = actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO
            val enterKey = event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP
            if (imeDone || enterKey) { attemptLogin(); true } else false
        }

        etUsername.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) v.showKeyboard() }
        etPassword.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) v.showKeyboard() }

        btnLogin.setOnClickListener { attemptLogin() }
        btnRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }

        tvForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun applyRoleUi(role: String) {
        tilPassword.hint = if (role == "admin") "Özel Şifre" else "Şifre"
    }

    private fun attemptLogin() {
        tilUsername.error = null
        tilPassword.error = null

        val u = etUsername.text?.toString()?.trim().orEmpty()
        val p = etPassword.text?.toString()?.trim().orEmpty()

        var ok = true
        if (u.isEmpty()) { tilUsername.error = "Kullanıcı adı/email zorunlu"; ok = false }
        if (p.isEmpty()) { tilPassword.error = "Şifre zorunlu"; ok = false }
        if (!ok) return

        hideKeyboard(etPassword)

        lifecycleScope.launch {
            when (val res = safeCall { ApiClient.api(this@LoginActivity).login(LoginRequest(u, p)) }) {
                is ApiResult.Success -> {
                    // TOKEN'I KAYDET
                    TokenStore.setToken(this@LoginActivity, res.data.token)

                    // "Beni hatırla" ayarı
                    val editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                        .putBoolean(KEY_REMEMBER, cbRemember.isChecked)
                    if (cbRemember.isChecked) {
                        editor.putString(EXTRA_USERNAME, res.data.user.name)
                        editor.putString(EXTRA_ROLE, "user")
                    } else {
                        editor.remove(EXTRA_USERNAME)
                        editor.remove(EXTRA_ROLE)
                    }
                    editor.apply()

                    Toast.makeText(this@LoginActivity, "Hoş geldin ${res.data.user.name}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java).apply {
                        putExtra(EXTRA_USERNAME, res.data.user.name)
                        putExtra(EXTRA_ROLE, "user")
                    })
                    finish()
                }
                is ApiResult.Error -> {
                    tilPassword.error = "Giriş başarısız: ${res.message}"
                }
            }
        }
    }

    private fun View.showKeyboard() {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

