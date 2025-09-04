package com.melihberat.mobiluyg

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)


        findViewById<MaterialToolbar?>(R.id.topAppBar)?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val emails = listOf(
            "melihberat00@gmail.com",
            "ayse@example.com",
            "ali@example.com",
            "zehra@example.com",
            "can@example.com"
        )

        bindEmails(emails)
    }

    private fun bindEmails(emails: List<String>) {
        val container = findViewById<ViewGroup?>(R.id.feedbackContainer) ?: return
        var idx = 0

        for (i in 0 until container.childCount) {
            val cardRoot = container.getChildAt(i) as? ViewGroup ?: continue
            val email = emails.getOrNull(idx) ?: continue
            idx++

            cardRoot.findViewById<TextView?>(R.id.tvFeedbackEmail)?.text = email

            cardRoot.findViewById<View?>(R.id.ivView)?.setOnClickListener {
                Toast.makeText(this, "Görüntüle: $email", Toast.LENGTH_SHORT).show()
                // TODO: Detay ekranı açmak istersen burada Intent başlat
            }

            cardRoot.findViewById<View?>(R.id.ivArchive)?.setOnClickListener {
                Toast.makeText(this, "Arşivlendi: $email", Toast.LENGTH_SHORT).show()
                // TODO: Arşivleme mantığını burada uygula
            }

            cardRoot.findViewById<View?>(R.id.ivDelete)?.setOnClickListener {

                container.removeView(cardRoot)
                Toast.makeText(this, "Silindi: $email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
