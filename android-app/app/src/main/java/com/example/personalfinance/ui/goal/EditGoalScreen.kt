package com.example.personalfinance.ui.goal

import android.app.DatePickerDialog
import android.content.Context

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Flag

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.personalfinance.viewmodel.GoalViewModel

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun EditGoalScreen(
    goalId: Long,
    onBack: () -> Unit,
    goalViewModel: GoalViewModel = viewModel()
) {

    val context =
        LocalContext.current

    val goal by
    goalViewModel.selectedGoal.collectAsState()

    val message by
    goalViewModel.message.collectAsState()


    val formatter =
        remember {
            NumberFormat.getNumberInstance(
                Locale(
                    "vi",
                    "VN"
                )
            )
        }


    /*
    |--------------------------------------------------------------------------
    | STATE
    |--------------------------------------------------------------------------
    */

    var name by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var targetAmount by remember {
        mutableStateOf("")
    }

    var startDate by remember {
        mutableStateOf("")
    }

    var deadline by remember {
        mutableStateOf("")
    }

    var initialized by remember {
        mutableStateOf(false)
    }

    var localMessage by remember {
        mutableStateOf<String?>(null)
    }


    /*
    |--------------------------------------------------------------------------
    | LOAD GOAL
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(goalId) {

        initialized =
            false

        goalViewModel.loadGoalDetail(
            goalId
        )
    }


    /*
    |--------------------------------------------------------------------------
    | FILL DATA
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(goal) {

        val data =
            goal

        if (
            data != null &&
            !initialized
        ) {

            name =
                data.name

            description =
                data.description
                    ?: ""

            targetAmount =
                formatEditAmountInput(
                    data.target_amount
                )

            startDate =
                data.start_date

            deadline =
                data.deadline

            initialized =
                true
        }
    }


    /*
    |--------------------------------------------------------------------------
    | LOADING
    |--------------------------------------------------------------------------
    */

    if (
        goal == null ||
        !initialized
    ) {

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme
                            .colorScheme
                            .background
                    ),
            contentAlignment =
                Alignment.Center
        ) {

            CircularProgressIndicator(
                color =
                    MaterialTheme
                        .colorScheme
                        .primary
            )
        }

        return
    }


    val data =
        goal!!


    val targetValue =
        targetAmount
            .toDoubleOrNull()
            ?: 0.0


    /*
    |--------------------------------------------------------------------------
    | SCREEN
    |--------------------------------------------------------------------------
    */

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                )
                .imePadding(),
        contentPadding =
            PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 16.dp,
                bottom = 36.dp
            ),
        verticalArrangement =
            Arrangement.spacedBy(
                14.dp
            )
    ) {


        /*
        |--------------------------------------------------------------------------
        | HEADER
        |--------------------------------------------------------------------------
        */

        item {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(
                            44.dp
                        ),
                    shape =
                        CircleShape,
                    color =
                        MaterialTheme
                            .colorScheme
                            .surfaceVariant
                            .copy(
                                alpha = 0.65f
                            )
                ) {

                    IconButton(
                        onClick =
                            onBack
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored
                                    .Filled
                                    .ArrowBack,
                            contentDescription =
                                "Quay lại"
                        )
                    }
                }


                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(
                                start = 14.dp
                            )
                ) {

                    Text(
                        text =
                            "Chỉnh sửa mục tiêu",
                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onBackground
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                3.dp
                            )
                    )


                    Text(
                        text =
                            "Cập nhật thông tin và thời gian hoàn thành",
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | PREVIEW
        |--------------------------------------------------------------------------
        */

        item {

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(
                        24.dp
                    ),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                    )
            ) {

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                18.dp
                            ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Surface(
                        modifier =
                            Modifier.size(
                                52.dp
                            ),
                        shape =
                            RoundedCornerShape(
                                17.dp
                            ),
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                                .copy(
                                    alpha = 0.12f
                                )
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Edit,
                                contentDescription =
                                    null,
                                tint =
                                    MaterialTheme
                                        .colorScheme
                                        .primary,
                                modifier =
                                    Modifier.size(
                                        26.dp
                                    )
                            )
                        }
                    }


                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(
                                    start = 14.dp
                                )
                    ) {

                        Text(
                            text =
                                name.ifBlank {
                                    "Mục tiêu"
                                },
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurface
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    3.dp
                                )
                        )


                        Text(
                            text =
                                if (
                                    targetValue > 0
                                ) {

                                    "${
                                        formatter.format(
                                            targetValue
                                        )
                                    } đ"

                                } else {

                                    "0 đ"
                                },
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    3.dp
                                )
                        )


                        Text(
                            text =
                                "Đã tích lũy ${
                                    formatter.format(
                                        data.current_amount
                                    )
                                } đ",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | BASIC INFORMATION
        |--------------------------------------------------------------------------
        */

        item {

            EditGoalFormCard(
                title =
                    "Thông tin mục tiêu",
                icon =
                    Icons.Default.Flag
            ) {

                OutlinedTextField(
                    value =
                        name,
                    onValueChange = {

                        name =
                            it

                        localMessage =
                            null
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {

                        Text(
                            "Tên mục tiêu"
                        )
                    },
                    placeholder = {

                        Text(
                            "Ví dụ: Mua laptop mới"
                        )
                    },
                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.Flag,
                            contentDescription =
                                null
                        )
                    },
                    singleLine =
                        true,
                    shape =
                        RoundedCornerShape(
                            16.dp
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )


                OutlinedTextField(
                    value =
                        description,
                    onValueChange = {

                        description =
                            it
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {

                        Text(
                            "Mô tả"
                        )
                    },
                    placeholder = {

                        Text(
                            "Mô tả ngắn về mục tiêu"
                        )
                    },
                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.EditNote,
                            contentDescription =
                                null
                        )
                    },
                    minLines =
                        2,
                    maxLines =
                        3,
                    shape =
                        RoundedCornerShape(
                            16.dp
                        )
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | TARGET
        |--------------------------------------------------------------------------
        */

        item {

            EditGoalFormCard(
                title =
                    "Kế hoạch tích lũy",
                icon =
                    Icons.Default
                        .AccountBalanceWallet
            ) {

                OutlinedTextField(
                    value =
                        targetAmount,
                    onValueChange = { value ->

                        targetAmount =
                            value.filter {
                                it.isDigit()
                            }

                        localMessage =
                            null
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {

                        Text(
                            "Số tiền mục tiêu"
                        )
                    },
                    placeholder = {

                        Text(
                            "Ví dụ: 20000000"
                        )
                    },
                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default
                                    .AccountBalanceWallet,
                            contentDescription =
                                null
                        )
                    },
                    suffix = {

                        Text(
                            "đ"
                        )
                    },
                    singleLine =
                        true,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),
                    shape =
                        RoundedCornerShape(
                            16.dp
                        )
                )


                if (
                    targetValue > 0
                ) {

                    Text(
                        text =
                            "${
                                formatter.format(
                                    targetValue
                                )
                            } đ",
                        modifier =
                            Modifier.padding(
                                start = 4.dp,
                                top = 5.dp
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary,
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            14.dp
                        )
                )


                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            15.dp
                        ),
                    color =
                        MaterialTheme
                            .colorScheme
                            .surfaceVariant
                            .copy(
                                alpha = 0.55f
                            )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(
                                14.dp
                            )
                    ) {

                        Text(
                            text =
                                "Đã tích lũy",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    3.dp
                                )
                        )


                        Text(
                            text =
                                "${
                                    formatter.format(
                                        data.current_amount
                                    )
                                } đ",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurface
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    4.dp
                                )
                        )


                        Text(
                            text =
                                "Số tiền đã tích lũy được quản lý qua lịch sử đóng góp.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | DATE
        |--------------------------------------------------------------------------
        */

        item {

            EditGoalFormCard(
                title =
                    "Thời gian thực hiện",
                icon =
                    Icons.Default.CalendarMonth
            ) {


                /*
                |--------------------------------------------------------------------------
                | START DATE
                |--------------------------------------------------------------------------
                */

                Text(
                    text =
                        "Ngày bắt đầu",
                    style =
                        MaterialTheme
                            .typography
                            .labelLarge,
                    fontWeight =
                        FontWeight.SemiBold
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            7.dp
                        )
                )


                OutlinedButton(
                    onClick = {

                        showEditGoalDatePicker(
                            context =
                                context,
                            currentDate =
                                startDate,
                            onDateSelected = {
                                    selectedDate ->

                                startDate =
                                    selectedDate

                                localMessage =
                                    null
                            }
                        )
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(
                                56.dp
                            ),
                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),
                    contentPadding =
                        PaddingValues(
                            horizontal = 16.dp
                        )
                ) {

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default
                                    .CalendarMonth,
                            contentDescription =
                                null,
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        )


                        Spacer(
                            modifier =
                                Modifier.size(
                                    12.dp
                                )
                        )


                        Text(
                            text =
                                if (
                                    startDate.isBlank()
                                ) {

                                    "Chọn ngày bắt đầu"

                                } else {

                                    formatEditGoalDate(
                                        startDate
                                    )
                                },
                            modifier =
                                Modifier.weight(
                                    1f
                                ),
                            color =
                                if (
                                    startDate.isBlank()
                                ) {

                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant

                                } else {

                                    MaterialTheme
                                        .colorScheme
                                        .onSurface
                                }
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )


                /*
                |--------------------------------------------------------------------------
                | DEADLINE
                |--------------------------------------------------------------------------
                */

                Text(
                    text =
                        "Hạn hoàn thành",
                    style =
                        MaterialTheme
                            .typography
                            .labelLarge,
                    fontWeight =
                        FontWeight.SemiBold
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            7.dp
                        )
                )


                OutlinedButton(
                    onClick = {

                        showEditGoalDatePicker(
                            context =
                                context,
                            currentDate =
                                deadline,
                            onDateSelected = {
                                    selectedDate ->

                                deadline =
                                    selectedDate

                                localMessage =
                                    null
                            }
                        )
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(
                                56.dp
                            ),
                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),
                    contentPadding =
                        PaddingValues(
                            horizontal = 16.dp
                        )
                ) {

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default
                                    .CalendarMonth,
                            contentDescription =
                                null,
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        )


                        Spacer(
                            modifier =
                                Modifier.size(
                                    12.dp
                                )
                        )


                        Text(
                            text =
                                if (
                                    deadline.isBlank()
                                ) {

                                    "Chọn hạn hoàn thành"

                                } else {

                                    formatEditGoalDate(
                                        deadline
                                    )
                                },
                            modifier =
                                Modifier.weight(
                                    1f
                                ),
                            color =
                                if (
                                    deadline.isBlank()
                                ) {

                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant

                                } else {

                                    MaterialTheme
                                        .colorScheme
                                        .onSurface
                                }
                        )
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | LOCAL MESSAGE
        |--------------------------------------------------------------------------
        */

        if (
            !localMessage.isNullOrBlank()
        ) {

            item {

                EditGoalMessageCard(
                    text =
                        localMessage ?: ""
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | VIEW MODEL MESSAGE
        |--------------------------------------------------------------------------
        */

        if (
            !message.isNullOrBlank()
        ) {

            item {

                EditGoalMessageCard(
                    text =
                        message ?: ""
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | UPDATE BUTTON
        |--------------------------------------------------------------------------
        */

        item {

            Button(
                onClick = {

                    localMessage =
                        null


                    val target =
                        targetAmount
                            .toDoubleOrNull()


                    /*
                    |--------------------------------------------------------------------------
                    | VALIDATE NAME
                    |--------------------------------------------------------------------------
                    */

                    if (
                        name.isBlank()
                    ) {

                        localMessage =
                            "Vui lòng nhập tên mục tiêu."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | VALIDATE TARGET
                    |--------------------------------------------------------------------------
                    */

                    if (
                        target == null ||
                        target <= 0
                    ) {

                        localMessage =
                            "Số tiền mục tiêu phải lớn hơn 0."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | CURRENT > TARGET
                    |--------------------------------------------------------------------------
                    */

                    if (
                        target <
                        data.current_amount
                    ) {

                        localMessage =
                            "Số tiền mục tiêu không được nhỏ hơn số tiền đã tích lũy."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | DATE
                    |--------------------------------------------------------------------------
                    */

                    if (
                        startDate.isBlank() ||
                        deadline.isBlank()
                    ) {

                        localMessage =
                            "Vui lòng chọn đầy đủ thời gian thực hiện."

                        return@Button
                    }


                    if (
                        !isValidEditGoalDateRange(
                            startDate =
                                startDate,
                            deadline =
                                deadline
                        )
                    ) {

                        localMessage =
                            "Hạn hoàn thành phải bằng hoặc sau ngày bắt đầu."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | UPDATE
                    |--------------------------------------------------------------------------
                    */

                    goalViewModel.updateGoal(
                        id =
                            goalId,
                        name =
                            name.trim(),
                        description =
                            description
                                .trim()
                                .ifBlank {
                                    null
                                },
                        targetAmount =
                            target,
                        startDate =
                            startDate,
                        deadline =
                            deadline,
                        status =
                            data.status,
                        onSuccess = {

                            onBack()
                        }
                    )
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            56.dp
                        ),
                shape =
                    RoundedCornerShape(
                        18.dp
                    ),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )
            ) {

                Text(
                    text =
                        "Lưu thay đổi",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        4.dp
                    )
            )


            TextButton(
                onClick =
                    onBack,
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        "Hủy",
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| FORM CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun EditGoalFormCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                22.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    2.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        16.dp
                    )
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(
                            38.dp
                        ),
                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),
                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                            .copy(
                                alpha = 0.10f
                            )
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                icon,
                            contentDescription =
                                null,
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary,
                            modifier =
                                Modifier.size(
                                    21.dp
                                )
                        )
                    }
                }


                Text(
                    text =
                        title,
                    modifier =
                        Modifier.padding(
                            start = 10.dp
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        15.dp
                    )
            )


            content()
        }
    }
}


/*
|--------------------------------------------------------------------------
| MESSAGE
|--------------------------------------------------------------------------
*/

@Composable
private fun EditGoalMessageCard(
    text: String
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                15.dp
            ),
        color =
            MaterialTheme
                .colorScheme
                .errorContainer
    ) {

        Text(
            text =
                text,
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                ),
            color =
                MaterialTheme
                    .colorScheme
                    .onErrorContainer,
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            fontWeight =
                FontWeight.Medium
        )
    }
}


/*
|--------------------------------------------------------------------------
| DATE PICKER
|--------------------------------------------------------------------------
*/

private fun showEditGoalDatePicker(
    context: Context,
    currentDate: String,
    onDateSelected: (String) -> Unit
) {

    val calendar =
        Calendar.getInstance()


    if (
        currentDate.isNotBlank()
    ) {

        try {

            val formatter =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
                )

            formatter.isLenient =
                false


            val parsedDate =
                formatter.parse(
                    currentDate
                )


            if (
                parsedDate != null
            ) {

                calendar.time =
                    parsedDate
            }

        } catch (_: Exception) {

        }
    }


    val dialog =
        DatePickerDialog(
            context,
            {
                    _,
                    selectedYear,
                    selectedMonth,
                    selectedDay ->

                val selectedCalendar =
                    Calendar.getInstance()


                selectedCalendar.set(
                    Calendar.YEAR,
                    selectedYear
                )

                selectedCalendar.set(
                    Calendar.MONTH,
                    selectedMonth
                )

                selectedCalendar.set(
                    Calendar.DAY_OF_MONTH,
                    selectedDay
                )


                val formatter =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.US
                    )


                onDateSelected(
                    formatter.format(
                        selectedCalendar.time
                    )
                )
            },
            calendar.get(
                Calendar.YEAR
            ),
            calendar.get(
                Calendar.MONTH
            ),
            calendar.get(
                Calendar.DAY_OF_MONTH
            )
        )


    dialog.show()
}


