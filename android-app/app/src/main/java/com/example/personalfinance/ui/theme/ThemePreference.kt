package com.example.personalfinance.ui.theme

import android.content.Context

object ThemePreference {

    private const val PREF_NAME =
        "tich_phu_settings"

    private const val KEY_THEME =
        "theme_mode"


    fun save(
        context: Context,
        mode: ThemeMode
    ) {

        context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(
                KEY_THEME,
                mode.name
            )
            .apply()
    }


    fun get(
        context: Context
    ): ThemeMode {

        val saved =
            context
                .getSharedPreferences(
                    PREF_NAME,
                    Context.MODE_PRIVATE
                )
                .getString(
                    KEY_THEME,
                    ThemeMode.SYSTEM.name
                )


        return try {

            ThemeMode.valueOf(
                saved ?: ThemeMode.SYSTEM.name
            )

        } catch (_: Exception) {

            ThemeMode.SYSTEM
        }
    }
}