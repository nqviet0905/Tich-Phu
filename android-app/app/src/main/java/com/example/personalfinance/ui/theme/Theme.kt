package com.example.personalfinance.ui.theme

import android.app.Activity
import android.os.Build

import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme

import androidx.compose.runtime.Composable

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

import androidx.core.view.WindowCompat


/*
|--------------------------------------------------------------------------
| LIGHT THEME
|--------------------------------------------------------------------------
*/

private val LightColors =
    lightColorScheme(

        primary =
            BrandPurple,

        onPrimary =
            Color.White,

        secondary =
            BrandGold,

        onSecondary =
            Color(0xFF2E2300),

        background =
            LightBackground,

        onBackground =
            LightTextPrimary,

        surface =
            LightSurface,

        onSurface =
            LightTextPrimary,

        surfaceVariant =
            LightSurfaceVariant,

        onSurfaceVariant =
            LightTextSecondary,

        outline =
            LightBorder,

        error =
            ExpenseRed
    )


/*
|--------------------------------------------------------------------------
| DARK THEME
|--------------------------------------------------------------------------
*/

private val DarkColors =
    darkColorScheme(

        primary =
            BrandPurpleLight,

        onPrimary =
            Color.White,

        secondary =
            BrandGold,

        onSecondary =
            Color(0xFF201700),

        background =
            DarkBackground,

        onBackground =
            DarkTextPrimary,

        surface =
            DarkSurface,

        onSurface =
            DarkTextPrimary,

        surfaceVariant =
            DarkSurfaceVariant,

        onSurfaceVariant =
            DarkTextSecondary,

        outline =
            DarkBorder,

        error =
            ExpenseRed
    )


/*
|--------------------------------------------------------------------------
| TÍCH PHÚ THEME
|--------------------------------------------------------------------------
*/

@Composable
fun PersonalFinanceTheme(

    dynamicColor: Boolean = false,

    content: @Composable () -> Unit

) {

    /*
    |--------------------------------------------------------------------------
    | CONTEXT
    |--------------------------------------------------------------------------
    */

    val context =
        LocalContext.current


    /*
    |--------------------------------------------------------------------------
    | THEME ĐÃ LƯU
    |--------------------------------------------------------------------------
    */

    val themeMode =
        ThemePreference.get(
            context
        )


    /*
    |--------------------------------------------------------------------------
    | THEME CỦA HỆ THỐNG
    |--------------------------------------------------------------------------
    */

    val systemDark =
        isSystemInDarkTheme()


    /*
    |--------------------------------------------------------------------------
    | XÁC ĐỊNH LIGHT / DARK
    |--------------------------------------------------------------------------
    */

    val darkTheme =
        when (themeMode) {

            ThemeMode.LIGHT -> {
                false
            }

            ThemeMode.DARK -> {
                true
            }

            ThemeMode.SYSTEM -> {
                systemDark
            }
        }


    /*
    |--------------------------------------------------------------------------
    | COLOR SCHEME
    |--------------------------------------------------------------------------
    */

    val colorScheme =
        when {

            dynamicColor &&
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.S -> {

                if (darkTheme) {

                    dynamicDarkColorScheme(
                        context
                    )

                } else {

                    dynamicLightColorScheme(
                        context
                    )
                }
            }


            darkTheme -> {

                DarkColors
            }


            else -> {

                LightColors
            }
        }


    /*
    |--------------------------------------------------------------------------
    | STATUS BAR
    |--------------------------------------------------------------------------
    */

    val view =
        LocalView.current


    if (!view.isInEditMode) {

        val activity =
            view.context as? Activity


        if (activity != null) {

            WindowCompat
                .getInsetsController(
                    activity.window,
                    view
                )
                .isAppearanceLightStatusBars =
                !darkTheme
        }
    }


    /*
    |--------------------------------------------------------------------------
    | MATERIAL THEME
    |--------------------------------------------------------------------------
    */

    MaterialTheme(

        colorScheme =
            colorScheme,

        typography =
            Typography,

        content =
            content
    )
}