package com.example.personalfinance.data.repository

import com.example.personalfinance.data.model.RegisterDeviceTokenRequest
import com.example.personalfinance.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class DeviceRepository {

    private val firebaseAuth =
        FirebaseAuth.getInstance()

    private suspend fun getAuthToken(): String {

        val user =
            firebaseAuth.currentUser
                ?: throw Exception(
                    "Chưa đăng nhập."
                )

        return user
            .getIdToken(false)
            .await()
            .token
            ?: throw Exception(
                "Không lấy được Firebase ID Token."
            )
    }

    suspend fun registerToken(
        fcmToken: String
    ): Result<String> {

        return try {

            val authToken =
                getAuthToken()

            val response =
                RetrofitClient.apiService
                    .registerDeviceToken(
                        authorization =
                            "Bearer $authToken",

                        request =
                            RegisterDeviceTokenRequest(
                                token = fcmToken
                            )
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.message
                        ?: "Đăng ký thiết bị thành công."
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể đăng ký thiết bị."
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun removeToken(
        fcmToken: String
    ): Result<String> {

        return try {

            val authToken =
                getAuthToken()

            val response =
                RetrofitClient.apiService
                    .removeDeviceToken(
                        authorization =
                            "Bearer $authToken",

                        request =
                            RegisterDeviceTokenRequest(
                                token = fcmToken
                            )
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.message
                        ?: "Đã gỡ thiết bị."
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể gỡ thiết bị."
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}