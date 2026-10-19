package com.example.personalfinance.ui.goal

import android.app.DatePickerDialog
import android.content.Context

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Savings

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.personalfinance.ui.theme.IncomeGreen
import com.example.personalfinance.viewmodel.GoalViewModel

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun AddContributionScreen(
    goalId: Long,
    onBack: () -> Unit,
    goalViewModel: GoalViewModel = viewModel()
) {

    val context =
        LocalContext.current


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

    var amount by remember {
        mutableStateOf("")
    }


    var note by remember {
        mutableStateOf("")
    }


    var date by remember {

        mutableStateOf(
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(
                Date()
            )
        )
    }


    var localMessage by remember {
        mutableStateOf<String?>(null)
    }


    val message by
    goalViewModel.message
        .collectAsState()


    /*
    |--------------------------------------------------------------------------
    | AMOUNT VALUE
    |--------------------------------------------------------------------------
    */

    val amountValue =
        amount.toDoubleOrNull()
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
                            "Thêm tiền tiết kiệm",
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
                            "Ghi nhận khoản tiền đóng góp cho mục tiêu",
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
                            IncomeGreen.copy(
                                alpha = 0.10f
                            )
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
                            IncomeGreen.copy(
                                alpha = 0.13f
                            )
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Savings,
                                contentDescription =
                                    null,
                                tint =
                                    IncomeGreen,
                                modifier =
                                    Modifier.size(
                                        28.dp
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
                                "Khoản đóng góp",
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
                                if (
                                    amountValue > 0
                                ) {

                                    "${
                                        formatter.format(
                                            amountValue
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
                                IncomeGreen
                        )
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | AMOUNT
        |--------------------------------------------------------------------------
        */

        item {

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
                                IncomeGreen.copy(
                                    alpha = 0.10f
                                )
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default
                                            .AccountBalanceWallet,
                                    contentDescription =
                                        null,
                                    tint =
                                        IncomeGreen,
                                    modifier =
                                        Modifier.size(
                                            21.dp
                                        )
                                )
                            }
                        }


                        Text(
                            text =
                                "Thông tin đóng góp",
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


                    /*
                    |--------------------------------------------------------------------------
                    | MONEY INPUT
                    |--------------------------------------------------------------------------
                    */

                    OutlinedTextField(
                        value =
                            amount,
                        onValueChange = { value ->

                            amount =
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
                                "Số tiền"
                            )
                        },
                        placeholder = {

                            Text(
                                "Ví dụ: 500000"
                            )
                        },
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Savings,
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
                        amountValue > 0
                    ) {

                        Text(
                            text =
                                "${
                                    formatter.format(
                                        amountValue
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
                            fontWeight =
                                FontWeight.SemiBold,
                            color =
                                IncomeGreen
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | DATE
                    |--------------------------------------------------------------------------
                    */

                    Text(
                        text =
                            "Ngày đóng góp",
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

                            showContributionDatePicker(
                                context =
                                    context,
                                currentDate =
                                    date,
                                onDateSelected = {
                                        selectedDate ->

                                    date =
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
                                    formatContributionDate(
                                        date
                                    ),
                                modifier =
                                    Modifier.weight(
                                        1f
                                    ),
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurface
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | NOTE
                    |--------------------------------------------------------------------------
                    */

                    OutlinedTextField(
                        value =
                            note,
                        onValueChange = {

                            note =
                                it
                        },
                        modifier =
                            Modifier.fillMaxWidth(),
                        label = {

                            Text(
                                "Ghi chú"
                            )
                        },
                        placeholder = {

                            Text(
                                "Ví dụ: Tiền tiết kiệm tháng này"
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
        }


        /*
        |--------------------------------------------------------------------------
        | LOCAL ERROR
        |--------------------------------------------------------------------------
        */

        if (
            !localMessage.isNullOrBlank()
        ) {

            item {

                ContributionMessageCard(
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

        if (
            !message.isNullOrBlank()
        ) {

            item {

                ContributionMessageCard(
                    text =
                        message ?: "",
                    isError =
                        true
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | SUBMIT
        |--------------------------------------------------------------------------
        */

        item {

            Button(
                onClick = {

                    localMessage =
                        null


                    val value =
                        amount.toDoubleOrNull()


                    /*
                    |--------------------------------------------------------------------------
                    | VALIDATE MONEY
                    |--------------------------------------------------------------------------
                    */

                    if (
                        value == null ||
                        value <= 0
                    ) {

                        localMessage =
                            "Số tiền đóng góp phải lớn hơn 0."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | VALIDATE DATE
                    |--------------------------------------------------------------------------
                    */

                    if (
                        date.isBlank()
                    ) {

                        localMessage =
                            "Vui lòng chọn ngày đóng góp."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | ADD CONTRIBUTION
                    |--------------------------------------------------------------------------
                    */

                    goalViewModel
                        .addContribution(
                            goalId =
                                goalId,
                            amount =
                                value,
                            contributionDate =
                                date,
                            note =
                                note
                                    .trim()
                                    .ifBlank {
                                        null
                                    },
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
                        "Xác nhận đóng góp",
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
| MESSAGE
|--------------------------------------------------------------------------
*/

@Composable
private fun ContributionMessageCard(
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
            if (
                isError
            ) {

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
                if (
                    isError
                ) {

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

private fun showContributionDatePicker(
    context: Context,
    currentDate: String,
    onDateSelected: (String) -> Unit
) {

    val calendar =
        Calendar.getInstance()


    /*
    |--------------------------------------------------------------------------
    | LOAD CURRENT DATE
    |--------------------------------------------------------------------------
    */

    if (
        currentDate.isNotBlank()
    ) {

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


            if (
                parsedDate != null
            ) {

                calendar.time =
                    parsedDate
            }

        } catch (_: Exception) {

        }
    }


    /*
    |--------------------------------------------------------------------------
    | SHOW DIALOG
    |--------------------------------------------------------------------------
    */

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


                val outputFormat =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.US
                    )


                onDateSelected(
                    outputFormat.format(
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
| FORMAT DATE
|--------------------------------------------------------------------------
|
| Backend:
| 2026-09-16
|
| Hiển thị:
| 16/09/2026
|
*/

private fun formatContributionDate(
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