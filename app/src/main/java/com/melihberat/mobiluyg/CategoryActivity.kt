package com.melihberat.mobiluyg

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
<<<<<<< HEAD
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import androidx.lifecycle.lifecycleScope
import com.melihberat.mobiluyg.Network.ApiClient
import com.melihberat.mobiluyg.Network.ApiResult
import com.melihberat.mobiluyg.Network.model.LessonDto
import com.melihberat.mobiluyg.Network.safeCall
import kotlinx.coroutines.launch
import java.util.Locale
=======
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.util.*
>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c

class CategoryActivity : AppCompatActivity() {

    companion object {
<<<<<<< HEAD
        const val EXTRA_CATEGORY = "EXTRA_CATEGORY" // Buraya genelde departmentId (Int) string olarak gelsin
=======
        const val EXTRA_CATEGORY = "EXTRA_CATEGORY"
>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c
        private const val PREF_NAME = "cat_items"
        private const val KEY_ITEMS = "items"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
<<<<<<< HEAD
        val categoryArg = intent.getStringExtra(EXTRA_CATEGORY).orEmpty()
        if (categoryArg.isNotBlank()) toolbar.title = categoryArg
=======
        val categoryTitle = intent.getStringExtra(EXTRA_CATEGORY).orEmpty()
        if (categoryTitle.isNotBlank()) toolbar.title = categoryTitle
>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c
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

<<<<<<< HEAD
        // toolbar.title genelde görünen isim; gerçek id EXTRA_CATEGORY ile gelebilir.
        loadLessonsForDepartment(categoryArg)
=======
        loadDynamicLessonsFor(toolbar.title?.toString().orEmpty())
>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c
    }

    override fun onResume() {
        super.onResume()
<<<<<<< HEAD
        val arg = intent.getStringExtra(EXTRA_CATEGORY).orEmpty()
        loadLessonsForDepartment(arg)
    }

    /**
     * Backend artık /api/departments/{id}/courses bekliyor.
     * Bu yüzden getLessons yerine getCoursesByDepartment kullanıyoruz.
     */
    private fun loadLessonsForDepartment(departmentIdStr: String) {
        val container = findViewById<LinearLayout>(R.id.categoryContainer)
        container.removeAllViews()

        val departmentId = departmentIdStr.toIntOrNull()
        if (departmentId == null) {
            // Eğer EXTRA_CATEGORY bir ad/başlık ise ve id yoksa burada karar vermelisin.
            // Şimdilik kullanıcıya bilgi verelim:
            Toast.makeText(this, "Geçersiz bölüm id: $departmentIdStr", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            when (val res = safeCall {
                ApiClient.api(this@CategoryActivity).getCoursesByDepartment(departmentId)
            }) {
                is ApiResult.Success -> {
                    res.data.forEach { lesson: LessonDto ->
                        container.addView(createLessonButton(lesson.title))
                    }
                }
                is ApiResult.Error -> {
                    Toast.makeText(
                        this@CategoryActivity,
                        "Dersler yüklenemedi: ${res.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
=======
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
>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c
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
<<<<<<< HEAD

=======
>>>>>>> 2061cb4c3c9637577fdb11e615072fd06abcf53c
