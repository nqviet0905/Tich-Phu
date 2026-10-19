package com.example.personalfinance.data.repository

import com.example.personalfinance.data.model.CreateTransactionRequest
import com.example.personalfinance.data.model.TransactionListData
import com.example.personalfinance.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.example.personalfinance.data.model.UpdateTransactionRequest
import com.example.personalfinance.data.model.Transaction

class TransactionRepository {

    suspend fun updateTransaction(
        id: Long,
        categoryId: Long,
        type: String,
        amount: Double,
        transactionDate: String,
        note: String?
    ): Result<String> {

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
                    .updateTransaction(
                        authorization =
                            "Bearer $token",

                        request =
                            UpdateTransactionRequest(
                                id = id,
                                category_id =
                                    categoryId,
                                type = type,
                                amount = amount,
                                transaction_date =
                                    transactionDate,
                                note = note
                            )
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.message
                        ?: "Cập nhật giao dịch thành công."
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể cập nhật giao dịch."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(
        id: Long
    ): Result<String> {

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
                    .deleteTransaction(
                        "Bearer $token",
                        id
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.message
                        ?: "Xóa giao dịch thành công."
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể xóa giao dịch."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTransactionDetail(
        id: Long
    ): Result<Transaction?> {

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
                    .getTransactionDetail(
                        "Bearer $token",
                        id
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.data
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể lấy giao dịch."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getTransactions(
        type: String? = null,
        month: Int? = null,
        year: Int? = null,
        page: Int = 1,
        limit: Int = 50
    ): Result<TransactionListData?> {

        return try {

            val user = FirebaseAuth
                .getInstance()
                .currentUser
                ?: return Result.failure(
                    Exception("Chưa đăng nhập.")
                )

            val token = user
                .getIdToken(false)
                .await()
                .token
                ?: return Result.failure(
                    Exception("Không lấy được token.")
                )

            val response =
                RetrofitClient.apiService
                    .getTransactions(
                        authorization = "Bearer $token",
                        type = type,
                        month = month,
                        year = year
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.data
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể lấy danh sách giao dịch."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTransaction(
        categoryId: Long,
        type: String,
        amount: Double,
        transactionDate: String,
        note: String?
    ): Result<String> {

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
                RetrofitClient.apiService.createTransaction(
                    authorization = "Bearer $token",
                    request =
                        CreateTransactionRequest(
                            category_id = categoryId,
                            type = type,
                            amount = amount,
                            transaction_date = transactionDate,
                            note = note
                        )
                )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.message
                        ?: "Tạo giao dịch thành công."
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể tạo giao dịch."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}