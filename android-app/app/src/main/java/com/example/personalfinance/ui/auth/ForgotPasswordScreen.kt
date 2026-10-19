package com.example.personalfinance.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalfinance.R
import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.viewmodel.AuthViewModel


@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    authViewModel: AuthViewModel
) {

    var email by remember {
        mutableStateOf("")
    }

    var localMessage by remember {
        mutableStateOf<String?>(null)
    }

    val message by
    authViewModel.resetMessage.collectAsState()


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF9F7FF),
                        Color(0xFFF1ECFF)
                    )
                )
            )
            .imePadding(),

        contentPadding =
            PaddingValues(
                bottom = 0.dp
            )
    ) {

        /*
        |--------------------------------------------------------------------------
        | HERO
        |--------------------------------------------------------------------------
        */

        item {

            ForgotPasswordHero(
                onBack = onBack
            )
        }


        /*
        |--------------------------------------------------------------------------
        | EMAIL CARD
        |--------------------------------------------------------------------------
        */

        item {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 18.dp
                    )
                    .offset(
                        y = (-28).dp
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
                            horizontal = 20.dp,
                            vertical = 22.dp
                        )
                ) {

                    Text(
                        text =
                            "Nhập email của bạn",

                        color =
                            Color(0xFF17114D),

                        fontSize =
                            22.sp,

                        fontWeight =
                            FontWeight.ExtraBold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )


                    Text(
                        text =
                            "Chúng tôi sẽ gửi liên kết đặt lại mật khẩu đến email bạn đã đăng ký.",

                        color =
                            Color(0xFF706B89),

                        fontSize =
                            13.sp,

                        lineHeight =
                            18.sp
                    )


                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | EMAIL FIELD
                    |--------------------------------------------------------------------------
                    */

                    OutlinedTextField(
                        value =
                            email,

                        onValueChange = {

                            email = it

                            localMessage =
                                null

                            authViewModel
                                .clearResetMessage()
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(62.dp),

                        placeholder = {

                            Text(
                                text =
                                    "example@email.com",

                                color =
                                    Color(0xFFA8A4BC)
                            )
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Outlined.Email,

                                contentDescription =
                                    null,

                                tint =
                                    Color(0xFF241A69)
                            )
                        },

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Email,

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
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    BrandPurple,

                                unfocusedBorderColor =
                                    Color(0xFFDDD8F0),

                                focusedContainerColor =
                                    Color(0xFFFEFDFF),

                                unfocusedContainerColor =
                                    Color(0xFFFEFDFF),

                                cursorColor =
                                    BrandPurple
                            )
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | ERROR LOCAL
                    |--------------------------------------------------------------------------
                    */

                    if (
                        !localMessage
                            .isNullOrBlank()
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(
                                    12.dp
                                )
                        )

                        ForgotPasswordMessageCard(
                            message =
                                localMessage ?: "",

                            isError =
                                true
                        )
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | FIREBASE MESSAGE
                    |--------------------------------------------------------------------------
                    */

                    if (
                        !message
                            .isNullOrBlank()
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(
                                    12.dp
                                )
                        )

                        ForgotPasswordMessageCard(
                            message =
                                message ?: "",

                            isError =
                                false
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | SEND BUTTON
                    |--------------------------------------------------------------------------
                    */

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .clip(
                                RoundedCornerShape(
                                    18.dp
                                )
                            )
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF9340FF),
                                        BrandPurple,
                                        Color(0xFF5924F6)
                                    )
                                )
                            )
                    ) {

                        Button(
                            onClick = {

                                val trimmedEmail =
                                    email.trim()


                                if (
                                    trimmedEmail.isBlank()
                                ) {

                                    localMessage =
                                        "Vui lòng nhập email."

                                    return@Button
                                }


                                if (
                                    !android.util.Patterns
                                        .EMAIL_ADDRESS
                                        .matcher(
                                            trimmedEmail
                                        )
                                        .matches()
                                ) {

                                    localMessage =
                                        "Email không hợp lệ."

                                    return@Button
                                }


                                localMessage =
                                    null


                                authViewModel
                                    .sendPasswordReset(
                                        trimmedEmail
                                    )
                            },

                            modifier =
                                Modifier.fillMaxSize(),

                            colors =
                                ButtonDefaults
                                    .buttonColors(
                                        containerColor =
                                            Color.Transparent
                                    ),

                            elevation =
                                ButtonDefaults
                                    .buttonElevation(
                                        defaultElevation =
                                            0.dp
                                    ),

                            shape =
                                RoundedCornerShape(
                                    18.dp
                                )
                        ) {

                            Box(
                                modifier =
                                    Modifier.fillMaxWidth(),

                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text =
                                        "Gửi liên kết đặt lại mật khẩu",

                                    color =
                                        Color.White,

                                    fontSize =
                                        15.sp,

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


                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | BACK LOGIN
                    |--------------------------------------------------------------------------
                    */

                    TextButton(
                        onClick =
                            onBack,

                        modifier =
                            Modifier.align(
                                Alignment.CenterHorizontally
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,

                            contentDescription =
                                null,

                            tint =
                                BrandPurple,

                            modifier =
                                Modifier.size(
                                    18.dp
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
                                "Quay lại đăng nhập",

                            color =
                                BrandPurple,

                            fontSize =
                                14.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | HOW IT WORKS
        |--------------------------------------------------------------------------
        */

        item {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 18.dp
                    )
                    .offset(
                        y = (-8).dp
                    )
            ) {

                Text(
                    text =
                        "Cách hoạt động",

                    color =
                        Color(0xFF17114D),

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.ExtraBold
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                ForgotStepCard(
                    number = "01",

                    icon =
                        Icons.Outlined.Email,

                    title =
                        "Bước 1",

                    description =
                        "Nhập đúng email bạn đã dùng để đăng ký tài khoản."
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                ForgotStepCard(
                    number = "02",

                    icon =
                        Icons.Outlined.MarkEmailRead,

                    title =
                        "Bước 2",

                    description =
                        "Kiểm tra hộp thư đến hoặc thư rác để mở email khôi phục."
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                ForgotStepCard(
                    number = "03",

                    icon =
                        Icons.Outlined.Shield,

                    title =
                        "Bước 3",

                    description =
                        "Nhấn vào liên kết trong email và tạo mật khẩu mới an toàn."
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | FOOTER
        |--------------------------------------------------------------------------
        */

        item {

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            ForgotFooter()
        }
    }
}


/*
|--------------------------------------------------------------------------
| HERO
|--------------------------------------------------------------------------
*/

@Composable
private fun ForgotPasswordHero(
    onBack: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                390.dp
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFFCFBFF),
                        Color(0xFFF2EDFF)
                    )
                )
            )
    ) {

        /*
        |--------------------------------------------------------------------------
        | PURPLE BACKGROUND
        |--------------------------------------------------------------------------
        */

        Box(
            modifier = Modifier
                .size(
                    220.dp
                )
                .align(
                    Alignment.CenterEnd
                )
                .offset(
                    x = 70.dp,
                    y = 25.dp
                )
                .clip(CircleShape)
                .background(
                    BrandPurple.copy(
                        alpha = 0.07f
                    )
                )
        )


        /*
        |--------------------------------------------------------------------------
        | BACK
        |--------------------------------------------------------------------------
        */

        Surface(
            modifier = Modifier
                .padding(
                    start = 20.dp,
                    top = 20.dp
                )
                .size(
                    46.dp
                ),

            shape =
                CircleShape,

            color =
                Color.White,

            shadowElevation =
                5.dp
        ) {

            IconButton(
                onClick =
                    onBack
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ArrowBack,

                    contentDescription =
                        "Quay lại",

                    tint =
                        Color(0xFF211A63)
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | LOGO
        |--------------------------------------------------------------------------
        */

        Image(
            painter =
                painterResource(
                    id =
                        R.drawable
                            .tich_phu_logo_horizontal
                ),

            contentDescription =
                "Tích Phú",

            modifier = Modifier
                .fillMaxWidth(
                    0.48f
                )
                .height(
                    92.dp
                )
                .align(
                    Alignment.TopCenter
                )
                .padding(
                    top = 7.dp
                ),

            contentScale =
                ContentScale.Fit
        )


        /*
        |--------------------------------------------------------------------------
        | TOP RIGHT SLOGAN
        |--------------------------------------------------------------------------
        */

        Column(
            modifier = Modifier
                .align(
                    Alignment.TopEnd
                )
                .padding(
                    top = 35.dp,
                    end = 18.dp
                )
        ) {

            Text(
                text =
                    "AN TOÀN HÔM NAY\n" +
                            "VỮNG BƯỚC\n" +
                            "TƯƠNG LAI",

                color =
                    Color(0xFF686181),

                fontSize =
                    7.5.sp,

                lineHeight =
                    12.sp,

                letterSpacing =
                    1.6.sp,

                fontWeight =
                    FontWeight.Medium
            )


            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )


            Box(
                modifier =
                    Modifier
                        .size(
                            width = 27.dp,
                            height = 2.dp
                        )
                        .background(
                            Color(0xFFD5A52A)
                        )
            )
        }


        /*
        |--------------------------------------------------------------------------
        | TITLE
        |--------------------------------------------------------------------------
        */

        Column(
            modifier = Modifier
                .align(
                    Alignment.CenterStart
                )
                .padding(
                    start = 20.dp
                )
                .offset(
                    y = 10.dp
                )
                .fillMaxWidth(
                    0.58f
                )
        ) {

            Text(
                text =
                    "Khôi phục\nmật khẩu",

                color =
                    Color(0xFF17114D),

                fontSize =
                    37.sp,

                lineHeight =
                    40.sp,

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
                    "Lấy lại quyền truy cập tài khoản\n" +
                            "Tích Phú chỉ trong vài bước đơn giản.",

                color =
                    Color(0xFF706B8B),

                fontSize =
                    13.sp,

                lineHeight =
                    18.sp
            )


            Spacer(
                modifier =
                    Modifier.height(
                        19.dp
                    )
            )


            Text(
                text =
                    "Chúng tôi luôn đồng hành\ncùng bạn!",

                color =
                    Color(0xFFD69817),

                fontSize =
                    16.sp,

                lineHeight =
                    21.sp,

                fontStyle =
                    FontStyle.Italic,

                fontWeight =
                    FontWeight.Medium
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
                            .forgot_password_hero
                ),

            contentDescription =
                "Khôi phục mật khẩu",

            modifier =
                Modifier
                    .size(
                        235.dp
                    )
                    .align(
                        Alignment.CenterEnd
                    )
                    .offset(
                        x = 32.dp,
                        y = 65.dp
                    ),

            contentScale =
                ContentScale.Fit
        )
    }
}


/*
|--------------------------------------------------------------------------
| STEP
|--------------------------------------------------------------------------
*/

@Composable
private fun ForgotStepCard(
    number: String,
    icon: ImageVector,
    title: String,
    description: String
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
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    2.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    14.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            /*
            |--------------------------------------------------------------------------
            | ICON
            |--------------------------------------------------------------------------
            */

            Surface(
                modifier =
                    Modifier.size(
                        48.dp
                    ),

                shape =
                    CircleShape,

                color =
                    BrandPurple.copy(
                        alpha = 0.09f
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
                            BrandPurple,

                        modifier =
                            Modifier.size(
                                25.dp
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


            /*
            |--------------------------------------------------------------------------
            | TEXT
            |--------------------------------------------------------------------------
            */

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        title,

                    color =
                        Color(0xFF17114D),

                    fontSize =
                        14.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            2.dp
                        )
                )


                Text(
                    text =
                        description,

                    color =
                        Color(0xFF716D88),

                    fontSize =
                        11.5.sp,

                    lineHeight =
                        16.sp
                )
            }


            Spacer(
                modifier =
                    Modifier.width(
                        8.dp
                    )
            )


            /*
            |--------------------------------------------------------------------------
            | NUMBER
            |--------------------------------------------------------------------------
            */

            Surface(
                modifier =
                    Modifier.size(
                        38.dp
                    ),

                shape =
                    CircleShape,

                color =
                    BrandPurple.copy(
                        alpha = 0.07f
                    )
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            number,

                        color =
                            BrandPurple,

                        fontSize =
                            11.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| FOOTER
|--------------------------------------------------------------------------
*/

@Composable
private fun ForgotFooter() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                118.dp
            )
            .background(
                brush =
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF7F3FF),
                            Color(0xFFEDE5FF)
                        )
                    ),

                shape =
                    RoundedCornerShape(
                        topStart = 52.dp,
                        topEnd = 52.dp
                    )
            ),

        contentAlignment =
            Alignment.Center
    ) {

        /*
        |--------------------------------------------------------------------------
        | KHÔNG CÓ 3 CHẤM
        |--------------------------------------------------------------------------
        */

        Text(
            text =
                "Tài chính tốt đẹp hơn mỗi ngày",

            color =
                Color(0xFFD49A17),

            fontSize =
                18.sp,

            fontStyle =
                FontStyle.Italic,

            fontWeight =
                FontWeight.Medium,

            textAlign =
                TextAlign.Center
        )
    }
}


/*
|--------------------------------------------------------------------------
| MESSAGE
|--------------------------------------------------------------------------
*/

@Composable
private fun ForgotPasswordMessageCard(
    message: String,
    isError: Boolean
) {

    val background =
        if (
            isError
        ) {

            ExpenseRed.copy(
                alpha = 0.10f
            )

        } else {

            BrandPurple.copy(
                alpha = 0.09f
            )
        }


    val textColor =
        if (
            isError
        ) {

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
            background
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

            fontSize =
                12.sp,

            fontWeight =
                FontWeight.SemiBold
        )
    }
}