package com.rk_tech.riggle_runner.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.rk_tech.riggle_runner.App
import com.rk_tech.riggle_runner.data.model.response.LoginResponseDetails

object SharedPrefManager {

    private val gson = Gson()
    private const val SHARED_PREF_NAME = "Laundry"
    private var preferences: SharedPreferences = App.getInstance()!!
        .getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    fun clear(): Boolean {
        val editor = preferences.edit()
        editor.clear()
        return editor.commit()
    }

    object KEY {
        const val USER = "user"
        const val USER_ID = "user_id"
        const val BEARER_TOKEN = "bearer_token"
        const val PROFILE_COMPLETED = "profile_completed"
        const val APPEARANCE_KEY = "appearance_key"
    }

    fun saveUser(bean: LoginResponseDetails) {
        val editor = preferences.edit()
        editor.putString(KEY.USER, gson.toJson(bean))
        editor.apply()
    }

    fun getSavedUser(): LoginResponseDetails? {
        val s: String? = preferences.getString(KEY.USER, null)
        return gson.fromJson(s, LoginResponseDetails::class.java)
    }

    fun saveUserId(userId: String) {
        val editor = preferences.edit()
        editor.putString(KEY.USER_ID, userId)
        editor.apply()
    }

    fun getCurrentUserId(): String? {
        return preferences.getString(KEY.USER_ID, null)
    }

    fun saveToke(userId: String) {
        val editor = preferences.edit()
        editor.putString(KEY.BEARER_TOKEN, userId)
        editor.apply()
    }

    fun getToken(): String? {
        return preferences.getString(KEY.BEARER_TOKEN, null)
    }

    fun profileCompleted(isProfile: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(KEY.PROFILE_COMPLETED, isProfile)
        editor.apply()
    }

    fun isProfileCompleted(): Boolean {
        return preferences.getBoolean(KEY.PROFILE_COMPLETED, false)
    }

    fun setAppearance(type: Int) {
        val editor = preferences.edit()
        editor.putInt(KEY.APPEARANCE_KEY, type)
        editor.apply()
    }

    fun getAppearance(): Int {
        return preferences.getInt(KEY.APPEARANCE_KEY, 0)
    }

}


