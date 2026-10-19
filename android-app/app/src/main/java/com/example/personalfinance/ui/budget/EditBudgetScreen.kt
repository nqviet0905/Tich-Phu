package com.example.personalfinance.ui.budget

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
import androidx.compose.material.icons.filled.Flag

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.ui.theme.IncomeGreen
import com.example.personalfinance.ui.theme.WarningOrange
import com.example.personalfinance.viewmodel.BudgetViewModel

import java.text.NumberFormat
import java.util.Locale


@Composable
fun EditBudgetScreen(
    budgetId: Long,
    onBack: () -> Unit,
    budgetViewModel: BudgetViewModel = viewModel()
) {

    /*
    |--------------------------------------------------------------------------
    | VIEWMODEL
    |--------------------------------------------------------------------------
    */

    val budget by
    budgetViewModel
        .selectedBudget
        .collectAsState()

    val message by
    budgetViewModel
        .message
        .collectAsState()


    /*
    |--------------------------------------------------------------------------
    | FORMATTER
    |--------------------------------------------------------------------------
    */

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

    var warningLevel by remember {
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
    | LOAD
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(budgetId) {

        initialized =
            false

        budgetViewModel
            .loadBudgetDetail(
                budgetId
            )
    }


    /*
    |--------------------------------------------------------------------------
    | FILL DATA
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(budget) {

        val item =
            budget

        if (
            item != null &&
            !initialized
        ) {

            amount =
                formatEditBudgetAmountInput(
                    item.amount
                )

            warningLevel =
                item.warning_level
                    .toString()

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
        budget == null ||
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
        budget!!


    /*
    |--------------------------------------------------------------------------
    | VALUES
    |--------------------------------------------------------------------------
    */

    val amountValue =
        amount
            .toDoubleOrNull()
            ?: 0.0


    val usagePercentage =
        if (
            data.amount > 0
        ) {

            (
                    data.spent_amount /
                            data.amount
                    ) * 100.0

        } else {

            0.0
        }


    val statusColor =
        when {

            data.spent_amount >
                    data.amount -> {

                ExpenseRed
            }


            usagePercentage >=
                    data.warning_level -> {

                WarningOrange
            }


            else -> {

                IncomeGreen
            }
        }


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
                            .weight(
                                1f
                            )
                            .padding(
                                start = 14.dp
                            )
                ) {

                    Text(
                        text =
                            "Chỉnh sửa ngân sách",
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
                            "Điều chỉnh hạn mức và mức cảnh báo",
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
                                .weight(
                                    1f
                                )
                                .padding(
                                    start = 14.dp
                                )
                    ) {

                        Text(
                            text =
                                data.category_name,
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurface,
                            maxLines =
                                1,
                            overflow =
                                TextOverflow.Ellipsis
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
                                "Tháng ${data.month}/${data.year}",
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
        | FIXED INFORMATION
        |--------------------------------------------------------------------------
        */

        item {

            EditBudgetFormCard(
                title =
                    "Thông tin ngân sách",
                icon =
                    Icons.Default
                        .CalendarMonth
            ) {

                EditBudgetInfoRow(
                    title =
                        "Danh mục",
                    value =
                        data.category_name
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )


                EditBudgetInfoRow(
                    title =
                        "Thời gian",
                    value =
                        "Tháng ${data.month}/${data.year}"
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )


                EditBudgetInfoRow(
                    title =
                        "Đã chi",
                    value =
                        "${
                            formatter.format(
                                data.spent_amount
                            )
                        } đ",
                    valueColor =
                        statusColor
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | AMOUNT
        |--------------------------------------------------------------------------
        */

        item {

            EditBudgetFormCard(
                title =
                    "Hạn mức chi tiêu",
                icon =
                    Icons.Default
                        .AccountBalanceWallet
            ) {

                OutlinedTextField(
                    value =
                        amount,
                    onValueChange = {
                            value ->

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
                            "Số tiền ngân sách"
                        )
                    },
                    placeholder = {

                        Text(
                            "Ví dụ: 3000000"
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
                                "Đã chi trong kỳ",
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
                                        data.spent_amount
                                    )
                                } đ",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color =
                                statusColor
                        )
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | WARNING LEVEL
        |--------------------------------------------------------------------------
        */

        item {

            EditBudgetFormCard(
                title =
                    "Cảnh báo chi tiêu",
                icon =
                    Icons.Default.Flag
            ) {

                OutlinedTextField(
                    value =
                        warningLevel,
                    onValueChange = {
                            value ->

                        warningLevel =
                            value
                                .filter {
                                    it.isDigit()
                                }
                                .take(
                                    3
                                )

                        localMessage =
                            null
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {

                        Text(
                            "Mức cảnh báo"
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
                    suffix = {

                        Text(
                            "%"
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


                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
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
                            .primary
                            .copy(
                                alpha = 0.07f
                            )
                ) {

                    Text(
                        text =
                            buildEditBudgetWarningDescription(
                                warningLevel
                            ),
                        modifier =
                            Modifier.padding(
                                13.dp
                            ),
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
        | LOCAL MESSAGE
        |--------------------------------------------------------------------------
        */

        if (
            !localMessage.isNullOrBlank()
        ) {

            item {

                EditBudgetMessageCard(
                    text =
                        localMessage ?: ""
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

                EditBudgetMessageCard(
                    text =
                        message ?: ""
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | SAVE
        |--------------------------------------------------------------------------
        */

        item {

            Button(
                onClick = {

                    localMessage =
                        null


                    val amountParsed =
                        amount
                            .toDoubleOrNull()

                    val warningParsed =
                        warningLevel
                            .toIntOrNull()


                    /*
                    |--------------------------------------------------------------------------
                    | AMOUNT
                    |--------------------------------------------------------------------------
                    */

                    if (
                        amountParsed == null ||
                        amountParsed <= 0
                    ) {

                        localMessage =
                            "Số tiền ngân sách phải lớn hơn 0."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | WARNING
                    |--------------------------------------------------------------------------
                    */

                    if (
                        warningParsed == null ||
                        warningParsed !in 1..100
                    ) {

                        localMessage =
                            "Mức cảnh báo phải từ 1 đến 100%."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | UPDATE
                    |--------------------------------------------------------------------------
                    */

                    budgetViewModel
                        .updateBudget(
                            id =
                                budgetId,
                            amount =
                                amountParsed,
                            warningLevel =
                                warningParsed,
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
private fun EditBudgetFormCard(
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
| INFO ROW
|--------------------------------------------------------------------------
*/

@Composable
private fun EditBudgetInfoRow(
    title: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color =
        MaterialTheme
            .colorScheme
            .onSurface
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text =
                title,
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )


        Spacer(
            modifier =
                Modifier.size(
                    12.dp
                )
        )


        Text(
            text =
                value,
            modifier =
                Modifier.weight(
                    1f
                ),
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            fontWeight =
                FontWeight.Bold,
            color =
                valueColor,
            maxLines =
                1,
            overflow =
                TextOverflow.Ellipsis
        )
    }
}


/*
|--------------------------------------------------------------------------
| MESSAGE
|--------------------------------------------------------------------------
*/

@Composable
private fun EditBudgetMessageCard(
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
| WARNING DESCRIPTION
|--------------------------------------------------------------------------
*/

private fun buildEditBudgetWarningDescription(
    warningLevel: String
): String {

    val value =
        warningLevel
            .toIntOrNull()


    return if (
        value != null &&
        value in 1..100
    ) {

        "Ứng dụng sẽ cảnh báo khi mức chi tiêu đạt khoảng $value% hạn mức ngân sách."

    } else {

        "Mức cảnh báo phải nằm trong khoảng từ 1% đến 100%."
    }
}


/*
|--------------------------------------------------------------------------
| FORMAT AMOUNT INPUT
|--------------------------------------------------------------------------
*/

private fun formatEditBudgetAmountInput(
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