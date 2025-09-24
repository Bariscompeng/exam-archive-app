package com.melihberat.mobiluyg

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
<<<<<<< HEAD
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.melihberat.mobiluyg.Network.ApiClient  // DİKKAT: Network (büyük N)
=======
>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

<<<<<<< HEAD
=======

>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c
        findViewById<MaterialToolbar?>(R.id.topAppBar)?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

<<<<<<< HEAD
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
=======
        val emails = listOf(
            "melihberat00@gmail.com",
            "ayse@example.com",
            "ali@example.com",
            "zehra@example.com",
            "can@example.com"
        )

        bindEmails(emails)
    }

>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c
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
<<<<<<< HEAD
                // İstersen burada detay ekranı açabilirsin
=======
                // TODO: Detay ekranı açmak istersen burada Intent başlat
>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c
            }

            cardRoot.findViewById<View?>(R.id.ivArchive)?.setOnClickListener {
                Toast.makeText(this, "Arşivlendi: $email", Toast.LENGTH_SHORT).show()
<<<<<<< HEAD
                // Backend'e arşiv isteği göndermek istersen burada yap
            }

            cardRoot.findViewById<View?>(R.id.ivDelete)?.setOnClickListener {
                // Kartı UI'dan kaldır
                container.removeView(cardRoot)
                Toast.makeText(this, "Silindi: $email", Toast.LENGTH_SHORT).show()
                // Backend'e silme isteği göndermek istersen burada yap
=======
                // TODO: Arşivleme mantığını burada uygula
            }

            cardRoot.findViewById<View?>(R.id.ivDelete)?.setOnClickListener {

                container.removeView(cardRoot)
                Toast.makeText(this, "Silindi: $email", Toast.LENGTH_SHORT).show()
>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c
            }
        }
    }
}
<<<<<<< HEAD

=======
>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c
