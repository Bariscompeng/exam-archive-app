package com.melihberat.mobiluyg

import android.content.Context
import android.os.Bundle
import android.view.*
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

class ExamActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var cardsContainer: LinearLayout
    private var filterButton: ImageView? = null
    private lateinit var btnAdd: MaterialCardView

    private val PREF_NAME = "exam_prefs"
    private val KEY_EXAMS = "exams"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener { handleBack() }

        etSearch = findViewById(R.id.etSearchExam)
        btnAdd   = findViewById(R.id.btnAddExam)

        filterButton   = findFilterButtonFrom(etSearch)
        cardsContainer = findCardsContainerFrom(etSearch)
            ?: LinearLayout(this).also { it.orientation = LinearLayout.VERTICAL }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleBack()
        })

        etSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(etSearch)
                filterExamCards(etSearch.text?.toString().orEmpty())
                true
            } else false
        }

        btnAdd.setOnClickListener {
            hideKeyboard(etSearch)
            val currentTitle = etSearch.text?.toString()?.trim().orEmpty()
            if (currentTitle.isEmpty()) {
                showTitleInputDialog()
            } else {
                showTypeMenuAndAdd(currentTitle, anchor = btnAdd)
            }
        }

        filterButton?.setOnClickListener {
            hideKeyboard(etSearch)
            filterExamCards(etSearch.text?.toString().orEmpty())
        }

        loadExams()
    }

    private fun handleBack() {
        if (isFilterActive()) {
            resetFilter()
        } else {
            finish()
        }
    }

    private fun isFilterActive(): Boolean {
        if (!etSearch.text.isNullOrBlank()) return true
        for (i in 0 until cardsContainer.childCount) {
            if (cardsContainer.getChildAt(i).visibility != View.VISIBLE) return true
        }
        return false
    }

    private fun resetFilter() {
        etSearch.text?.clear()
        for (i in 0 until cardsContainer.childCount) {
            cardsContainer.getChildAt(i).visibility = View.VISIBLE
        }
    }

    private fun findFilterButtonFrom(editText: EditText): ImageView? {
        val row = editText.parent as? ViewGroup ?: return null
        var editIndex = -1
        for (i in 0 until row.childCount) {
            if (row.getChildAt(i) === editText) { editIndex = i; break }
        }
        if (editIndex == -1) return null
        for (j in editIndex + 1 until row.childCount) {
            val v = row.getChildAt(j)
            if (v is ImageView) return v
        }
        return null
    }

    private fun findCardsContainerFrom(editText: EditText): LinearLayout? {
        val row = editText.parent as? ViewGroup ?: return null
        val searchCard = row.parent as? ViewGroup ?: return null
        val rootLinear = searchCard.parent as? ViewGroup ?: return null
        return rootLinear.getChildAt(1) as? LinearLayout
    }

    private fun showTitleInputDialog() {
        val input = EditText(this).apply {
            hint = "Sınav adı"
            setSingleLine(true)
        }
        AlertDialog.Builder(this)
            .setTitle("Yeni sınav")
            .setView(input)
            .setNegativeButton("İptal", null)
            .setPositiveButton("Devam") { _, _ ->
                val title = input.text?.toString()?.trim().orEmpty()
                if (title.isEmpty()) {
                    Toast.makeText(this, "Sınav adı boş olamaz", Toast.LENGTH_SHORT).show()
                } else {
                    showTypeMenuAndAdd(title, anchor = btnAdd)
                }
            }.show()
    }

    private fun showTypeMenuAndAdd(title: String, anchor: View) {
        val menu = PopupMenu(this, anchor)
        menu.menu.add("Vize")
        menu.menu.add("Final")
        menu.menu.add("Quiz")
        menu.menu.add("Ödev")
        menu.setOnMenuItemClickListener { item ->
            val subtitle = item.title?.toString() ?: "Vize"
            addExamCard(title, subtitle)
            saveExam(title, subtitle)
            true
        }
        menu.show()
    }

    private fun addExamCard(title: String, subtitle: String) {
        val card = createExamCard(title, subtitle)
        cardsContainer.addView(card)
        attachDeleteToCard(card, title, subtitle)
    }

    private fun attachDeleteToCard(cardRoot: View, title: String, subtitle: String) {
        if (cardRoot !is ViewGroup) return
        val imageViews = mutableListOf<ImageView>()
        fun dfs(v: View) {
            when (v) {
                is ImageView -> imageViews.add(v)
                is ViewGroup -> for (i in 0 until v.childCount) dfs(v.getChildAt(i))
            }
        }
        dfs(cardRoot)
        if (imageViews.isNotEmpty()) {
            val deleteView = imageViews.last()
            deleteView.setOnClickListener {
                AlertDialog.Builder(this)
                    .setMessage("Silmek istediğinize emin misiniz?")
                    .setNegativeButton("İptal", null)
                    .setPositiveButton("Sil") { _, _ ->
                        cardsContainer.removeView(cardRoot)
                        removeExam(title, subtitle)
                    }
                    .show()
            }
        }
    }

    private fun filterExamCards(queryRaw: String) {
        val q = queryRaw.trim().lowercase()
        for (i in 0 until cardsContainer.childCount) {
            val child = cardsContainer.getChildAt(i)
            if (child is ViewGroup) {
                val text = collectAllTexts(child).joinToString(" ").lowercase()
                child.visibility = if (q.isEmpty() || text.contains(q)) View.VISIBLE else View.GONE
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

    private fun createExamCard(title: String, subtitle: String): MaterialCardView {
        val card = MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = dp(4f).toInt() }
            setCardBackgroundColor(0xFFA6A1FF.toInt())
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

        val tvTitle = TextView(this).apply {
            text = title
            setTextColor(0xFF555879.toInt())
            textSize = 16f
            typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
        }

        val tvSub = TextView(this).apply {
            text = subtitle
            setTextColor(0xFF555879.toInt())
            textSize = 14f
        }

        val ivTrash = ImageView(this).apply {
            setImageResource(R.drawable.ic_delete_24)
            imageTintList = android.content.res.ColorStateList.valueOf(0xFF555879.toInt())
            layoutParams = LinearLayout.LayoutParams(dp(24f).toInt(), dp(24f).toInt())
        }

        col.addView(tvTitle)
        col.addView(tvSub)
        row.addView(col)
        row.addView(ivTrash)
        card.addView(row)
        return card
    }

    private fun hideKeyboard(target: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(target.windowToken, 0)
    }

    private fun dp(v: Float): Float = v * resources.displayMetrics.density

    private fun saveExam(title: String, subtitle: String) {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_EXAMS, "[]")
        val arr = JSONArray(jsonStr)
        val obj = JSONObject()
        obj.put("title", title)
        obj.put("subtitle", subtitle)
        arr.put(obj)
        prefs.edit().putString(KEY_EXAMS, arr.toString()).apply()
    }

    private fun removeExam(title: String, subtitle: String) {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_EXAMS, "[]")
        val arr = JSONArray(jsonStr)
        val newArr = JSONArray()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            if (!(obj.getString("title") == title && obj.getString("subtitle") == subtitle)) {
                newArr.put(obj)
            }
        }
        prefs.edit().putString(KEY_EXAMS, newArr.toString()).apply()
    }

    private fun loadExams() {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_EXAMS, "[]")
        val arr = JSONArray(jsonStr)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val title = obj.getString("title")
            val subtitle = obj.getString("subtitle")
            addExamCard(title, subtitle)
        }
    }
}
