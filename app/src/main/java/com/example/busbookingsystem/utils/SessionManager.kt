package com.example.busbookingsystem.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("BusAppSession", Context.MODE_PRIVATE)

    companion object {
        const val KEY_USER_ID = "user_id"
    }


    fun saveUserId(id: Int) {
        prefs.edit().putInt(KEY_USER_ID, id).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, 0)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}