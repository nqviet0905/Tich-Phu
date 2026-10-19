package com.example.personalfinance.ui.notification

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun NotificationScreen(

    onBack: () -> Unit,

    notificationViewModel:
    NotificationViewModel = viewModel()

) {

    val state by
    notificationViewModel
        .uiState
        .collectAsState()


    /*
    |--------------------------------------------------------------------------
    | UNREAD COUNT
    |--------------------------------------------------------------------------
    */

    val unreadCount =
        state.notifications.count {
            it.is_read == 0
        }


    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                )

    ) {

        /*
        |--------------------------------------------------------------------------
        | HEADER
        |--------------------------------------------------------------------------
        */

        NotificationHeader(

            unreadCount =
                unreadCount,

            onBack =
                onBack
        )


        /*
        |--------------------------------------------------------------------------
        | INITIAL LOADING
        |--------------------------------------------------------------------------
        */

        if (
            state.isLoading &&
            state.notifications.isEmpty()
        ) {

            Box(

                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center

            ) {

                CircularProgressIndicator(
                    color =
                        BrandPurple
                )
            }

            return@Column
        }


        /*
        |--------------------------------------------------------------------------
        | CONTENT
        |--------------------------------------------------------------------------
        */

        LazyColumn(

            modifier =
                Modifier.fillMaxSize(),

            contentPadding =
                PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 14.dp,
                    bottom = 100.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )

        ) {

            /*
            |--------------------------------------------------------------------------
            | MARK ALL
            |--------------------------------------------------------------------------
            */

            if (unreadCount > 0) {

                item {

                    MarkAllReadButton(

                        isUpdating =
                            state.isUpdating,

                        unreadCount =
                            unreadCount,

                        onClick = {

                            notificationViewModel
                                .markAllAsRead()
                        }
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | ERROR
            |--------------------------------------------------------------------------
            */

            if (
                !state.error.isNullOrBlank()
            ) {

                item {

                    NotificationErrorCard(

                        message =
                            state.error
                                ?: "Đã xảy ra lỗi.",

                        onRetry = {

                            notificationViewModel
                                .loadNotifications()
                        },

                        onDismiss = {

                            notificationViewModel
                                .clearError()
                        }
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | EMPTY
            |--------------------------------------------------------------------------
            */

            if (
                state.notifications.isEmpty() &&
                !state.isLoading
            ) {

                item {

                    NotificationEmptyState()
                }

            } else {

                /*
                |--------------------------------------------------------------------------
                | TITLE
                |--------------------------------------------------------------------------
                */

                item {

                    Column {

                        Text(

                            text =
                                "Gần đây",

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


                        Spacer(
                            modifier =
                                Modifier.height(
                                    2.dp
                                )
                        )


                        Text(

                            text =
                                "Các cập nhật tài chính mới nhất của bạn",

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


                /*
                |--------------------------------------------------------------------------
                | NOTIFICATIONS
                |--------------------------------------------------------------------------
                */

                items(

                    items =
                        state.notifications,

                    key = {
                        it.id
                    }

                ) { notification ->

                    val isUnread =
                        notification.is_read == 0


                    NotificationItem(

                        title =
                            notification.title,

                        message =
                            notification.message,

                        type =
                            notification.type,

                        createdAt =
                            notification.created_at,

                        isUnread =
                            isUnread,

                        onClick = {

                            if (isUnread) {

                                notificationViewModel
                                    .markAsRead(
                                        notification.id
                                    )
                            }
                        }
                    )
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| HEADER
|--------------------------------------------------------------------------
*/

@Composable
private fun NotificationHeader(

    unreadCount: Int,

    onBack: () -> Unit

) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 10.dp,
                    end = 18.dp,
                    top = 12.dp,
                    bottom = 4.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically

    ) {

        /*
        |--------------------------------------------------------------------------
        | BACK
        |--------------------------------------------------------------------------
        */

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
                    "Quay lại",

                tint =
                    MaterialTheme
                        .colorScheme
                        .onBackground
            )
        }


        Spacer(
            modifier =
                Modifier.width(
                    4.dp
                )
        )


        /*
        |--------------------------------------------------------------------------
        | TITLE
        |--------------------------------------------------------------------------
        */

        Column(
            modifier =
                Modifier.weight(
                    1f
                )
        ) {

            Text(

                text =
                    "Thông báo",

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


            Text(

                text =
                    if (unreadCount > 0) {

                        "$unreadCount thông báo chưa đọc"

                    } else {

                        "Bạn đã đọc tất cả thông báo"
                    },

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


        /*
        |--------------------------------------------------------------------------
        | HEADER ICON
        |--------------------------------------------------------------------------
        */

        Surface(

            modifier =
                Modifier.size(
                    44.dp
                ),

            shape =
                CircleShape,

            color =
                BrandPurple.copy(
                    alpha = 0.10f
                )

        ) {

            Box(

                contentAlignment =
                    Alignment.Center

            ) {

                Icon(

                    imageVector =
                        Icons.Default
                            .Notifications,

                    contentDescription =
                        null,

                    tint =
                        BrandPurple,

                    modifier =
                        Modifier.size(
                            22.dp
                        )
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| MARK ALL READ
|--------------------------------------------------------------------------
*/

@Composable
private fun MarkAllReadButton(

    isUpdating: Boolean,

    unreadCount: Int,

    onClick: () -> Unit

) {

    OutlinedButton(

        onClick =
            onClick,

        enabled =
            !isUpdating,

        modifier =
            Modifier
                .fillMaxWidth()
                .height(
                    52.dp
                ),

        shape =
            RoundedCornerShape(
                16.dp
            )

    ) {

        if (isUpdating) {

            CircularProgressIndicator(

                modifier =
                    Modifier.size(
                        18.dp
                    ),

                strokeWidth =
                    2.dp
            )


            Spacer(
                modifier =
                    Modifier.width(
                        9.dp
                    )
            )


            Text(
                text =
                    "Đang cập nhật..."
            )

        } else {

            Icon(

                imageVector =
                    Icons.Default
                        .CheckCircle,

                contentDescription =
                    null,

                modifier =
                    Modifier.size(
                        19.dp
                    )
            )


            Spacer(
                modifier =
                    Modifier.width(
                        8.dp
                    )
            )


            Text(

                text =
                    "Đánh dấu tất cả đã đọc ($unreadCount)",

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| NOTIFICATION ITEM
|--------------------------------------------------------------------------
*/

@Composable
private fun NotificationItem(

    title: String,

    message: String,

    type: String,

    createdAt: String?,

    isUnread: Boolean,

    onClick: () -> Unit

) {

    val accentColor =
        notificationTypeColor(
            type
        )


    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick
                ),

        shape =
            RoundedCornerShape(
                20.dp
            ),

        colors =
            CardDefaults
                .cardColors(

                    containerColor =
                        if (isUnread) {

                            accentColor.copy(
                                alpha = 0.075f
                            )

                        } else {

                            MaterialTheme
                                .colorScheme
                                .surface
                        }
                ),

        elevation =
            CardDefaults
                .cardElevation(

                    defaultElevation =
                        if (isUnread) {
                            2.dp
                        } else {
                            1.dp
                        }
                )

    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        16.dp
                    ),

            verticalAlignment =
                Alignment.Top

        ) {

            /*
            |--------------------------------------------------------------------------
            | ICON
            |--------------------------------------------------------------------------
            */

            Surface(

                modifier =
                    Modifier.size(
                        46.dp
                    ),

                shape =
                    RoundedCornerShape(
                        15.dp
                    ),

                color =
                    accentColor.copy(
                        alpha = 0.12f
                    )

            ) {

                Box(

                    contentAlignment =
                        Alignment.Center

                ) {

                    Icon(

                        imageVector =
                            Icons.Default
                                .Notifications,

                        contentDescription =
                            null,

                        tint =
                            accentColor,

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
                        13.dp
                    )
            )


            /*
            |--------------------------------------------------------------------------
            | CONTENT
            |--------------------------------------------------------------------------
            */

            Column(

                modifier =
                    Modifier.weight(
                        1f
                    )

            ) {

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Text(

                        text =
                            title,

                        modifier =
                            Modifier.weight(
                                1f
                            ),

                        style =
                            MaterialTheme
                                .typography
                                .titleSmall,

                        fontWeight =
                            if (isUnread) {

                                FontWeight.ExtraBold

                            } else {

                                FontWeight.SemiBold
                            },

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,

                        maxLines =
                            2,

                        overflow =
                            TextOverflow.Ellipsis
                    )


                    if (isUnread) {

                        Spacer(
                            modifier =
                                Modifier.width(
                                    8.dp
                                )
                        )


                        /*
                        |--------------------------------------------------------------------------
                        | UNREAD DOT
                        |--------------------------------------------------------------------------
                        */

                        Box(

                            modifier =
                                Modifier
                                    .size(
                                        9.dp
                                    )
                                    .background(
                                        accentColor,
                                        CircleShape
                                    )
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            6.dp
                        )
                )


                Text(

                    text =
                        message,

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )


                if (
                    !createdAt
                        .isNullOrBlank()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                10.dp
                            )
                    )


                    HorizontalDivider(

                        color =
                            MaterialTheme
                                .colorScheme
                                .outlineVariant
                                .copy(
                                    alpha = 0.50f
                                )
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    Row(

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        Text(

                            text =
                                formatNotificationTime(
                                    createdAt
                                ),

                            style =
                                MaterialTheme
                                    .typography
                                    .labelSmall,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )


                        Spacer(
                            modifier =
                                Modifier.weight(
                                    1f
                                )
                        )


                        if (isUnread) {

                            Text(

                                text =
                                    "Chưa đọc",

                                style =
                                    MaterialTheme
                                        .typography
                                        .labelSmall,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    accentColor
                            )

                        } else {

                            Text(

                                text =
                                    "Đã đọc",

                                style =
                                    MaterialTheme
                                        .typography
                                        .labelSmall,

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| EMPTY STATE
|--------------------------------------------------------------------------
*/

@Composable
private fun NotificationEmptyState() {

    Box(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    top = 70.dp
                ),

        contentAlignment =
            Alignment.Center

    ) {

        Column(

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Surface(

                modifier =
                    Modifier.size(
                        90.dp
                    ),

                shape =
                    CircleShape,

                color =
                    BrandPurple.copy(
                        alpha = 0.10f
                    )

            ) {

                Box(

                    contentAlignment =
                        Alignment.Center

                ) {

                    Icon(

                        imageVector =
                            Icons.Default
                                .Notifications,

                        contentDescription =
                            null,

                        tint =
                            BrandPurple,

                        modifier =
                            Modifier.size(
                                42.dp
                            )
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            Text(

                text =
                    "Chưa có thông báo",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(
                        5.dp
                    )
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| ERROR CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun NotificationErrorCard(

    message: String,

    onRetry: () -> Unit,

    onDismiss: () -> Unit

) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                18.dp
            ),

        colors =
            CardDefaults
                .cardColors(

                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .errorContainer
                )

    ) {

        Column(

            modifier =
                Modifier.padding(
                    16.dp
                )

        ) {

            Text(

                text =
                    message,

                color =
                    MaterialTheme
                        .colorScheme
                        .onErrorContainer,

                fontWeight =
                    FontWeight.SemiBold
            )


            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
                    )
            )


            Row {

                Button(
                    onClick =
                        onRetry
                ) {

                    Text(
                        "Thử lại"
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(
                            8.dp
                        )
                )


                TextButton(
                    onClick =
                        onDismiss
                ) {

                    Text(
                        "Đóng"
                    )
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| TYPE COLOR
|--------------------------------------------------------------------------
*/

private fun notificationTypeColor(
    type: String
): Color {

    return when (
        type.lowercase()
    ) {

        /*
        |--------------------------------------------------------------------------
        | NGÂN SÁCH
        |--------------------------------------------------------------------------
        */

        "budget" ->
            Color(
                0xFFFF9800
            )


        /*
        |--------------------------------------------------------------------------
        | MỤC TIÊU
        |--------------------------------------------------------------------------
        */

        "goal" ->
            Color(
                0xFF10A66A
            )


        /*
        |--------------------------------------------------------------------------
        | KẾ HOẠCH
        |--------------------------------------------------------------------------
        */

        "plan" ->
            Color(
                0xFF3F7AE0
            )


        /*
        |--------------------------------------------------------------------------
        | HỆ THỐNG / QR PAYMENT
        |--------------------------------------------------------------------------
        */

        "system" ->
            BrandPurple


        /*
        |--------------------------------------------------------------------------
        | DEFAULT
        |--------------------------------------------------------------------------
        */

        else ->
            BrandPurple
    }
}


/*
|--------------------------------------------------------------------------
| FORMAT TIME
|--------------------------------------------------------------------------
*/

private fun formatNotificationTime(
    value: String
): String {

    /*
    |--------------------------------------------------------------------------
    | MYSQL FORMAT
    |--------------------------------------------------------------------------
    |
    | 2026-09-15 21:30:45
    |
    | ->
    |
    | 21:30 • 15/09/2026
    |
    */

    return try {

        val inputFormat =
            SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            )


        val outputFormat =
            SimpleDateFormat(
                "HH:mm • dd/MM/yyyy",
                Locale(
                    "vi",
                    "VN"
                )
            )


        val date =
            inputFormat.parse(
                value
            )


        if (date != null) {

            outputFormat.format(
                date
            )

        } else {

            value
        }

    } catch (
        _: Exception
    ) {

        value
    }
}