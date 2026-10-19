package com.example.personalfinance.data.repository

import com.example.personalfinance.data.model.ApiResponse
import com.example.personalfinance.data.model.BankAccount
import com.example.personalfinance.data.model.Category
import com.example.personalfinance.data.model.MerchantCategorySuggestion
import com.example.personalfinance.data.model.QrPaymentRequest
import com.example.personalfinance.data.model.QrPaymentResult
import com.example.personalfinance.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await


class QrPaymentRepository {

    private val apiService =
        RetrofitClient.apiService

    private val firebaseAuth =
        FirebaseAuth.getInstance()


    /*
    |--------------------------------------------------------------------------
    | FIREBASE TOKEN
    |--------------------------------------------------------------------------
    */

    private suspend fun getAuthorizationHeader(): String {

        val user =
            firebaseAuth.currentUser
                ?: throw Exception(
                    "Bạn chưa đăng nhập."
                )

        val token =
            user.getIdToken(false)
                .await()
                .token
                ?: throw Exception(
                    "Không thể lấy token đăng nhập."
                )

        return "Bearer $token"
    }


    /*
    |--------------------------------------------------------------------------
    | BANK ACCOUNTS
    |--------------------------------------------------------------------------
    */

    suspend fun getBankAccounts():
            Result<List<BankAccount>> {

        return try {

            val response =
                apiService.getBankAccounts(
                    getAuthorizationHeader()
                )

            if (response.isSuccessful) {

                val body =
                    response.body()

                if (
                    body?.success == true &&
                    body.data != null
                ) {

                    Result.success(
                        body.data
                    )

                } else {

                    Result.failure(
                        Exception(
                            body?.message
                                ?: "Không thể lấy tài khoản ngân hàng."
                        )
                    )
                }

            } else {

                Result.failure(
                    Exception(
                        parseError(
                            response.errorBody()?.string(),
                            response.code()
                        )
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    /*
    |--------------------------------------------------------------------------
    | EXPENSE CATEGORIES
    |--------------------------------------------------------------------------
    */

    suspend fun getExpenseCategories():
            Result<List<Category>> {

        return try {

            val response =
                apiService.getCategories(
                    authorization =
                        getAuthorizationHeader(),

                    type =
                        "expense"
                )

            if (response.isSuccessful) {

                val body =
                    response.body()

                if (
                    body?.success == true &&
                    body.data != null
                ) {

                    Result.success(
                        body.data
                    )

                } else {

                    Result.failure(
                        Exception(
                            body?.message
                                ?: "Không thể lấy danh mục chi tiêu."
                        )
                    )
                }

            } else {

                Result.failure(
                    Exception(
                        parseError(
                            response.errorBody()?.string(),
                            response.code()
                        )
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    /*
    |--------------------------------------------------------------------------
    | MERCHANT SUGGESTION
    |--------------------------------------------------------------------------
    */

    suspend fun getMerchantSuggestion(
        merchantName: String
    ): Result<MerchantCategorySuggestion> {

        return try {

            val response =
                apiService.getMerchantCategorySuggestion(
                    authorization =
                        getAuthorizationHeader(),

                    merchantName =
                        merchantName
                )

            if (response.isSuccessful) {

                val body =
                    response.body()

                if (
                    body?.success == true &&
                    body.data != null
                ) {

                    Result.success(
                        body.data
                    )

                } else {

                    Result.failure(
                        Exception(
                            body?.message
                                ?: "Không thể lấy danh mục gợi ý."
                        )
                    )
                }

            } else {

                Result.failure(
                    Exception(
                        parseError(
                            response.errorBody()?.string(),
                            response.code()
                        )
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    /*
    |--------------------------------------------------------------------------
    | PAY QR
    |--------------------------------------------------------------------------
    */

    suspend fun payQr(
        request: QrPaymentRequest
    ): Result<QrPaymentResult> {

        return try {

            val response =
                apiService.payByQr(
                    authorization =
                        getAuthorizationHeader(),

                    request =
                        request
                )

            if (response.isSuccessful) {

                val body =
                    response.body()

                if (
                    body?.success == true &&
                    body.data != null
                ) {

                    Result.success(
                        body.data
                    )

                } else {

                    Result.failure(
                        Exception(
                            body?.message
                                ?: "Thanh toán QR thất bại."
                        )
                    )
                }

            } else {

                Result.failure(
                    Exception(
                        parseError(
                            response.errorBody()?.string(),
                            response.code()
                        )
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    /*
    |--------------------------------------------------------------------------
    | ERROR
    |--------------------------------------------------------------------------
    */

    private fun parseError(
        errorBody: String?,
        httpCode: Int
    ): String {

        if (errorBody.isNullOrBlank()) {

            return "Lỗi HTTP $httpCode"
        }

        return try {

            val apiError =
                Gson().fromJson(
                    errorBody,
                    ApiResponse::class.java
                )

            apiError.message
                ?: "Lỗi HTTP $httpCode"

        } catch (_: Exception) {

            errorBody
        }
    }
}