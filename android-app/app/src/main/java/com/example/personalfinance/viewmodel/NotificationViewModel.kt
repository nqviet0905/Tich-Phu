package com.example.personalfinance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinance.data.model.AppNotification
import com.example.personalfinance.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class NotificationUiState(

    val isLoading: Boolean = false,

    val isUpdating: Boolean = false,

    val notifications: List<AppNotification> =
        emptyList(),

    val error: String? = null
)


class NotificationViewModel : ViewModel() {

    private val repository =
        NotificationRepository()


    private val _uiState =
        MutableStateFlow(
            NotificationUiState()
        )


    val uiState:
            StateFlow<NotificationUiState> =
        _uiState.asStateFlow()


    init {

        loadNotifications()
    }


    /*
    |--------------------------------------------------------------------------
    | LOAD NOTIFICATIONS
    |--------------------------------------------------------------------------
    */

    fun loadNotifications() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(

                    isLoading =
                        true,

                    error =
                        null
                )


            repository
                .getNotifications()
                .onSuccess { notifications ->

                    _uiState.value =
                        _uiState.value.copy(

                            isLoading =
                                false,

                            notifications =
                                notifications,

                            error =
                                null
                        )
                }
                .onFailure { exception ->

                    _uiState.value =
                        _uiState.value.copy(

                            isLoading =
                                false,

                            error =
                                exception.message
                                    ?: "Không thể tải thông báo."
                        )
                }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | MARK ONE AS READ
    |--------------------------------------------------------------------------
    */

    fun markAsRead(
        id: Long
    ) {

        /*
        |--------------------------------------------------------------------------
        | TÌM THÔNG BÁO
        |--------------------------------------------------------------------------
        */

        val notification =
            _uiState.value
                .notifications
                .firstOrNull {
                    it.id == id
                }


        /*
        |--------------------------------------------------------------------------
        | NẾU KHÔNG TỒN TẠI HOẶC ĐÃ ĐỌC
        |--------------------------------------------------------------------------
        */

        if (
            notification == null ||
            notification.is_read == 1
        ) {

            return
        }


        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(

                    isUpdating =
                        true,

                    error =
                        null
                )


            repository
                .readNotification(
                    id
                )
                .onSuccess {

                    val updatedNotifications =
                        _uiState.value
                            .notifications
                            .map { item ->

                                if (
                                    item.id ==
                                    id
                                ) {

                                    item.copy(
                                        is_read = 1
                                    )

                                } else {

                                    item
                                }
                            }


                    _uiState.value =
                        _uiState.value.copy(

                            isUpdating =
                                false,

                            notifications =
                                updatedNotifications,

                            error =
                                null
                        )
                }
                .onFailure { exception ->

                    _uiState.value =
                        _uiState.value.copy(

                            isUpdating =
                                false,

                            error =
                                exception.message
                                    ?: "Không thể đánh dấu thông báo đã đọc."
                        )
                }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | MARK ALL AS READ
    |--------------------------------------------------------------------------
    */

    fun markAllAsRead() {

        /*
        |--------------------------------------------------------------------------
        | KHÔNG CÓ THÔNG BÁO CHƯA ĐỌC
        |--------------------------------------------------------------------------
        */

        val hasUnread =
            _uiState.value
                .notifications
                .any {
                    it.is_read == 0
                }


        if (!hasUnread) {

            return
        }


        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(

                    isUpdating =
                        true,

                    error =
                        null
                )


            repository
                .readAll()
                .onSuccess {

                    val updatedNotifications =
                        _uiState.value
                            .notifications
                            .map { notification ->

                                notification.copy(
                                    is_read = 1
                                )
                            }


                    _uiState.value =
                        _uiState.value.copy(

                            isUpdating =
                                false,

                            notifications =
                                updatedNotifications,

                            error =
                                null
                        )
                }
                .onFailure { exception ->

                    _uiState.value =
                        _uiState.value.copy(

                            isUpdating =
                                false,

                            error =
                                exception.message
                                    ?: "Không thể đánh dấu tất cả đã đọc."
                        )
                }
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
    | REFRESH
    |--------------------------------------------------------------------------
    */

    fun refresh() {

        loadNotifications()
    }
}