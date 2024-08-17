package com.indevconsultancy.percentagecalculator.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferences class.
 * Saves and loads night mode settings for the user.
 */
class ThemeHelper(context: Context) {
    private val themePreferences: SharedPreferences

    init {
        themePreferences = context.getSharedPreferences("app_theme", Context.MODE_PRIVATE)
    }

    /**
     * Sets the night mode state (day mode = false; night mode = true).
     * @param state -- If the user wants to change to night mode.
     */
    fun setNightModeState(state: Boolean?) {
        val editor = themePreferences.edit()
        editor.putBoolean("NightMode", state!!)
        editor.apply()
    }

    /**
     * @return The night mode setting for the user.
     */
    fun loadNightMode(): Boolean {
        return themePreferences.getBoolean("NightMode", false)
    }
}