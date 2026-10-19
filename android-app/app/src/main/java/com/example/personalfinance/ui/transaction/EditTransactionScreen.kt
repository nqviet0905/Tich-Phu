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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Storefront

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionScreen(
    transactionId: Long,
    onBack: () -> Unit,
    transactionViewModel: TransactionViewModel = viewModel()
) {

    val context =
        LocalContext.current


    /*
    |--------------------------------------------------------------------------
    | VIEWMODEL
    |--------------------------------------------------------------------------
    */

    val transaction by
    transactionViewModel
        .selectedTransaction
        .collectAsState()


    val categories by
    transactionViewModel
        .categories
        .collectAsState()


    val message by
    transactionViewModel
        .createMessage
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
        mutableStateOf("")
    }


    var note by remember {
        mutableStateOf("")
    }


    var categoryExpanded by remember {
        mutableStateOf(
            false
        )
    }


    var initialized by remember {
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
    | LOAD DETAIL
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(
        transactionId
    ) {

        initialized =
            false

        localMessage =
            null

        transactionViewModel
            .loadTransactionDetail(
                transactionId
            )
    }


    /*
    |--------------------------------------------------------------------------
    | INITIALIZE DATA
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(
        transaction
    ) {

        val item =
            transaction


        if (
            item != null &&
            item.id == transactionId &&
            !initialized
        ) {

            type =
                item.type


            categoryId =
                item.category_id


            amount =
                formatEditTransactionAmountInput(
                    item.amount
                )


            transactionDate =
                item.transaction_date


            note =
                item.note
                    ?: ""


            transactionViewModel
                .loadCategories(
                    item.type
                )


            initialized =
                true
        }
    }


    /*
    |--------------------------------------------------------------------------
    | RELOAD CATEGORIES WHEN TYPE CHANGES
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(
        type
    ) {

        if (
            initialized
        ) {

            transactionViewModel
                .loadCategories(
                    type
                )
        }
    }


    /*
    |--------------------------------------------------------------------------
    | LOADING
    |--------------------------------------------------------------------------
    */

    val currentTransaction =
        transaction


    if (
        currentTransaction == null ||
        currentTransaction.id != transactionId ||
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


    /*
    |--------------------------------------------------------------------------
    | QR?
    |--------------------------------------------------------------------------
    */

    val isQrPayment =
        currentTransaction
            .payment_method ==
                "qr_payment"


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


    val amountValue =
        amount.toDoubleOrNull()
            ?: 0.0


    val selectedCategory =
        categories.find {

            it.id ==
                    categoryId
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
                            if (
                                isQrPayment
                            ) {

                                "Chi tiết thanh toán QR"

                            } else {

                                "Chỉnh sửa giao dịch"
                            },
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
                            if (
                                isQrPayment
                            ) {

                                "Xem giao dịch và điều chỉnh phân loại"

                            } else {

                                "Cập nhật thông tin giao dịch"
                            },
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
                                        isQrPayment
                                    ) {

                                        Icons.Default
                                            .QrCode2

                                    } else if (
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
                                if (
                                    isQrPayment &&
                                    !currentTransaction
                                        .merchant_name
                                        .isNullOrBlank()
                                ) {

                                    currentTransaction
                                        .merchant_name
                                        .orEmpty()

                                } else {

                                    selectedCategory
                                        ?.name
                                        ?: currentTransaction
                                            .category_name
                                },
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            fontWeight =
                                FontWeight.Bold,
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
                                buildEditTransactionDateTime(
                                    date =
                                        transactionDate,
                                    time =
                                        currentTransaction
                                            .transaction_time
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
        | QR INFORMATION
        |--------------------------------------------------------------------------
        */

        if (
            isQrPayment
        ) {

            item {

                QrEditTransactionInfoCard(
                    merchantName =
                        currentTransaction
                            .merchant_name,
                    referenceCode =
                        currentTransaction
                            .reference_code,
                    transactionTime =
                        currentTransaction
                            .transaction_time
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | TYPE
        |--------------------------------------------------------------------------
        */

        item {

            Column {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

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


                    if (
                        isQrPayment
                    ) {

                        Spacer(
                            modifier =
                                Modifier.size(
                                    7.dp
                                )
                        )


                        Icon(
                            imageVector =
                                Icons.Default.Lock,
                            contentDescription =
                                null,
                            modifier =
                                Modifier.size(
                                    16.dp
                                ),
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }


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

                    EditTransactionTypeOption(
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
                        enabled =
                            !isQrPayment,
                        onClick = {

                            if (
                                !isQrPayment &&
                                type != "expense"
                            ) {

                                type =
                                    "expense"

                                categoryId =
                                    null

                                categoryExpanded =
                                    false

                                localMessage =
                                    null
                            }
                        },
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    )


                    EditTransactionTypeOption(
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
                        enabled =
                            !isQrPayment,
                        onClick = {

                            if (
                                !isQrPayment &&
                                type != "income"
                            ) {

                                type =
                                    "income"

                                categoryId =
                                    null

                                categoryExpanded =
                                    false

                                localMessage =
                                    null
                            }
                        },
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    )
                }


                if (
                    isQrPayment
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    Text(
                        text =
                            "Thanh toán QR luôn được ghi nhận là chi tiêu nên không thể đổi loại giao dịch.",
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
        | CATEGORY
        |--------------------------------------------------------------------------
        */

        item {

            EditTransactionFormCard(
                title =
                    "Danh mục",
                icon =
                    Icons.Default.Category
            ) {

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
                                ?: currentTransaction
                                    .category_name,
                        onValueChange = {},
                        readOnly =
                            true,
                        modifier =
                            Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                        label = {

                            Text(
                                "Danh mục giao dịch"
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


                if (
                    isQrPayment
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    Text(
                        text =
                            "Bạn vẫn có thể đổi danh mục nếu giao dịch QR được phân loại chưa đúng.",
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
        | AMOUNT
        |--------------------------------------------------------------------------
        */

        item {

            EditTransactionFormCard(
                title =
                    "Số tiền",
                icon =
                    Icons.Default.Payments,
                locked =
                    isQrPayment
            ) {

                OutlinedTextField(
                    value =
                        amount,
                    onValueChange = {
                            value ->

                        if (
                            !isQrPayment
                        ) {

                            amount =
                                value.filter {
                                    it.isDigit()
                                }

                            localMessage =
                                null
                        }
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {

                        Text(
                            "Số tiền giao dịch"
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
                    trailingIcon = {

                        if (
                            isQrPayment
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Lock,
                                contentDescription =
                                    "Không thể chỉnh sửa",
                                modifier =
                                    Modifier.size(
                                        18.dp
                                    )
                            )
                        }
                    },
                    suffix = {

                        Text(
                            "đ"
                        )
                    },
                    readOnly =
                        isQrPayment,
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
                            typeColor
                    )
                }


                if (
                    isQrPayment
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    TransactionLockedHint(
                        text =
                            "Số tiền của giao dịch QR được lấy từ giao dịch thanh toán và không thể chỉnh sửa."
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

            EditTransactionFormCard(
                title =
                    "Ngày giao dịch",
                icon =
                    Icons.Default.CalendarMonth,
                locked =
                    isQrPayment
            ) {

                if (
                    isQrPayment
                ) {

                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(
                                16.dp
                            ),
                        color =
                            MaterialTheme
                                .colorScheme
                                .surfaceVariant
                                .copy(
                                    alpha = 0.55f
                                )
                    ) {

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        16.dp
                                    ),
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
                                    buildEditTransactionDateTime(
                                        date =
                                            transactionDate,
                                        time =
                                            currentTransaction
                                                .transaction_time
                                    ),
                                modifier =
                                    Modifier.weight(
                                        1f
                                    ),
                                fontWeight =
                                    FontWeight.SemiBold
                            )


                            Icon(
                                imageVector =
                                    Icons.Default.Lock,
                                contentDescription =
                                    null,
                                modifier =
                                    Modifier.size(
                                        18.dp
                                    )
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    TransactionLockedHint(
                        text =
                            "Thời gian thanh toán QR được hệ thống ghi nhận tự động."
                    )

                } else {

                    OutlinedButton(
                        onClick = {

                            showEditTransactionDatePicker(
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

                        Icon(
                            imageVector =
                                Icons.Default
                                    .CalendarMonth,
                            contentDescription =
                                null
                        )


                        Spacer(
                            modifier =
                                Modifier.size(
                                    12.dp
                                )
                        )


                        Text(
                            text =
                                formatEditTransactionDate(
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

            EditTransactionFormCard(
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
                            "Ghi chú về giao dịch"
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

                EditTransactionMessageCard(
                    text =
                        localMessage
                            ?: ""
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

                EditTransactionMessageCard(
                    text =
                        message
                            ?: ""
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | UPDATE
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
                            "Số tiền giao dịch phải lớn hơn 0."

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
                    | UPDATE
                    |--------------------------------------------------------------------------
                    */

                    transactionViewModel
                        .updateTransaction(
                            id =
                                transactionId,
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
                                if (
                                    isQrPayment
                                ) {

                                    MaterialTheme
                                        .colorScheme
                                        .primary

                                } else {

                                    typeColor
                                }
                        )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Edit,
                    contentDescription =
                        null,
                    modifier =
                        Modifier.size(
                            19.dp
                        )
                )


                Spacer(
                    modifier =
                        Modifier.size(
                            7.dp
                        )
                )


                Text(
                    text =
                        if (
                            isQrPayment
                        ) {

                            "Lưu phân loại & ghi chú"

                        } else {

                            "Lưu thay đổi"
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
private fun EditTransactionFormCard(
    title: String,
    icon: ImageVector,
    locked: Boolean = false,
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


                if (
                    locked
                ) {

                    Spacer(
                        modifier =
                            Modifier.size(
                                7.dp
                            )
                    )


                    Icon(
                        imageVector =
                            Icons.Default.Lock,
                        contentDescription =
                            "Đã khóa",
                        modifier =
                            Modifier.size(
                                16.dp
                            ),
                        tint =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
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
private fun EditTransactionTypeOption(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier =
            modifier.clickable(
                enabled =
                    enabled
            ) {

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
                            alpha =
                                if (
                                    enabled
                                ) {

                                    0.12f

                                } else {

                                    0.07f
                                }
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
                        selected &&
                        enabled
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
                            if (
                                enabled
                            ) {

                                color

                            } else {

                                color.copy(
                                    alpha = 0.55f
                                )
                            },
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

                        if (
                            enabled
                        ) {

                            color

                        } else {

                            color.copy(
                                alpha = 0.65f
                            )
                        }

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
| QR INFO
|--------------------------------------------------------------------------
*/

@Composable
private fun QrEditTransactionInfoCard(
    merchantName: String?,
    referenceCode: String?,
    transactionTime: String?
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
                            44.dp
                        ),
                    shape =
                        RoundedCornerShape(
                            14.dp
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
                                Icons.Default.Storefront,
                            contentDescription =
                                null,
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.size(
                            11.dp
                        )
                )


                Column(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(
                        text =
                            merchantName
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?: "Thanh toán QR",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.ExtraBold,
                        maxLines =
                            1,
                        overflow =
                            TextOverflow.Ellipsis
                    )


                    Text(
                        text =
                            "Thông tin thanh toán",
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


            if (
                !transactionTime.isNullOrBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(
                            14.dp
                        )
                )


                QrEditInfoRow(
                    title =
                        "Thời gian",
                    value =
                        formatEditTransactionTime(
                            transactionTime
                        )
                )
            }


            if (
                !referenceCode.isNullOrBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(
                            9.dp
                        )
                )


                QrEditInfoRow(
                    title =
                        "Mã giao dịch",
                    value =
                        referenceCode
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        9.dp
                    )
            )


            QrEditInfoRow(
                title =
                    "Phương thức",
                value =
                    "Thanh toán QR"
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| QR INFO ROW
|--------------------------------------------------------------------------
*/

@Composable
private fun QrEditInfoRow(
    title: String,
    value: String
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
                    .bodySmall,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )


        Spacer(
            modifier =
                Modifier.size(
                    16.dp
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
                    .bodySmall,
            fontWeight =
                FontWeight.SemiBold,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurface
        )
    }
}


/*
|--------------------------------------------------------------------------
| LOCK HINT
|--------------------------------------------------------------------------
*/

@Composable
private fun TransactionLockedHint(
    text: String
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                14.dp
            ),
        color =
            MaterialTheme
                .colorScheme
                .surfaceVariant
                .copy(
                    alpha = 0.55f
                )
    ) {

        Row(
            modifier =
                Modifier.padding(
                    12.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Default.Lock,
                contentDescription =
                    null,
                modifier =
                    Modifier.size(
                        16.dp
                    ),
                tint =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )


            Spacer(
                modifier =
                    Modifier.size(
                        8.dp
                    )
            )


            Text(
                text =
                    text,
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
| MESSAGE
|--------------------------------------------------------------------------
*/

@Composable
private fun EditTransactionMessageCard(
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

private fun showEditTransactionDatePicker(
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


            val parsed =
                formatter.parse(
                    currentDate
                )


            if (
                parsed != null
            ) {

                calendar.time =
                    parsed
            }

        } catch (_: Exception) {

        }
    }


    val dialog =
        DatePickerDialog(
            context,
            {
                    _,
                    year,
                    month,
                    day ->

                val selectedCalendar =
                    Calendar.getInstance()


                selectedCalendar.set(
                    Calendar.YEAR,
                    year
                )

                selectedCalendar.set(
                    Calendar.MONTH,
                    month
                )

                selectedCalendar.set(
                    Calendar.DAY_OF_MONTH,
                    day
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


    /*
    |--------------------------------------------------------------------------
    | KHÔNG CHỌN NGÀY TƯƠNG LAI
    |--------------------------------------------------------------------------
    */

    dialog.datePicker.maxDate =
        System.currentTimeMillis()


    dialog.show()
}


/*
|--------------------------------------------------------------------------
| DATE
|--------------------------------------------------------------------------
*/

private fun formatEditTransactionDate(
    date: String
): String {

    return try {

        val input =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            )


        input.isLenient =
            false


        val output =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale(
                    "vi",
                    "VN"
                )
            )


        val parsed =
            input.parse(
                date
            )


        if (
            parsed != null
        ) {

            output.format(
                parsed
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
| DATE + TIME
|--------------------------------------------------------------------------
*/

private fun buildEditTransactionDateTime(
    date: String,
    time: String?
): String {

    val formattedDate =
        formatEditTransactionDate(
            date
        )


    val formattedTime =
        formatEditTransactionTime(
            time
        )


    return if (
        formattedTime.isBlank()
    ) {

        formattedDate

    } else {

        "$formattedDate • $formattedTime"
    }
}


/*
|--------------------------------------------------------------------------
| TIME
|--------------------------------------------------------------------------
*/

private fun formatEditTransactionTime(
    time: String?
): String {

    if (
        time.isNullOrBlank()
    ) {

        return ""
    }


    return try {

        if (
            time.length >= 5
        ) {

            time.substring(
                0,
                5
            )

        } else {

            time
        }

    } catch (_: Exception) {

        time
    }
}


/*
|--------------------------------------------------------------------------
| AMOUNT INPUT
|--------------------------------------------------------------------------
*/

private fun formatEditTransactionAmountInput(
    value: Double
): String {

    return if (
        value % 1.0 ==
        0.0
    ) {

        value
            .toLong()
            .toString()

    } else {

        value.toString()
    }
}