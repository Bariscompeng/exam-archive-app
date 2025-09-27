package com.melihberat.mobiluyg.Network

import android.content.Context

/**
 * Basit SharedPreferences tabanlı token saklama.
 */
object TokenStore {
    private const val PREFS = "auth_prefs"
    private const val KEY_TOKEN = "auth_token"

    // Async sürümler (isteğe bağlı)
    suspend fun saveToken(context: Context, token: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    suspend fun getToken(context: Context): String? {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_TOKEN, null)
    }

    suspend fun clearToken(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_TOKEN)
            .apply()
    }

    // Senkron yardımcılar (Login/Register içinde bunları kullandık)
    fun saveTokenSync(context: Context, token: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun getTokenSync(context: Context): String? {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_TOKEN, null)
    }
}

