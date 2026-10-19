package com.example.personalfinance.data.repository

import com.example.personalfinance.data.model.DepositData
import com.example.personalfinance.data.model.DepositRequest
import com.example.personalfinance.data.model.RecipientData
import com.example.personalfinance.data.model.TransferData
import com.example.personalfinance.data.model.TransferRequest
import com.example.personalfinance.data.model.WalletBalanceData
import com.example.personalfinance.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import org.json.JSONObject


class WalletRepository {

    /*
    |--------------------------------------------------------------------------
    | TOKEN
    |--------------------------------------------------------------------------
    */

    private suspend fun getAuthorizationToken(): String {

        val user =
            FirebaseAuth
                .getInstance()
                .currentUser
                ?: throw Exception(
                    "Chưa đăng nhập."
                )

        val token =
            user
                .getIdToken(false)
                .await()
                .token
                ?: throw Exception(
                    "Không lấy được token."
                )

        return "Bearer $token"
    }


    /*
    |--------------------------------------------------------------------------
    | PARSE ERROR BODY
    |--------------------------------------------------------------------------
    */

    private fun parseErrorMessage(
        errorBody: String?,
        fallback: String
    ): String {

        if (errorBody.isNullOrBlank()) {
            return fallback
        }

        return try {

            val json =
                JSONObject(
                    errorBody
                )

            json.optString(
                "message",
                fallback
            ).takeIf {
                it.isNotBlank()
            } ?: fallback

        } catch (_: Exception) {

            fallback
        }
    }


    /*
    |--------------------------------------------------------------------------
    | BALANCE
    |--------------------------------------------------------------------------
    */

    suspend fun getBalance():
            Result<WalletBalanceData> {

        return try {

            val authorization =
                getAuthorizationToken()

            val response =
                RetrofitClient
                    .apiService
                    .getWalletBalance(
                        authorization =
                            authorization
                    )


            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                val data =
                    response.body()?.data
                        ?: return Result.failure(
                            Exception(
                                "Không nhận được thông tin số dư."
                            )
                        )

                Result.success(
                    data
                )

            } else {

                val message =
                    parseErrorMessage(
                        errorBody =
                            response
                                .errorBody()
                                ?.string(),
                        fallback =
                            response
                                .body()
                                ?.message
                                ?: "Không thể lấy số dư."
                    )

                Result.failure(
                    Exception(
                        message
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(
                e
            )
        }
    }


    /*
    |--------------------------------------------------------------------------
    | RECIPIENT
    |--------------------------------------------------------------------------
    */

    suspend fun getRecipient(
        accountNumber: String
    ): Result<RecipientData> {

        return try {

            val authorization =
                getAuthorizationToken()

            val cleanAccountNumber =
                accountNumber.filter {
                    it.isDigit()
                }


            if (
                cleanAccountNumber.isBlank()
            ) {

                return Result.failure(
                    Exception(
                        "Số tài khoản không hợp lệ."
                    )
                )
            }


            val response =
                RetrofitClient
                    .apiService
                    .getRecipient(
                        authorization =
                            authorization,
                        accountNumber =
                            cleanAccountNumber
                    )


            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                val data =
                    response.body()?.data
                        ?: return Result.failure(
                            Exception(
                                "Không nhận được thông tin người nhận."
                            )
                        )

                Result.success(
                    data
                )

            } else {

                val message =
                    parseErrorMessage(
                        errorBody =
                            response
                                .errorBody()
                                ?.string(),
                        fallback =
                            response
                                .body()
                                ?.message
                                ?: "Không tìm thấy tài khoản người nhận."
                    )

                Result.failure(
                    Exception(
                        message
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(
                e
            )
        }
    }


    /*
    |--------------------------------------------------------------------------
    | DEPOSIT
    |--------------------------------------------------------------------------
    */

    suspend fun deposit(
        amount: Double,
        method: String,
        note: String?
    ): Result<DepositData> {

        return try {

            val authorization =
                getAuthorizationToken()


            val response =
                RetrofitClient
                    .apiService
                    .deposit(
                        authorization =
                            authorization,
                        request =
                            DepositRequest(
                                amount =
                                    amount,
                                method =
                                    method,
                                note =
                                    note
                            )
                    )


            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                val data =
                    response.body()?.data
                        ?: return Result.failure(
                            Exception(
                                "Không nhận được kết quả nạp tiền."
                            )
                        )

                Result.success(
                    data
                )

            } else {

                val message =
                    parseErrorMessage(
                        errorBody =
                            response
                                .errorBody()
                                ?.string(),
                        fallback =
                            response
                                .body()
                                ?.message
                                ?: "Nạp tiền thất bại."
                    )

                Result.failure(
                    Exception(
                        message
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(
                e
            )
        }
    }


    /*
    |--------------------------------------------------------------------------
    | TRANSFER
    |--------------------------------------------------------------------------
    */

    suspend fun transfer(
        receiver: String,
        amount: Double,
        note: String?
    ): Result<TransferData> {

        return try {

            val authorization =
                getAuthorizationToken()

            val cleanReceiver =
                receiver.filter {
                    it.isDigit()
                }


            if (
                cleanReceiver.isBlank()
            ) {

                return Result.failure(
                    Exception(
                        "Số tài khoản người nhận không hợp lệ."
                    )
                )
            }


            val response =
                RetrofitClient
                    .apiService
                    .transfer(
                        authorization =
                            authorization,
                        request =
                            TransferRequest(
                                receiver =
                                    cleanReceiver,
                                amount =
                                    amount,
                                note =
                                    note
                            )
                    )


            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                val data =
                    response.body()?.data
                        ?: return Result.failure(
                            Exception(
                                "Không nhận được kết quả chuyển tiền."
                            )
                        )

                Result.success(
                    data
                )

            } else {

                val message =
                    parseErrorMessage(
                        errorBody =
                            response
                                .errorBody()
                                ?.string(),
                        fallback =
                            response
                                .body()
                                ?.message
                                ?: "Chuyển tiền thất bại."
                    )

                Result.failure(
                    Exception(
                        message
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(
                e
            )
        }
    }
}