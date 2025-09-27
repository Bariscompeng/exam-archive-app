// app/src/main/java/com/melihberat/mobiluyg/FavoritesActivity.kt
package com.melihberat.mobiluyg

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

import androidx.lifecycle.lifecycleScope
import com.melihberat.mobiluyg.Network.ApiClient
import com.melihberat.mobiluyg.Network.ApiResult
import com.melihberat.mobiluyg.Network.safeCall
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_PREV = "EXTRA_PREV"

        // Diğer ekranlardan hızlı ekleme için
        const val EXTRA_ADD_FAV = "EXTRA_ADD_FAV"
        const val EXTRA_FAV_LESSON = "EXTRA_FAV_LESSON"
        const val EXTRA_FAV_TITLE = "EXTRA_FAV_TITLE"
        const val EXTRA_FAV_DESC = "EXTRA_FAV_DESC"

        // (İstersen local cache için bırakabiliriz; ama backend’e geçiyoruz)
        private const val PREF_NAME = "favs"
        private const val PREF_DYNAMIC_SET = "dynamic_entries"
    }

    private lateinit var bottom: BottomNavigationView

    // --- (Opsiyonel) local cache yardımcıları ---
    private fun prefs() = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    private fun getStringSet(key: String): MutableSet<String> =
        (prefs().getStringSet(key, emptySet())?.toMutableSet()) ?: mutableSetOf()
    private fun saveStringSet(key: String, set: MutableSet<String>) {
        prefs().edit().putStringSet(key, HashSet(set)).apply()
    }
    private fun makeDynamicEntry(lesson: String, title: String, desc: String) =
        "$lesson|$title|$desc"
    private fun addDynamic(lesson: String, title: String, desc: String) {
        val set = getStringSet(PREF_DYNAMIC_SET)
        set.removeAll { it.startsWith("$lesson|$title|") }
        set.add(makeDynamicEntry(lesson, title, desc))
        saveStringSet(PREF_DYNAMIC_SET, set)
    }
    private fun removeDynamic(lesson: String, title: String) {
        val set = getStringSet(PREF_DYNAMIC_SET)
        set.removeAll { it.startsWith("$lesson|$title|") }
        saveStringSet(PREF_DYNAMIC_SET, set)
    }
    // -------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        findViewById<MaterialToolbar>(R.id.topAppBar)
            .setNavigationOnClickListener { onBackPressed() }

        bottom = findViewById(R.id.bottomNav)
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_fav -> true
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    })
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        putExtra(EXTRA_PREV, "Favorites")
                    })
                    true
                }
                else -> false
            }
        }
        bottom.menu.findItem(R.id.nav_fav).isChecked = true

        // Başka ekrandan hızlı ekleme geldiyse önce onu göster
        if (intent.getBooleanExtra(EXTRA_ADD_FAV, false)) {
            val lesson = intent.getStringExtra(EXTRA_FAV_LESSON).orEmpty()
            val itemTitle = intent.getStringExtra(EXTRA_FAV_TITLE).orEmpty()
            val itemDesc = intent.getStringExtra(EXTRA_FAV_DESC).orEmpty()
            addFavoriteCard(lesson, itemTitle, itemDesc)
            // (Opsiyonel) local’e de yaz
            addDynamic(lesson, itemTitle, itemDesc)
        }

        // İlk yüklemede backend + (opsiyonel) local cache’i ekrana bas
        rebuildFavoritesFromBackendAndLocal()
    }

    override fun onResume() {
        super.onResume()
        bottom.menu.findItem(R.id.nav_fav).isChecked = true
        rebuildFavoritesFromBackendAndLocal()
    }

    /** Backend’ten favorileri çek + local cache’i de ekleyip listeyi tazele */
    private fun rebuildFavoritesFromBackendAndLocal() {
        val container = getFavContainer() ?: return
        container.removeAllViews()

        // 1) Backend’ten çek
        lifecycleScope.launch {
            when (val r = safeCall { ApiClient.api(this@FavoritesActivity).getFavorites() }) {
                is ApiResult.Success -> {
                    // NOT: Aşağıdaki alan adları backend DTO’na göre ayarla.
                    // Örn: lessonId (String), text (String), options (List<String>)
                    r.data.forEach { fav ->
                        val lesson = fav.lessonId ?: ""          // yoksa "" ver
                        val title  = fav.text.orEmpty()
                        val desc   = fav.options?.joinToString(" • ").orEmpty()
                        addFavoriteCard(lesson, title, desc)
                    }
                }
                is ApiResult.Error -> {
                    android.widget.Toast.makeText(
                        this@FavoritesActivity,
                        "Favoriler alınamadı: ${r.message}",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }

            // 2) (Opsiyonel) local cache’i de ekrana ekle
            val set = getStringSet(PREF_DYNAMIC_SET)
            for (entry in set) {
                val parts = entry.split("|", limit = 3)
                if (parts.size == 3) {
                    val (lesson, title, desc) = parts
                    addFavoriteCard(lesson, title, desc)
                }
            }
        }
    }

    private fun addFavoriteCard(lesson: String, itemTitle: String, itemDesc: String) {
        val container = getFavContainer() ?: return
        if (containerHasCard(container, key = "$lesson|$itemTitle")) return

        val card = MaterialCardView(this).apply {
            setCardBackgroundColor(Color.parseColor("#C9CEFF"))
            radius = dp(16f)
            cardElevation = dp(4f)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(10f).toInt() }
        }

        val inner = RelativeLayout(this).apply {
            setPadding(dp(16f).toInt(), dp(16f).toInt(), dp(16f).toInt(), dp(16f).toInt())
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            tag = "$lesson|$itemTitle"
        }

        val tvTitleId = View.generateViewId()
        val tvTitle = TextView(this).apply {
            id = tvTitleId
            text = if (lesson.isNotBlank()) lesson else "Ders"
            setTextColor(Color.parseColor("#555879"))
            textSize = 16f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply { addRule(RelativeLayout.ALIGN_PARENT_START) }
        }

        val starId = View.generateViewId()
        val star = ImageView(this).apply {
            id = starId
            setImageResource(R.drawable.ic_star_filled)
            imageTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FFC107"))
            layoutParams = RelativeLayout.LayoutParams(dp(20f).toInt(), dp(20f).toInt()).apply {
                addRule(RelativeLayout.ALIGN_PARENT_END)
                topMargin = dp(2f).toInt()
            }
            setOnClickListener {
                // Şimdilik sadece ekrandan ve local cache’ten kaldırıyoruz.
                // Backend’e “favori sil” koyarsan burada API çağrısı da yapabilirsin.
                (card.parent as? ViewGroup)?.removeView(card)
                removeDynamic(lesson, itemTitle)
            }
        }

        (tvTitle.layoutParams as RelativeLayout.LayoutParams).apply {
            addRule(RelativeLayout.LEFT_OF, starId)
            marginEnd = dp(12f).toInt()
        }

        val tvLine1 = TextView(this).apply {
            id = View.generateViewId()
            text = if (itemTitle.isNotBlank()) itemTitle else "Başlık"
            setTextColor(Color.parseColor("#555879"))
            textSize = 13f
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply { addRule(RelativeLayout.BELOW, tvTitleId); topMargin = dp(10f).toInt() }
        }

        val tvLine2 = TextView(this).apply {
            text = itemDesc
            setTextColor(Color.parseColor("#555879"))
            textSize = 12f
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply { addRule(RelativeLayout.BELOW, tvTitleId); topMargin = dp(36f).toInt() }
        }

        inner.addView(tvTitle)
        inner.addView(star)
        inner.addView(tvLine1)
        inner.addView(tvLine2)
        card.addView(inner)
        container.addView(card)
    }

    private fun getFavContainer(): LinearLayout? {
        val scroll = findViewById<NestedScrollView>(R.id.favScroll) ?: return null
        return scroll.findViewById(R.id.favContainer)
    }

    private fun containerHasCard(container: LinearLayout, key: String): Boolean {
        for (i in 0 until container.childCount) {
            val card = container.getChildAt(i) as? MaterialCardView ?: continue
            val inner = card.getChildAt(0) as? ViewGroup ?: continue
            if (inner.tag == key) return true
        }
        return false
    }

    private fun dp(v: Float): Float = v * resources.displayMetrics.density

    override fun onBackPressed() {
        when (intent.getStringExtra(EXTRA_PREV)) {
            "Profile" -> startActivity(Intent(this, ProfileActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
            else -> super.onBackPressed()
        }
    }
}
