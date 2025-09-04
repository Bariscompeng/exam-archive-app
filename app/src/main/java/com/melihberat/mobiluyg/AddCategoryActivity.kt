package com.melihberat.mobiluyg

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var listContainer: LinearLayout
    private var filterButton: ImageView? = null
    private lateinit var btnAdd: MaterialCardView

    private val PREF_NAME = "cat_items"
    private val KEY_ITEMS = "items"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener { finish() }

        etSearch = findViewById(R.id.etSearchExam)
        btnAdd   = findViewById(R.id.btnAddExam)

        filterButton   = findFilterButtonFrom(etSearch)
        listContainer  = findViewById(R.id.examList)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { finish() }
        })

        etSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(etSearch)
                filterItems(etSearch.text?.toString().orEmpty())
                true
            } else false
        }

        btnAdd.setOnClickListener {
            hideKeyboard(etSearch)
            showCategoryDialog()
        }

        filterButton?.setOnClickListener {
            hideKeyboard(etSearch)
            filterItems(etSearch.text?.toString().orEmpty())
        }

        loadItems()
    }

    private fun showCategoryDialog() {
        val inputCat = EditText(this).apply {
            hint = "Kategori başlığı"
            setSingleLine(true)
        }
        AlertDialog.Builder(this)
            .setTitle("Kategori")
            .setView(inputCat)
            .setNegativeButton("İptal", null)
            .setPositiveButton("Devam") { _, _ ->
                val category = inputCat.text?.toString()?.trim().orEmpty()
                if (category.isEmpty()) {
                    Toast.makeText(this, "Kategori boş olamaz", Toast.LENGTH_SHORT).show()
                } else {
                    showLessonDialog(category)
                }
            }.show()
    }

    private fun showLessonDialog(category: String) {
        val inputLesson = EditText(this).apply {
            hint = "Ders adı"
            setSingleLine(true)
        }
        AlertDialog.Builder(this)
            .setTitle("Ders")
            .setView(inputLesson)
            .setNegativeButton("Geri", null)
            .setPositiveButton("Ekle") { _, _ ->
                val lesson = inputLesson.text?.toString()?.trim().orEmpty()
                if (lesson.isEmpty()) {
                    Toast.makeText(this, "Ders adı boş olamaz", Toast.LENGTH_SHORT).show()
                } else {
                    addItemCard(category, lesson)
                    saveItem(category, lesson)
                }
            }.show()
    }

    private fun addItemCard(category: String, lesson: String) {
        val card = createItemCard(category, lesson)
        listContainer.addView(card)
        attachDelete(card, category, lesson)
    }

    private fun attachDelete(cardRoot: View, category: String, lesson: String) {
        if (cardRoot !is ViewGroup) return
        val ivs = mutableListOf<ImageView>()
        fun walk(v: View) {
            when (v) {
                is ImageView -> ivs.add(v)
                is ViewGroup -> for (i in 0 until v.childCount) walk(v.getChildAt(i))
            }
        }
        walk(cardRoot)
        if (ivs.isNotEmpty()) {
            ivs.last().setOnClickListener {
                AlertDialog.Builder(this)
                    .setMessage("Silmek istediğinize emin misiniz?")
                    .setNegativeButton("İptal", null)
                    .setPositiveButton("Sil") { _, _ ->
                        listContainer.removeView(cardRoot)
                        removeItem(category, lesson)
                    }
                    .show()
            }
        }
    }

    private fun filterItems(queryRaw: String) {
        val q = foldTr(queryRaw)
        for (i in 0 until listContainer.childCount) {
            val child = listContainer.getChildAt(i)
            if (child is ViewGroup) {
                val text = collectAllTexts(child).joinToString(" ")
                val folded = foldTr(text)
                child.visibility = if (q.isEmpty() || folded.contains(q)) View.VISIBLE else View.GONE
            }
        }
    }

    private fun collectAllTexts(root: View): List<String> {
        val out = mutableListOf<String>()
        fun dfs(v: View) {
            when (v) {
                is TextView -> out.add(v.text?.toString().orEmpty())
                is ViewGroup -> for (i in 0 until v.childCount) dfs(v.getChildAt(i))
            }
        }
        dfs(root)
        return out
    }

    private fun createItemCard(category: String, lesson: String): MaterialCardView {
        val card = MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = dp(6f).toInt() }
            setCardBackgroundColor(android.graphics.Color.parseColor("#A6A1FF"))
            radius = dp(8f)
            cardElevation = 0f
        }

        val row = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(16f).toInt(), dp(16f).toInt(), dp(16f).toInt(), dp(16f).toInt())
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val col = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            orientation = LinearLayout.VERTICAL
        }

        val tvCategory = TextView(this).apply {
            text = category
            setTextColor(0xFF555879.toInt())
            textSize = 16f
            typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            gravity = android.view.Gravity.START
        }

        val centerCol = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }

        val tvLesson = TextView(this).apply {
            text = lesson
            setTextColor(0xFF555879.toInt())
            textSize = 14f
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }

        val ivTrash = ImageView(this).apply {
            setImageResource(R.drawable.ic_delete_24)
            imageTintList = android.content.res.ColorStateList.valueOf(0xFF555879.toInt())
            layoutParams = LinearLayout.LayoutParams(dp(24f).toInt(), dp(24f).toInt())
        }

        centerCol.addView(tvLesson)
        col.addView(tvCategory)
        col.addView(centerCol)
        row.addView(col)
        row.addView(ivTrash)
        card.addView(row)
        return card
    }

    private fun saveItem(category: String, lesson: String) {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(KEY_ITEMS, "[]"))
        val obj = JSONObject()
        obj.put("category", category)
        obj.put("lesson", lesson)
        arr.put(obj)
        prefs.edit().putString(KEY_ITEMS, arr.toString()).apply()
    }

    private fun removeItem(category: String, lesson: String) {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(KEY_ITEMS, "[]"))
        val newArr = JSONArray()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            if (!(o.optString("category") == category && o.optString("lesson") == lesson)) {
                newArr.put(o)
            }
        }
        prefs.edit().putString(KEY_ITEMS, newArr.toString()).apply()
    }

    private fun loadItems() {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(KEY_ITEMS, "[]"))
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val c = o.optString("category")
            val l = o.optString("lesson")
            addItemCard(c, l)
        }
    }

    private fun findFilterButtonFrom(editText: EditText): ImageView? {
        val row = editText.parent as? ViewGroup ?: return null
        var idx = -1
        for (i in 0 until row.childCount) if (row.getChildAt(i) === editText) { idx = i; break }
        if (idx == -1) return null
        for (j in idx + 1 until row.childCount) {
            val v = row.getChildAt(j)
            if (v is ImageView) return v
        }
        return null
    }

    private fun hideKeyboard(target: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(target.windowToken, 0)
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
}
