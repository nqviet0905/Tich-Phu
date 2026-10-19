package com.example.personalfinance.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.personalfinance.data.model.ApiResponse
import com.example.personalfinance.data.model.SyncUserRequest
import com.example.personalfinance.data.model.User
import com.example.personalfinance.data.remote.RetrofitClient
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val firebaseAuth =
        FirebaseAuth.getInstance()

    private val gson =
        Gson()

    /*
    |--------------------------------------------------------------------------
    | LOGIN EMAIL / PASSWORD
    |--------------------------------------------------------------------------
    */

    suspend fun login(
        email: String,
        password: String
    ): Result<String> {

        return try {

            firebaseAuth
                .signInWithEmailAndPassword(
                    email,
                    password
                )
                .await()

            Result.success(
                "Đăng nhập thành công."
            )

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    /*
    |--------------------------------------------------------------------------
    | REGISTER EMAIL / PASSWORD
    |--------------------------------------------------------------------------
    */

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<String> {

        return try {

            val result =
                firebaseAuth
                    .createUserWithEmailAndPassword(
                        email,
                        password
                    )
                    .await()

            val firebaseUser =
                result.user
                    ?: return Result.failure(
                        Exception(
                            "Không thể tạo tài khoản Firebase."
                        )
                    )

            val token =
                firebaseUser
                    .getIdToken(true)
                    .await()
                    .token
                    ?: return Result.failure(
                        Exception(
                            "Không lấy được Firebase ID Token."
                        )
                    )

            val response =
                RetrofitClient
                    .apiService
                    .syncUser(
                        authorization =
                            "Bearer $token",

                        request =
                            SyncUserRequest(
                                name =
                                    name.trim(),

                                email =
                                    email.trim()
                            )
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    "Đăng ký thành công."
                )

            } else {

                val errorBody =
                    try {

                        response
                            .errorBody()
                            ?.string()

                    } catch (_: Exception) {

                        null
                    }

                Result.failure(
                    Exception(
                        getErrorMessage(
                            httpCode =
                                response.code(),

                            rawError =
                                errorBody
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
    | GOOGLE SIGN-IN
    |--------------------------------------------------------------------------
    |
    | Google Account
    |       ↓
    | Credential Manager
    |       ↓
    | Google ID Token
    |       ↓
    | Firebase Authentication
    |       ↓
    | Firebase ID Token
    |       ↓
    | sync-user.php
    |       ↓
    | MySQL
    |
    |--------------------------------------------------------------------------
    */

    suspend fun signInWithGoogle(
        context: Context,
        serverClientId: String
    ): Result<User> {

        return try {

            /*
            |--------------------------------------------------------------------------
            | CREDENTIAL MANAGER
            |--------------------------------------------------------------------------
            */

            val credentialManager =
                CredentialManager.create(
                    context
                )

            /*
            |--------------------------------------------------------------------------
            | GOOGLE OPTION
            |--------------------------------------------------------------------------
            */

            val googleIdOption =
                GetGoogleIdOption
                    .Builder()
                    .setFilterByAuthorizedAccounts(
                        false
                    )
                    .setServerClientId(
                        serverClientId
                    )
                    .setAutoSelectEnabled(
                        false
                    )
                    .build()

            /*
            |--------------------------------------------------------------------------
            | REQUEST
            |--------------------------------------------------------------------------
            */

            val request =
                GetCredentialRequest
                    .Builder()
                    .addCredentialOption(
                        googleIdOption
                    )
                    .build()

            /*
            |--------------------------------------------------------------------------
            | MỞ GOOGLE ACCOUNT PICKER
            |--------------------------------------------------------------------------
            */

            val result =
                credentialManager
                    .getCredential(
                        context =
                            context,

                        request =
                            request
                    )

            val credential =
                result.credential

            /*
            |--------------------------------------------------------------------------
            | KIỂM TRA GOOGLE CREDENTIAL
            |--------------------------------------------------------------------------
            */

            if (
                credential !is CustomCredential
            ) {

                return Result.failure(
                    Exception(
                        "Thông tin đăng nhập Google không hợp lệ."
                    )
                )
            }

            if (
                credential.type !=
                GoogleIdTokenCredential
                    .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {

                return Result.failure(
                    Exception(
                        "Loại tài khoản Google không được hỗ trợ."
                    )
                )
            }

            /*
            |--------------------------------------------------------------------------
            | LẤY GOOGLE ID TOKEN
            |--------------------------------------------------------------------------
            */

            val googleIdTokenCredential =
                GoogleIdTokenCredential
                    .createFrom(
                        credential.data
                    )

            val googleIdToken =
                googleIdTokenCredential.idToken

            if (
                googleIdToken.isBlank()
            ) {

                return Result.failure(
                    Exception(
                        "Không lấy được Google ID Token."
                    )
                )
            }

            /*
            |--------------------------------------------------------------------------
            | GOOGLE -> FIREBASE
            |--------------------------------------------------------------------------
            */

            val firebaseCredential =
                GoogleAuthProvider
                    .getCredential(
                        googleIdToken,
                        null
                    )

            val authResult =
                firebaseAuth
                    .signInWithCredential(
                        firebaseCredential
                    )
                    .await()

            val firebaseUser =
                authResult.user
                    ?: return Result.failure(
                        Exception(
                            "Không lấy được tài khoản Firebase."
                        )
                    )

            /*
            |--------------------------------------------------------------------------
            | KIỂM TRA EMAIL
            |--------------------------------------------------------------------------
            */

            if (
                firebaseUser.email
                    .isNullOrBlank()
            ) {

                firebaseAuth.signOut()

                return Result.failure(
                    Exception(
                        "Tài khoản Google không cung cấp email."
                    )
                )
            }

            /*
            |--------------------------------------------------------------------------
            | SYNC FIREBASE -> MYSQL
            |--------------------------------------------------------------------------
            */

            val syncResult =
                syncCurrentUser()

            if (
                syncResult.isSuccess
            ) {

                Result.success(
                    syncResult.getOrThrow()
                )

            } else {

                Result.failure(
                    syncResult.exceptionOrNull()
                        ?: Exception(
                            "Không thể đồng bộ tài khoản Google."
                        )
                )
            }

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    googleErrorMessage(
                        e
                    ),
                    e
                )
            )
        }
    }

    /*
    |--------------------------------------------------------------------------
    | RESET PASSWORD
    |--------------------------------------------------------------------------
    */

    suspend fun sendPasswordReset(
        email: String
    ): Result<String> {

        return try {

            val cleanEmail =
                email.trim().lowercase()

            if (cleanEmail.isBlank()) {

                return Result.failure(
                    Exception(
                        "Vui lòng nhập email."
                    )
                )
            }


            val response =
                RetrofitClient
                    .apiService
                    .forgotPassword(
                        request =
                            mapOf(
                                "email" to cleanEmail
                            )
                    )


            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.message
                        ?: "Đã gửi liên kết đặt lại mật khẩu đến email của bạn."
                )

            } else {

                val errorBody =
                    try {

                        response
                            .errorBody()
                            ?.string()

                    } catch (_: Exception) {

                        null
                    }


                Result.failure(
                    Exception(
                        getErrorMessage(
                            httpCode =
                                response.code(),
                            rawError =
                                errorBody
                        )
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
    | SYNC FIREBASE -> MYSQL
    |--------------------------------------------------------------------------
    */

    suspend fun syncCurrentUser():
            Result<User> {

        return try {

            val firebaseUser =
                firebaseAuth.currentUser
                    ?: return Result.failure(
                        Exception(
                            "Chưa đăng nhập Firebase."
                        )
                    )

            /*
            |--------------------------------------------------------------------------
            | FIREBASE TOKEN
            |--------------------------------------------------------------------------
            */

            val tokenResult =
                firebaseUser
                    .getIdToken(true)
                    .await()

            val idToken =
                tokenResult.token
                    ?: return Result.failure(
                        Exception(
                            "Không lấy được Firebase ID Token."
                        )
                    )

            /*
            |--------------------------------------------------------------------------
            | EMAIL
            |--------------------------------------------------------------------------
            */

            val email =
                firebaseUser.email
                    ?.trim()
                    ?.takeIf {
                        it.isNotEmpty()
                    }
                    ?: return Result.failure(
                        Exception(
                            "Tài khoản Firebase không có email."
                        )
                    )

            /*
            |--------------------------------------------------------------------------
            | NAME
            |--------------------------------------------------------------------------
            |
            | Google:
            | firebaseUser.displayName
            |
            | Email/password:
            | nếu không có displayName -> lấy phần trước @
            |
            |--------------------------------------------------------------------------
            */

            val name =
                firebaseUser
                    .displayName
                    ?.trim()
                    ?.takeIf {
                        it.isNotEmpty()
                    }
                    ?: email
                        .substringBefore("@")
                        .trim()

            /*
            |--------------------------------------------------------------------------
            | BACKEND SYNC
            |--------------------------------------------------------------------------
            */

            val response =
                RetrofitClient
                    .apiService
                    .syncUser(
                        authorization =
                            "Bearer $idToken",

                        request =
                            SyncUserRequest(
                                name =
                                    name,

                                email =
                                    email
                            )
                    )

            /*
            |--------------------------------------------------------------------------
            | HTTP SUCCESS
            |--------------------------------------------------------------------------
            */

            if (
                response.isSuccessful
            ) {

                val body =
                    response.body()

                if (
                    body == null
                ) {

                    return Result.failure(
                        Exception(
                            "Backend không trả về dữ liệu."
                        )
                    )
                }

                if (
                    !body.success
                ) {

                    return Result.failure(
                        Exception(
                            body.message
                        )
                    )
                }

                val user =
                    body.data

                if (
                    user == null
                ) {

                    return Result.failure(
                        Exception(
                            "Backend đồng bộ thành công nhưng không trả về người dùng."
                        )
                    )
                }

                Result.success(
                    user
                )

            } else {

                /*
                |--------------------------------------------------------------------------
                | HTTP 400 / 401 / 404 / 500
                |--------------------------------------------------------------------------
                */

                val errorBody =
                    try {

                        response
                            .errorBody()
                            ?.string()

                    } catch (_: Exception) {

                        null
                    }

                Result.failure(
                    Exception(
                        getErrorMessage(
                            httpCode =
                                response.code(),

                            rawError =
                                errorBody
                        )
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "Không thể đồng bộ tài khoản.",
                    e
                )
            )
        }
    }

    /*
    |--------------------------------------------------------------------------
    | ĐỌC LỖI BACKEND
    |--------------------------------------------------------------------------
    */

    private fun getErrorMessage(
        httpCode: Int,
        rawError: String?
    ): String {

        if (
            !rawError.isNullOrBlank()
        ) {

            try {

                val json =
                    gson.fromJson(
                        rawError,
                        Map::class.java
                    )

                val message =
                    json["message"]
                        ?.toString()
                        ?.trim()

                if (
                    !message.isNullOrBlank()
                ) {

                    return message
                }

            } catch (_: Exception) {
            }

            return "HTTP $httpCode: $rawError"
        }

        return "HTTP $httpCode - Đồng bộ tài khoản thất bại."
    }

    /*
    |--------------------------------------------------------------------------
    | GOOGLE ERROR MESSAGE
    |--------------------------------------------------------------------------
    */

    private fun googleErrorMessage(
        exception: Exception
    ): String {

        val message =
            exception.message
                ?.trim()

        /*
         * Khi người dùng đóng cửa sổ
         * chọn tài khoản Google.
         */
        if (
            exception::class
                .java
                .simpleName
                .contains(
                    "Cancellation",
                    ignoreCase = true
                )
        ) {

            return "Bạn đã hủy đăng nhập bằng Google."
        }

        if (
            !message.isNullOrBlank()
        ) {

            return message
        }

        return "Không thể đăng nhập bằng Google."
    }

    /*
    |--------------------------------------------------------------------------
    | LOGOUT
    |--------------------------------------------------------------------------
    */

    fun logout() {

        firebaseAuth.signOut()
    }

    /*
    |--------------------------------------------------------------------------
    | CHECK LOGIN
    |--------------------------------------------------------------------------
    */

    fun isLoggedIn(): Boolean {

        return firebaseAuth
            .currentUser != null
    }
}