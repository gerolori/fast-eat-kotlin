package com.example.mangiaebasta.core

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object SharedPreferencesUtils {
    private const val PREFS_NAME = "app_prefs"
    private const val LAST_VISITED_PAGE_KEY = "last_visited_page"

    fun getStoredSID(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return sharedPreferences.getString("sid", null)
    }

    fun getStoredUID(context: Context): Int? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return sharedPreferences.getInt("uid", -1).takeIf { it != -1 }
    }

    fun storeAppPrefs(
        context: Context,
        sid: String,
        uid: Int,
    ) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("sid", sid)
        editor.putInt("uid", uid)
        editor.apply()
    }

    fun saveLastVisitedPage(
        context: Context,
        page: String?,
    ) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(LAST_VISITED_PAGE_KEY, page)
        editor.apply()
    }

    fun getLastVisitedPage(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return sharedPreferences.getString(LAST_VISITED_PAGE_KEY, null)
    }
}
