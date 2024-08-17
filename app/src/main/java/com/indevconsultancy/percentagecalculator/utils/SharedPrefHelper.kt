package com.indevconsultancy.percentagecalculator.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper(context: Context) {
    private var settings: SharedPreferences
    private var editor: SharedPreferences.Editor

    init {
        settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        editor = settings.edit()
    }

    fun getString(key: String?, defValue: String?): String? {
        return settings.getString(key, defValue)
    }

    fun setString(key: String?, value: String?): SharedPrefHelper {
        editor.putString(key, value)
        editor.commit()
        print(this)
        return this
    }

    fun getInt(key: String?, defValue: Int): Int {
        return settings.getInt(key, defValue)
    }

    fun setInt(key: String?, value: Int): SharedPrefHelper {
        editor.putInt(key, value)
        editor.commit()
        return this
    }

    fun removeAll(context: Context) {
        settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        editor = settings.edit()
        editor.clear()
        editor.commit()
        editor.apply()
    }

    fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return settings.getBoolean(key, defValue)
    }

    fun setBoolean(key: String?, value: Boolean): SharedPrefHelper {
        editor.putBoolean(key, value)
        editor.commit()
        return this
    }

    companion object {
        private const val PREF_FILE = "PercentageCalculator"
        private val instances: MutableMap<Context, SharedPrefHelper> = HashMap()
        fun getInstance(context: Context): SharedPrefHelper? {
            if (!instances.containsKey(context)) instances[context] = SharedPrefHelper(context)
            return instances[context]
        }
    }
}