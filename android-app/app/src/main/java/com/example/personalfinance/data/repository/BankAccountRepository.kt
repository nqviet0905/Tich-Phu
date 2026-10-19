package com.example.personalfinance.data.repository

import com.example.personalfinance.data.model.ApiResponse
import com.example.personalfinance.data.model.BankAccount
import com.example.personalfinance.data.model.BankAccountActionData
import com.example.personalfinance.data.model.BankAccountActionRequest
import com.example.personalfinance.data.model.CreateBankAccountRequest
import com.example.personalfinance.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

class BankAccountRepository {

    private val apiService =
        RetrofitClient.apiService

    private val firebaseAuth =
        FirebaseAuth.getInstance()


    /*
    |--------------------------------------------------------------------------
    | TOKEN
    |--------------------------------------------------------------------------
    */

    private suspend fun getAuthorizationHeader(): String {

        val user =
            firebaseAuth.currentUser
                ?: throw Exception(
                    "Bạn chưa đăng nhập."
                )

        val token =
            user.getIdToken(true)
                .await()
                .token
                ?: throw Exception(
                    "Không thể lấy token đăng nhập."
                )

        return "Bearer $token"
    }


    /*
    |--------------------------------------------------------------------------
    | LIST
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
                                ?: "Không thể lấy danh sách tài khoản."
                        )
                    )
                }

            } else {

                Result.failure(
                    Exception(
                        getErrorMessage(
                            response.errorBody()
                                ?.string(),
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
    | CREATE
    |--------------------------------------------------------------------------
    */

    suspend fun createBankAccount(
        bankCode: String,
        bankName: String,
        accountNumber: String,
        accountName: String,
        isDefault: Boolean
    ): Result<BankAccount> {

        return try {

            val request =
                CreateBankAccountRequest(
                    bankCode = bankCode.trim(),
                    bankName = bankName.trim(),
                    accountNumber =
                        accountNumber
                            .replace(" ", "")
                            .trim(),
                    accountName =
                        accountName.trim(),
                    isDefault =
                        isDefault
                )

            val response =
                apiService.createBankAccount(
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
                                ?: "Liên kết tài khoản thất bại."
                        )
                    )
                }

            } else {

                Result.failure(
                    Exception(
                        getErrorMessage(
                            response.errorBody()
                                ?.string(),
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
    | SET DEFAULT
    |--------------------------------------------------------------------------
    */

    suspend fun setDefaultBankAccount(
        bankAccountId: Long
    ): Result<BankAccountActionData> {

        return try {

            val response =
                apiService.setDefaultBankAccount(
                    authorization =
                        getAuthorizationHeader(),

                    request =
                        BankAccountActionRequest(
                            bankAccountId
                        )
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
                                ?: "Không thể đặt tài khoản mặc định."
                        )
                    )
                }

            } else {

                Result.failure(
                    Exception(
                        getErrorMessage(
                            response.errorBody()
                                ?.string(),
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
    | DELETE
    |--------------------------------------------------------------------------
    */

    suspend fun deleteBankAccount(
        bankAccountId: Long
    ): Result<BankAccountActionData> {

        return try {

            val response =
                apiService.deleteBankAccount(
                    authorization =
                        getAuthorizationHeader(),

                    request =
                        BankAccountActionRequest(
                            bankAccountId
                        )
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
                                ?: "Không thể hủy liên kết."
                        )
                    )
                }

            } else {

                Result.failure(
                    Exception(
                        getErrorMessage(
                            response.errorBody()
                                ?.string(),
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
    | ERROR PARSER
    |--------------------------------------------------------------------------
    */

    private fun getErrorMessage(
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