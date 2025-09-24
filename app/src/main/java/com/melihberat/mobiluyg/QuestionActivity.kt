package com.melihberat.mobiluyg

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class QuestionActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout

    private val PREF_NAME = "question_prefs"
    private val KEY_QUESTIONS = "questions_json"

    private data class Question(val id: String = UUID.randomUUID().toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        container = findViewById(R.id.questionContainer)

        findViewById<EditText>(R.id.etSearchExam).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCards(s?.toString().orEmpty())
            }
        })

        loadQuestionsAndRender()

        findViewById<MaterialCardView>(R.id.btnAddExam).setOnClickListener { addNewQuestionCardPersisted() }
    }

    private fun addNewQuestionCardPersisted() {
        val q = Question()
        saveQuestion(q)
        addQuestionCardView(q)
        renumberCards()

        (container.parent as? ViewGroup)?.let { parent ->
            parent.post { (parent.parent as? View)?.scrollTo(0, container.bottom) }
        }
    }

    private fun addQuestionCardView(q: Question) {
        val card = createQuestionCard().apply { tag = q.id }
        container.addView(card)
        setDeleteListenerToAllImages(card)
        card.findViewById<ImageView?>(R.id.ivEdit)?.setOnClickListener {
            Toast.makeText(this, "Düzenleme yakında", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setDeleteListenerToAllImages(root: View?) {
        if (root == null) return
        if (root is ImageView) {
            if (root.id != R.id.ivEdit) {
                root.setOnClickListener { clicked ->
                    val card = findOwningCard(clicked) ?: return@setOnClickListener
                    val id = (card.tag as? String) ?: return@setOnClickListener

                    AlertDialog.Builder(this)
                        .setMessage("Silmek istediğinize emin misiniz?")
                        .setNegativeButton("İptal", null)
                        .setPositiveButton("Sil") { _, _ ->
                            removeQuestionById(id)
                            container.removeView(card)
                            renumberCards()
                        }
                        .show()
                }
            }
        }
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                setDeleteListenerToAllImages(root.getChildAt(i))
            }
        }
    }

    private fun findOwningCard(start: View): View? {
        var p: View? = start
        while (p != null) {
            if (p.parent === container) return p
            p = p.parent as? View
        }
        return null
    }

    private fun createQuestionCard(): MaterialCardView {
        val card = MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = dp(4f).toInt() }
            setCardBackgroundColor(0xFFC9C6FF.toInt())
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
            id = R.id.tvExamTitle
            text = "Soru"
            setTextColor(0xFF555879.toInt())
            textSize = 24f
            typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { gravity = android.view.Gravity.CENTER_HORIZONTAL }
        }

        val tvType = TextView(this).apply {
            id = R.id.tvExamType
            text = "Çözümü Göster"
            setTextColor(0xFF555879.toInt())
            textSize = 17f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { gravity = android.view.Gravity.CENTER_HORIZONTAL }
        }

        val ivEdit = ImageView(this).apply {
            id = R.id.ivEdit
            setImageResource(R.drawable.ic_edit_24)
            imageTintList = android.content.res.ColorStateList.valueOf(0xFF555879.toInt())
            layoutParams = LinearLayout.LayoutParams(dp(24f).toInt(), dp(24f).toInt()).apply {
                rightMargin = dp(8f).toInt()
            }
        }

        val ivDelete = ImageView(this).apply {
            setImageResource(R.drawable.ic_delete_24)
            imageTintList = android.content.res.ColorStateList.valueOf(0xFF555879.toInt())
            layoutParams = LinearLayout.LayoutParams(dp(24f).toInt(), dp(24f).toInt())
        }

        col.addView(tvTitle)
        col.addView(tvType)
        row.addView(col)
        row.addView(ivEdit)
        row.addView(ivDelete)
        card.addView(row)
        return card
    }

    private fun renumberCards() {
        var visibleIndex = 1
        for (i in 0 until container.childCount) {
            val cardView = container.getChildAt(i)
            if (cardView.visibility == View.VISIBLE) {
                cardView.findViewById<TextView?>(R.id.tvExamTitle)?.text = "Soru $visibleIndex"
                visibleIndex++
            }
        }
    }

    private fun filterCards(queryRaw: String) {
        val q = queryRaw.trim().lowercase()
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            val text = collectAllTexts(child).joinToString(" ").lowercase()
            child.visibility = if (q.isEmpty() || text.contains(q)) View.VISIBLE else View.GONE
        }
        renumberCards()
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

    private fun dp(v: Float): Float = v * resources.displayMetrics.density

    private fun saveQuestion(q: Question) {
        val arr = getSavedArray()
        arr.put(JSONObject().apply { put("id", q.id) })
        saveArray(arr)
    }

    private fun removeQuestionById(id: String) {
        val arr = getSavedArray()
        val newArr = JSONArray()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            if (obj.optString("id") != id) newArr.put(obj)
        }
        saveArray(newArr)
    }

    private fun loadQuestionsAndRender() {
        val arr = getSavedArray()
        for (i in 0 until arr.length()) {
            val id = arr.getJSONObject(i).optString("id")
            addQuestionCardView(Question(id))
        }
        renumberCards()
    }

    private fun getSavedArray(): JSONArray {
        val sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val json = sp.getString(KEY_QUESTIONS, "[]") ?: "[]"
        return JSONArray(json)
    }

    private fun saveArray(arr: JSONArray) {
        val sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        sp.edit().putString(KEY_QUESTIONS, arr.toString()).apply()
    }
}
