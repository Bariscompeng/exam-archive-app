package com.melihberat.mobiluyg

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import java.util.Locale

import androidx.lifecycle.lifecycleScope
import com.melihberat.mobiluyg.Network.ApiClient
import com.melihberat.mobiluyg.Network.ApiResult
import com.melihberat.mobiluyg.Network.safeCall
import kotlinx.coroutines.launch

class LessonActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LESSON       = "EXTRA_LESSON"        // başlık
        const val EXTRA_LESSON_ID    = "EXTRA_LESSON_ID"     // Int id (varsa)

        private const val PREF_NAME        = "favs"
        private const val PREF_DYNAMIC_SET = "dynamic_entries"

        private const val EXAM_PREFS = "exam_prefs"
        private const val KEY_EXAMS  = "exams"
    }

    private lateinit var toolbar: MaterialToolbar
    private lateinit var dynamicList: LinearLayout

    private var lessonTitle: String = ""
    private var lessonId: Int? = null   // <-- id’yi ayrı tut

    private fun prefs() = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

    private fun getDynamicSet(): MutableSet<String> =
        (prefs().getStringSet(PREF_DYNAMIC_SET, emptySet())?.toMutableSet()) ?: mutableSetOf()

    private fun saveDynamicSet(set: MutableSet<String>) {
        prefs().edit().putStringSet(PREF_DYNAMIC_SET, HashSet(set)).apply()
    }

    private fun makeEntry(lesson: String, title: String, desc: String) =
        "$lesson|$title|$desc"

    private fun isFav(lesson: String, title: String): Boolean {
        val keyPrefix = "$lesson|$title|"
        return getDynamicSet().any { it.startsWith(keyPrefix) }
    }

    private fun addFav(lesson: String, title: String, desc: String) {
        val set = getDynamicSet()
        set.removeAll { it.startsWith("$lesson|$title|") }
        set.add(makeEntry(lesson, title, desc))
        saveDynamicSet(set)
    }

    private fun removeFav(lesson: String, title: String) {
        val set = getDynamicSet()
        set.removeAll { it.startsWith("$lesson|$title|") }
        saveDynamicSet(set)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)

        toolbar = findViewById(R.id.topAppBar)

        // Başlık + (varsa) id’yi al
        lessonTitle = intent.getStringExtra(EXTRA_LESSON) ?: ""
        lessonId = intent.getIntExtra(EXTRA_LESSON_ID, -1).takeIf { it > 0 }

        if (lessonTitle.isNotBlank()) toolbar.title = lessonTitle
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_home
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (javaClass != HomeActivity::class.java) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_fav -> {
                    if (javaClass != HomeActivity::class.java) {
                        startActivity(Intent(this, FavoritesActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_profile -> {
                    if (javaClass != HomeActivity::class.java) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish()
                    }
                    true
                }
                else -> false
            }
        }

        dynamicList = findViewById(R.id.examDynamicList)

        // Listeyi yükle
        loadDynamicExams()
    }

    override fun onResume() {
        super.onResume()
        refreshDynamicStars()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        refreshDynamicStars()
    }

    private fun refreshDynamicStars() {
        val lesson = (lessonTitle.takeIf { it.isNotBlank() } ?: toolbar.title?.toString().orEmpty()).trim()
        forEachDynamicStar { star, type, _ ->
            setStarTint(star, isFav(lesson, type))
        }
    }

    private fun setStarTint(star: ImageView, fav: Boolean) {
        val color = if (fav) "#FFD54F" else "#FFFFFF"
        star.imageTintList = android.content.res.ColorStateList.valueOf(
            android.graphics.Color.parseColor(color)
        )
    }

    private fun loadDynamicExams() {
        dynamicList.removeAllViews()

        val lessonNameForKey = (lessonTitle.takeIf { it.isNotBlank() } ?: toolbar.title?.toString().orEmpty()).trim()

        lifecycleScope.launch {
            when (val r = safeCall {
                // API String bekliyorsa id’yi stringe çevir ve parametre adıyla gönder
                ApiClient.api(this@LessonActivity).getQuestions(
                    lessonId = (lessonId ?: 0).toString()
                )
            }) {
                is ApiResult.Success -> {
                    r.data.forEach { q ->
                        val type   = q.text.take(40)
                        val topics = q.options.joinToString(" • ")

                        val card = createLessonStyleExamCard(
                            type = type,
                            topics = topics
                        )

                        val star = card.findViewWithTag<ImageView>("star")
                        if (star != null) {
                            // q.id (Int?) → String anahtar
                            val key = (q.id ?: 0).toString()
                            setStarTint(star, isFav(lessonNameForKey, key))
                            attachFavoriteToggle(
                                star = star,
                                lesson = lessonNameForKey,
                                itemTitle = key,     // << Int yerine String
                                itemDesc = q.text
                            )
                        }
                        dynamicList.addView(card)
                    }
                }
                is ApiResult.Error -> {
                    // TODO: kullanıcıya uygun bir hata gösterimi ekle
                }
            }
        }
    }

    private fun createLessonStyleExamCard(type: String, topics: String): MaterialCardView {
        val card = MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = dp(10f).toInt() }
            setCardBackgroundColor(0xFFC9C6FF.toInt())
            radius = dp(12f)
            cardElevation = 0f
        }

        val row = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(dp(16f).toInt(), dp(16f).toInt(), dp(16f).toInt(), dp(16f).toInt())
        }

        val col = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }

        val tvType = TextView(this).apply {
            text = type.ifEmpty { "—" }
            setTextColor(0xFF555879.toInt())
            textSize = 16f
            typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }

        val tvTopics = TextView(this).apply {
            text = topics.ifEmpty { "—" }
            setTextColor(0xFF555879.toInt())
            textSize = 13f
            val p = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            p.topMargin = dp(6f).toInt()
            layoutParams = p
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }

        val star = ImageView(this).apply {
            setImageResource(R.drawable.ic_star_filled)
            imageTintList = android.content.res.ColorStateList.valueOf(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(dp(22f).toInt(), dp(22f).toInt())
            tag = "star"
        }

        col.addView(tvType)
        col.addView(tvTopics)
        row.addView(col)
        row.addView(star)
        card.addView(row)
        return card
    }

    private fun attachFavoriteToggle(
        star: ImageView,
        lesson: String,
        itemTitle: String,
        itemDesc: String
    ) {
        star.setOnClickListener {
            val nowFav = !isFav(lesson, itemTitle)
            if (nowFav) {
                addFav(lesson, itemTitle, itemDesc)
                setStarTint(star, true)
            } else {
                removeFav(lesson, itemTitle)
                setStarTint(star, false)
            }
        }
    }

    private fun dp(v: Float): Float = v * resources.displayMetrics.density

    private fun forEachDynamicStar(block: (star: ImageView, title: String, desc: String) -> Unit) {
        fun walk(v: View) {
            when (v) {
                is ImageView -> if (v.tag == "star") {
                    val parentRow = v.parent as ViewGroup
                    val col = parentRow.getChildAt(0) as ViewGroup
                    val typeText = (col.getChildAt(0) as TextView).text?.toString() ?: ""
                    val topicsText = (col.getChildAt(1) as TextView).text?.toString() ?: ""
                    block(v, typeText, topicsText)
                }
                is ViewGroup -> for (i in 0 until v.childCount) walk(v.getChildAt(i))
            }
        }
        walk(dynamicList)
    }

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

