package com.atiga.cakeorder.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    companion object {
        const val KEY_LOGIN = "isLogin"
        const val KEY_USERNAME = "username"
        const val TOKEN = "token"
        const val USER_ID = "userId"
        const val ROLE_ID = "roleID"
    }

    private var pref: SharedPreferences = context.getSharedPreferences("Session", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = pref.edit()

    fun createLoginSession(username: String, token: String, userId: String, roleId: String) {
        editor.putBoolean(KEY_LOGIN, true)
        editor.putString(KEY_USERNAME, username)
        editor.putString(TOKEN, token)
        editor.putString(USER_ID, userId)
        editor.putString(ROLE_ID, roleId)

        editor.commit()
    }

    fun logout() {
        editor.clear()
        editor.commit()
    }

    val isLogin: Boolean = pref.getBoolean(KEY_LOGIN, false)

    fun saveToPreference(key: String, value: String) = editor.putString(key, value).commit()

    fun getFromPreference(key: String) = pref.getString(key, "")

}