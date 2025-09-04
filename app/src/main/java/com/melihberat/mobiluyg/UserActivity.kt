package com.melihberat.mobiluyg

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import org.json.JSONArray
import org.json.JSONObject

data class User(val email: String, val name: String)

class UserActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var etSearch: EditText
    private var filterButton: ImageView? = null

    private val allUsers = mutableListOf<User>()
    private val visibleUsers = mutableListOf<User>()

    private val USER_PREF_NAME = "user_prefs"
    private val KEY_USERS_JSON = "users_json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener { handleBack() }

        container = findViewById(R.id.questionContainer)
        etSearch  = findViewById(R.id.etSearchExam)
        filterButton = findFilterButtonFrom(etSearch)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleBack()
        })

        etSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
        etSearch.setOnEditorActionListener { _, actionId, event ->
            val isEnter  = (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            val isSearch = (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE)
            if (isEnter || isSearch) {
                hideKeyboard(etSearch)
                applyFilter(etSearch.text?.toString().orEmpty())
                true
            } else false
        }

        filterButton?.setOnClickListener {
            hideKeyboard(etSearch)
            applyFilter(etSearch.text?.toString().orEmpty())
        }

        setUsers(
            listOf(
                User("melihberat00@gmail.com", "Melih Berat"),
                User("ayse.yilmaz@example.com", "Ayşe Yılmaz"),
                User("mehmet.kaya@example.com", "Mehmet Kaya"),
                User("zeynep.demir@example.com", "Zeynep Demir"),
                User("ali.celik@example.com", "Ali Çelik")
            )
        )
    }

    private fun setUsers(users: List<User>) {
        allUsers.clear()
        allUsers.addAll(users)
        visibleUsers.clear()
        visibleUsers.addAll(users)
        renderUsers(visibleUsers)
        saveUsersToPrefs(allUsers)
    }

    private fun saveUsersToPrefs(users: List<User>) {
        val arr = JSONArray()
        for (u in users) {
            arr.put(JSONObject().apply {
                put("email", u.email)
                put("name", u.name)
            })
        }
        getSharedPreferences(USER_PREF_NAME, MODE_PRIVATE)
            .edit()
            .putString(KEY_USERS_JSON, arr.toString())
            .apply()
    }

    private fun applyFilter(queryRaw: String) {
        val q = queryRaw.trim().lowercase()
        visibleUsers.clear()
        if (q.isEmpty()) {
            visibleUsers.addAll(allUsers)
        } else {
            for (u in allUsers) {
                val haystack = "${u.email} ${u.name}".lowercase()
                if (haystack.contains(q)) visibleUsers.add(u)
            }
        }
        renderUsers(visibleUsers)
    }

    private fun renderUsers(users: List<User>) {
        container.removeAllViews()
        for (u in users) {
            container.addView(inflateUserCard(u))
        }
    }

    private fun inflateUserCard(user: User): View {
        val v = layoutInflater.inflate(R.layout.item_user_card, container, false)
        v.findViewById<TextView?>(R.id.tvExamTitle)?.text = "${user.email} • ${user.name}"
        return v
    }

    private fun isFilterActive(): Boolean {
        if (!etSearch.text.isNullOrBlank()) return true
        return visibleUsers.size != allUsers.size
    }

    private fun resetFilter() {
        etSearch.text?.clear()
        visibleUsers.clear()
        visibleUsers.addAll(allUsers)
        renderUsers(visibleUsers)
    }

    private fun handleBack() {
        if (isFilterActive()) {
            resetFilter()
        } else {
            finish()
        }
    }

    private fun findFilterButtonFrom(editText: EditText): ImageView? {
        val row = editText.parent as? ViewGroup ?: return null
        var editIndex = -1
        for (i in 0 until row.childCount) {
            if (row.getChildAt(i) === editText) { editIndex = i; break }
        }
        if (editIndex == -1) return null
        for (j in editIndex + 1 until row.childCount) {
            val v = row.getChildAt(j)
            if (v is ImageView) return v
        }
        return null
    }

    private fun hideKeyboard(target: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(target.windowToken, 0)
    }
}
