package com.example.personalfinance.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinance.data.model.User
import com.example.personalfinance.data.repository.AuthRepository
import com.example.personalfinance.utils.FcmTokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val message: String? = null,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    private val repository =
        AuthRepository()

    private val _uiState =
        MutableStateFlow(
            AuthUiState(
                isLoggedIn =
                    repository.isLoggedIn()
            )
        )

    val uiState: StateFlow<AuthUiState> =
        _uiState.asStateFlow()

    private val _registerMessage =
        MutableStateFlow<String?>(null)

    val registerMessage: StateFlow<String?> =
        _registerMessage.asStateFlow()

    private val _resetMessage =
        MutableStateFlow<String?>(null)

    val resetMessage: StateFlow<String?> =
        _resetMessage.asStateFlow()

    /*
    |--------------------------------------------------------------------------
    | LOGIN EMAIL / PASSWORD
    |--------------------------------------------------------------------------
    */

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    message = null,
                    error = null
                )

            val loginResult =
                repository.login(
                    email = email,
                    password = password
                )

            if (
                loginResult.isFailure
            ) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        user = null,
                        error =
                            loginResult
                                .exceptionOrNull()
                                ?.message
                                ?: "Đăng nhập thất bại."
                    )

                return@launch
            }

            /*
            |--------------------------------------------------------------------------
            | SYNC MYSQL
            |--------------------------------------------------------------------------
            */

            val syncResult =
                repository
                    .syncCurrentUser()

            if (
                syncResult.isSuccess
            ) {

                val user =
                    syncResult.getOrNull()

                _uiState.value =
                    AuthUiState(
                        isLoading = false,
                        isLoggedIn = true,
                        user = user,
                        message = null,
                        error = null
                    )

                registerFcmToken()

                onSuccess()

            } else {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        error =
                            syncResult
                                .exceptionOrNull()
                                ?.message
                                ?: "Đăng nhập thành công nhưng không thể đồng bộ tài khoản."
                    )
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | GOOGLE SIGN-IN
    |--------------------------------------------------------------------------
    */

    fun signInWithGoogle(
        context: Context,
        serverClientId: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            /*
            |--------------------------------------------------------------------------
            | RESET STATE
            |--------------------------------------------------------------------------
            */

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    message = null,
                    error = null
                )

            /*
            |--------------------------------------------------------------------------
            | GOOGLE -> FIREBASE -> MYSQL
            |--------------------------------------------------------------------------
            */

            val result =
                repository
                    .signInWithGoogle(
                        context =
                            context,

                        serverClientId =
                            serverClientId
                    )

            /*
            |--------------------------------------------------------------------------
            | SUCCESS
            |--------------------------------------------------------------------------
            */

            result.onSuccess { user ->

                _uiState.value =
                    AuthUiState(
                        isLoading = false,
                        isLoggedIn = true,
                        user = user,
                        message = null,
                        error = null
                    )

                /*
                |--------------------------------------------------------------------------
                | FCM TOKEN
                |--------------------------------------------------------------------------
                */

                registerFcmToken()

                /*
                |--------------------------------------------------------------------------
                | NAVIGATION
                |--------------------------------------------------------------------------
                */

                onSuccess()
            }

            /*
            |--------------------------------------------------------------------------
            | FAILURE
            |--------------------------------------------------------------------------
            */

            result.onFailure { exception ->

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        user = null,
                        error =
                            exception.message
                                ?: "Không thể đăng nhập bằng Google."
                    )
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | REGISTER EMAIL / PASSWORD
    |--------------------------------------------------------------------------
    */

    fun register(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            _registerMessage.value =
                null

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    message = null,
                    error = null
                )

            val result =
                repository.register(
                    name = name,
                    email = email,
                    password = password
                )

            /*
            |--------------------------------------------------------------------------
            | SUCCESS
            |--------------------------------------------------------------------------
            */

            result.onSuccess { message ->

                _registerMessage.value =
                    message

                /*
                 * Repository.register() đã:
                 *
                 * Firebase register
                 *        ↓
                 * sync-user.php
                 *        ↓
                 * MySQL
                 *
                 * Nhưng để lấy User đầy đủ vào state,
                 * ta sync lại một lần.
                 */

                val syncResult =
                    repository
                        .syncCurrentUser()

                if (
                    syncResult.isSuccess
                ) {

                    val user =
                        syncResult.getOrNull()

                    _uiState.value =
                        AuthUiState(
                            isLoading = false,
                            isLoggedIn = true,
                            user = user,
                            message = null,
                            error = null
                        )

                    registerFcmToken()

                    onSuccess()

                } else {

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            error =
                                syncResult
                                    .exceptionOrNull()
                                    ?.message
                                    ?: "Đăng ký thành công nhưng không thể tải thông tin tài khoản."
                        )
                }
            }

            /*
            |--------------------------------------------------------------------------
            | FAILURE
            |--------------------------------------------------------------------------
            */

            result.onFailure { exception ->

                _registerMessage.value =
                    exception.message
                        ?: "Đăng ký thất bại."

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        user = null,
                        error =
                            exception.message
                                ?: "Đăng ký thất bại."
                    )
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | FORGOT PASSWORD
    |--------------------------------------------------------------------------
    */

    fun sendPasswordReset(
        email: String
    ) {

        viewModelScope.launch {

            _resetMessage.value =
                null

            val result =
                repository
                    .sendPasswordReset(
                        email = email
                    )

            result.onSuccess { message ->

                _resetMessage.value =
                    message
            }

            result.onFailure { exception ->

                _resetMessage.value =
                    exception.message
                        ?: "Không thể gửi email đặt lại mật khẩu."
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | SYNC FIREBASE -> MYSQL
    |--------------------------------------------------------------------------
    */

    fun syncUser() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    message = null,
                    error = null
                )

            val result =
                repository
                    .syncCurrentUser()

            result.onSuccess { user ->

                _uiState.value =
                    AuthUiState(
                        isLoading = false,
                        isLoggedIn = true,
                        user = user,
                        message =
                            "Đồng bộ tài khoản thành công.",
                        error = null
                    )
            }

            result.onFailure { exception ->

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error =
                            exception.message
                                ?: "Không thể đồng bộ tài khoản."
                    )
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | REGISTER FCM TOKEN
    |--------------------------------------------------------------------------
    */

    private suspend fun registerFcmToken() {

        try {

            FcmTokenManager
                .registerCurrentDevice()

        } catch (_: Exception) {
        }
    }

    /*
    |--------------------------------------------------------------------------
    | CLEAR ERROR
    |--------------------------------------------------------------------------
    */

    fun clearError() {

        _uiState.value =
            _uiState.value.copy(
                error = null
            )
    }

    /*
    |--------------------------------------------------------------------------
    | CLEAR REGISTER MESSAGE
    |--------------------------------------------------------------------------
    */

    fun clearRegisterMessage() {

        _registerMessage.value =
            null
    }

    /*
    |--------------------------------------------------------------------------
    | CLEAR RESET MESSAGE
    |--------------------------------------------------------------------------
    */

    fun clearResetMessage() {

        _resetMessage.value =
            null
    }

    /*
    |--------------------------------------------------------------------------
    | LOGOUT
    |--------------------------------------------------------------------------
    */

    fun logout() {

        repository.logout()

        _uiState.value =
            AuthUiState(
                isLoading = false,
                isLoggedIn = false,
                user = null,
                message = null,
                error = null
            )

        _registerMessage.value =
            null

        _resetMessage.value =
            null
    }
}