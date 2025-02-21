package com.ilya.reaction.logik

import android.content.Context
import android.content.SharedPreferences
import android.util.Log


object PreferenceHelper {

    private const val PREFERENCE_NAME = "UserPrefs"
    private const val KEY_SHOW_ELEMENT = "showElement"
    private const val KEY_USER_TEXT = "userText"

    private const val KEY_ROOM_ID = "roomId"

    private const val KEY_STRING_1 = "key_string_1"
    private const val KEY_STRING_2 = "key_string_2"
    private const val KEY_STRING_3 = "key_string_3"
    private const val KEY_STRING_4 = "key_string_4"
    private const val KEY_MESSAGE_LIST = "messageList"
    // Константы для работы с SharedPreferences
    private const val SHARED_PREFERENCES_NAME = "YourSharedPreferencesName"


    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }


    private lateinit var appContext: Context
    private val sharedPreferences: SharedPreferences by lazy {
        appContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }


    fun setUserKey(context: Context, text: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_USER_TEXT, text)
        editor.apply()
    }

    fun getUserKey(context: Context): String? {
       Log.d("getUserKey", "${getSharedPreferences(context).getString(KEY_USER_TEXT, "")}")
        return getSharedPreferences(context).getString(KEY_USER_TEXT, "")
    }

    fun removeUserKey(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.remove(KEY_USER_TEXT)
        editor.apply()
    }

    fun saveRoomId(context: Context, roomId: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_ROOM_ID, roomId)
        editor.apply()
    }

    fun getRoomId(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_ROOM_ID, "")
    }





    fun savefirstname(context: Context, value: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_STRING_1, value)
        editor.apply()
    }

    fun getfirstnamed(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_STRING_1, "")
    }

    fun savelastnme(context: Context, value: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_STRING_2, value)
        editor.apply()
    }

    fun getlastnme(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_STRING_2, "")
    }

    fun saveidgroup(context: Context, value: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_STRING_3, value)
        editor.apply()
    }

    fun getidgroup(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_STRING_3, "")
    }

    // Удаление groupId из SharedPreferences
    fun removeGroupId(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.remove(KEY_STRING_3) // Удаляем только ключ groupId
        editor.apply() // Применяем изменения
    }

}


