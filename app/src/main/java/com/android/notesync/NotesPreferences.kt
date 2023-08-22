package com.android.notesync

import android.content.Context
import android.content.SharedPreferences

private const val KEY_LOGIN_STATE = "key_login_state"
private const val KEY_LOGIN_USER = "key_login_user"

class NotesPreferences(context: Context) {

    private val preferences: SharedPreferences
            = context.getSharedPreferences("travel-blog", Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean = preferences.getBoolean(KEY_LOGIN_STATE, false)

    fun setLoggedIn(loggedIn: Boolean) {
        preferences.edit().putBoolean(KEY_LOGIN_STATE, loggedIn).apply()
    }

    fun setLoggedOff(loggedOff: Boolean) {
        preferences.edit().putBoolean(KEY_LOGIN_STATE, loggedOff).apply()
    }

    fun setUsername(username:String)
    {
        preferences.edit().putString(KEY_LOGIN_USER, username).apply ()
    }
    fun getUsername(): String? {
        return preferences.getString(KEY_LOGIN_USER, null)
    }
}
