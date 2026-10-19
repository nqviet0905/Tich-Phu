package com.example.personalfinance.ui.transaction

import android.app.DatePickerDialog
import android.content.Context

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.ui.theme.IncomeGreen
import com.example.personalfinance.viewmodel.TransactionViewModel

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onBack: () -> Unit,
    transactionViewModel: TransactionViewModel = viewModel()
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

    var type by remember {
        mutableStateOf(
            "expense"
        )
    }


    var categoryId by remember {
        mutableStateOf<Long?>(
            null
        )
    }


    var amount by remember {
        mutableStateOf("")
    }


    var transactionDate by remember {

        mutableStateOf(
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(
                Date()
            )
        )
    }


    var note by remember {
        mutableStateOf("")
    }


    var categoryExpanded by remember {
        mutableStateOf(
            false
        )
    }


    var localMessage by remember {
        mutableStateOf<String?>(
            null
        )
    }


    /*
    |--------------------------------------------------------------------------
    | VIEWMODEL
    |--------------------------------------------------------------------------
    */

    val message by
    transactionViewModel
        .createMessage
        .collectAsState()


    val categories by
    transactionViewModel
        .categories
        .collectAsState()


    /*
    |--------------------------------------------------------------------------
    | LOAD CATEGORY
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(type) {

        categoryId =
            null

        categoryExpanded =
            false

        transactionViewModel
            .loadCategories(
                type
            )
    }


    /*
    |--------------------------------------------------------------------------
    | VALUES
    |--------------------------------------------------------------------------
    */

    val selectedCategory =
        categories.find {
            it.id ==
                    categoryId
        }


    val amountValue =
        amount.toDoubleOrNull()
            ?: 0.0


    val isIncome =
        type ==
                "income"


    val typeColor =
        if (
            isIncome
        ) {

            IncomeGreen

        } else {

            ExpenseRed
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
                            "Thêm giao dịch",
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
                            "Ghi lại thu nhập và chi tiêu của bạn",
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
        | TYPE
        |--------------------------------------------------------------------------
        */

        item {

            Column {

                Text(
                    text =
                        "Loại giao dịch",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.ExtraBold
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            9.dp
                        )
                )


                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )
                ) {

                    TransactionTypeOption(
                        title =
                            "Chi tiêu",
                        icon =
                            Icons.Default
                                .ArrowUpward,
                        selected =
                            type ==
                                    "expense",
                        color =
                            ExpenseRed,
                        onClick = {

                            type =
                                "expense"

                            localMessage =
                                null
                        },
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    )


                    TransactionTypeOption(
                        title =
                            "Thu nhập",
                        icon =
                            Icons.Default
                                .ArrowDownward,
                        selected =
                            type ==
                                    "income",
                        color =
                            IncomeGreen,
                        onClick = {

                            type =
                                "income"

                            localMessage =
                                null
                        },
                        modifier =
                            Modifier.weight(
                                1f
                            )
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
                    CardDefaults
                        .cardColors(
                            containerColor =
                                typeColor.copy(
                                    alpha = 0.09f
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
                            typeColor.copy(
                                alpha = 0.13f
                            )
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    if (
                                        isIncome
                                    ) {

                                        Icons.Default
                                            .ArrowDownward

                                    } else {

                                        Icons.Default
                                            .ArrowUpward
                                    },
                                contentDescription =
                                    null,
                                tint =
                                    typeColor,
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
                                .weight(
                                    1f
                                )
                                .padding(
                                    start = 14.dp
                                )
                    ) {

                        Text(
                            text =
                                selectedCategory
                                    ?.name
                                    ?: if (
                                        isIncome
                                    ) {

                                        "Khoản thu"

                                    } else {

                                        "Khoản chi"
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
                                "${
                                    if (
                                        isIncome
                                    ) {
                                        "+"
                                    } else {
                                        "-"
                                    }
                                }${
                                    formatter.format(
                                        amountValue
                                    )
                                } đ",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color =
                                typeColor
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    3.dp
                                )
                        )


                        Text(
                            text =
                                formatAddTransactionDate(
                                    transactionDate
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
        }


        /*
        |--------------------------------------------------------------------------
        | MAIN INFORMATION
        |--------------------------------------------------------------------------
        */

        item {

            AddTransactionFormCard(
                title =
                    "Thông tin giao dịch",
                icon =
                    Icons.Default.ReceiptLong
            ) {


                /*
                |--------------------------------------------------------------------------
                | CATEGORY
                |--------------------------------------------------------------------------
                */

                ExposedDropdownMenuBox(
                    expanded =
                        categoryExpanded,
                    onExpandedChange = {

                        categoryExpanded =
                            !categoryExpanded
                    }
                ) {

                    OutlinedTextField(
                        value =
                            selectedCategory
                                ?.name
                                ?: "Chọn danh mục",
                        onValueChange = {},
                        readOnly =
                            true,
                        modifier =
                            Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                        label = {

                            Text(
                                "Danh mục"
                            )
                        },
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Category,
                                contentDescription =
                                    null,
                                tint =
                                    typeColor
                            )
                        },
                        trailingIcon = {

                            ExposedDropdownMenuDefaults
                                .TrailingIcon(
                                    expanded =
                                        categoryExpanded
                                )
                        },
                        shape =
                            RoundedCornerShape(
                                16.dp
                            )
                    )


                    ExposedDropdownMenu(
                        expanded =
                            categoryExpanded,
                        onDismissRequest = {

                            categoryExpanded =
                                false
                        }
                    ) {

                        if (
                            categories.isEmpty()
                        ) {

                            DropdownMenuItem(
                                text = {

                                    Text(
                                        "Không có danh mục"
                                    )
                                },
                                onClick = {}
                            )

                        } else {

                            categories.forEach {
                                    category ->

                                DropdownMenuItem(
                                    text = {

                                        Text(
                                            category.name
                                        )
                                    },
                                    onClick = {

                                        categoryId =
                                            category.id

                                        categoryExpanded =
                                            false

                                        localMessage =
                                            null
                                    }
                                )
                            }
                        }
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
                | AMOUNT
                |--------------------------------------------------------------------------
                */

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
                            "Số tiền"
                        )
                    },
                    placeholder = {

                        Text(
                            "Ví dụ: 150000"
                        )
                    },
                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.Payments,
                            contentDescription =
                                null,
                            tint =
                                typeColor
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
                    amountValue >
                    0
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
                            typeColor
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | DATE
        |--------------------------------------------------------------------------
        */

        item {

            AddTransactionFormCard(
                title =
                    "Thời gian",
                icon =
                    Icons.Default
                        .CalendarMonth
            ) {

                Text(
                    text =
                        "Ngày giao dịch",
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

                        showAddTransactionDatePicker(
                            context =
                                context,
                            currentDate =
                                transactionDate,
                            onDateSelected = {
                                    selectedDate ->

                                transactionDate =
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
                                formatAddTransactionDate(
                                    transactionDate
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
            }
        }


        /*
        |--------------------------------------------------------------------------
        | NOTE
        |--------------------------------------------------------------------------
        */

        item {

            AddTransactionFormCard(
                title =
                    "Ghi chú",
                icon =
                    Icons.Default.EditNote
            ) {

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
                            "Nội dung"
                        )
                    },
                    placeholder = {

                        Text(
                            if (
                                isIncome
                            ) {

                                "Ví dụ: Lương tháng này"

                            } else {

                                "Ví dụ: Ăn trưa cùng bạn bè"
                            }
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
                        4,
                    shape =
                        RoundedCornerShape(
                            16.dp
                        )
                )
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

                AddTransactionMessageCard(
                    text =
                        localMessage
                            ?: ""
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | VIEWMODEL MESSAGE
        |--------------------------------------------------------------------------
        */

        if (
            !message.isNullOrBlank()
        ) {

            item {

                AddTransactionMessageCard(
                    text =
                        message
                            ?: ""
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


                    val category =
                        categoryId


                    val amountParsed =
                        amount
                            .toDoubleOrNull()


                    /*
                    |--------------------------------------------------------------------------
                    | CATEGORY
                    |--------------------------------------------------------------------------
                    */

                    if (
                        category == null ||
                        category <= 0
                    ) {

                        localMessage =
                            "Vui lòng chọn danh mục."

                        return@Button
                    }


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
                            "Số tiền phải lớn hơn 0."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | DATE
                    |--------------------------------------------------------------------------
                    */

                    if (
                        transactionDate.isBlank()
                    ) {

                        localMessage =
                            "Vui lòng chọn ngày giao dịch."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | CREATE
                    |--------------------------------------------------------------------------
                    */

                    transactionViewModel
                        .createTransaction(
                            categoryId =
                                category,
                            type =
                                type,
                            amount =
                                amountParsed,
                            transactionDate =
                                transactionDate,
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
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                typeColor
                        )
            ) {

                Text(
                    text =
                        if (
                            isIncome
                        ) {

                            "Lưu khoản thu"

                        } else {

                            "Lưu khoản chi"
                        },
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
private fun AddTransactionFormCard(
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
            CardDefaults
                .cardColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .surface
                ),
        elevation =
            CardDefaults
                .cardElevation(
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
| TYPE OPTION
|--------------------------------------------------------------------------
*/

@Composable
private fun TransactionTypeOption(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier =
            modifier.clickable {
                onClick()
            },
        shape =
            RoundedCornerShape(
                20.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (
                        selected
                    ) {

                        color.copy(
                            alpha = 0.12f
                        )

                    } else {

                        MaterialTheme
                            .colorScheme
                            .surface
                    }
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    if (
                        selected
                    ) {
                        3.dp
                    } else {
                        1.dp
                    }
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 14.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.Center
        ) {

            Surface(
                modifier =
                    Modifier.size(
                        36.dp
                    ),
                shape =
                    RoundedCornerShape(
                        12.dp
                    ),
                color =
                    color.copy(
                        alpha = 0.13f
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
                            color,
                        modifier =
                            Modifier.size(
                                20.dp
                            )
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.size(
                        8.dp
                    )
            )


            Text(
                text =
                    title,
                color =
                    if (
                        selected
                    ) {

                        color

                    } else {

                        MaterialTheme
                            .colorScheme
                            .onSurface
                    },
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| MESSAGE
|--------------------------------------------------------------------------
*/

@Composable
private fun AddTransactionMessageCard(
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

private fun showAddTransactionDatePicker(
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


    /*
    |--------------------------------------------------------------------------
    | KHÔNG CHO CHỌN NGÀY TƯƠNG LAI
    |--------------------------------------------------------------------------
    */

    dialog.datePicker.maxDate =
        System.currentTimeMillis()


    dialog.show()
}


/*
|--------------------------------------------------------------------------
| FORMAT DATE
|--------------------------------------------------------------------------
|
| Backend:
| 2026-09-25
|
| UI:
| 25/09/2026
|
*/

private fun formatAddTransactionDate(
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