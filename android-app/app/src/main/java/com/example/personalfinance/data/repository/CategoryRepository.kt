package com.example.personalfinance.data.repository

import com.example.personalfinance.data.model.Category
import com.example.personalfinance.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class CategoryRepository {

    suspend fun getCategories(
        type: String
    ): Result<List<Category>> {

        return try {

            val user =
                FirebaseAuth.getInstance().currentUser
                    ?: return Result.failure(
                        Exception("Chưa đăng nhập.")
                    )

            val token =
                user.getIdToken(false)
                    .await()
                    .token
                    ?: return Result.failure(
                        Exception("Không lấy được token.")
                    )

            val response =
                RetrofitClient.apiService
                    .getCategories(
                        authorization = "Bearer $token",
                        type = type
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
                            ?: "Không thể lấy danh mục."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}