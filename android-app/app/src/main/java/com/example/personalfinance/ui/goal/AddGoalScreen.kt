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
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Flag

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
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
fun AddGoalScreen(
    onBack: () -> Unit,
    goalViewModel: GoalViewModel = viewModel()
) {

    val context =
        LocalContext.current

    val formatter =
        remember {
            NumberFormat.getNumberInstance(
                Locale("vi", "VN")
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

    var currentAmount by remember {
        mutableStateOf("0")
    }

    var startDate by remember {
        mutableStateOf("")
    }

    var deadline by remember {
        mutableStateOf("")
    }

    var localMessage by remember {
        mutableStateOf<String?>(null)
    }

    val message by
    goalViewModel.message.collectAsState()


    /*
    |--------------------------------------------------------------------------
    | MONEY VALUE
    |--------------------------------------------------------------------------
    */

    val targetValue =
        targetAmount.toDoubleOrNull()
            ?: 0.0

    val currentValue =
        currentAmount.toDoubleOrNull()
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
                            "Tạo mục tiêu tiết kiệm",
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
                            "Lập mục tiêu và theo dõi hành trình tích lũy của bạn",
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
                                    Icons.Default.Flag,
                                contentDescription =
                                    null,
                                tint =
                                    MaterialTheme
                                        .colorScheme
                                        .primary,
                                modifier =
                                    Modifier.size(
                                        27.dp
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
                                "Số tiền mục tiêu",
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
                                if (targetValue > 0) {

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
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | THÔNG TIN MỤC TIÊU
        |--------------------------------------------------------------------------
        */

        item {

            GoalFormCard(
                title =
                    "Thông tin mục tiêu",
                icon =
                    Icons.Default.Flag
            ) {

                OutlinedTextField(
                    value =
                        name,
                    onValueChange = {

                        name = it

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
        | KẾ HOẠCH TÍCH LŨY
        |--------------------------------------------------------------------------
        */

        item {

            GoalFormCard(
                title =
                    "Kế hoạch tích lũy",
                icon =
                    Icons.Default
                        .AccountBalanceWallet
            ) {

                /*
                |--------------------------------------------------------------------------
                | TARGET
                |--------------------------------------------------------------------------
                */

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


                if (targetValue > 0) {

                    Text(
                        text =
                            "${
                                formatter.format(
                                    targetValue
                                )
                            } đ",
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary,
                        modifier =
                            Modifier.padding(
                                start = 4.dp,
                                top = 5.dp
                            )
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )


                /*
                |--------------------------------------------------------------------------
                | CURRENT
                |--------------------------------------------------------------------------
                */

                OutlinedTextField(
                    value =
                        currentAmount,
                    onValueChange = { value ->

                        currentAmount =
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
                            "Số tiền hiện có"
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


                if (currentValue > 0) {

                    Text(
                        text =
                            "${
                                formatter.format(
                                    currentValue
                                )
                            } đ",
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,
                        modifier =
                            Modifier.padding(
                                start = 4.dp,
                                top = 5.dp
                            )
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | THỜI GIAN
        |--------------------------------------------------------------------------
        */

        item {

            GoalFormCard(
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

                        showGoalDatePicker(
                            context =
                                context,
                            currentDate =
                                startDate,
                            onDateSelected = { selectedDate ->

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

                                    formatGoalDate(
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

                        showGoalDatePicker(
                            context =
                                context,
                            currentDate =
                                deadline,
                            onDateSelected = { selectedDate ->

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

                                    formatGoalDate(
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
        | LOCAL ERROR
        |--------------------------------------------------------------------------
        */

        if (!localMessage.isNullOrBlank()) {

            item {

                GoalMessageCard(
                    text =
                        localMessage ?: "",
                    isError =
                        true
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | SERVER MESSAGE
        |--------------------------------------------------------------------------
        */

        if (!message.isNullOrBlank()) {

            item {

                GoalMessageCard(
                    text =
                        message ?: "",
                    isError =
                        true
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | BUTTON
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

                    val current =
                        currentAmount
                            .toDoubleOrNull()


                    /*
                    |--------------------------------------------------------------------------
                    | NAME
                    |--------------------------------------------------------------------------
                    */

                    if (name.isBlank()) {

                        localMessage =
                            "Vui lòng nhập tên mục tiêu."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | TARGET
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
                    | CURRENT
                    |--------------------------------------------------------------------------
                    */

                    if (
                        current == null ||
                        current < 0
                    ) {

                        localMessage =
                            "Số tiền hiện có không hợp lệ."

                        return@Button
                    }


                    if (
                        current > target
                    ) {

                        localMessage =
                            "Số tiền hiện có không được lớn hơn số tiền mục tiêu."

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
                            "Vui lòng chọn ngày bắt đầu và hạn hoàn thành."

                        return@Button
                    }


                    if (
                        !isValidGoalDateRange(
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
                    | CREATE
                    |--------------------------------------------------------------------------
                    */

                    goalViewModel.createGoal(
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
                        currentAmount =
                            current,
                        startDate =
                            startDate,
                        deadline =
                            deadline,
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
                        "Tạo mục tiêu",
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
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                    fontWeight =
                        FontWeight.SemiBold
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
private fun GoalFormCard(
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
private fun GoalMessageCard(
    text: String,
    isError: Boolean
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                15.dp
            ),
        color =
            if (isError) {

                MaterialTheme
                    .colorScheme
                    .errorContainer

            } else {

                MaterialTheme
                    .colorScheme
                    .primaryContainer
            }
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
                if (isError) {

                    MaterialTheme
                        .colorScheme
                        .onErrorContainer

                } else {

                    MaterialTheme
                        .colorScheme
                        .onPrimaryContainer
                },
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

private fun showGoalDatePicker(
    context: Context,
    currentDate: String,
    onDateSelected: (String) -> Unit
) {

    val calendar =
        Calendar.getInstance()


    if (currentDate.isNotBlank()) {

        try {

            val inputFormat =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
                )

            inputFormat.isLenient =
                false


            val parsedDate =
                inputFormat.parse(
                    currentDate
                )


            if (parsedDate != null) {

                calendar.time =
                    parsedDate
            }

        } catch (_: Exception) {

        }
    }


    val year =
        calendar.get(
            Calendar.YEAR
        )

    val month =
        calendar.get(
            Calendar.MONTH
        )

    val day =
        calendar.get(
            Calendar.DAY_OF_MONTH
        )


    val dialog =
        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->

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


                val outputFormat =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.US
                    )


                val result =
                    outputFormat.format(
                        selectedCalendar.time
                    )


                onDateSelected(
                    result
                )
            },
            year,
            month,
            day
        )


    dialog.show()
}


/*
|--------------------------------------------------------------------------
| VALID DATE
|--------------------------------------------------------------------------
*/

private fun isValidGoalDateRange(
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
|
| Database:
| 2026-09-16
|
| Giao diện:
| 16/09/2026
|
*/

private fun formatGoalDate(
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


        if (parsedDate != null) {

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