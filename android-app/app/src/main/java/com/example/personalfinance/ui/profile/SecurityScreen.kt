package com.example.personalfinance.ui.profile

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SecurityScreen(
    onBack: () -> Unit,
    onChangePasswordClick: () -> Unit
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // ------------------------------------------------------------
        // HEADER
        // ------------------------------------------------------------

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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                        text = "Bảo mật",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )
                }
            }
        }

        // ------------------------------------------------------------
        // HERO
        // ------------------------------------------------------------

        item {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme
                        .colorScheme
                        .primary
                        .copy(alpha = 0.09f)
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = RoundedCornerShape(23.dp),
                        color = MaterialTheme
                            .colorScheme
                            .primary
                            .copy(alpha = 0.13f)
                    ) {

                        Box(
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.Security,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme
                                    .colorScheme
                                    .primary
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text = "Bảo vệ tài khoản của bạn",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )
                }
            }
        }

        // ------------------------------------------------------------
        // TITLE
        // ------------------------------------------------------------

        item {

            Text(
                text = "Bảo mật tài khoản",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // ------------------------------------------------------------
        // SETTINGS
        // ------------------------------------------------------------

        item {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {

                Column {

                    // ------------------------------------------------
                    // ĐỔI MẬT KHẨU
                    // ------------------------------------------------

                    SecuritySettingRow(
                        icon = Icons.Outlined.Key,
                        iconColor = Color(0xFF7557E9),
                        title = "Đổi mật khẩu",
                        subtitle = "Gửi email để đặt lại mật khẩu đăng nhập",
                        onClick = onChangePasswordClick
                    )

                    SecurityDivider()

                    // ------------------------------------------------
                    // FIREBASE
                    // ------------------------------------------------

                    SecurityInfoRow(
                        icon = Icons.Outlined.Email,
                        iconColor = Color(0xFF318BF0),
                        title = "Xác thực tài khoản",
                        subtitle =
                            "Tài khoản được xác thực thông qua Firebase Authentication"
                    )

                    SecurityDivider()

                    // ------------------------------------------------
                    // TOKEN
                    // ------------------------------------------------

                    SecurityInfoRow(
                        icon = Icons.Outlined.Lock,
                        iconColor = Color(0xFF159653),
                        title = "Phiên đăng nhập",
                        subtitle =
                            "Các yêu cầu API được bảo vệ bằng Firebase ID Token"
                    )
                }
            }
        }

        // ------------------------------------------------------------
        // SECURITY NOTE
        // ------------------------------------------------------------

        item {

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme
                    .colorScheme
                    .surfaceVariant
                    .copy(alpha = 0.55f)
            ) {

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(11.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Lưu ý bảo mật",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "Không chia sẻ mật khẩu hoặc mã xác thực cho người khác.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


// ====================================================================
// SETTING ROW
// ====================================================================

@Composable
private fun SecuritySettingRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                start = 15.dp,
                end = 8.dp,
                top = 14.dp,
                bottom = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ICON

        Surface(
            modifier = Modifier.size(46.dp),
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
            modifier = Modifier.width(10.dp)
        )

        // TEXT

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            /*
             * Dòng mô tả:
             * - 8sp
             * - chỉ 1 dòng
             * - không tự xuống dòng
             */

            Text(
                text = subtitle,
                fontSize = 8.sp,
                lineHeight = 9.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false
            )
        }

        Spacer(
            modifier = Modifier.width(2.dp)
        )

        // MŨI TÊN

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}


// ====================================================================
// INFO ROW
// ====================================================================

@Composable
private fun SecurityInfoRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 15.dp,
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(46.dp),
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
            modifier = Modifier.width(10.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.width(2.dp)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}


// ====================================================================
// DIVIDER
// ====================================================================

@Composable
private fun SecurityDivider() {

    HorizontalDivider(
        modifier = Modifier.padding(
            start = 73.dp
        ),
        color = MaterialTheme
            .colorScheme
            .outlineVariant
            .copy(alpha = 0.5f)
    )
}