package com.melihberat.mobiluyg

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.util.*

class CategoryActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        private const val PREF_NAME = "cat_items"
        private const val KEY_ITEMS = "items"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        val categoryTitle = intent.getStringExtra(EXTRA_CATEGORY).orEmpty()
        if (categoryTitle.isNotBlank()) toolbar.title = categoryTitle
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_home
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (this !is HomeActivity) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_fav -> {
                    if (this !is FavoritesActivity) {
                        startActivity(Intent(this, FavoritesActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_profile -> {
                    if (this !is ProfileActivity) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish()
                    }
                    true
                }
                else -> false
            }
        }

        loadDynamicLessonsFor(toolbar.title?.toString().orEmpty())
    }

    override fun onResume() {
        super.onResume()
        loadDynamicLessonsFor(findViewById<MaterialToolbar>(R.id.topAppBar).title?.toString().orEmpty())
    }

    private fun loadDynamicLessonsFor(category: String) {
        val container = findViewById<LinearLayout>(R.id.categoryContainer)
        container.removeAllViews()
        val prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(KEY_ITEMS, "[]"))
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val cat = o.optString("category")
            val lesson = o.optString("lesson")
            if (sameTr(cat, category)) {
                container.addView(createLessonButton(lesson))
            }
        }
    }

    private fun createLessonButton(text: String): MaterialButton {
        val btn = MaterialButton(this, null, com.google.android.material.R.attr.materialButtonStyle)
        btn.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(64f).toInt()
        ).apply { bottomMargin = dp(14f).toInt() }
        btn.text = text
        btn.isAllCaps = false
        btn.setTextColor(0xFF555879.toInt())
        btn.textSize = 18f
        btn.cornerRadius = dp(12f).toInt()
        btn.iconPadding = dp(12f).toInt()
        btn.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFC9C6FF.toInt())
        btn.setOnClickListener {
            startActivity(
                Intent(this, LessonActivity::class.java)
                    .putExtra(LessonActivity.EXTRA_LESSON, text)
            )
        }
        return btn
    }

    private fun dp(v: Float): Float = v * resources.displayMetrics.density

    private fun foldTr(s: String): String {
        val map = mapOf(
            'ç' to 'c','Ç' to 'c',
            'ğ' to 'g','Ğ' to 'g',
            'ı' to 'i','I' to 'i','İ' to 'i',
            'ö' to 'o','Ö' to 'o',
            'ş' to 's','Ş' to 's',
            'ü' to 'u','Ü' to 'u'
        )
        val b = StringBuilder()
        for (ch in s) b.append(map[ch] ?: ch.lowercaseChar())
        return b.toString().lowercase(Locale.ROOT).trim()
    }

    private fun sameTr(a: String, b: String): Boolean = foldTr(a) == foldTr(b)
}
