package com.example.personalfinance.ui.wallet

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import com.example.personalfinance.data.model.TransferData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinance.ui.theme.BrandGold
import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.ui.theme.BrandPurpleDark
import com.example.personalfinance.viewmodel.WalletViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransferScreen(
    onBack: () -> Unit,
    walletViewModel: WalletViewModel = viewModel()
) {

    val state by
    walletViewModel.uiState.collectAsState()

    val recipient by
    walletViewModel.recipient.collectAsState()

    val formatter =
        NumberFormat.getNumberInstance(
            Locale("vi", "VN")
        )

    var accountNumber by remember {
        mutableStateOf("")
    }

    var amount by remember {
        mutableStateOf("")
    }

    var note by remember {
        mutableStateOf("")
    }

    var localError by remember {
        mutableStateOf<String?>(null)
    }

    var transferResult by remember {
        mutableStateOf<TransferData?>(null)
    }

    val quickAmounts =
        listOf(
            100_000,
            500_000,
            1_000_000,
            2_000_000
        )

    LaunchedEffect(Unit) {
        walletViewModel.loadBalance()
    }

    if (transferResult != null) {

        val result =
            transferResult!!

        AlertDialog(

            onDismissRequest = {
                // Không cho tắt bằng cách bấm ra ngoài
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
                        "Chuyển tiền thành công",
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
                            "${formatter.format(result.amount)} đ",
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
                            Modifier.height(16.dp)
                    )

                    TransferSuccessRow(
                        title =
                            "Người nhận",
                        value =
                            result.receiver_name
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?: "Người dùng Tích Phú"
                    )

                    TransferSuccessRow(
                        title =
                            "Số tài khoản",
                        value =
                            result.receiver_account_number
                                ?: ""
                    )

                    TransferSuccessRow(
                        title =
                            "Số dư còn lại",
                        value =
                            "${formatter.format(result.balance)} đ"
                    )
                }
            },

            confirmButton = {

                Button(
                    onClick = {

                        transferResult =
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
                start = 16.dp,
                end = 16.dp,
                top = 14.dp,
                bottom = 34.dp
            ),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
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
                        Modifier.size(42.dp),
                    shape =
                        CircleShape,
                    color =
                        MaterialTheme
                            .colorScheme
                            .surfaceVariant
                            .copy(alpha = 0.7f)
                ) {

                    IconButton(
                        onClick = onBack
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
                        Modifier.size(10.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Chuyển tiền",
                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium,
                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Text(
                        text =
                            "Chuyển khoản nhanh chóng và an toàn",
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

                Surface(
                    modifier =
                        Modifier.size(42.dp),
                    shape =
                        CircleShape,
                    color =
                        BrandPurple.copy(
                            alpha = 0.08f
                        )
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.HelpOutline,
                            contentDescription =
                                null,
                            tint =
                                BrandPurple
                        )
                    }
                }
            }
        }

        /*
        |--------------------------------------------------------------------------
        | BALANCE
        |--------------------------------------------------------------------------
        */

        item {

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(26.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.Transparent
                    )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush =
                                Brush.linearGradient(
                                    listOf(
                                        BrandPurpleDark,
                                        BrandPurple,
                                        Color(0xFF7D39FF)
                                    )
                                ),
                            shape =
                                RoundedCornerShape(
                                    26.dp
                                )
                        )
                        .padding(20.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .align(
                                Alignment.TopEnd
                            )
                            .clip(CircleShape)
                            .background(
                                Color.White.copy(
                                    alpha = 0.05f
                                )
                            )
                    )

                    Column {

                        Text(
                            text =
                                "Số dư khả dụng",
                            color =
                                Color.White.copy(
                                    alpha = 0.82f
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        if (
                            state.isLoadingBalance &&
                            state.balance == 0.0
                        ) {

                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(28.dp),
                                strokeWidth = 2.dp,
                                color =
                                    Color.White
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
                                    Color.White,
                                maxLines =
                                    1,
                                overflow =
                                    TextOverflow.Ellipsis
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
                                Color(0xFF0DBE8C)
                        ) {

                            Text(
                                text =
                                    "Phí 0đ • chuyển trong hệ thống",
                                modifier =
                                    Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                color =
                                    Color.White,
                                fontWeight =
                                    FontWeight.Bold,
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
        | NGƯỜI NHẬN
        |--------------------------------------------------------------------------
        */

        item {

            TransferCard {

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
                            "Người nhận",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Person,
                            contentDescription =
                                null,
                            tint =
                                BrandPurple,
                            modifier =
                                Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.size(4.dp)
                        )

                        Text(
                            text =
                                "Chọn từ danh bạ",
                            color =
                                BrandPurple,
                            fontWeight =
                                FontWeight.SemiBold,
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                /*
                |--------------------------------------------------------------------------
                | THÔNG TIN NGƯỜI NHẬN
                |--------------------------------------------------------------------------
                */

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(17.dp),
                    color =
                        BrandPurple.copy(
                            alpha = 0.055f
                        )
                ) {

                    Row(
                        modifier =
                            Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 10.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Surface(
                            modifier =
                                Modifier.size(44.dp),
                            shape =
                                CircleShape,
                            color =
                                BrandPurple.copy(
                                    alpha = 0.13f
                                )
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Person,
                                    contentDescription =
                                        null,
                                    tint =
                                        BrandPurple,
                                    modifier =
                                        Modifier.size(25.dp)
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

                            if (
                                recipient != null
                            ) {

                                Text(
                                    text =
                                        recipient?.name
                                            ?.takeIf {
                                                it.isNotBlank()
                                            }
                                            ?: "Người dùng Tích Phú",
                                    fontWeight =
                                        FontWeight.ExtraBold,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(2.dp)
                                )

                                Text(
                                    text =
                                        "Tích Phú •••• ${
                                            recipient
                                                ?.account_number
                                                ?.takeLast(4)
                                                ?: ""
                                        }",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )

                            } else {

                                Text(
                                    text =
                                        "Chưa chọn người nhận",
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(2.dp)
                                )

                                Text(
                                    text =
                                        "Nhập số tài khoản bên dưới",
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

                        Text(
                            text =
                                "›",
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineSmall,
                            color =
                                BrandPurple
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                /*
                |--------------------------------------------------------------------------
                | SỐ TÀI KHOẢN
                |--------------------------------------------------------------------------
                */

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    Text(
                        text =
                            "Số tài khoản",
                        fontWeight =
                            FontWeight.SemiBold,
                        modifier =
                            Modifier.weight(0.32f)
                    )

                    OutlinedTextField(
                        value =
                            formatAccountNumber(
                                accountNumber
                            ),

                        onValueChange = { input ->

                            val clean =
                                input
                                    .filter {
                                        it.isDigit()
                                    }
                                    .take(12)

                            accountNumber =
                                clean

                            localError =
                                null

                            walletViewModel
                                .clearMessage()

                            walletViewModel
                                .clearRecipient()

                            if (
                                clean.length == 12
                            ) {

                                walletViewModel
                                    .lookupRecipient(
                                        clean
                                    )
                            }
                        },

                        modifier =
                            Modifier.weight(0.68f),

                        placeholder = {

                            Text(
                                "1024 5688 8899"
                            )
                        },

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            ),

                        singleLine =
                            true,

                        shape =
                            RoundedCornerShape(
                                15.dp
                            )
                    )
                }

                /*
                |--------------------------------------------------------------------------
                | TRA CỨU
                |--------------------------------------------------------------------------
                */

                if (
                    state.isLookingUpRecipient &&
                    accountNumber.length == 12 &&
                    recipient == null
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(16.dp),
                            strokeWidth =
                                2.dp,
                            color =
                                BrandPurple
                        )

                        Spacer(
                            modifier =
                                Modifier.size(7.dp)
                        )

                        Text(
                            text =
                                "Đang kiểm tra tài khoản...",
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
        | AMOUNT
        |--------------------------------------------------------------------------
        */

        item {

            TransferCard {

                Text(
                    text =
                        "Số tiền chuyển",
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
                    value =
                        amount,
                    onValueChange = {

                        amount =
                            it.filter { char ->
                                char.isDigit()
                            }

                        localError =
                            null

                        walletViewModel
                            .clearMessage()
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("0")
                    },
                    suffix = {
                        Text(
                            "đ",
                            fontWeight =
                                FontWeight.Bold
                        )
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),
                    singleLine =
                        true,
                    shape =
                        RoundedCornerShape(
                            16.dp
                        )
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            7.dp
                        )
                ) {

                    quickAmounts.forEach {
                            quickAmount ->

                        Surface(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .clickable {

                                        amount =
                                            quickAmount
                                                .toString()

                                        localError =
                                            null
                                    },
                            shape =
                                RoundedCornerShape(
                                    50
                                ),
                            color =
                                BrandPurple.copy(
                                    alpha =
                                        0.08f
                                )
                        ) {

                            Text(
                                text =
                                    formatter
                                        .format(
                                            quickAmount
                                        ),
                                modifier =
                                    Modifier.padding(
                                        vertical =
                                            9.dp
                                    ),
                                textAlign =
                                    TextAlign.Center,
                                color =
                                    BrandPurple,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Text(
                        text =
                            "Lời nhắn (tùy chọn)",
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            "${note.length}/50",
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(7.dp)
                )

                OutlinedTextField(
                    value =
                        note,
                    onValueChange = {

                        if (
                            it.length <= 50
                        ) {
                            note = it
                        }
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Ví dụ: Chuyển tiền ăn uống"
                        )
                    },
                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.Edit,
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
            }
        }

        /*
        |--------------------------------------------------------------------------
        | SOURCE
        |--------------------------------------------------------------------------
        */

        item {

            TransferCard {

                Text(
                    text =
                        "Tài khoản nguồn",
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

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),
                    color =
                        BrandPurple.copy(
                            alpha =
                                0.05f
                        )
                ) {

                    Row(
                        modifier =
                            Modifier.padding(
                                13.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Surface(
                            modifier =
                                Modifier.size(44.dp),
                            shape =
                                RoundedCornerShape(
                                    13.dp
                                ),
                            color =
                                BrandPurple
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
                                        Color.White
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
                                text =
                                    "Ví Tích Phú •••• ${
                                        state.accountNumber
                                            ?.takeLast(4)
                                            ?: "----"
                                    }",
                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                text =
                                    "Số dư: ${
                                        formatter.format(
                                            state.balance
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

                        Text(
                            text = "›",
                            color =
                                BrandPurple,
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineSmall
                        )
                    }
                }
            }
        }

        /*
        |--------------------------------------------------------------------------
        | TRANSACTION INFO
        |--------------------------------------------------------------------------
        */

        item {

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(
                        22.dp
                    ),
                color =
                    BrandGold.copy(
                        alpha =
                            0.16f
                    )
            ) {

                Row(
                    modifier =
                        Modifier.padding(
                            16.dp
                        ),
                    verticalAlignment =
                        Alignment.Top
                ) {

                    Surface(
                        modifier =
                            Modifier.size(48.dp),
                        shape =
                            RoundedCornerShape(
                                14.dp
                            ),
                        color =
                            BrandGold.copy(
                                alpha =
                                    0.30f
                            )
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Send,
                                contentDescription =
                                    null,
                                tint =
                                    Color(0xFFB87A00)
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
                            text =
                                "Thông tin giao dịch",
                            fontWeight =
                                FontWeight.ExtraBold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        TransferInfoRow(
                            title =
                                "Phí chuyển:",
                            value =
                                "0 đ"
                        )

                        TransferInfoRow(
                            title =
                                "Thời gian:",
                            value =
                                "Ngay lập tức"
                        )

                        TransferInfoRow(
                            title =
                                "Nội dung:",
                            value =
                                "Chuyển tiền cá nhân"
                        )
                    }
                }
            }
        }

        /*
        |--------------------------------------------------------------------------
        | ERROR
        |--------------------------------------------------------------------------
        */

        if (
            !localError.isNullOrBlank()
        ) {

            item {

                TransferMessage(
                    message =
                        localError ?: "",
                    isError =
                        true
                )
            }
        }

        if (
            !state.error.isNullOrBlank()
        ) {

            item {

                TransferMessage(
                    message =
                        state.error ?: "",
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

                    val amountValue =
                        amount.toDoubleOrNull()

                    if (
                        accountNumber.length != 12
                    ) {

                        localError =
                            "Vui lòng nhập đúng số tài khoản người nhận."

                        return@Button
                    }

                    if (
                        recipient == null
                    ) {

                        localError =
                            "Không tìm thấy người nhận. Vui lòng kiểm tra số tài khoản."

                        return@Button
                    }

                    if (
                        amountValue == null ||
                        amountValue <= 0
                    ) {

                        localError =
                            "Vui lòng nhập số tiền hợp lệ."

                        return@Button
                    }

                    if (
                        amountValue >
                        state.balance
                    ) {

                        localError =
                            "Số dư không đủ để thực hiện giao dịch."

                        return@Button
                    }

                    localError = null

                    walletViewModel.transfer(
                        receiver =
                            accountNumber,
                        amount =
                            amountValue,
                        note =
                            note.ifBlank {
                                null
                            },
                        onSuccess = { result ->

                            transferResult =
                                result
                        }
                    )
                },
                enabled =
                    !state.isSubmitting,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                shape =
                    RoundedCornerShape(
                        18.dp
                    ),
                colors =
                    ButtonDefaults
                        .buttonColors(
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
                        strokeWidth =
                            2.dp,
                        color =
                            Color.White
                    )

                } else {

                    Text(
                        text =
                            "Xác nhận chuyển tiền",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.size(8.dp)
                    )

                    Icon(
                        imageVector =
                            Icons.Default.Send,
                        contentDescription =
                            null
                    )
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| FORMAT ACCOUNT NUMBER
|--------------------------------------------------------------------------
*/

private fun formatAccountNumber(
    value: String
): String {

    return value
        .filter {
            it.isDigit()
        }
        .chunked(4)
        .joinToString(" ")
}


/*
|--------------------------------------------------------------------------
| CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun TransferCard(
    content:
    @Composable ColumnScope.() -> Unit
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
                Modifier.padding(
                    16.dp
                ),
            content =
                content
        )
    }
}


/*
|--------------------------------------------------------------------------
| INFO ROW
|--------------------------------------------------------------------------
*/

@Composable
private fun TransferInfoRow(
    title: String,
    value: String
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 3.dp
                ),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text =
                title,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant,
            style =
                MaterialTheme
                    .typography
                    .bodySmall
        )

        Text(
            text =
                value,
            fontWeight =
                FontWeight.SemiBold,
            style =
                MaterialTheme
                    .typography
                    .bodySmall
        )
    }
}


/*
|--------------------------------------------------------------------------
| MESSAGE
|--------------------------------------------------------------------------
*/

@Composable
private fun TransferMessage(
    message: String,
    isError: Boolean
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                14.dp
            ),
        color =
            if (isError) {

                MaterialTheme
                    .colorScheme
                    .errorContainer

            } else {

                BrandPurple.copy(
                    alpha = 0.09f
                )
            }
    ) {

        Text(
            text =
                message,
            modifier =
                Modifier.padding(
                    12.dp
                ),
            color =
                if (isError) {

                    MaterialTheme
                        .colorScheme
                        .onErrorContainer

                } else {

                    BrandPurple
                },
            fontWeight =
                FontWeight.SemiBold,
            style =
                MaterialTheme
                    .typography
                    .bodySmall
        )
    }
}

@Composable
private fun TransferSuccessRow(
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
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text =
                title,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant,
            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )

        Text(
            text =
                value,
            fontWeight =
                FontWeight.Bold,
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            textAlign =
                TextAlign.End
        )
    }
}