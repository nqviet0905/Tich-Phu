package com.example.personalfinance.ui.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinance.ui.theme.BrandGold
import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.ui.theme.BrandPurpleDark
import com.example.personalfinance.viewmodel.WalletViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DepositScreen(
    onBack: () -> Unit,
    walletViewModel: WalletViewModel = viewModel()
) {

    val state by
    walletViewModel.uiState.collectAsState()

    val formatter =
        NumberFormat.getNumberInstance(
            Locale("vi", "VN")
        )

    var amount by remember {
        mutableStateOf("")
    }

    var selectedMethod by remember {
        mutableStateOf("bank")
    }

    var note by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var depositSuccessAmount by remember {
        mutableStateOf<Double?>(null)
    }

    val quickAmounts =
        listOf(
            100_000,
            500_000,
            1_000_000,
            2_000_000
        )

    /*
    |--------------------------------------------------------------------------
    | LOAD BALANCE
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(Unit) {
        walletViewModel.loadBalance()
    }

    if (depositSuccessAmount != null) {

        val successAmount =
            depositSuccessAmount!!

        AlertDialog(

            onDismissRequest = {
                // Không đóng khi bấm ra ngoài
            },

            icon = {

                Icon(
                    imageVector =
                        Icons.Default.CheckCircle,
                    contentDescription =
                        null,
                    tint =
                        Color(0xFF16A34A),
                    modifier =
                        Modifier.size(54.dp)
                )
            },

            title = {

                Text(
                    text =
                        "Nạp tiền thành công",
                    fontWeight =
                        FontWeight.ExtraBold,
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
                            "${formatter.format(successAmount)} đ",
                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color =
                            BrandPurple
                    )

                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )

                    DepositSuccessRow(
                        title =
                            "Phương thức",
                        value =
                            when (selectedMethod) {

                                "bank" ->
                                    "Tài khoản ngân hàng"

                                "card" ->
                                    "Thẻ ATM / Thẻ ghi nợ"

                                "wallet" ->
                                    "Ví điện tử"

                                else ->
                                    selectedMethod
                            }
                    )

                    DepositSuccessRow(
                        title =
                            "Số dư mới",
                        value =
                            "${formatter.format(state.balance)} đ"
                    )
                }
            },

            confirmButton = {

                Button(
                    onClick = {

                        depositSuccessAmount =
                            null

                        walletViewModel
                            .clearMessage()

                        onBack()
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                BrandPurple
                        )
                ) {

                    Text(
                        text =
                            "Hoàn tất",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),
        contentPadding =
            PaddingValues(
                bottom = 40.dp
            )
    ) {

        /*
        |--------------------------------------------------------------------------
        | HEADER
        |--------------------------------------------------------------------------
        */

        item {

            Column(
                modifier = Modifier.padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 16.dp
                )
            ) {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Surface(
                        modifier = Modifier
                            .size(46.dp)
                            .clickable {
                                onBack()
                            },
                        shape = CircleShape,
                        color =
                            MaterialTheme
                                .colorScheme
                                .surfaceVariant
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.ArrowBack,
                                contentDescription =
                                    "Quay lại"
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.size(12.dp)
                    )

                    Column {

                        Text(
                            text = "Nạp tiền",
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineMedium,
                            fontWeight =
                                FontWeight.ExtraBold
                        )

                        Text(
                            text =
                                "Nạp tiền nhanh chóng và an toàn",
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
                }

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )

                /*
                |--------------------------------------------------------------------------
                | BALANCE
                |--------------------------------------------------------------------------
                */

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush =
                                Brush.linearGradient(
                                    colors =
                                        listOf(
                                            BrandPurpleDark,
                                            BrandPurple,
                                            Color(0xFF7D58FF)
                                        )
                                ),
                            shape =
                                RoundedCornerShape(28.dp)
                        )
                        .padding(22.dp)
                ) {

                    Column {

                        Text(
                            text =
                                "Số dư hiện tại",
                            color =
                                Color.White.copy(
                                    alpha = 0.80f
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.height(7.dp)
                        )

                        if (
                            state.isLoadingBalance &&
                            state.balance == 0.0
                        ) {

                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(28.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )

                        } else {

                            Text(
                                text =
                                    "${formatter.format(state.balance)} đ",
                                style =
                                    MaterialTheme
                                        .typography
                                        .headlineLarge,
                                fontWeight =
                                    FontWeight.ExtraBold,
                                color =
                                    Color.White
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Surface(
                            shape =
                                RoundedCornerShape(50),
                            color =
                                Color.White.copy(
                                    alpha = 0.12f
                                )
                        ) {

                            Text(
                                text =
                                    "Tích lũy hôm nay • Thịnh vượng ngày mai",
                                modifier =
                                    Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                color =
                                    BrandGold,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,
                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        }
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

            DepositSectionCard {

                Text(
                    text =
                        "Số tiền nạp",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.ExtraBold
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = {

                        amount =
                            it.filter { char ->
                                char.isDigit()
                            }

                        errorMessage = null
                        walletViewModel.clearMessage()
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Nhập số tiền"
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector =
                                Icons.Default.Payments,
                            contentDescription = null,
                            tint = BrandPurple
                        )
                    },
                    suffix = {
                        Text("đ")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),
                    singleLine = true,
                    shape =
                        RoundedCornerShape(18.dp)
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        androidx.compose.foundation.layout.Arrangement.spacedBy(
                            7.dp
                        )
                ) {

                    quickAmounts.forEach { value ->

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {

                                    amount =
                                        value.toString()

                                    errorMessage = null

                                    walletViewModel
                                        .clearMessage()
                                },
                            shape =
                                RoundedCornerShape(14.dp),
                            color =
                                BrandPurple.copy(
                                    alpha = 0.08f
                                )
                        ) {

                            Text(
                                text =
                                    formatter.format(value),
                                modifier =
                                    Modifier.padding(
                                        vertical = 10.dp
                                    ),
                                color =
                                    BrandPurple,
                                fontWeight =
                                    FontWeight.SemiBold,
                                textAlign =
                                    TextAlign.Center,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall
                            )
                        }
                    }
                }
            }
        }

        /*
        |--------------------------------------------------------------------------
        | METHOD
        |--------------------------------------------------------------------------
        */

        item {

            DepositSectionCard {

                Text(
                    text =
                        "Phương thức nạp tiền",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.ExtraBold
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                DepositMethod(
                    icon =
                        Icons.Default.AccountBalance,
                    title =
                        "Tài khoản ngân hàng",
                    subtitle =
                        "Ngân hàng liên kết",
                    selected =
                        selectedMethod == "bank",
                    onClick = {
                        selectedMethod = "bank"
                    }
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                DepositMethod(
                    icon =
                        Icons.Default.CreditCard,
                    title =
                        "Thẻ ATM / Thẻ ghi nợ",
                    subtitle =
                        "Visa, Mastercard, Napas",
                    selected =
                        selectedMethod == "card",
                    onClick = {
                        selectedMethod = "card"
                    }
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                DepositMethod(
                    icon =
                        Icons.Default.Wallet,
                    title =
                        "Ví điện tử",
                    subtitle =
                        "MoMo, ZaloPay, Viettel Money",
                    selected =
                        selectedMethod == "wallet",
                    onClick = {
                        selectedMethod = "wallet"
                    }
                )
            }
        }

        /*
        |--------------------------------------------------------------------------
        | NOTE + BUTTON
        |--------------------------------------------------------------------------
        */

        item {

            DepositSectionCard {

                Text(
                    text =
                        "Ghi chú",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.ExtraBold
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = {

                        if (
                            it.length <= 100
                        ) {
                            note = it
                        }
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Ví dụ: Nạp tiền tiết kiệm"
                        )
                    },
                    minLines = 2,
                    maxLines = 3,
                    shape =
                        RoundedCornerShape(18.dp)
                )

                /*
                |--------------------------------------------------------------------------
                | LOCAL ERROR
                |--------------------------------------------------------------------------
                */

                if (
                    !errorMessage
                        .isNullOrBlank()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    DepositMessage(
                        message =
                            errorMessage ?: "",
                        isError = true
                    )
                }

                /*
                |--------------------------------------------------------------------------
                | BACKEND ERROR
                |--------------------------------------------------------------------------
                */

                if (
                    !state.error
                        .isNullOrBlank()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    DepositMessage(
                        message =
                            state.error ?: "",
                        isError = true
                    )
                }

                /*
                |--------------------------------------------------------------------------
                | SUCCESS
                |--------------------------------------------------------------------------
                */

                if (
                    !state.successMessage
                        .isNullOrBlank()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    DepositMessage(
                        message =
                            state.successMessage ?: "",
                        isError = false
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )

                Button(
                    onClick = {

                        val amountValue =
                            amount.toDoubleOrNull()

                        if (
                            amountValue == null ||
                            amountValue <= 0
                        ) {

                            errorMessage =
                                "Vui lòng nhập số tiền hợp lệ."

                            return@Button
                        }

                        errorMessage = null

                        walletViewModel.deposit(
                            amount =
                                amountValue,
                            method =
                                selectedMethod,
                            note =
                                note.ifBlank {
                                    null
                                },
                            onSuccess = {

                                depositSuccessAmount =
                                    amountValue
                            }
                        )
                    },
                    enabled =
                        !state.isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape =
                        RoundedCornerShape(19.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                BrandPurple
                        )
                ) {

                    if (
                        state.isSubmitting
                    ) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )

                    } else {

                        Icon(
                            imageVector =
                                Icons.Default.Add,
                            contentDescription =
                                null
                        )

                        Spacer(
                            modifier =
                                Modifier.size(7.dp)
                        )

                        Text(
                            text =
                                "Xác nhận nạp tiền",
                            fontWeight =
                                FontWeight.Bold,
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium
                        )
                    }
                }
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
private fun DepositSectionCard(
    content:
    @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 18.dp,
                end = 18.dp,
                top = 14.dp
            ),
        shape =
            RoundedCornerShape(24.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp),
            content = content
        )
    }
}


/*
|--------------------------------------------------------------------------
| METHOD
|--------------------------------------------------------------------------
*/

@Composable
private fun DepositMethod(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape =
            RoundedCornerShape(18.dp),
        color =
            if (selected) {

                BrandPurple.copy(
                    alpha = 0.08f
                )

            } else {

                MaterialTheme
                    .colorScheme
                    .surfaceVariant
                    .copy(
                        alpha = 0.45f
                    )
            }
    ) {

        Row(
            modifier =
                Modifier.padding(14.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Surface(
                modifier =
                    Modifier.size(44.dp),
                shape =
                    RoundedCornerShape(14.dp),
                color =
                    BrandPurple.copy(
                        alpha = 0.12f
                    )
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription =
                            null,
                        tint =
                            BrandPurple
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.size(12.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text = subtitle,
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

            RadioButton(
                selected =
                    selected,
                onClick =
                    onClick
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
private fun DepositMessage(
    message: String,
    isError: Boolean
) {

    val backgroundColor =
        if (isError) {

            MaterialTheme
                .colorScheme
                .errorContainer

        } else {

            BrandPurple.copy(
                alpha = 0.09f
            )
        }

    val textColor =
        if (isError) {

            MaterialTheme
                .colorScheme
                .onErrorContainer

        } else {

            BrandPurple
        }

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(14.dp),
        color =
            backgroundColor
    ) {

        Text(
            text =
                message,
            modifier =
                Modifier.padding(12.dp),
            color =
                textColor,
            style =
                MaterialTheme
                    .typography
                    .bodySmall,
            fontWeight =
                FontWeight.SemiBold
        )
    }
}

@Composable
private fun DepositSuccessRow(
    title: String,
    value: String
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 5.dp
                ),
        horizontalArrangement =
            androidx.compose.foundation.layout.Arrangement
                .SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text =
                title,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )

        Text(
            text =
                value,
            fontWeight =
                FontWeight.Bold,
            textAlign =
                TextAlign.End
        )
    }
}