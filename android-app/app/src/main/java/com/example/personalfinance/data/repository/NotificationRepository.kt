package com.example.personalfinance.data.repository

import com.example.personalfinance.data.model.AppNotification
import com.example.personalfinance.data.model.ReadNotificationRequest
import com.example.personalfinance.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class NotificationRepository {

    private suspend fun getToken(): String {

        val user =
            FirebaseAuth.getInstance().currentUser
                ?: throw Exception("Chưa đăng nhập.")

        return user
            .getIdToken(false)
            .await()
            .token
            ?: throw Exception("Không lấy được token.")
    }

    suspend fun getNotifications():
            Result<List<AppNotification>> {

        return try {

            val token = getToken()

            val response =
                RetrofitClient.apiService
                    .getNotifications(
                        "Bearer $token"
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.data
                        ?: emptyList()
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể tải thông báo."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readNotification(
        id: Long
    ): Result<String> {

        return try {

            val token = getToken()

            val response =
                RetrofitClient.apiService
                    .readNotification(
                        authorization =
                            "Bearer $token",
                        request =
                            ReadNotificationRequest(
                                id = id
                            )
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.message
                        ?: "Đã đọc thông báo."
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể cập nhật thông báo."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readAll():
            Result<String> {

        return try {

            val token = getToken()

            val response =
                RetrofitClient.apiService
                    .readAllNotifications(
                        "Bearer $token"
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.message
                        ?: "Đã đọc tất cả."
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể cập nhật thông báo."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}