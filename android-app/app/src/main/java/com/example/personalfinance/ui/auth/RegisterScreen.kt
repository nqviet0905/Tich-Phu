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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalfinance.R
import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.ui.theme.BrandPurpleDark
import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.viewmodel.AuthViewModel


@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    authViewModel: AuthViewModel
) {

    val context =
        LocalContext.current

    val authState by
    authViewModel.uiState.collectAsState()

    val registerMessage by
    authViewModel.registerMessage.collectAsState()

    var name by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }

    var agreeTerms by remember {
        mutableStateOf(true)
    }

    var localMessage by remember {
        mutableStateOf<String?>(null)
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFF7F5FF)
            )
            .imePadding(),
        contentPadding =
            PaddingValues(
                bottom = 26.dp
            )
    ) {

        /*
        |--------------------------------------------------------------------------
        | HERO
        |--------------------------------------------------------------------------
        */

        item {

            RegisterHero()
        }


        /*
        |--------------------------------------------------------------------------
        | FORM CARD
        |--------------------------------------------------------------------------
        */

        item {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp
                    )
                    .offset(
                        y = (-24).dp
                    ),
                shape =
                    RoundedCornerShape(
                        32.dp
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
                            top = 22.dp,
                            bottom = 22.dp
                        )
                ) {

                    /*
                    |--------------------------------------------------------------------------
                    | NAME
                    |--------------------------------------------------------------------------
                    */

                    RegisterFieldLabel(
                        text = "Họ và tên"
                    )

                    Spacer(
                        modifier =
                            Modifier.height(7.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = {

                            name = it
                            localMessage = null

                            authViewModel.clearError()
                            authViewModel.clearRegisterMessage()
                        },
                        modifier =
                            Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Nhập họ và tên của bạn"
                            )
                        },
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Person,
                                contentDescription =
                                    null,
                                tint =
                                    Color(0xFF625D8C)
                            )
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                imeAction =
                                    ImeAction.Next
                            ),
                        singleLine = true,
                        shape =
                            RoundedCornerShape(
                                18.dp
                            ),
                        colors =
                            registerFieldColors()
                    )

                    Spacer(
                        modifier =
                            Modifier.height(15.dp)
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | EMAIL
                    |--------------------------------------------------------------------------
                    */

                    RegisterFieldLabel(
                        text = "Email"
                    )

                    Spacer(
                        modifier =
                            Modifier.height(7.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {

                            email = it
                            localMessage = null

                            authViewModel.clearError()
                            authViewModel.clearRegisterMessage()
                        },
                        modifier =
                            Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Nhập email của bạn"
                            )
                        },
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Email,
                                contentDescription =
                                    null,
                                tint =
                                    Color(0xFF625D8C)
                            )
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Email,
                                imeAction =
                                    ImeAction.Next
                            ),
                        singleLine = true,
                        shape =
                            RoundedCornerShape(
                                18.dp
                            ),
                        colors =
                            registerFieldColors()
                    )

                    Spacer(
                        modifier =
                            Modifier.height(15.dp)
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | PASSWORD
                    |--------------------------------------------------------------------------
                    */

                    RegisterFieldLabel(
                        text = "Mật khẩu"
                    )

                    Spacer(
                        modifier =
                            Modifier.height(7.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {

                            password = it
                            localMessage = null

                            authViewModel.clearError()
                            authViewModel.clearRegisterMessage()
                        },
                        modifier =
                            Modifier.fillMaxWidth(),
                        placeholder = {

                            Text(
                                "Tạo mật khẩu (ít nhất 6 ký tự)"
                            )
                        },
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Lock,
                                contentDescription =
                                    null,
                                tint =
                                    Color(0xFF625D8C)
                            )
                        },
                        trailingIcon = {

                            IconButton(
                                onClick = {

                                    passwordVisible =
                                        !passwordVisible
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (
                                            passwordVisible
                                        ) {
                                            Icons.Default.Visibility
                                        } else {
                                            Icons.Default.VisibilityOff
                                        },
                                    contentDescription =
                                        if (
                                            passwordVisible
                                        ) {
                                            "Ẩn mật khẩu"
                                        } else {
                                            "Hiện mật khẩu"
                                        },
                                    tint =
                                        Color(0xFF625D8C)
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
                                    ImeAction.Next
                            ),
                        singleLine = true,
                        shape =
                            RoundedCornerShape(
                                18.dp
                            ),
                        colors =
                            registerFieldColors()
                    )

                    Spacer(
                        modifier =
                            Modifier.height(15.dp)
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | CONFIRM PASSWORD
                    |--------------------------------------------------------------------------
                    */

                    RegisterFieldLabel(
                        text = "Nhập lại mật khẩu"
                    )

                    Spacer(
                        modifier =
                            Modifier.height(7.dp)
                    )

                    OutlinedTextField(
                        value =
                            confirmPassword,
                        onValueChange = {

                            confirmPassword = it
                            localMessage = null

                            authViewModel.clearError()
                            authViewModel.clearRegisterMessage()
                        },
                        modifier =
                            Modifier.fillMaxWidth(),
                        placeholder = {

                            Text(
                                "Nhập lại mật khẩu"
                            )
                        },
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Lock,
                                contentDescription =
                                    null,
                                tint =
                                    Color(0xFF625D8C)
                            )
                        },
                        trailingIcon = {

                            IconButton(
                                onClick = {

                                    confirmPasswordVisible =
                                        !confirmPasswordVisible
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (
                                            confirmPasswordVisible
                                        ) {
                                            Icons.Default.Visibility
                                        } else {
                                            Icons.Default.VisibilityOff
                                        },
                                    contentDescription =
                                        if (
                                            confirmPasswordVisible
                                        ) {
                                            "Ẩn mật khẩu"
                                        } else {
                                            "Hiện mật khẩu"
                                        },
                                    tint =
                                        Color(0xFF625D8C)
                                )
                            }
                        },
                        visualTransformation =
                            if (
                                confirmPasswordVisible
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
                        singleLine = true,
                        shape =
                            RoundedCornerShape(
                                18.dp
                            ),
                        colors =
                            registerFieldColors()
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | MESSAGES
                    |--------------------------------------------------------------------------
                    */

                    if (
                        !localMessage
                            .isNullOrBlank()
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        RegisterMessageCard(
                            message =
                                localMessage ?: "",
                            isError = true
                        )
                    }

                    if (
                        !authState.error
                            .isNullOrBlank()
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        RegisterMessageCard(
                            message =
                                authState.error ?: "",
                            isError = true
                        )
                    }

                    if (
                        !registerMessage
                            .isNullOrBlank()
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        RegisterMessageCard(
                            message =
                                registerMessage ?: "",
                            isError = false
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | REGISTER GRADIENT BUTTON
                    |--------------------------------------------------------------------------
                    */

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .clip(
                                RoundedCornerShape(
                                    19.dp
                                )
                            )
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF9D43FF),
                                        BrandPurple,
                                        Color(0xFF5428FF)
                                    )
                                )
                            )
                    ) {

                        Button(
                            onClick = {

                                localMessage = null

                                authViewModel.clearError()
                                authViewModel.clearRegisterMessage()


                                if (
                                    name.trim()
                                        .isBlank()
                                ) {

                                    localMessage =
                                        "Vui lòng nhập họ và tên."

                                    return@Button
                                }


                                if (
                                    email.trim()
                                        .isBlank()
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
                                    password.length < 6
                                ) {

                                    localMessage =
                                        "Mật khẩu phải có ít nhất 6 ký tự."

                                    return@Button
                                }


                                if (
                                    confirmPassword
                                        .isBlank()
                                ) {

                                    localMessage =
                                        "Vui lòng nhập lại mật khẩu."

                                    return@Button
                                }


                                if (
                                    password !=
                                    confirmPassword
                                ) {

                                    localMessage =
                                        "Mật khẩu nhập lại không khớp."

                                    return@Button
                                }


                                if (!agreeTerms) {

                                    localMessage =
                                        "Bạn cần đồng ý với điều khoản sử dụng."

                                    return@Button
                                }


                                authViewModel.register(
                                    name =
                                        name.trim(),
                                    email =
                                        email.trim(),
                                    password =
                                        password,
                                    onSuccess = {
                                        onRegisterSuccess()
                                    }
                                )
                            },

                            enabled =
                                !authState.isLoading,

                            modifier =
                                Modifier.fillMaxSize(),

                            shape =
                                RoundedCornerShape(
                                    19.dp
                                ),

                            colors =
                                ButtonDefaults
                                    .buttonColors(
                                        containerColor =
                                            Color.Transparent,

                                        disabledContainerColor =
                                            BrandPurple.copy(
                                                alpha = 0.45f
                                            )
                                    ),

                            elevation =
                                ButtonDefaults
                                    .buttonElevation(
                                        defaultElevation =
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
                                        Modifier.fillMaxWidth()
                                ) {

                                    Text(
                                        text =
                                            "Tạo tài khoản",
                                        modifier =
                                            Modifier.align(
                                                Alignment.Center
                                            ),
                                        color =
                                            Color.White,
                                        fontSize =
                                            17.sp,
                                        fontWeight =
                                            FontWeight.Bold
                                    )

                                    Icon(
                                        imageVector =
                                            Icons.Default.ArrowForward,
                                        contentDescription =
                                            null,
                                        tint =
                                            Color.White,
                                        modifier =
                                            Modifier
                                                .align(
                                                    Alignment.CenterEnd
                                                )
                                                .size(
                                                    24.dp
                                                )
                                    )
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
                            Modifier.height(19.dp)
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
                                Color(0xFFDAD7EB)
                        )

                        Text(
                            text =
                                "  hoặc  ",
                            color =
                                Color(0xFF817D99),
                            fontSize =
                                13.sp
                        )

                        HorizontalDivider(
                            modifier =
                                Modifier.weight(1f),
                            color =
                                Color(0xFFDAD7EB)
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(17.dp)
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | GOOGLE
                    |--------------------------------------------------------------------------
                    */

                    OutlinedButton(
                        onClick = {

                            localMessage = null

                            authViewModel.clearError()
                            authViewModel.clearRegisterMessage()

                            authViewModel.signInWithGoogle(
                                context =
                                    context,

                                serverClientId =
                                    context.getString(
                                        R.string
                                            .default_web_client_id
                                    ),

                                onSuccess = {
                                    onRegisterSuccess()
                                }
                            )
                        },

                        enabled =
                            !authState.isLoading,

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp),

                        shape =
                            RoundedCornerShape(
                                18.dp
                            ),

                        border =
                            BorderStroke(
                                width = 1.2.dp,
                                color =
                                    BrandPurple
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
                                    25.dp
                                ),
                            contentScale =
                                ContentScale.Fit
                        )

                        Spacer(
                            modifier =
                                Modifier.size(12.dp)
                        )

                        Text(
                            text =
                                "Tiếp tục với Google",
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
                    | TERMS
                    |--------------------------------------------------------------------------
                    */

                    Spacer(
                        modifier =
                            Modifier.height(15.dp)
                    )

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        verticalAlignment =
                            Alignment.Top
                    ) {

                        Checkbox(
                            checked =
                                agreeTerms,

                            onCheckedChange = {
                                agreeTerms = it
                            },

                            colors =
                                CheckboxDefaults.colors(
                                    checkedColor =
                                        BrandPurple,

                                    uncheckedColor =
                                        Color(0xFF85819A)
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.size(4.dp)
                        )

                        Text(
                            text =
                                "Tôi đồng ý với Điều khoản sử dụng và Chính sách bảo mật của Tích Phú",
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(
                                        top = 11.dp
                                    ),
                            fontSize =
                                12.5.sp,
                            lineHeight =
                                17.sp,
                            color =
                                Color(0xFF625E7D)
                        )
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | LOGIN LINK
        |--------------------------------------------------------------------------
        */

        item {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(
                        y = (-5).dp
                    ),
                horizontalArrangement =
                    Arrangement.Center,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        "Đã có tài khoản? ",
                    fontSize =
                        14.sp,
                    color =
                        Color(0xFF363052)
                )

                Text(
                    text =
                        "Đăng nhập",
                    color =
                        BrandPurple,
                    fontSize =
                        14.sp,
                    fontWeight =
                        FontWeight.ExtraBold,
                    modifier =
                        Modifier.clickable {

                            onBack()
                        }
                )

                Spacer(
                    modifier =
                        Modifier.size(4.dp)
                )

                Text(
                    text = "›",
                    color =
                        BrandPurple,
                    fontSize =
                        22.sp,
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | BOTTOM SLOGAN
        |--------------------------------------------------------------------------
        */

        item {

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text =
                    "Tài chính tốt đẹp hơn mỗi ngày",
                modifier =
                    Modifier.fillMaxWidth(),
                color =
                    Color(0xFFC38B18),
                fontSize =
                    20.sp,
                lineHeight =
                    25.sp,
                fontStyle =
                    FontStyle.Italic,
                fontWeight =
                    FontWeight.Medium,
                textAlign =
                    TextAlign.Center
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| HERO
|--------------------------------------------------------------------------
*/

@Composable
private fun RegisterHero() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .background(
                brush =
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF17065E),
                            BrandPurpleDark,
                            Color(0xFF4D25D5),
                            BrandPurple
                        )
                    )
            )
    ) {

        /*
        |--------------------------------------------------------------------------
        | DECORATION
        |--------------------------------------------------------------------------
        */

        Box(
            modifier = Modifier
                .size(210.dp)
                .align(
                    Alignment.TopEnd
                )
                .offset(
                    x = 60.dp,
                    y = (-45).dp
                )
                .clip(CircleShape)
                .background(
                    Color.White.copy(
                        alpha = 0.045f
                    )
                )
        )
        /*
        |--------------------------------------------------------------------------
        | LOGO
        |--------------------------------------------------------------------------
        */

        Column(
            modifier = Modifier
                .align(
                    Alignment.TopStart
                )
                .padding(
                    start = 28.dp,
                    top = 22.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Image(
                painter =
                    painterResource(
                        id =
                            R.drawable
                                .tich_phu_app_icon
                    ),
                contentDescription =
                    "Tích Phú",
                modifier =
                    Modifier
                        .size(
                            105.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                28.dp
                            )
                        ),
                contentScale =
                    ContentScale.Crop
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    "Tích Phú",
                color =
                    Color.White,
                fontSize =
                    31.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text =
                    "TÍCH LŨY HÔM NAY\nTHỊNH VƯỢNG MAI SAU",
                color =
                    Color.White.copy(
                        alpha = 0.86f
                    ),
                fontSize =
                    8.5.sp,
                lineHeight =
                    12.sp,
                letterSpacing =
                    1.3.sp,
                textAlign =
                    TextAlign.Center
            )
        }


        /*
        |--------------------------------------------------------------------------
        | HERO IMAGE
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
                null,
            modifier =
                Modifier
                    .size(
                        230.dp
                    )
                    .align(
                        Alignment.CenterEnd
                    )
                    .offset(
                        x = 42.dp,
                        y = 40.dp
                    ),
            contentScale =
                ContentScale.Fit
        )


        /*
        |--------------------------------------------------------------------------
        | SMALL CARDS
        |--------------------------------------------------------------------------
        */

        Surface(
            modifier =
                Modifier
                    .align(
                        Alignment.TopCenter
                    )
                    .offset(
                        x = 52.dp,
                        y = 103.dp
                    ),
            shape =
                RoundedCornerShape(
                    14.dp
                ),
            color =
                Color.White.copy(
                    alpha = 0.12f
                )
        ) {

            Text(
                text =
                    "Kỷ luật\nhôm nay",
                modifier =
                    Modifier.padding(
                        horizontal = 11.dp,
                        vertical = 8.dp
                    ),
                color =
                    Color.White,
                fontSize =
                    11.sp,
                lineHeight =
                    14.sp
            )
        }


        /*
        |--------------------------------------------------------------------------
        | TITLE BOTTOM
        |--------------------------------------------------------------------------
        */

        Column(
            modifier = Modifier
                .align(
                    Alignment.BottomStart
                )
                .padding(
                    start = 22.dp,
                    end = 22.dp,
                    bottom = 32.dp
                )
        ) {

            Text(
                text =
                    "Tạo tài khoản mới",
                color =
                    Color.White,
                fontSize =
                    29.sp,
                lineHeight =
                    34.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    "Bắt đầu hành trình tài chính tốt đẹp hơn\ncùng Tích Phú 💛",
                color =
                    Color.White.copy(
                        alpha = 0.83f
                    ),
                fontSize =
                    14.sp,
                lineHeight =
                    20.sp
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| LABEL
|--------------------------------------------------------------------------
*/

@Composable
private fun RegisterFieldLabel(
    text: String
) {

    Text(
        text =
            text,
        color =
            Color(0xFF201A4F),
        fontSize =
            14.sp,
        fontWeight =
            FontWeight.Bold
    )
}


/*
|--------------------------------------------------------------------------
| FIELD COLORS
|--------------------------------------------------------------------------
*/

@Composable
private fun registerFieldColors() =
    OutlinedTextFieldDefaults.colors(

        /*
        |--------------------------------------------------------------------------
        | MÀU CHỮ KHI NHẬP
        |--------------------------------------------------------------------------
        */

        focusedTextColor =
            Color(0xFF201A4F),

        unfocusedTextColor =
            Color(0xFF201A4F),


        /*
        |--------------------------------------------------------------------------
        | BORDER
        |--------------------------------------------------------------------------
        */

        focusedBorderColor =
            BrandPurple,

        unfocusedBorderColor =
            Color(0xFFE0DDF0),


        /*
        |--------------------------------------------------------------------------
        | BACKGROUND
        |--------------------------------------------------------------------------
        */

        focusedContainerColor =
            Color(0xFFFBFAFF),

        unfocusedContainerColor =
            Color(0xFFFBFAFF),


        /*
        |--------------------------------------------------------------------------
        | ICON
        |--------------------------------------------------------------------------
        */

        focusedLeadingIconColor =
            BrandPurple,

        unfocusedLeadingIconColor =
            Color(0xFF625D8C),


        /*
        |--------------------------------------------------------------------------
        | PLACEHOLDER
        |--------------------------------------------------------------------------
        */

        focusedPlaceholderColor =
            Color(0xFF9A96AE),

        unfocusedPlaceholderColor =
            Color(0xFF9A96AE),


        /*
        |--------------------------------------------------------------------------
        | CURSOR
        |--------------------------------------------------------------------------
        */

        cursorColor =
            BrandPurple
    )


/*
|--------------------------------------------------------------------------
| MESSAGE
|--------------------------------------------------------------------------
*/

@Composable
private fun RegisterMessageCard(
    message: String,
    isError: Boolean
) {

    val containerColor =
        if (isError) {
            ExpenseRed.copy(
                alpha = 0.10f
            )
        } else {
            BrandPurple.copy(
                alpha = 0.09f
            )
        }

    val textColor =
        if (isError) {
            ExpenseRed
        } else {
            BrandPurple
        }

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                14.dp
            ),
        color =
            containerColor
    ) {

        Text(
            text =
                message,
            modifier =
                Modifier.padding(
                    12.dp
                ),
            color =
                textColor,
            style =
                MaterialTheme
                    .typography
                    .bodySmall,
            fontWeight =
                FontWeight.SemiBold
        )
    }
}