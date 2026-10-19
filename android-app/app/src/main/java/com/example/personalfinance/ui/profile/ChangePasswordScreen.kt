package com.example.personalfinance.ui.profile

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.personalfinance.viewmodel.AuthViewModel

import kotlinx.coroutines.delay


@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
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

    val resetMessage by
    authViewModel
        .resetMessage
        .collectAsState()

    val email =
        authState.user
            ?.email
            ?.trim()
            .orEmpty()

    var cooldownSeconds by remember {
        mutableStateOf(
            0
        )
    }


    /*
    |--------------------------------------------------------------------------
    | CLEAR OLD MESSAGE
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(Unit) {

        authViewModel
            .clearResetMessage()
    }


    /*
    |--------------------------------------------------------------------------
    | COOLDOWN
    |--------------------------------------------------------------------------
    |
    | Tránh người dùng bấm gửi liên tục và bị Firebase giới hạn.
    |
    */

    LaunchedEffect(
        cooldownSeconds
    ) {

        if (
            cooldownSeconds > 0
        ) {

            delay(
                1000
            )

            cooldownSeconds =
                cooldownSeconds - 1
        }
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
                bottom = 36.dp
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

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(
                            44.dp
                        ),
                    shape =
                        CircleShape,
                    color =
                        MaterialTheme
                            .colorScheme
                            .surfaceVariant
                            .copy(
                                alpha = 0.65f
                            )
                ) {

                    IconButton(
                        onClick =
                            onBack
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored
                                    .Filled
                                    .ArrowBack,
                            contentDescription =
                                "Quay lại"
                        )
                    }
                }


                Column(
                    modifier =
                        Modifier
                            .weight(
                                1f
                            )
                            .padding(
                                start = 14.dp
                            )
                ) {

                    Text(
                        text =
                            "Đổi mật khẩu",
                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall,
                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                3.dp
                            )
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | HERO
        |--------------------------------------------------------------------------
        */

        item {

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(
                        24.dp
                    ),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .primary
                                .copy(
                                    alpha = 0.09f
                                )
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
                                74.dp
                            ),
                        shape =
                            RoundedCornerShape(
                                24.dp
                            ),
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                                .copy(
                                    alpha = 0.13f
                                )
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Outlined.Lock,
                                contentDescription =
                                    null,
                                tint =
                                    MaterialTheme
                                        .colorScheme
                                        .primary,
                                modifier =
                                    Modifier.size(
                                        36.dp
                                    )
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                15.dp
                            )
                    )


                    Text(
                        text =
                            "Đặt lại mật khẩu qua email",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.ExtraBold,
                        textAlign =
                            TextAlign.Center
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                7.dp
                            )
                    )


                    Text(
                        text =
                            "Tích Phú sẽ gửi liên kết đặt lại mật khẩu đến email của tài khoản đang đăng nhập.",
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
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | EMAIL
        |--------------------------------------------------------------------------
        */

        item {

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
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                17.dp
                            )
                ) {

                    Text(
                        text =
                            "Email nhận liên kết",
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
                                13.dp
                            )
                    )


                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(
                                16.dp
                            ),
                        color =
                            MaterialTheme
                                .colorScheme
                                .surfaceVariant
                                .copy(
                                    alpha = 0.55f
                                )
                    ) {

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        15.dp
                                    ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

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
                                            Icons.Outlined.Email,
                                        contentDescription =
                                            null,
                                        tint =
                                            MaterialTheme
                                                .colorScheme
                                                .primary
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
                                        "Email tài khoản",
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
                                        if (
                                            email.isNotBlank()
                                        ) {
                                            email
                                        } else {
                                            "Không tìm thấy email"
                                        },
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium,
                                    fontWeight =
                                        FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | MESSAGE
        |--------------------------------------------------------------------------
        */

        if (
            !resetMessage.isNullOrBlank()
        ) {

            item {

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            17.dp
                        ),
                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                            .copy(
                                alpha = 0.09f
                            )
                ) {

                    Text(
                        text =
                            resetMessage
                                ?: "",
                        modifier =
                            Modifier.padding(
                                15.dp
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        fontWeight =
                            FontWeight.SemiBold,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | SEND BUTTON
        |--------------------------------------------------------------------------
        */

        item {

            Button(
                onClick = {

                    if (
                        email.isNotBlank() &&
                        cooldownSeconds == 0
                    ) {

                        authViewModel
                            .clearResetMessage()

                        authViewModel
                            .sendPasswordReset(
                                email
                            )

                        /*
                         * Không cho bấm liên tục.
                         */
                        cooldownSeconds =
                            60
                    }
                },
                enabled =
                    email.isNotBlank() &&
                            cooldownSeconds == 0,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            54.dp
                        ),
                shape =
                    RoundedCornerShape(
                        18.dp
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Outlined.Email,
                    contentDescription =
                        null,
                    modifier =
                        Modifier.size(
                            20.dp
                        )
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            7.dp
                        )
                )


                Text(
                    text =
                        if (
                            cooldownSeconds > 0
                        ) {
                            "Gửi lại sau ${cooldownSeconds}s"
                        } else {
                            "Gửi email đặt lại mật khẩu"
                        },
                    fontSize =
                        13.sp,
                    fontWeight =
                        FontWeight.Bold,
                    maxLines =
                        1,
                    softWrap =
                        false
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | INSTRUCTION
        |--------------------------------------------------------------------------
        */

        item {

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(
                        20.dp
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
                        Modifier.padding(
                            17.dp
                        )
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Outlined.Security,
                            contentDescription =
                                null,
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    9.dp
                                )
                        )


                        Text(
                            text =
                                "Sau khi gửi email",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.ExtraBold
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )


                    PasswordInstruction(
                        number =
                            "1",
                        text =
                            "Mở hộp thư của email đang đăng nhập."
                    )


                    PasswordInstruction(
                        number =
                            "2",
                        text =
                            "Mở email đặt lại mật khẩu từ Tích Phú."
                    )


                    PasswordInstruction(
                        number =
                            "3",
                        text =
                            "Nhấn liên kết trong email và nhập mật khẩu mới."
                    )


                    PasswordInstruction(
                        number =
                            "4",
                        text =
                            "Sau khi đổi thành công, sử dụng mật khẩu mới cho lần đăng nhập tiếp theo."
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | NOTE
        |--------------------------------------------------------------------------
        */

        item {

            Text(
                text =
                    "Nếu chưa nhận được email, hãy kiểm tra thư rác hoặc chờ một lúc trước khi gửi lại.",
                modifier =
                    Modifier.fillMaxWidth(),
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
                textAlign =
                    TextAlign.Center
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| INSTRUCTION
|--------------------------------------------------------------------------
*/

@Composable
private fun PasswordInstruction(
    number: String,
    text: String
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 6.dp
                ),
        verticalAlignment =
            Alignment.Top
    ) {

        Surface(
            modifier =
                Modifier.size(
                    27.dp
                ),
            shape =
                CircleShape,
            color =
                MaterialTheme
                    .colorScheme
                    .primary
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        number,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onPrimary,
                    style =
                        MaterialTheme
                            .typography
                            .labelSmall,
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }


        Spacer(
            modifier =
                Modifier.width(
                    10.dp
                )
        )


        Text(
            text =
                text,
            modifier =
                Modifier.weight(
                    1f
                ),
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )
    }
}