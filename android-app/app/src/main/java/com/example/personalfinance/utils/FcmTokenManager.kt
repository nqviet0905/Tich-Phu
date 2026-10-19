package com.example.personalfinance.utils

import com.example.personalfinance.data.repository.DeviceRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object FcmTokenManager {

    suspend fun registerCurrentDevice():
            Result<String> {

        return try {

            val token =
                FirebaseMessaging
                    .getInstance()
                    .token
                    .await()

            DeviceRepository()
                .registerToken(token)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}