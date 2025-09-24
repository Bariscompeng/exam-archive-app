package com.melihberat.mobiluyg

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.melihberat.mobiluyg.Network.ApiClient  // DİKKAT: Network (büyük N)

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        findViewById<MaterialToolbar?>(R.id.topAppBar)?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Backend'den feedbackleri çekip e-posta listesine dönüştür
        lifecycleScope.launch {
            try {
                val items = ApiClient.api(this@FeedbackActivity).getFeedbacks()
                val emails: List<String> = items.mapNotNull { it.email?.takeIf { e -> e.isNotBlank() } }
                if (emails.isEmpty()) {
                    Toast.makeText(this@FeedbackActivity, "Gösterilecek feedback bulunamadı.", Toast.LENGTH_SHORT).show()
                }
                bindEmails(emails)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@FeedbackActivity, "Feedback yüklenemedi: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Kartlardaki e-posta, görüntüle/arsivle/sil tıklamaları
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
                // İstersen burada detay ekranı açabilirsin
            }

            cardRoot.findViewById<View?>(R.id.ivArchive)?.setOnClickListener {
                Toast.makeText(this, "Arşivlendi: $email", Toast.LENGTH_SHORT).show()
                // Backend'e arşiv isteği göndermek istersen burada yap
            }

            cardRoot.findViewById<View?>(R.id.ivDelete)?.setOnClickListener {
                // Kartı UI'dan kaldır
                container.removeView(cardRoot)
                Toast.makeText(this, "Silindi: $email", Toast.LENGTH_SHORT).show()
                // Backend'e silme isteği göndermek istersen burada yap
            }
        }
    }
}

