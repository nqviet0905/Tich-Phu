package com.example.personalfinance.ui.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinance.data.model.BankAccount
import com.example.personalfinance.data.model.QrPaymentData
import com.example.personalfinance.data.model.QrPaymentResult
import com.example.personalfinance.viewmodel.QrPaymentViewModel
import java.text.NumberFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrPaymentConfirmScreen(

    qrData: QrPaymentData,

    onBack: () -> Unit,

    onPaymentSuccess: (
        QrPaymentResult
    ) -> Unit,

    viewModel: QrPaymentViewModel = viewModel()
) {

    val uiState by
    viewModel.uiState
        .collectAsStateWithLifecycle()


    /*
    |--------------------------------------------------------------------------
    | AMOUNT
    |--------------------------------------------------------------------------
    */

    var amountText by remember(qrData.amount) {

        mutableStateOf(
            qrData.amount
                ?.toLong()
                ?.toString()
                ?: ""
        )
    }


    /*
    |--------------------------------------------------------------------------
    | NOTE
    |--------------------------------------------------------------------------
    */

    var note by remember {

        mutableStateOf("")
    }


    /*
    |--------------------------------------------------------------------------
    | BANK DIALOG
    |--------------------------------------------------------------------------
    */

    var showBankDialog by remember {

        mutableStateOf(false)
    }


    /*
    |--------------------------------------------------------------------------
    | LOAD DATA
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(
        qrData.merchantName
    ) {

        viewModel.loadPaymentData(
            qrData.merchantName
        )
    }


    /*
    |--------------------------------------------------------------------------
    | SELECTED BANK
    |--------------------------------------------------------------------------
    */

    val selectedBank =

        uiState.bankAccounts
            .firstOrNull {

                it.id ==
                        uiState.selectedBankAccountId
            }


    /*
    |--------------------------------------------------------------------------
    | PAYMENT RESULT
    |--------------------------------------------------------------------------
    */

    val paymentResult =
        uiState.paymentResult


    /*
    |--------------------------------------------------------------------------
    | SUCCESS DIALOG
    |--------------------------------------------------------------------------
    */

    if (paymentResult != null) {

        PaymentSuccessDialog(

            result =
                paymentResult,

            onDone = {

                onPaymentSuccess(
                    paymentResult
                )

                viewModel.reset()
            }
        )
    }


    /*
    |--------------------------------------------------------------------------
    | BANK SELECT DIALOG
    |--------------------------------------------------------------------------
    */

    if (showBankDialog) {

        BankSelectionDialog(

            accounts =
                uiState.bankAccounts,

            selectedId =
                uiState.selectedBankAccountId,

            onDismiss = {

                showBankDialog = false
            },

            onSelect = { account ->

                viewModel.selectBankAccount(
                    account.id
                )

                showBankDialog = false
            }
        )
    }


    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Xác nhận thanh toán",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,

                            contentDescription =
                                "Quay lại"
                        )
                    }
                },

                colors =
                    TopAppBarDefaults
                        .topAppBarColors(

                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surface
                        )
            )
        }

    ) { innerPadding ->


        /*
        |--------------------------------------------------------------------------
        | LOADING
        |--------------------------------------------------------------------------
        */

        if (uiState.isLoading) {

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),

                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator()
            }

            return@Scaffold
        }


        LazyColumn(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)

        ) {


            /*
            |--------------------------------------------------------------------------
            | MERCHANT
            |--------------------------------------------------------------------------
            */

            item {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                Column(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Card(

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

                        Icon(

                            imageVector =
                                Icons.Default.Storefront,

                            contentDescription =
                                null,

                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary,

                            modifier =
                                Modifier.padding(
                                    18.dp
                                )
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )


                    Text(

                        text =
                            qrData.merchantName,

                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,

                        fontWeight =
                            FontWeight.Bold,

                        textAlign =
                            TextAlign.Center
                    )


                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )


                    Text(

                        text =
                            "${qrData.bankCode} •••• ${
                                qrData.accountNumber
                                    .takeLast(4)
                            }",

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | AMOUNT
            |--------------------------------------------------------------------------
            */

            item {

                if (qrData.amount != null) {

                    Text(

                        text =
                            formatMoney(
                                qrData.amount
                            ),

                        modifier =
                            Modifier.fillMaxWidth(),

                        textAlign =
                            TextAlign.Center,

                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )

                } else {

                    OutlinedTextField(

                        value =
                            amountText,

                        onValueChange = {

                            amountText =
                                it.filter { char ->

                                    char.isDigit()
                                }
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {

                            Text(
                                "Số tiền thanh toán"
                            )
                        },

                        suffix = {

                            Text("đ")
                        },

                        singleLine =
                            true,

                        keyboardOptions =
                            KeyboardOptions(

                                keyboardType =
                                    KeyboardType.Number
                            )
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | RECEIVER INFO
            |--------------------------------------------------------------------------
            */

            item {

                PaymentSectionCard(

                    title =
                        "Người nhận"
                ) {

                    InfoRow(

                        title =
                            qrData.merchantName,

                        description =
                            "${qrData.bankCode} •••• ${
                                qrData.accountNumber
                                    .takeLast(4)
                            }"
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | SOURCE BANK
            |--------------------------------------------------------------------------
            */

            item {

                PaymentSectionCard(

                    title =
                        "Nguồn thanh toán"
                ) {

                    if (selectedBank == null) {

                        Text(

                            text =
                                "Bạn chưa có tài khoản ngân hàng được liên kết.",

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .error
                        )

                    } else {

                        Row(

                            modifier =
                                Modifier.fillMaxWidth(),

                            verticalAlignment =
                                Alignment.CenterVertically

                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.AccountBalance,

                                contentDescription =
                                    null,

                                tint =
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                            )


                            Column(

                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .padding(
                                            start = 12.dp
                                        )
                            ) {

                                Text(

                                    text =
                                        selectedBank.bankName,

                                    fontWeight =
                                        FontWeight.SemiBold
                                )


                                Text(

                                    text =
                                        selectedBank
                                            .maskedAccountNumber
                                            ?: "•••• ${
                                                selectedBank.accountNumber
                                                    .takeLast(4)
                                            }",

                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium,

                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )
                            }


                            TextButton(

                                onClick = {

                                    showBankDialog =
                                        true
                                }
                            ) {

                                Text("Thay đổi")
                            }
                        }
                    }
                }
            }


            /*
            |--------------------------------------------------------------------------
            | CATEGORY
            |--------------------------------------------------------------------------
            */

            item {

                Text(

                    text =
                        "Mục đích thanh toán",

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                if (
                    uiState.categories.isEmpty()
                ) {

                    Text(

                        text =
                            "Không có danh mục chi tiêu.",

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )

                } else {

                    LazyRow(

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                8.dp
                            )
                    ) {

                        items(
                            uiState.categories,
                            key = {
                                it.id
                            }
                        ) { category ->


                            val selected =

                                uiState
                                    .selectedCategoryId ==
                                        category.id


                            val suggested =

                                uiState
                                    .suggestedCategoryId ==
                                        category.id


                            FilterChip(

                                selected =
                                    selected,

                                onClick = {

                                    viewModel
                                        .selectCategory(
                                            category.id
                                        )
                                },

                                label = {

                                    Text(

                                        text =
                                            if (suggested) {

                                                "${category.name} • Gợi ý"

                                            } else {

                                                category.name
                                            }
                                    )
                                },

                                leadingIcon = {

                                    if (selected) {

                                        Icon(

                                            imageVector =
                                                Icons.Default.CheckCircle,

                                            contentDescription =
                                                null
                                        )
                                    }
                                },

                                colors =
                                    FilterChipDefaults
                                        .filterChipColors()
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

                OutlinedTextField(

                    value =
                        note,

                    onValueChange = {

                        note = it
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
                            "Không bắt buộc"
                        )
                    },

                    maxLines =
                        3
                )
            }


            /*
            |--------------------------------------------------------------------------
            | ERROR
            |--------------------------------------------------------------------------
            */

            if (
                !uiState.error
                    .isNullOrBlank()
            ) {

                item {

                    Card(

                        modifier =
                            Modifier.fillMaxWidth(),

                        colors =
                            CardDefaults
                                .cardColors(

                                    containerColor =
                                        MaterialTheme
                                            .colorScheme
                                            .errorContainer
                                )
                    ) {

                        Text(

                            text =
                                uiState.error
                                    ?: "",

                            modifier =
                                Modifier.padding(
                                    14.dp
                                ),

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onErrorContainer
                        )
                    }
                }
            }


            /*
            |--------------------------------------------------------------------------
            | PAY BUTTON
            |--------------------------------------------------------------------------
            */

            item {

                val amount =

                    qrData.amount
                        ?: amountText
                            .toDoubleOrNull()
                        ?: 0.0


                Button(

                    onClick = {

                        viewModel.pay(

                            qrData =
                                qrData,

                            amount =
                                amount,

                            note =
                                note,

                            onSuccess = {

                                /*
                                 * Không navigate ngay.
                                 *
                                 * UiState sẽ nhận paymentResult
                                 * và mở dialog thành công.
                                 */
                            }
                        )
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                    enabled =
                        !uiState.isPaying &&
                                selectedBank != null &&
                                amount > 0,

                    shape =
                        RoundedCornerShape(
                            18.dp
                        ),

                    colors =
                        ButtonDefaults
                            .buttonColors(

                                containerColor =
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                            )
                ) {

                    if (uiState.isPaying) {

                        CircularProgressIndicator(

                            modifier =
                                Modifier.height(
                                    22.dp
                                ),

                            strokeWidth =
                                2.dp,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimary
                        )

                    } else {

                        Text(

                            text =
                                "Thanh toán ${
                                    formatMoney(
                                        amount
                                    )
                                }",

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| SECTION CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun PaymentSectionCard(

    title: String,

    content:
    @Composable () -> Unit

) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                18.dp
            ),

        colors =
            CardDefaults
                .cardColors(

                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .surfaceContainerLow
                )
    ) {

        Column(

            modifier =
                Modifier.padding(
                    16.dp
                )
        ) {

            Text(

                text =
                    title,

                style =
                    MaterialTheme
                        .typography
                        .labelLarge,

                fontWeight =
                    FontWeight.Bold,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )


            Spacer(
                modifier =
                    Modifier.height(12.dp)
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
private fun InfoRow(

    title: String,

    description: String

) {

    Column {

        Text(

            text =
                title,

            fontWeight =
                FontWeight.SemiBold
        )


        Spacer(
            modifier =
                Modifier.height(3.dp)
        )


        Text(

            text =
                description,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )
    }
}


/*
|--------------------------------------------------------------------------
| BANK SELECTION DIALOG
|--------------------------------------------------------------------------
*/

@Composable
private fun BankSelectionDialog(

    accounts:
    List<BankAccount>,

    selectedId:
    Long?,

    onDismiss:
        () -> Unit,

    onSelect:
        (BankAccount) -> Unit

) {

    AlertDialog(

        onDismissRequest =
            onDismiss,

        title = {

            Text(
                "Chọn tài khoản"
            )
        },

        text = {

            if (
                accounts.isEmpty()
            ) {

                Text(
                    "Bạn chưa liên kết tài khoản ngân hàng."
                )

            } else {

                Column(

                    verticalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {

                    accounts.forEach { account ->


                        OutlinedButton(

                            onClick = {

                                onSelect(
                                    account
                                )
                            },

                            modifier =
                                Modifier.fillMaxWidth()
                        ) {

                            Row(

                                modifier =
                                    Modifier.fillMaxWidth(),

                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Icon(

                                    imageVector =
                                        Icons.Default.AccountBalance,

                                    contentDescription =
                                        null
                                )


                                Column(

                                    modifier =
                                        Modifier
                                            .weight(1f)
                                            .padding(
                                                horizontal =
                                                    12.dp
                                            )
                                ) {

                                    Text(

                                        text =
                                            account.bankName,

                                        fontWeight =
                                            FontWeight.SemiBold
                                    )


                                    Text(

                                        text =
                                            account
                                                .maskedAccountNumber
                                                ?: "•••• ${
                                                    account.accountNumber
                                                        .takeLast(4)
                                                }",

                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodySmall
                                    )
                                }


                                if (
                                    account.id ==
                                    selectedId
                                ) {

                                    Icon(

                                        imageVector =
                                            Icons.Default.CheckCircle,

                                        contentDescription =
                                            null,

                                        tint =
                                            MaterialTheme
                                                .colorScheme
                                                .primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },

        confirmButton = {

            TextButton(
                onClick =
                    onDismiss
            ) {

                Text("Đóng")
            }
        }
    )
}


/*
|--------------------------------------------------------------------------
| SUCCESS
|--------------------------------------------------------------------------
*/

@Composable
private fun PaymentSuccessDialog(

    result:
    QrPaymentResult,

    onDone:
        () -> Unit

) {

    AlertDialog(

        onDismissRequest = {},

        icon = {

            Icon(

                imageVector =
                    Icons.Default.CheckCircle,

                contentDescription =
                    null,

                tint =
                    MaterialTheme
                        .colorScheme
                        .primary
            )
        },

        title = {

            Text(
                text =
                    "Thanh toán thành công",
                textAlign =
                    TextAlign.Center
            )
        },

        text = {

            Column(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(

                    text =
                        result.merchantName,

                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                Text(

                    text =
                        formatMoney(
                            result.amount
                        ),

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                HorizontalDivider()


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                Text(
                    "Danh mục: ${result.categoryName}"
                )


                Text(
                    "Mã giao dịch: ${result.referenceCode}"
                )


                Text(
                    "${result.transactionDate} ${result.transactionTime}"
                )
            }
        },

        confirmButton = {

            Button(
                onClick =
                    onDone
            ) {

                Text("Xong")
            }
        }
    )
}


/*
|--------------------------------------------------------------------------
| FORMAT MONEY
|--------------------------------------------------------------------------
*/

private fun formatMoney(
    amount: Double
): String {

    val formatter =
        NumberFormat
            .getNumberInstance(
                Locale("vi", "VN")
            )

    return "${
        formatter.format(
            amount
        )
    } đ"
}