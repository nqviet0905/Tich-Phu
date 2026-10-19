package com.example.personalfinance.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.personalfinance.R

@Composable
fun BrandLogo(
    modifier: Modifier = Modifier
) {

    val logoRes =
        if (isSystemInDarkTheme()) {
            R.drawable.tich_phu_icon_dark
        } else {
            R.drawable.tich_phu_icon_light
        }

    Image(
        painter = painterResource(
            id = logoRes
        ),
        contentDescription = "Tích Phú",
        modifier = modifier
    )
}
