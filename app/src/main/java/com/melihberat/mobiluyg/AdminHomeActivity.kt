package com.melihberat.mobiluyg

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import org.json.JSONArray

class AdminHomeActivity : AppCompatActivity() {

    private val EXAM_PREF_NAME = "exam_prefs"
    private val EXAM_KEY = "exams"

    private val Q_PREF_NAME = "question_prefs"
    private val Q_KEY = "questions_json"

    private val USER_PREF_NAME = "user_prefs"
    private val KEY_USERS_JSON = "users_json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        val username = intent.getStringExtra(LoginActivity.EXTRA_USERNAME) ?: "admin"
        findViewById<TextView?>(R.id.tvWelcomeAdmin)?.text = "Hoş geldin $username"

        findViewById<MaterialButton>(R.id.btnExamManage).setOnClickListener {
            startActivity(Intent(this, ExamActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnQuestionManage).setOnClickListener {
            startActivity(Intent(this, QuestionActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnCategoryManage).setOnClickListener {
            startActivity(Intent(this, AddCategoryActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnUserManage).setOnClickListener {
            startActivity(Intent(this, UserActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnFeedbackManage).setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnProfile).setOnClickListener {
            startActivity(
                Intent(this, ProfileActivity::class.java)
                    .putExtra(LoginActivity.EXTRA_USERNAME, username)
            )
        }

        updateCounters()
    }

    override fun onResume() {
        super.onResume()
        updateCounters()
    }

    private fun updateCounters() {
        val examCount = try {
            val sp = getSharedPreferences(EXAM_PREF_NAME, MODE_PRIVATE)
            JSONArray(sp.getString(EXAM_KEY, "[]") ?: "[]").length()
        } catch (_: Exception) { 0 }
        findViewById<TextView?>(R.id.tvExamCount)?.text = examCount.toString()

        val questionCount = try {
            val sp = getSharedPreferences(Q_PREF_NAME, MODE_PRIVATE)
            JSONArray(sp.getString(Q_KEY, "[]") ?: "[]").length()
        } catch (_: Exception) { 0 }
        findViewById<TextView?>(R.id.tvQuestionCount)?.text = questionCount.toString()

        val userCount = try {
            val sp = getSharedPreferences(USER_PREF_NAME, MODE_PRIVATE)
            JSONArray(sp.getString(KEY_USERS_JSON, "[]") ?: "[]").length()
        } catch (_: Exception) { 0 }
        findViewById<TextView?>(R.id.tvUserCount)?.text = userCount.toString()
    }
}
