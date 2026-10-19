package com.example.personalfinance.ui.profile

import android.app.Activity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

import com.example.personalfinance.R
import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.ui.theme.BrandPurpleDark
import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.ui.theme.ThemeMode
import com.example.personalfinance.ui.theme.ThemePreference
import com.example.personalfinance.viewmodel.AuthViewModel


@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onBankAccountClick: () -> Unit,
    onNotificationClick: () -> Unit = {},
    onCurrencyClick: () -> Unit = {},
    onSecurityClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    authViewModel: AuthViewModel
) {

    /*
    |--------------------------------------------------------------------------
    | STATE
    |--------------------------------------------------------------------------
    */

    val authState by
    authViewModel
        .uiState
        .collectAsState()


    val user =
        authState.user


    val context =
        LocalContext.current


    var showLogoutDialog by remember {
        mutableStateOf(
            false
        )
    }


    var showThemeDialog by remember {
        mutableStateOf(
            false
        )
    }


    var selectedTheme by remember {

        mutableStateOf(
            ThemePreference.get(
                context
            )
        )
    }


    /*
    |--------------------------------------------------------------------------
    | SYNC USER
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(
        authState.isLoggedIn,
        authState.user,
        authState.isLoading
    ) {

        if (
            authState.isLoggedIn &&
            authState.user == null &&
            !authState.isLoading &&
            authState.error == null
        ) {

            authViewModel
                .syncUser()
        }
    }


    /*
    |--------------------------------------------------------------------------
    | THEME DIALOG
    |--------------------------------------------------------------------------
    */

    if (
        showThemeDialog
    ) {

        ProfileThemeDialog(
            selectedTheme =
                selectedTheme,
            onThemeSelected = {

                selectedTheme =
                    it
            },
            onDismiss = {

                showThemeDialog =
                    false

                selectedTheme =
                    ThemePreference.get(
                        context
                    )
            },
            onApply = {

                ThemePreference.save(
                    context,
                    selectedTheme
                )


                showThemeDialog =
                    false


                val activity =
                    context as? Activity


                activity?.recreate()
            }
        )
    }


    /*
    |--------------------------------------------------------------------------
    | LOGOUT DIALOG
    |--------------------------------------------------------------------------
    */

    if (
        showLogoutDialog
    ) {

        AlertDialog(
            onDismissRequest = {

                showLogoutDialog =
                    false
            },
            shape =
                RoundedCornerShape(
                    26.dp
                ),
            icon = {

                Surface(
                    modifier =
                        Modifier.size(
                            62.dp
                        ),
                    shape =
                        CircleShape,
                    color =
                        ExpenseRed.copy(
                            alpha = 0.12f
                        )
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Outlined
                                    .Logout,
                            contentDescription =
                                null,
                            tint =
                                ExpenseRed,
                            modifier =
                                Modifier.size(
                                    29.dp
                                )
                        )
                    }
                }
            },
            title = {

                Text(
                    text =
                        "Đăng xuất",
                    fontWeight =
                        FontWeight.ExtraBold
                )
            },
            text = {

                Text(
                    text =
                        "Bạn có chắc chắn muốn đăng xuất khỏi Tích Phú không?"
                )
            },
            confirmButton = {

                Button(
                    onClick = {

                        showLogoutDialog =
                            false

                        onLogout()
                    },
                    shape =
                        RoundedCornerShape(
                            14.dp
                        ),
                    colors =
                        ButtonDefaults
                            .buttonColors(
                                containerColor =
                                    ExpenseRed
                            )
                ) {

                    Text(
                        text =
                            "Đăng xuất",
                        color =
                            Color.White,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {

                        showLogoutDialog =
                            false
                    }
                ) {

                    Text(
                        "Hủy"
                    )
                }
            }
        )
    }


    /*
    |--------------------------------------------------------------------------
    | SCREEN
    |--------------------------------------------------------------------------
    */

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                ),
        contentPadding =
            PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 16.dp,
                bottom = 115.dp
            ),
        verticalArrangement =
            Arrangement.spacedBy(
                14.dp
            )
    ) {


        /*
        |--------------------------------------------------------------------------
        | HEADER
        |--------------------------------------------------------------------------
        */

        item {

            Column {

                Text(
                    text =
                        "Cá nhân",
                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,
                    fontWeight =
                        FontWeight.ExtraBold,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onBackground
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            3.dp
                        )
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | LOADING
        |--------------------------------------------------------------------------
        */

        if (
            authState.isLoading
        ) {

            item {

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(
                                300.dp
                            ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )
                }
            }

            return@LazyColumn
        }


        /*
        |--------------------------------------------------------------------------
        | ERROR
        |--------------------------------------------------------------------------
        */

        if (
            authState.error != null
        ) {

            item {

                ProfileErrorCard(
                    message =
                        authState.error
                            ?: "Không thể tải thông tin tài khoản.",
                    onRetry = {

                        authViewModel
                            .clearError()

                        authViewModel
                            .syncUser()
                    }
                )
            }

            return@LazyColumn
        }


        /*
        |--------------------------------------------------------------------------
        | USER NULL
        |--------------------------------------------------------------------------
        */

        if (
            user == null
        ) {

            item {

                ProfileEmptyUserCard(
                    onRetry = {

                        authViewModel
                            .syncUser()
                    }
                )
            }

            return@LazyColumn
        }


        /*
        |--------------------------------------------------------------------------
        | USER DATA
        |--------------------------------------------------------------------------
        */

        val displayName =
            user.name
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: "Người dùng"


        val displayEmail =
            user.email
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: "Chưa có email"


        val currency =
            user.currency
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: "VND"


        /*
        |--------------------------------------------------------------------------
        | PROFILE HERO
        |--------------------------------------------------------------------------
        */

        item {

            ProfileHeroCard(
                displayName =
                    displayName,
                displayEmail =
                    displayEmail,
                currency =
                    currency
            )
        }


        /*
        |--------------------------------------------------------------------------
        | ACCOUNT INFO
        |--------------------------------------------------------------------------
        */

        item {

            ProfileSectionTitle(
                title =
                    "Thông tin tài khoản"
            )
        }


        item {

            ProfileInformationCard(
                displayName =
                    displayName,
                displayEmail =
                    displayEmail,
                currency =
                    currency
            )
        }


        /*
        |--------------------------------------------------------------------------
        | ACCOUNT SETTINGS
        |--------------------------------------------------------------------------
        */

        item {

            ProfileSectionTitle(
                title =
                    "Tài khoản & Cài đặt"
            )
        }


        item {

            ProfileSettingsCard {

                // TÀI KHOẢN NGÂN HÀNG
                ProfileSettingRow(
                    icon = Icons.Outlined.AccountBalance,
                    iconColor = Color(0xFF159653),
                    title = "Tài khoản liên kết",
                    subtitle = "Quản lý tài khoản ngân hàng",
                    onClick = onBankAccountClick
                )

                ProfileSettingDivider()

                // THÔNG BÁO
                ProfileSettingRow(
                    icon = Icons.Outlined.NotificationsNone,
                    iconColor = Color(0xFFF39A35),
                    title = "Thông báo",
                    subtitle = "Quản lý thông báo ứng dụng",
                    onClick = onNotificationClick
                )
            }
        }


        /*
 |--------------------------------------------------------------------------
 | APP SETTINGS
 |--------------------------------------------------------------------------
 */

        item {

            ProfileSectionTitle(
                title = "Cài đặt ứng dụng"
            )
        }

        item {

            ProfileSettingsCard {

                // GIAO DIỆN
                ProfileSettingRow(
                    icon = Icons.Outlined.DarkMode,
                    iconColor = Color(0xFF7557E9),
                    title = "Giao diện",
                    subtitle = "Sáng, tối hoặc theo hệ thống",
                    onClick = {
                        selectedTheme = ThemePreference.get(context)
                        showThemeDialog = true
                    }
                )

                ProfileSettingDivider()

                // BẢO MẬT
                ProfileSettingRow(
                    icon = Icons.Outlined.Lock,
                    iconColor = ExpenseRed,
                    title = "Bảo mật",
                    subtitle = "Quản lý bảo mật tài khoản",
                    onClick = onSecurityClick
                )

                ProfileSettingDivider()

                // GIỚI THIỆU
                ProfileSettingRow(
                    icon = Icons.Outlined.Info,
                    iconColor = Color(0xFF58627B),
                    title = "Giới thiệu Tích Phú",
                    subtitle = "Thông tin về ứng dụng",
                    onClick = onAboutClick
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | LOGOUT
        |--------------------------------------------------------------------------
        */

        item {

            Button(
                onClick = {

                    showLogoutDialog =
                        true
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            54.dp
                        ),
                shape =
                    RoundedCornerShape(
                        17.dp
                    ),
                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                ExpenseRed
                        )
            ) {

                Icon(
                    imageVector =
                        Icons.AutoMirrored.Outlined.Logout,
                    contentDescription =
                        null,
                    tint =
                        Color.White
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            8.dp
                        )
                )


                Text(
                    text =
                        "Đăng xuất",
                    color =
                        Color.White,
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| HERO
|--------------------------------------------------------------------------
*/

@Composable
private fun ProfileHeroCard(
    displayName: String,
    displayEmail: String,
    currency: String
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                28.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.Transparent
            )
    ) {

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        brush =
                            Brush.linearGradient(
                                colors =
                                    listOf(
                                        BrandPurpleDark,
                                        BrandPurple,
                                        Color(
                                            0xFF8D5CFF
                                        )
                                    )
                            ),
                        shape =
                            RoundedCornerShape(
                                28.dp
                            )
                    )
                    .padding(
                        horizontal = 20.dp,
                        vertical = 24.dp
                    )
        ) {

            Column(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {


                /*
                |--------------------------------------------------------------------------
                | AVATAR
                |--------------------------------------------------------------------------
                */

                Surface(
                    modifier =
                        Modifier.size(
                            92.dp
                        ),
                    shape =
                        CircleShape,
                    color =
                        Color.White.copy(
                            alpha = 0.18f
                        )
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Surface(
                            modifier =
                                Modifier.size(
                                    76.dp
                                ),
                            shape =
                                CircleShape,
                            color =
                                Color.White
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Image(
                                    painter =
                                        painterResource(
                                            R.drawable
                                                .tich_phu_app_icon
                                        ),
                                    contentDescription =
                                        "Tích Phú",
                                    modifier =
                                        Modifier.size(
                                            60.dp
                                        )
                                )
                            }
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            13.dp
                        )
                )


                /*
                |--------------------------------------------------------------------------
                | NAME
                |--------------------------------------------------------------------------
                */

                Text(
                    text =
                        displayName,
                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,
                    color =
                        Color.White,
                    fontWeight =
                        FontWeight.ExtraBold,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow.Ellipsis
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            4.dp
                        )
                )


                Text(
                    text =
                        displayEmail,
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        Color.White.copy(
                            alpha = 0.82f
                        ),
                    maxLines =
                        1,
                    overflow =
                        TextOverflow.Ellipsis
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            14.dp
                        )
                )


                /*
                |--------------------------------------------------------------------------
                | CURRENCY
                |--------------------------------------------------------------------------
                */

                Surface(
                    shape =
                        RoundedCornerShape(
                            50.dp
                        ),
                    color =
                        Color.White.copy(
                            alpha = 0.14f
                        )
                ) {

                    Row(
                        modifier =
                            Modifier.padding(
                                horizontal = 15.dp,
                                vertical = 7.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Outlined
                                    .Payments,
                            contentDescription =
                                null,
                            tint =
                                Color.White,
                            modifier =
                                Modifier.size(
                                    16.dp
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    6.dp
                                )
                        )


                        Text(
                            text =
                                "Tiền tệ: $currency",
                            color =
                                Color.White,
                            style =
                                MaterialTheme
                                    .typography
                                    .labelMedium,
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| INFORMATION CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun ProfileInformationCard(
    displayName: String,
    displayEmail: String,
    currency: String
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                22.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
        ) {

            ProfileInfoRow(
                icon =
                    Icons.Outlined.Person,
                iconColor =
                    BrandPurple,
                label =
                    "Họ và tên",
                value =
                    displayName
            )


            ProfileInfoDivider()


            ProfileInfoRow(
                icon =
                    Icons.Outlined.Email,
                iconColor =
                    Color(
                        0xFF318BF0
                    ),
                label =
                    "Email",
                value =
                    displayEmail
            )


            ProfileInfoDivider()


            ProfileInfoRow(
                icon =
                    Icons.Outlined.Payments,
                iconColor =
                    Color(
                        0xFF159653
                    ),
                label =
                    "Đơn vị tiền",
                value =
                    currency
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| SECTION TITLE
|--------------------------------------------------------------------------
*/

@Composable
private fun ProfileSectionTitle(
    title: String
) {

    Text(
        text =
            title,
        style =
            MaterialTheme
                .typography
                .titleMedium,
        fontWeight =
            FontWeight.ExtraBold,
        color =
            MaterialTheme
                .colorScheme
                .onBackground
    )
}


/*
|--------------------------------------------------------------------------
| PROFILE INFO ROW
|--------------------------------------------------------------------------
*/

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 11.dp
                ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Surface(
            modifier =
                Modifier.size(
                    46.dp
                ),
            shape =
                RoundedCornerShape(
                    14.dp
                ),
            color =
                iconColor.copy(
                    alpha = 0.11f
                )
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        icon,
                    contentDescription =
                        null,
                    tint =
                        iconColor,
                    modifier =
                        Modifier.size(
                            23.dp
                        )
                )
            }
        }


        Spacer(
            modifier =
                Modifier.width(
                    12.dp
                )
        )


        Column(
            modifier =
                Modifier.weight(
                    1f
                )
        ) {

            Text(
                text =
                    label,
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )


            Spacer(
                modifier =
                    Modifier.height(
                        2.dp
                    )
            )


            Text(
                text =
                    value,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                fontWeight =
                    FontWeight.SemiBold,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurface,
                maxLines =
                    1,
                overflow =
                    TextOverflow.Ellipsis
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| INFO DIVIDER
|--------------------------------------------------------------------------
*/

@Composable
private fun ProfileInfoDivider() {

    HorizontalDivider(
        modifier =
            Modifier.padding(
                start = 58.dp
            ),
        color =
            MaterialTheme
                .colorScheme
                .outlineVariant
                .copy(
                    alpha = 0.5f
                )
    )
}


/*
|--------------------------------------------------------------------------
| SETTINGS CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun ProfileSettingsCard(
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                22.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.fillMaxWidth(),
            content =
                content
        )
    }
}


/*
|--------------------------------------------------------------------------
| SETTING ROW
|--------------------------------------------------------------------------
*/

@Composable
private fun ProfileSettingRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    trailingText: String? = null,
    onClick: (() -> Unit)? = null
) {

    val rowModifier =
        if (onClick != null) {
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
        } else {
            Modifier.fillMaxWidth()
        }

    Row(
        modifier = rowModifier
            .height(76.dp)
            .padding(
                horizontal = 15.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        /*
        |--------------------------------------------------------------------------
        | ICON
        |--------------------------------------------------------------------------
        */

        Surface(
            modifier = Modifier.size(45.dp),
            shape = RoundedCornerShape(14.dp),
            color = iconColor.copy(alpha = 0.11f)
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(23.dp)
                )
            }
        }


        Spacer(
            modifier = Modifier.width(12.dp)
        )


        /*
        |--------------------------------------------------------------------------
        | TITLE + SUBTITLE
        |--------------------------------------------------------------------------
        */

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (subtitle.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | TRAILING TEXT
        |--------------------------------------------------------------------------
        */

        if (trailingText != null) {

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = trailingText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }


        /*
        |--------------------------------------------------------------------------
        | ARROW
        |--------------------------------------------------------------------------
        */

        if (onClick != null) {

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Icon(
                imageVector =
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
                modifier =
                    Modifier.size(22.dp)
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| SETTINGS DIVIDER
|--------------------------------------------------------------------------
*/

@Composable
private fun ProfileSettingDivider() {

    HorizontalDivider(
        modifier =
            Modifier.padding(
                start = 72.dp,
                end = 15.dp
            ),
        color =
            MaterialTheme
                .colorScheme
                .outlineVariant
                .copy(
                    alpha = 0.5f
                )
    )
}


/*
|--------------------------------------------------------------------------
| THEME DIALOG
|--------------------------------------------------------------------------
*/

@Composable
private fun ProfileThemeDialog(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {

    Dialog(
        onDismissRequest =
            onDismiss
    ) {

        Surface(
            modifier =
                Modifier.fillMaxWidth(),
            shape =
                RoundedCornerShape(
                    28.dp
                ),
            color =
                MaterialTheme
                    .colorScheme
                    .surface,
            tonalElevation =
                8.dp,
            shadowElevation =
                16.dp
        ) {

            Column(
                modifier =
                    Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 22.dp
                    )
            ) {


                /*
                |--------------------------------------------------------------------------
                | HEADER
                |--------------------------------------------------------------------------
                */

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Surface(
                        modifier =
                            Modifier.size(
                                50.dp
                            ),
                        shape =
                            RoundedCornerShape(
                                16.dp
                            ),
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                                .copy(
                                    alpha = 0.11f
                                )
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Outlined.Palette,
                                contentDescription =
                                    null,
                                tint =
                                    MaterialTheme
                                        .colorScheme
                                        .primary,
                                modifier =
                                    Modifier.size(
                                        26.dp
                                    )
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.width(
                                13.dp
                            )
                    )


                    Column {

                        Text(
                            text =
                                "Giao diện",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.ExtraBold
                        )


                        Text(
                            text =
                                "Chọn chế độ hiển thị",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            20.dp
                        )
                )


                /*
                |--------------------------------------------------------------------------
                | LIGHT
                |--------------------------------------------------------------------------
                */

                ThemeOptionCard(
                    title =
                        "Sáng",
                    subtitle =
                        "Giao diện sáng và rõ ràng",
                    icon =
                        Icons.Outlined
                            .LightMode,
                    selected =
                        selectedTheme ==
                                ThemeMode.LIGHT,
                    onClick = {

                        onThemeSelected(
                            ThemeMode.LIGHT
                        )
                    }
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )


                /*
                |--------------------------------------------------------------------------
                | DARK
                |--------------------------------------------------------------------------
                */

                ThemeOptionCard(
                    title =
                        "Tối",
                    subtitle =
                        "Dịu mắt khi sử dụng trong môi trường tối",
                    icon =
                        Icons.Outlined.DarkMode,
                    selected =
                        selectedTheme ==
                                ThemeMode.DARK,
                    onClick = {

                        onThemeSelected(
                            ThemeMode.DARK
                        )
                    }
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )


                /*
                |--------------------------------------------------------------------------
                | SYSTEM
                |--------------------------------------------------------------------------
                */

                ThemeOptionCard(
                    title =
                        "Theo hệ thống",
                    subtitle =
                        "Tự động theo giao diện của thiết bị",
                    icon =
                        Icons.Outlined
                            .DesktopWindows,
                    selected =
                        selectedTheme ==
                                ThemeMode.SYSTEM,
                    onClick = {

                        onThemeSelected(
                            ThemeMode.SYSTEM
                        )
                    }
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            20.dp
                        )
                )


                HorizontalDivider(
                    color =
                        MaterialTheme
                            .colorScheme
                            .outlineVariant
                            .copy(
                                alpha = 0.5f
                            )
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )


                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.End,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    TextButton(
                        onClick =
                            onDismiss
                    ) {

                        Text(
                            text =
                                "Hủy",
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.width(
                                8.dp
                            )
                    )


                    Button(
                        onClick =
                            onApply,
                        modifier =
                            Modifier.height(
                                48.dp
                            ),
                        shape =
                            RoundedCornerShape(
                                50.dp
                            ),
                        contentPadding =
                            PaddingValues(
                                horizontal = 23.dp
                            )
                    ) {

                        Text(
                            text =
                                "Áp dụng",
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| THEME OPTION
|--------------------------------------------------------------------------
*/

@Composable
private fun ThemeOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {

    val borderColor =
        if (
            selected
        ) {

            MaterialTheme
                .colorScheme
                .primary

        } else {

            MaterialTheme
                .colorScheme
                .outlineVariant
                .copy(
                    alpha = 0.65f
                )
        }


    val backgroundColor =
        if (
            selected
        ) {

            MaterialTheme
                .colorScheme
                .primary
                .copy(
                    alpha = 0.10f
                )

        } else {

            MaterialTheme
                .colorScheme
                .surfaceVariant
                .copy(
                    alpha = 0.28f
                )
        }


    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {

                    onClick()
                },
        shape =
            RoundedCornerShape(
                19.dp
            ),
        color =
            backgroundColor,
        border =
            BorderStroke(
                width =
                    if (
                        selected
                    ) {
                        1.5.dp
                    } else {
                        1.dp
                    },
                color =
                    borderColor
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 13.dp,
                        vertical = 13.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            RadioButton(
                selected =
                    selected,
                onClick =
                    onClick,
                colors =
                    RadioButtonDefaults.colors(
                        selectedColor =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )
            )


            Spacer(
                modifier =
                    Modifier.width(
                        3.dp
                    )
            )


            Surface(
                modifier =
                    Modifier.size(
                        42.dp
                    ),
                shape =
                    RoundedCornerShape(
                        13.dp
                    ),
                color =
                    if (
                        selected
                    ) {

                        MaterialTheme
                            .colorScheme
                            .primary
                            .copy(
                                alpha = 0.13f
                            )

                    } else {

                        MaterialTheme
                            .colorScheme
                            .surface
                    }
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            icon,
                        contentDescription =
                            null,
                        tint =
                            if (
                                selected
                            ) {

                                MaterialTheme
                                    .colorScheme
                                    .primary

                            } else {

                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                            },
                        modifier =
                            Modifier.size(
                                22.dp
                            )
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.width(
                        11.dp
                    )
            )


            Column(
                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {

                Text(
                    text =
                        title,
                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge,
                    fontWeight =
                        if (
                            selected
                        ) {

                            FontWeight.Bold

                        } else {

                            FontWeight.SemiBold
                        },
                    color =
                        if (
                            selected
                        ) {

                            MaterialTheme
                                .colorScheme
                                .primary

                        } else {

                            MaterialTheme
                                .colorScheme
                                .onSurface
                        }
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            2.dp
                        )
                )


                Text(
                    text =
                        subtitle,
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| ERROR
|--------------------------------------------------------------------------
*/

@Composable
private fun ProfileErrorCard(
    message: String,
    onRetry: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                22.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .errorContainer
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        20.dp
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    "Không thể tải tài khoản",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.ExtraBold,
                color =
                    MaterialTheme
                        .colorScheme
                        .onErrorContainer
            )


            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )


            Text(
                text =
                    message,
                color =
                    MaterialTheme
                        .colorScheme
                        .onErrorContainer,
                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(
                        15.dp
                    )
            )


            Button(
                onClick =
                    onRetry
            ) {

                Text(
                    text =
                        "Thử lại",
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| EMPTY USER
|--------------------------------------------------------------------------
*/

@Composable
private fun ProfileEmptyUserCard(
    onRetry: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                22.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        24.dp
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Surface(
                modifier =
                    Modifier.size(
                        64.dp
                    ),
                shape =
                    CircleShape,
                color =
                    MaterialTheme
                        .colorScheme
                        .primary
                        .copy(
                            alpha = 0.10f
                        )
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Outlined.Person,
                        contentDescription =
                            null,
                        tint =
                            MaterialTheme
                                .colorScheme
                                .primary,
                        modifier =
                            Modifier.size(
                                30.dp
                            )
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            Text(
                text =
                    "Chưa tải được thông tin tài khoản",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.ExtraBold
            )


            Spacer(
                modifier =
                    Modifier.height(
                        5.dp
                    )
            )


            Text(
                text =
                    "Hãy thử đồng bộ lại dữ liệu tài khoản của bạn.",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(
                        15.dp
                    )
            )


            Button(
                onClick =
                    onRetry
            ) {

                Text(
                    text =
                        "Đồng bộ lại",
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}