package com.example.personalfinance.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalfinance.R
import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.viewmodel.AuthViewModel


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onForgotPassword: () -> Unit,
    onRegisterClick: () -> Unit,
    onBack: () -> Unit,
    authViewModel: AuthViewModel
) {

    val context =
        LocalContext.current

    val authState by
    authViewModel.uiState.collectAsState()

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var localMessage by remember {
        mutableStateOf("")
    }


    /*
    |--------------------------------------------------------------------------
    | MAIN
    |--------------------------------------------------------------------------
    */

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFFAF9FF)
            )
            .imePadding(),

        contentPadding =
            PaddingValues(
                bottom = 22.dp
            )
    ) {

        /*
        |--------------------------------------------------------------------------
        | HERO
        |--------------------------------------------------------------------------
        */

        item {

            LoginLightHero()
        }


        /*
        |--------------------------------------------------------------------------
        | LOGIN CARD
        |--------------------------------------------------------------------------
        */

        item {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp
                    )
                    .offset(
                        y = (-32).dp
                    ),

                shape =
                    RoundedCornerShape(
                        30.dp
                    ),

                color =
                    Color.White,

                shadowElevation =
                    8.dp
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 24.dp,
                            bottom = 24.dp
                        )
                ) {

                    /*
                    |--------------------------------------------------------------------------
                    | EMAIL
                    |--------------------------------------------------------------------------
                    */

                    Text(
                        text =
                            "Email",

                        color =
                            Color(0xFF171244),

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    OutlinedTextField(
                        value =
                            email,

                        onValueChange = {

                            email = it

                            localMessage = ""

                            authViewModel
                                .clearError()
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(62.dp),

                        placeholder = {

                            Text(
                                text =
                                    "@email.com",

                                color =
                                    Color(0xFFA29FB8)
                            )
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Email,

                                contentDescription =
                                    null,

                                tint =
                                    Color(0xFF302968)
                            )
                        },

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Email,

                                imeAction =
                                    ImeAction.Next
                            ),

                        singleLine =
                            true,

                        shape =
                            RoundedCornerShape(
                                17.dp
                            ),

                        colors =
                            loginFieldColors()
                    )


                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | PASSWORD
                    |--------------------------------------------------------------------------
                    */

                    Text(
                        text =
                            "Mật khẩu",

                        color =
                            Color(0xFF171244),

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    OutlinedTextField(
                        value =
                            password,

                        onValueChange = {

                            password = it

                            localMessage = ""

                            authViewModel
                                .clearError()
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(62.dp),

                        placeholder = {

                            Text(
                                text =
                                    "Nhập mật khẩu của bạn",

                                color =
                                    Color(0xFFA29FB8)
                            )
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Lock,

                                contentDescription =
                                    null,

                                tint =
                                    Color(0xFF302968)
                            )
                        },

                        trailingIcon = {

                            Row(
                                modifier =
                                    Modifier
                                        .clickable {

                                            passwordVisible =
                                                !passwordVisible
                                        }
                                        .padding(
                                            horizontal = 7.dp,
                                            vertical = 8.dp
                                        ),

                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector =
                                        if (
                                            passwordVisible
                                        ) {

                                            Icons.Default
                                                .Visibility

                                        } else {

                                            Icons.Default
                                                .VisibilityOff
                                        },

                                    contentDescription =
                                        null,

                                    tint =
                                        Color(0xFF4D4684),

                                    modifier =
                                        Modifier.size(
                                            21.dp
                                        )
                                )

                                Spacer(
                                    modifier =
                                        Modifier.size(
                                            5.dp
                                        )
                                )

                                Text(
                                    text =
                                        if (
                                            passwordVisible
                                        ) {
                                            "Ẩn"
                                        } else {
                                            "Hiện"
                                        },

                                    color =
                                        BrandPurple,

                                    fontSize =
                                        14.sp,

                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        },

                        visualTransformation =
                            if (
                                passwordVisible
                            ) {

                                VisualTransformation.None

                            } else {

                                PasswordVisualTransformation()
                            },

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Password,

                                imeAction =
                                    ImeAction.Done
                            ),

                        singleLine =
                            true,

                        shape =
                            RoundedCornerShape(
                                17.dp
                            ),

                        colors =
                            loginFieldColors()
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | FORGOT PASSWORD
                    |--------------------------------------------------------------------------
                    */

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.End
                    ) {

                        TextButton(
                            onClick =
                                onForgotPassword,

                            contentPadding =
                                PaddingValues(
                                    start = 6.dp,
                                    end = 0.dp,
                                    top = 4.dp,
                                    bottom = 4.dp
                                )
                        ) {

                            Text(
                                text =
                                    "Quên mật khẩu?",

                                color =
                                    BrandPurple,

                                fontSize =
                                    14.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | ERROR
                    |--------------------------------------------------------------------------
                    */

                    if (
                        localMessage.isNotBlank()
                    ) {

                        LoginErrorMessage(
                            message =
                                localMessage
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    10.dp
                                )
                        )
                    }


                    if (
                        !authState.error
                            .isNullOrBlank()
                    ) {

                        LoginErrorMessage(
                            message =
                                authState.error
                                    ?: ""
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    10.dp
                                )
                        )
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | LOGIN BUTTON
                    |--------------------------------------------------------------------------
                    */

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(
                                RoundedCornerShape(
                                    22.dp
                                )
                            )
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF963DFF),
                                        Color(0xFF6236FF),
                                        Color(0xFF291573)
                                    )
                                )
                            )
                    ) {

                        Button(
                            onClick = {

                                localMessage = ""

                                authViewModel
                                    .clearError()


                                if (
                                    email.isBlank()
                                ) {

                                    localMessage =
                                        "Vui lòng nhập email."

                                    return@Button
                                }


                                if (
                                    !android.util.Patterns
                                        .EMAIL_ADDRESS
                                        .matcher(
                                            email.trim()
                                        )
                                        .matches()
                                ) {

                                    localMessage =
                                        "Email không hợp lệ."

                                    return@Button
                                }


                                if (
                                    password.isBlank()
                                ) {

                                    localMessage =
                                        "Vui lòng nhập mật khẩu."

                                    return@Button
                                }


                                authViewModel.login(
                                    email =
                                        email.trim(),

                                    password =
                                        password,

                                    onSuccess = {

                                        onLoginSuccess()
                                    }
                                )
                            },

                            enabled =
                                !authState.isLoading,

                            modifier =
                                Modifier.fillMaxSize(),

                            shape =
                                RoundedCornerShape(
                                    22.dp
                                ),

                            colors =
                                ButtonDefaults
                                    .buttonColors(
                                        containerColor =
                                            Color.Transparent,

                                        disabledContainerColor =
                                            BrandPurple.copy(
                                                alpha = 0.42f
                                            )
                                    ),

                            elevation =
                                ButtonDefaults
                                    .buttonElevation(
                                        defaultElevation =
                                            0.dp,

                                        pressedElevation =
                                            0.dp
                                    )
                        ) {

                            if (
                                authState.isLoading
                            ) {

                                CircularProgressIndicator(
                                    modifier =
                                        Modifier.size(
                                            23.dp
                                        ),

                                    strokeWidth =
                                        2.dp,

                                    color =
                                        Color.White
                                )

                            } else {

                                Box(
                                    modifier =
                                        Modifier.fillMaxWidth(),

                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Text(
                                        text =
                                            "Đăng nhập",

                                        color =
                                            Color.White,

                                        fontSize =
                                            18.sp,

                                        fontWeight =
                                            FontWeight.Bold
                                    )


                                    /*
                                    |--------------------------------------------------------------------------
                                    | GOLD ARROW
                                    |--------------------------------------------------------------------------
                                    */

                                    Surface(
                                        modifier =
                                            Modifier
                                                .size(45.dp)
                                                .align(
                                                    Alignment.CenterEnd
                                                ),

                                        shape =
                                            CircleShape,

                                        color =
                                            Color(0xFFFFD66C),

                                        shadowElevation =
                                            4.dp
                                    ) {

                                        Box(
                                            contentAlignment =
                                                Alignment.Center
                                        ) {

                                            Icon(
                                                imageVector =
                                                    Icons.Default
                                                        .ArrowForward,

                                                contentDescription =
                                                    null,

                                                tint =
                                                    Color(0xFF28186D),

                                                modifier =
                                                    Modifier.size(
                                                        23.dp
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | OR
                    |--------------------------------------------------------------------------
                    */

                    Spacer(
                        modifier =
                            Modifier.height(
                                24.dp
                            )
                    )

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        HorizontalDivider(
                            modifier =
                                Modifier.weight(1f),

                            color =
                                Color(0xFFD9D6E8)
                        )

                        Text(
                            text =
                                "   HOẶC   ",

                            color =
                                Color(0xFF817D99),

                            fontSize =
                                11.5.sp,

                            letterSpacing =
                                1.sp,

                            fontWeight =
                                FontWeight.Medium
                        )

                        HorizontalDivider(
                            modifier =
                                Modifier.weight(1f),

                            color =
                                Color(0xFFD9D6E8)
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                20.dp
                            )
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | GOOGLE
                    |--------------------------------------------------------------------------
                    */

                    OutlinedButton(
                        onClick = {

                            localMessage = ""

                            authViewModel
                                .clearError()


                            authViewModel
                                .signInWithGoogle(
                                    context =
                                        context,

                                    serverClientId =
                                        context.getString(
                                            R.string
                                                .default_web_client_id
                                        ),

                                    onSuccess = {

                                        onLoginSuccess()
                                    }
                                )
                        },

                        enabled =
                            !authState.isLoading,

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(58.dp),

                        shape =
                            RoundedCornerShape(
                                18.dp
                            ),

                        border =
                            BorderStroke(
                                width = 1.dp,

                                color =
                                    Color(0xFFE0DDED)
                            ),

                        colors =
                            ButtonDefaults
                                .outlinedButtonColors(
                                    containerColor =
                                        Color.White,

                                    contentColor =
                                        Color(0xFF211B52)
                                )
                    ) {

                        Image(
                            painter =
                                painterResource(
                                    id =
                                        R.drawable
                                            .google_logo
                                ),

                            contentDescription =
                                "Google",

                            modifier =
                                Modifier.size(
                                    26.dp
                                ),

                            contentScale =
                                ContentScale.Fit
                        )

                        Spacer(
                            modifier =
                                Modifier.size(
                                    13.dp
                                )
                        )

                        Text(
                            text =
                                "Đăng nhập với Google",

                            color =
                                Color(0xFF211B52),

                            fontSize =
                                15.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | REGISTER
                    |--------------------------------------------------------------------------
                    */

                    Spacer(
                        modifier =
                            Modifier.height(
                                25.dp
                            )
                    )

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.Center,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                "Chưa có tài khoản? ",

                            color =
                                Color(0xFF686482),

                            fontSize =
                                14.sp
                        )

                        Text(
                            text =
                                "Tạo tài khoản mới",

                            modifier =
                                Modifier.clickable {

                                    onRegisterClick()
                                },

                            color =
                                BrandPurple,

                            fontSize =
                                14.sp,

                            fontWeight =
                                FontWeight.ExtraBold,

                            maxLines =
                                1,

                            overflow =
                                TextOverflow.Ellipsis
                        )

                        Spacer(
                            modifier =
                                Modifier.size(
                                    4.dp
                                )
                        )

                        Text(
                            text =
                                "›",

                            color =
                                BrandPurple,

                            fontSize =
                                21.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | FOOTER
        |--------------------------------------------------------------------------
        */

        item {

            LoginFooter()
        }
    }
}


/*
|--------------------------------------------------------------------------
| HERO
|--------------------------------------------------------------------------
*/

@Composable
private fun LoginLightHero() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                375.dp
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFFCFBFF),
                        Color(0xFFF5F1FF)
                    )
                )
            )
    ) {

        /*
        |--------------------------------------------------------------------------
        | BACKGROUND DECORATION
        |--------------------------------------------------------------------------
        */

        Box(
            modifier = Modifier
                .size(
                    205.dp
                )
                .align(
                    Alignment.CenterEnd
                )
                .offset(
                    x = 62.dp,
                    y = 25.dp
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 95.dp,
                        bottomStart = 95.dp
                    )
                )
                .background(
                    BrandPurple.copy(
                        alpha = 0.055f
                    )
                )
        )


        Box(
            modifier = Modifier
                .size(
                    130.dp
                )
                .align(
                    Alignment.BottomEnd
                )
                .offset(
                    x = (-12).dp,
                    y = 42.dp
                )
                .clip(
                    CircleShape
                )
                .background(
                    Color(0xFFB99BFF)
                        .copy(
                            alpha = 0.09f
                        )
                )
        )


        /*
        |--------------------------------------------------------------------------
        | LOGO + BRAND
        |--------------------------------------------------------------------------
        */

        Row(
            modifier = Modifier
                .align(
                    Alignment.TopStart
                )
                .padding(
                    start = 22.dp,
                    top = 18.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Image(
                painter =
                    painterResource(
                        id =
                            R.drawable
                                .tich_phu_app_icon
                    ),

                contentDescription =
                    "Logo Tích Phú",

                modifier =
                    Modifier
                        .size(
                            70.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                19.dp
                            )
                        ),

                contentScale =
                    ContentScale.Crop
            )


            Spacer(
                modifier =
                    Modifier.size(
                        10.dp
                    )
            )


            Column {

                Text(
                    text =
                        "Tích Phú",

                    color =
                        Color(0xFF17114D),

                    fontSize =
                        25.sp,

                    lineHeight =
                        28.sp,

                    fontWeight =
                        FontWeight.ExtraBold
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            2.dp
                        )
                )


                Text(
                    text =
                        "TÍCH LŨY HÔM NAY",

                    color =
                        Color(0xFF696582),

                    fontSize =
                        7.5.sp,

                    letterSpacing =
                        1.2.sp,

                    fontWeight =
                        FontWeight.Medium
                )


                Text(
                    text =
                        "THỊNH VƯỢNG MAI SAU",

                    color =
                        Color(0xFF696582),

                    fontSize =
                        7.5.sp,

                    letterSpacing =
                        1.2.sp,

                    fontWeight =
                        FontWeight.Medium
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | TOP RIGHT TEXT
        |--------------------------------------------------------------------------
        */

        Column(
            modifier = Modifier
                .align(
                    Alignment.TopEnd
                )
                .padding(
                    top = 30.dp,
                    end = 20.dp
                )
        ) {

            Text(
                text =
                    "NHỮNG BƯỚC NHỎ\n" +
                            "TẠO NÊN TƯƠNG LAI\n" +
                            "LỚN LAO",

                color =
                    Color(0xFF5E597C),

                fontSize =
                    7.3.sp,

                lineHeight =
                    12.sp,

                letterSpacing =
                    1.4.sp,

                fontWeight =
                    FontWeight.Medium
            )


            Spacer(
                modifier =
                    Modifier.height(
                        7.dp
                    )
            )


            Box(
                modifier =
                    Modifier
                        .size(
                            width = 28.dp,
                            height = 2.dp
                        )
                        .background(
                            Color(0xFFD4A42E)
                        )
            )
        }


        /*
        |--------------------------------------------------------------------------
        | LOGIN TEXT
        |--------------------------------------------------------------------------
        */

        Column(
            modifier = Modifier
                .align(
                    Alignment.CenterStart
                )
                .padding(
                    start = 23.dp
                )
                .offset(
                    y = 48.dp
                )
                .fillMaxWidth(
                    0.54f
                )
        ) {

            Text(
                text =
                    "Xin chào!",

                color =
                    Color(0xFF17124D),

                fontSize =
                    22.sp,

                lineHeight =
                    26.sp,

                fontWeight =
                    FontWeight.Medium
            )


            Spacer(
                modifier =
                    Modifier.height(
                        2.dp
                    )
            )


            Text(
                text =
                    "Đăng nhập",

                color =
                    Color(0xFF17114D),

                fontSize =
                    38.sp,

                lineHeight =
                    42.sp,

                fontWeight =
                    FontWeight.ExtraBold
            )


            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
                    )
            )


            Text(
                text =
                    "Tiếp tục hành trình tích lũy,\n" +
                            "kiến tạo tương lai thịnh vượng\n" +
                            "cùng Tích Phú.",

                color =
                    Color(0xFF706B8B),

                fontSize =
                    12.5.sp,

                lineHeight =
                    18.sp
            )
        }


        /*
        |--------------------------------------------------------------------------
        | HERO ARTWORK
        |--------------------------------------------------------------------------
        */

        Image(
            painter =
                painterResource(
                    id =
                        R.drawable
                            .welcome_hero
                ),

            contentDescription =
                "Minh họa tài chính",

            modifier =
                Modifier
                    .size(
                        245.dp
                    )
                    .align(
                        Alignment.CenterEnd
                    )
                    .offset(
                        x = 36.dp,
                        y = 69.dp
                    ),

            contentScale =
                ContentScale.Fit
        )
    }
}


/*
|--------------------------------------------------------------------------
| FOOTER
|--------------------------------------------------------------------------
*/

@Composable
private fun LoginFooter() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                116.dp
            )
            .offset(
                y = (-12).dp
            )
            .background(
                brush =
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFBFAFF),
                            Color(0xFFF1ECFF)
                        )
                    ),

                shape =
                    RoundedCornerShape(
                        topStart = 46.dp,
                        topEnd = 46.dp
                    )
            )
    ) {

        /*
        |--------------------------------------------------------------------------
        | FOOTER TEXT
        |--------------------------------------------------------------------------
        */

        Text(
            text =
                "QUẢN LÝ TÀI CHÍNH\n" +
                        "CHO MỘT CUỘC SỐNG TỐT ĐẸP HƠN",

            modifier =
                Modifier
                    .align(
                        Alignment.CenterStart
                    )
                    .padding(
                        start = 24.dp
                    ),

            color =
                Color(0xFF77718F),

            fontSize =
                8.sp,

            lineHeight =
                13.dp.value.sp,

            letterSpacing =
                1.7.sp,

            fontWeight =
                FontWeight.Medium
        )


        /*
        |--------------------------------------------------------------------------
        | DOTS
        |--------------------------------------------------------------------------
        */

        Row(
            modifier =
                Modifier
                    .align(
                        Alignment.CenterEnd
                    )
                    .padding(
                        end = 28.dp
                    ),

            horizontalArrangement =
                Arrangement.spacedBy(
                    8.dp
                )
        ) {

            Box(
                modifier =
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            BrandPurple
                        )
            )


            Box(
                modifier =
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            BrandPurple.copy(
                                alpha = 0.18f
                            )
                        )
            )


            Box(
                modifier =
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            BrandPurple.copy(
                                alpha = 0.18f
                            )
                        )
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| FIELD COLORS
|--------------------------------------------------------------------------
*/

@Composable
private fun loginFieldColors() =
    OutlinedTextFieldDefaults.colors(

        focusedTextColor =
            Color(0xFF171244),

        unfocusedTextColor =
            Color(0xFF171244),

        focusedBorderColor =
            BrandPurple,

        unfocusedBorderColor =
            Color(0xFFE1DEED),

        focusedContainerColor =
            Color(0xFFFEFDFF),

        unfocusedContainerColor =
            Color(0xFFFEFDFF),

        focusedLeadingIconColor =
            BrandPurple,

        cursorColor =
            BrandPurple
    )


/*
|--------------------------------------------------------------------------
| ERROR
|--------------------------------------------------------------------------
*/

@Composable
private fun LoginErrorMessage(
    message: String
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                14.dp
            ),

        color =
            ExpenseRed.copy(
                alpha = 0.10f
            )
    ) {

        Text(
            text =
                message,

            modifier =
                Modifier.padding(
                    12.dp
                ),

            color =
                ExpenseRed,

            style =
                MaterialTheme
                    .typography
                    .bodySmall,

            fontWeight =
                FontWeight.SemiBold
        )
    }
}