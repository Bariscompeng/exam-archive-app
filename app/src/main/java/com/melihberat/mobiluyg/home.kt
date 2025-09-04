package com.melihberat.mobiluyg

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.Normalizer
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var bottom: BottomNavigationView
    private lateinit var etSearch: EditText

    private val categoryAliases = mapOf(
        "bilgisayar bilimleri" to "Bilgisayar Bilimleri",
        "matematik" to "Matematik",
        "fizik" to "Fizik",
        "kimya" to "Kimya",
        "biyoloji" to "Biyoloji",
        "sanat" to "Sanat",
        // kısa yazımlar (opsiyon)
        "cs" to "Bilgisayar Bilimleri",
        "bio" to "Biyoloji"
    )

    private val lessonAliases = mapOf(
        // Bilgisayar Bilimleri
        "algoritma" to "Algoritma",
        "veri yapilari" to "Veri Yapıları",
        "veri yapıları" to "Veri Yapıları",
        "programlama" to "Programlama",
        "c" to "C Dilinde Programlama",
        "c dili" to "C Dilinde Programlama",
        "java" to "Java Programlama",
        "python" to "Python Programlama",
        "veritabani" to "Veritabanı",
        "veritabanı" to "Veritabanı",
        "sql" to "SQL",

        // Matematik
        "lineer cebir" to "Lineer Cebir",
        "analiz" to "Matematiksel Analiz",
        "integral" to "İntegral",
        "turev" to "Türev",
        "türev" to "Türev",
        "olasilik" to "Olasılık",
        "olasılık" to "Olasılık",

        // Fizik
        "mekanik" to "Mekanik",
        "elektromanyetizma" to "Elektromanyetizma",
        "kuantum" to "Kuantum Fiziği",

        // Kimya
        "organik kimya" to "Organik Kimya",
        "analitik kimya" to "Analitik Kimya",

        // Biyoloji
        "genetik" to "Genetik",

        // Sanat
        "müzik" to "Müzik",
        "muzik" to "Müzik",
        "resim" to "Resim",
        "heykel" to "Heykel"
    )


    private var lastNavigateAt = 0L
    private fun safeNavigate(block: () -> Unit) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastNavigateAt < 500) return // 0.5 sn içinde ikinci çağrıyı yoksay
        lastNavigateAt = now
        block()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val username = intent.getStringExtra(LoginActivity.EXTRA_USERNAME)
            ?.takeIf { it.isNotBlank() } ?: "Melih"
        tvWelcome.text = "Hoş geldin $username"

        fun openCategoryFromCard(title: String) {
            startActivity(Intent(this, CategoryActivity::class.java).apply {
                putExtra(CategoryActivity.EXTRA_CATEGORY, title)
            })
        }
        findViewById<View>(R.id.llCatCS).setOnClickListener { openCategoryFromCard("Bilgisayar Bilimleri") }
        findViewById<View>(R.id.llCatMath).setOnClickListener { openCategoryFromCard("Matematik") }
        findViewById<View>(R.id.llCatPhysics).setOnClickListener { openCategoryFromCard("Fizik") }
        findViewById<View>(R.id.llCatChem).setOnClickListener { openCategoryFromCard("Kimya") }
        findViewById<View>(R.id.llCatBio).setOnClickListener { openCategoryFromCard("Biyoloji") }
        findViewById<View>(R.id.llCatArt).setOnClickListener { openCategoryFromCard("Sanat") }


        etSearch = findViewById(R.id.etSearch)
        etSearch.setOnEditorActionListener { _, actionId, event ->
            val isImeAction = actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
            val isEnterKey = event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN

            if (isImeAction || isEnterKey) {
                handleSearch()
                true
            } else {
                false
            }
        }


        bottom = findViewById(R.id.bottomNav)
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_fav -> {
                    startActivity(
                        Intent(this, FavoritesActivity::class.java)
                            .putExtra(LoginActivity.EXTRA_USERNAME, username)
                    )
                    true
                }
                R.id.nav_profile -> {
                    startActivity(
                        Intent(this, ProfileActivity::class.java)
                            .putExtra(LoginActivity.EXTRA_USERNAME, username)
                    )
                    true
                }
                else -> false
            }
        }
        bottom.menu.findItem(R.id.nav_home).isChecked = true
    }

    override fun onResume() {
        super.onResume()
        bottom.menu.findItem(R.id.nav_home).isChecked = true
    }


    private fun handleSearch() {

        currentFocus?.clearFocus()
        getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(etSearch.windowToken, 0)

        val raw = etSearch.text?.toString()?.trim().orEmpty()
        if (raw.isEmpty()) {
            Toast.makeText(this, "Lütfen bir ders veya kategori yazın.", Toast.LENGTH_SHORT).show()
            return
        }

        val key = normalizeTr(raw)


        categoryAliases[key]?.let { cat ->
            safeNavigate { openCategory(cat) }
            return
        }


        lessonAliases[key]?.let { canonical ->
            safeNavigate { openLesson(canonical) }
            return
        }


        lessonAliases.keys.firstOrNull { key.contains(it) || it.contains(key) }?.let { lk ->
            safeNavigate { openLesson(lessonAliases[lk]!!) }
            return
        }


        categoryAliases.keys.firstOrNull { key.contains(it) || it.contains(key) }?.let { ck ->
            safeNavigate { openCategory(categoryAliases[ck]!!) }
            return
        }

        Toast.makeText(this, "Bu ada uygun ders/kategori bulunamadı.", Toast.LENGTH_SHORT).show()
    }

    private fun openCategory(title: String) {
        startActivity(Intent(this, CategoryActivity::class.java).apply {
            putExtra(CategoryActivity.EXTRA_CATEGORY, title)
        })
    }

    private fun openLesson(canonicalLesson: String) {
        startActivity(Intent(this, LessonActivity::class.java).apply {
            putExtra(LessonActivity.EXTRA_LESSON, canonicalLesson)
        })
    }

    private fun normalizeTr(input: String): String {
        val lower = input.lowercase(Locale("tr", "TR"))
        val noDiacritics = Normalizer.normalize(lower, Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
        return noDiacritics
            .replace('ğ', 'g')
            .replace('ü', 'u')
            .replace('ş', 's')
            .replace('ı', 'i')
            .replace('ö', 'o')
            .replace('ç', 'c')
            .trim()
    }
}