/*
|--------------------------------------------------------------------------
| VALID DATE
|--------------------------------------------------------------------------
*/

private fun isValidEditGoalDateRange(
    startDate: String,
    deadline: String
): Boolean {

    return try {

        val formatter =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            )

        formatter.isLenient =
            false


        val start =
            formatter.parse(
                startDate
            )

        val end =
            formatter.parse(
                deadline
            )


        if (
            start == null ||
            end == null
        ) {

            false

        } else {

            !end.before(
                start
            )
        }

    } catch (_: Exception) {

        false
    }
}


/*
|--------------------------------------------------------------------------
| FORMAT DATE
|--------------------------------------------------------------------------
*/

private fun formatEditGoalDate(
    date: String
): String {

    return try {

        val inputFormat =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            )

        inputFormat.isLenient =
            false


        val outputFormat =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale(
                    "vi",
                    "VN"
                )
            )


        val parsedDate =
            inputFormat.parse(
                date
            )


        if (
            parsedDate != null
        ) {

            outputFormat.format(
                parsedDate
            )

        } else {

            date
        }

    } catch (_: Exception) {

        date
    }
}


/*
|--------------------------------------------------------------------------
| FORMAT INPUT AMOUNT
|--------------------------------------------------------------------------
*/

private fun formatEditAmountInput(
    value: Double
): String {

    return if (
        value % 1.0 == 0.0
    ) {

        value
            .toLong()
            .toString()

    } else {

        value.toString()
    }
}