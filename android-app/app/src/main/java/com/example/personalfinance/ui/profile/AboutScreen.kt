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
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.ShowChart

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.ui.theme.BrandPurpleDark


@Composable
fun AboutScreen(
    onBack: () -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),
        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 16.dp,
            bottom = 36.dp
        ),
        verticalArrangement = Arrangement.spacedBy(
            16.dp
        )
    ) {

        /*
        |--------------------------------------------------------------------------
        | HEADER
        |--------------------------------------------------------------------------
        */

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = MaterialTheme
                        .colorScheme
                        .surfaceVariant
                        .copy(alpha = 0.65f)
                ) {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored
                                    .Filled
                                    .ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                }


                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 14.dp)
                ) {

                    Text(
                        text = "Giới thiệu Tích Phú",
                        style = MaterialTheme
                            .typography
                            .headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )


                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )


                    Text(
                        text = "Thông tin về ứng dụng",
                        style = MaterialTheme
                            .typography
                            .bodySmall,
                        color = MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    BrandPurpleDark,
                                    BrandPurple,
                                    Color(0xFF8D5CFF)
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(
                            horizontal = 22.dp,
                            vertical = 28.dp
                        )
                ) {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Surface(
                            modifier = Modifier.size(82.dp),
                            shape = RoundedCornerShape(25.dp),
                            color = Color.White.copy(
                                alpha = 0.18f
                            )
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Outlined
                                            .AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                        }


                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )


                        Text(
                            text = "TÍCH PHÚ",
                            style = MaterialTheme
                                .typography
                                .headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )


                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )


                        Text(
                            text =
                                "Quản lý tài chính cá nhân thông minh",
                            style = MaterialTheme
                                .typography
                                .bodyMedium,
                            color = Color.White.copy(
                                alpha = 0.85f
                            ),
                            textAlign = TextAlign.Center
                        )


                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )


                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = Color.White.copy(
                                alpha = 0.15f
                            )
                        ) {

                            Text(
                                text = "Phiên bản 1.0.0",
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 7.dp
                                ),
                                color = Color.White,
                                style = MaterialTheme
                                    .typography
                                    .labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | ABOUT
        |--------------------------------------------------------------------------
        */

        item {

            AboutSectionTitle(
                title = "Về Tích Phú"
            )
        }


        item {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text =
                            "Tích Phú là ứng dụng hỗ trợ quản lý thu chi và lập kế hoạch tài chính cá nhân trên nền tảng Android.",
                        style = MaterialTheme
                            .typography
                            .bodyMedium,
                        color = MaterialTheme
                            .colorScheme
                            .onSurface
                    )


                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )


                    Text(
                        text =
                            "Ứng dụng giúp người dùng theo dõi giao dịch, quản lý ngân sách, xây dựng kế hoạch tài chính, theo dõi mục tiêu tiết kiệm và xem thống kê tài chính.",
                        style = MaterialTheme
                            .typography
                            .bodyMedium,
                        color = MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | FEATURES
        |--------------------------------------------------------------------------
        */

        item {

            AboutSectionTitle(
                title = "Chức năng nổi bật"
            )
        }


        item {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {

                Column {

                    AboutFeatureRow(
                        icon = Icons.Outlined
                            .AccountBalanceWallet,
                        iconColor = Color(0xFF318BF0),
                        title = "Quản lý thu chi",
                        subtitle =
                            "Theo dõi các giao dịch thu nhập và chi tiêu."
                    )


                    AboutDivider()


                    AboutFeatureRow(
                        icon = Icons.Outlined.Savings,
                        iconColor = Color(0xFF159653),
                        title = "Ngân sách và mục tiêu",
                        subtitle =
                            "Quản lý ngân sách và theo dõi tiến độ tiết kiệm."
                    )


                    AboutDivider()


                    AboutFeatureRow(
                        icon = Icons.Outlined.ShowChart,
                        iconColor = Color(0xFFF39A35),
                        title = "Thống kê tài chính",
                        subtitle =
                            "Phân tích tình hình thu chi theo thời gian."
                    )


                    AboutDivider()


                    AboutFeatureRow(
                        icon = Icons.Outlined.QrCodeScanner,
                        iconColor = Color(0xFF7557E9),
                        title = "Thanh toán QR",
                        subtitle =
                            "Quét QR và ghi nhận giao dịch thanh toán mô phỏng."
                    )


                    AboutDivider()


                    AboutFeatureRow(
                        icon = Icons.Outlined.Security,
                        iconColor = Color(0xFFE55353),
                        title = "Bảo mật tài khoản",
                        subtitle =
                            "Xác thực bằng Firebase và bảo vệ API bằng ID Token."
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | PROJECT INFO
        |--------------------------------------------------------------------------
        */

        item {

            AboutSectionTitle(
                title = "Thông tin ứng dụng"
            )
        }


        item {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            ) {

                Column(
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 8.dp
                    )
                ) {

                    AboutInfoRow(
                        label = "Tên ứng dụng",
                        value = "Tích Phú"
                    )

                    HorizontalDivider(
                        color = MaterialTheme
                            .colorScheme
                            .outlineVariant
                            .copy(alpha = 0.5f)
                    )

                    AboutInfoRow(
                        label = "Phiên bản",
                        value = "1.0.0"
                    )

                    HorizontalDivider(
                        color = MaterialTheme
                            .colorScheme
                            .outlineVariant
                            .copy(alpha = 0.5f)
                    )

                    AboutInfoRow(
                        label = "Nền tảng",
                        value = "Android"
                    )

                    HorizontalDivider(
                        color = MaterialTheme
                            .colorScheme
                            .outlineVariant
                            .copy(alpha = 0.5f)
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | NOTICE
        |--------------------------------------------------------------------------
        */

        item {

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme
                    .colorScheme
                    .surfaceVariant
                    .copy(alpha = 0.5f)
            ) {

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment =
                        Alignment.Top
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = BrandPurple,
                        modifier = Modifier.size(22.dp)
                    )


                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )


                    Text(
                        text =
                            "Các chức năng ngân hàng, nạp tiền, chuyển tiền và thanh toán QR trong Tích Phú phục vụ mục đích mô phỏng trong phạm vi ứng dụng.",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme
                            .typography
                            .bodySmall,
                        color = MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                    )
                }
            }
        }


        item {

            Text(
                text = "© 2026 Tích Phú",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme
                    .typography
                    .bodySmall,
                color = MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
            )
        }
    }
}


@Composable
private fun AboutSectionTitle(
    title: String
) {

    Text(
        text = title,
        style = MaterialTheme
            .typography
            .titleMedium,
        fontWeight = FontWeight.ExtraBold
    )
}


@Composable
private fun AboutFeatureRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 14.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(46.dp),
            shape = RoundedCornerShape(14.dp),
            color = iconColor.copy(
                alpha = 0.11f
            )
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
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


        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                style = MaterialTheme
                    .typography
                    .bodyMedium,
                fontWeight = FontWeight.Bold
            )


            Spacer(
                modifier = Modifier.height(2.dp)
            )


            Text(
                text = subtitle,
                style = MaterialTheme
                    .typography
                    .bodySmall,
                color = MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
            )
        }
    }
}


@Composable
private fun AboutInfoRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 14.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme
                .typography
                .bodyMedium,
            color = MaterialTheme
                .colorScheme
                .onSurfaceVariant
        )


        Text(
            text = value,
            style = MaterialTheme
                .typography
                .bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
private fun AboutDivider() {

    HorizontalDivider(
        modifier = Modifier.padding(
            start = 74.dp
        ),
        color = MaterialTheme
            .colorScheme
            .outlineVariant
            .copy(alpha = 0.5f)
    )
}