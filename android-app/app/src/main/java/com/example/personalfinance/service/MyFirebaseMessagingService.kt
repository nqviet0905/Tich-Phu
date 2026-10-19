package com.example.personalfinance.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.personalfinance.MainActivity
import com.example.personalfinance.R
import com.example.personalfinance.data.repository.DeviceRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService :
    FirebaseMessagingService() {

    override fun onNewToken(
        token: String
    ) {

        super.onNewToken(token)

        if (
            FirebaseAuth
                .getInstance()
                .currentUser != null
        ) {

            CoroutineScope(
                Dispatchers.IO
            ).launch {

                DeviceRepository()
                    .registerToken(token)
            }
        }
    }

    override fun onMessageReceived(
        remoteMessage: RemoteMessage
    ) {

        super.onMessageReceived(
            remoteMessage
        )

        val title =
            remoteMessage.notification
                ?.title
                ?: remoteMessage.data["title"]
                ?: "Tích Phú"

        val message =
            remoteMessage.notification
                ?.body
                ?: remoteMessage.data["message"]
                ?: "Bạn có thông báo mới."

        showNotification(
            title = title,
            message = message
        )
    }

    private fun showNotification(
        title: String,
        message: String
    ) {

        val channelId =
            "tich_phu_notifications"

        val notificationManager =
            getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    channelId,
                    "Thông báo Tích Phú",
                    NotificationManager
                        .IMPORTANCE_DEFAULT
                )

            notificationManager
                .createNotificationChannel(
                    channel
                )
        }

        val intent =
            Intent(
                this,
                MainActivity::class.java
            ).apply {

                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or
                        PendingIntent.FLAG_UPDATE_CURRENT
            )

        val notification =
            NotificationCompat
                .Builder(
                    this,
                    channelId
                )
                .setSmallIcon(
                    R.mipmap.ic_launcher
                )
                .setContentTitle(
                    title
                )
                .setContentText(
                    message
                )
                .setStyle(
                    NotificationCompat
                        .BigTextStyle()
                        .bigText(
                            message
                        )
                )
                .setAutoCancel(
                    true
                )
                .setContentIntent(
                    pendingIntent
                )
                .build()

        notificationManager
            .notify(
                System.currentTimeMillis()
                    .toInt(),
                notification
            )
    }
}