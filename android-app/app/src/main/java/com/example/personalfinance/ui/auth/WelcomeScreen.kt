package com.example.personalfinance.ui.auth

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalfinance.R
import com.example.personalfinance.ui.theme.BrandPurple


@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {

    val backgroundBrush =
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFCFBFF),
                Color(0xFFF7F4FF),
                Color.White,
                Color(0xFFF5F1FF)
            )
        )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 14.dp,
            bottom = 28.dp
        ),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        /*
        |--------------------------------------------------------------------------
        | LOGO
        |--------------------------------------------------------------------------
        */

        item {

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Image(
                painter =
                    painterResource(
                        id =
                            R.drawable
                                .tich_phu_logo_horizontal
                    ),
                contentDescription =
                    "Logo Tích Phú",
                modifier = Modifier
                    .fillMaxWidth(0.99f)
                    .height(145.dp),
                contentScale =
                    ContentScale.Fit
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text =
                    "Quản lý tài chính thông minh",
                fontSize =
                    14.sp,
                fontWeight =
                    FontWeight.Medium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )
        }


        /*
        |--------------------------------------------------------------------------
        | HERO
        |--------------------------------------------------------------------------
        */

        item {

            WelcomeHeroSection()

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )
        }


        /*
        |--------------------------------------------------------------------------
        | FEATURES
        |--------------------------------------------------------------------------
        */

        item {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {

                WelcomeFeatureCard(
                    modifier =
                        Modifier.weight(1f),
                    icon =
                        Icons.Outlined.BarChart,
                    title =
                        "Theo dõi\nchi tiêu",
                    description =
                        "Rõ ràng"
                )

                WelcomeFeatureCard(
                    modifier =
                        Modifier.weight(1f),
                    icon =
                        Icons.Outlined.Savings,
                    title =
                        "Đạt mục tiêu\ntài chính",
                    description =
                        "Chủ động"
                )

                WelcomeFeatureCard(
                    modifier =
                        Modifier.weight(1f),
                    icon =
                        Icons.Outlined
                            .AccountBalanceWallet,
                    title =
                        "Xây dựng\ntương lai",
                    description =
                        "Bền vững"
                )
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )
        }
        /*
        |--------------------------------------------------------------------------
        | LOGIN
        |--------------------------------------------------------------------------
        */

        item {

            Button(
                onClick =
                    onLoginClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                shape =
                    RoundedCornerShape(
                        19.dp
                    ),
                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                BrandPurple
                        ),
                elevation =
                    ButtonDefaults
                        .buttonElevation(
                            defaultElevation =
                                7.dp,
                            pressedElevation =
                                2.dp
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
                            "Đăng nhập",
                        fontSize =
                            17.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            Color.White
                    )

                    Icon(
                        imageVector =
                            Icons.Default.ArrowForward,
                        contentDescription =
                            null,
                        modifier =
                            Modifier
                                .align(
                                    Alignment.CenterEnd
                                )
                                .padding(
                                    end = 4.dp
                                )
                                .size(23.dp),
                        tint =
                            Color.White
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(11.dp)
            )
        }


        /*
        |--------------------------------------------------------------------------
        | REGISTER
        |--------------------------------------------------------------------------
        */

        item {

            OutlinedButton(
                onClick =
                    onRegisterClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape =
                    RoundedCornerShape(
                        19.dp
                    ),
                border =
                    BorderStroke(
                        width = 1.5.dp,
                        color =
                            BrandPurple
                    ),
                colors =
                    ButtonDefaults
                        .outlinedButtonColors(
                            contentColor =
                                BrandPurple
                        )
            ) {

                Text(
                    text =
                        "Đăng ký",
                    fontSize =
                        17.sp,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )
        }


        /*
        |--------------------------------------------------------------------------
        | FOOTER
        |--------------------------------------------------------------------------
        */

        item {

            Text(
                text =
                    "Tích lũy hôm nay • Thịnh vượng ngày mai",
                fontSize =
                    11.sp,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
                        .copy(alpha = 0.75f),
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
private fun WelcomeHeroSection() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp),
        contentAlignment =
            Alignment.Center
    ) {

        /*
        |--------------------------------------------------------------------------
        | BACKGROUND GLOW
        |--------------------------------------------------------------------------
        */

        Box(
            modifier =
                Modifier
                    .size(320.dp)
                    .clip(
                        CircleShape
                    )
                    .background(
                        Brush.radialGradient(
                            colors =
                                listOf(
                                    BrandPurple.copy(
                                        alpha = 0.13f
                                    ),
                                    BrandPurple.copy(
                                        alpha = 0.04f
                                    ),
                                    Color.Transparent
                                )
                        )
                    )
        )


        /*
        |--------------------------------------------------------------------------
        | HERO PNG
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
                "Minh họa tài chính Tích Phú",
            modifier = Modifier
                .fillMaxWidth()
                .height(325.dp)
                .align(
                    Alignment.Center
                )
                .offset(
                    y = 12.dp
                ),
            contentScale =
                ContentScale.Fit
        )


        /*
        |--------------------------------------------------------------------------
        | LEFT TOP CARD
        |--------------------------------------------------------------------------
        */

        Surface(
            modifier = Modifier
                .align(
                    Alignment.TopStart
                )
                .padding(
                    start = 2.dp,
                    top = 30.dp
                ),
            shape =
                RoundedCornerShape(
                    17.dp
                ),
            color =
                Color.White.copy(
                    alpha = 0.94f
                ),
            shadowElevation =
                4.dp
        ) {

            Row(
                modifier =
                    Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 8.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Outlined.Savings,
                    contentDescription =
                        null,
                    tint =
                        BrandPurple,
                    modifier =
                        Modifier.size(20.dp)
                )

                Spacer(
                    modifier =
                        Modifier.size(6.dp)
                )

                Text(
                    text =
                        "Tiết kiệm\nhôm nay",
                    fontSize =
                        10.sp,
                    lineHeight =
                        13.sp,
                    fontWeight =
                        FontWeight.Medium,
                    color =
                        Color(0xFF332C67)
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | RIGHT TOP CARD
        |--------------------------------------------------------------------------
        */

        Surface(
            modifier = Modifier
                .align(
                    Alignment.TopEnd
                )
                .padding(
                    end = 2.dp,
                    top = 34.dp
                ),
            shape =
                RoundedCornerShape(
                    17.dp
                ),
            color =
                Color.White.copy(
                    alpha = 0.94f
                ),
            shadowElevation =
                4.dp
        ) {

            Row(
                modifier =
                    Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 8.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Outlined.BarChart,
                    contentDescription =
                        null,
                    tint =
                        BrandPurple,
                    modifier =
                        Modifier.size(20.dp)
                )

                Spacer(
                    modifier =
                        Modifier.size(6.dp)
                )

                Text(
                    text =
                        "Tự do\ntài chính",
                    fontSize =
                        10.sp,
                    lineHeight =
                        13.sp,
                    fontWeight =
                        FontWeight.Medium,
                    color =
                        Color(0xFF332C67)
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | LEFT SLOGAN
        |--------------------------------------------------------------------------
        */

        Text(
            text =
                "Tài chính tốt đẹp\nhơn mỗi ngày",
            modifier = Modifier
                .align(
                    Alignment.CenterStart
                )
                .offset(
                    y = 45.dp
                )
                .padding(
                    start = 2.dp
                ),
            fontSize =
                13.sp,
            lineHeight =
                17.sp,
            fontStyle =
                FontStyle.Italic,
            fontWeight =
                FontWeight.Medium,
            color =
                Color(0xFF745BCC)
        )


        /*
        |--------------------------------------------------------------------------
        | RIGHT LOWER CARD
        |--------------------------------------------------------------------------
        */

        Surface(
            modifier = Modifier
                .align(
                    Alignment.CenterEnd
                )
                .offset(
                    y = 68.dp
                ),
            shape =
                RoundedCornerShape(
                    17.dp
                ),
            color =
                Color.White.copy(
                    alpha = 0.94f
                ),
            shadowElevation =
                4.dp
        ) {

            Row(
                modifier =
                    Modifier.padding(
                        horizontal = 9.dp,
                        vertical = 8.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "♥",
                    fontSize =
                        18.sp,
                    color =
                        BrandPurple
                )

                Spacer(
                    modifier =
                        Modifier.size(5.dp)
                )

                Text(
                    text =
                        "Cuộc sống\ntốt đẹp hơn",
                    fontSize =
                        9.5.sp,
                    lineHeight =
                        12.sp,
                    color =
                        Color(0xFF342D68)
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| FEATURE
|--------------------------------------------------------------------------
*/

@Composable
private fun WelcomeFeatureCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    description: String
) {

    Column(
        modifier =
            modifier,
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Surface(
            modifier =
                Modifier.size(
                    52.dp
                ),
            shape =
                RoundedCornerShape(
                    16.dp
                ),
            color =
                BrandPurple.copy(
                    alpha = 0.075f
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
                Modifier.height(
                    7.dp
                )
        )

        Text(
            text =
                title,
            modifier =
                Modifier.fillMaxWidth(),
            fontSize =
                11.5.sp,
            lineHeight =
                14.sp,
            fontWeight =
                FontWeight.Bold,
            textAlign =
                TextAlign.Center,
            color =
                Color(0xFF252052),
            maxLines =
                2,
            overflow =
                TextOverflow.Ellipsis
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
            fontSize =
                10.sp,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant,
            textAlign =
                TextAlign.Center
        )
    }
}