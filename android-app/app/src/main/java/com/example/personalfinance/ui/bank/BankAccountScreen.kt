package com.example.personalfinance.ui.bank

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Search

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.personalfinance.data.model.BankAccount
import com.example.personalfinance.viewmodel.BankAccountViewModel


/*
|--------------------------------------------------------------------------
| COLORS
|--------------------------------------------------------------------------
*/

private val BankGreen =
    Color(
        0xFF13A05A
    )

private val BankGreenDark =
    Color(
        0xFF08783E
    )

private val BankDanger =
    Color(
        0xFFE53935
    )


/*
|--------------------------------------------------------------------------
| SUPPORTED BANK
|--------------------------------------------------------------------------
*/

data class SupportedBank(
    val code: String,
    val name: String,
    val shortName: String
)


private val supportedBanks =
    listOf(

        SupportedBank(
            code = "VCB",
            name = "Ngân hàng TMCP Ngoại thương Việt Nam",
            shortName = "Vietcombank"
        ),

        SupportedBank(
            code = "BIDV",
            name = "Ngân hàng TMCP Đầu tư và Phát triển Việt Nam",
            shortName = "BIDV"
        ),

        SupportedBank(
            code = "CTG",
            name = "Ngân hàng TMCP Công thương Việt Nam",
            shortName = "VietinBank"
        ),

        SupportedBank(
            code = "MBB",
            name = "Ngân hàng TMCP Quân đội",
            shortName = "MB Bank"
        ),

        SupportedBank(
            code = "TCB",
            name = "Ngân hàng TMCP Kỹ thương Việt Nam",
            shortName = "Techcombank"
        ),

        SupportedBank(
            code = "TPB",
            name = "Ngân hàng TMCP Tiên Phong",
            shortName = "TPBank"
        ),

        SupportedBank(
            code = "ACB",
            name = "Ngân hàng TMCP Á Châu",
            shortName = "ACB"
        ),

        SupportedBank(
            code = "STB",
            name = "Ngân hàng TMCP Sài Gòn Thương Tín",
            shortName = "Sacombank"
        )
    )


/*
|--------------------------------------------------------------------------
| SCREEN
|--------------------------------------------------------------------------
*/

@Composable
fun BankAccountScreen(
    onBack: () -> Unit,
    viewModel: BankAccountViewModel = viewModel()
) {

    val state by
    viewModel.uiState
        .collectAsState()


    /*
    |--------------------------------------------------------------------------
    | DIALOG STATES
    |--------------------------------------------------------------------------
    */

    var showSelectBank by remember {
        mutableStateOf(
            false
        )
    }


    var selectedBank by remember {
        mutableStateOf<SupportedBank?>(
            null
        )
    }


    var selectedAccount by remember {
        mutableStateOf<BankAccount?>(
            null
        )
    }


    var accountToSetDefault by remember {
        mutableStateOf<BankAccount?>(
            null
        )
    }


    var accountToDelete by remember {
        mutableStateOf<BankAccount?>(
            null
        )
    }


    /*
    |--------------------------------------------------------------------------
    | LOAD
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(Unit) {

        viewModel.loadAccounts()
    }


    /*
    |--------------------------------------------------------------------------
    | DATA
    |--------------------------------------------------------------------------
    */

    val defaultAccount =
        state.accounts
            .firstOrNull {
                it.isDefault
            }


    val otherAccounts =
        state.accounts
            .filter {
                !it.isDefault
            }


    /*
    |--------------------------------------------------------------------------
    | ROOT
    |--------------------------------------------------------------------------
    */

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                )
    ) {


        /*
        |--------------------------------------------------------------------------
        | HEADER
        |--------------------------------------------------------------------------
        */

        BankAccountHeader(
            onBack =
                onBack,
            onAdd = {

                showSelectBank =
                    true
            }
        )


        /*
        |--------------------------------------------------------------------------
        | CONTENT
        |--------------------------------------------------------------------------
        */

        LazyColumn(
            modifier =
                Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 6.dp,
                    bottom = 36.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    14.dp
                )
        ) {


            /*
            |--------------------------------------------------------------------------
            | MESSAGE ERROR
            |--------------------------------------------------------------------------
            */

            if (
                !state.error.isNullOrBlank()
            ) {

                item {

                    BankMessageCard(
                        text =
                            state.error
                                ?: "",
                        error =
                            true
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | SUCCESS MESSAGE
            |--------------------------------------------------------------------------
            */

            if (
                !state.successMessage
                    .isNullOrBlank()
            ) {

                item {

                    BankMessageCard(
                        text =
                            state.successMessage
                                ?: "",
                        error =
                            false
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | LOADING
            |--------------------------------------------------------------------------
            */

            if (
                state.isLoading &&
                state.accounts.isEmpty()
            ) {

                item {

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    260.dp
                                ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        CircularProgressIndicator(
                            color =
                                BankGreen
                        )
                    }
                }

                return@LazyColumn
            }


            /*
            |--------------------------------------------------------------------------
            | SUMMARY
            |--------------------------------------------------------------------------
            */

            item {

                BankAccountSummaryCard(
                    accountCount =
                        state.accounts.size,
                    defaultAccount =
                        defaultAccount
                )
            }


            /*
            |--------------------------------------------------------------------------
            | EMPTY
            |--------------------------------------------------------------------------
            */

            if (
                state.accounts.isEmpty()
            ) {

                item {

                    EmptyBankCard(
                        onAdd = {

                            showSelectBank =
                                true
                        }
                    )
                }

            } else {


                /*
                |--------------------------------------------------------------------------
                | DEFAULT ACCOUNT
                |--------------------------------------------------------------------------
                */

                item {

                    Text(
                        text =
                            "Tài khoản thanh toán mặc định",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onBackground
                    )
                }


                item {

                    if (
                        defaultAccount != null
                    ) {

                        DefaultBankCard(
                            account =
                                defaultAccount,
                            onClick = {

                                selectedAccount =
                                    defaultAccount
                            }
                        )

                    } else {

                        NoDefaultBankCard()
                    }
                }


                /*
                |--------------------------------------------------------------------------
                | OTHER ACCOUNTS
                |--------------------------------------------------------------------------
                */

                if (
                    otherAccounts.isNotEmpty()
                ) {

                    item {

                        Spacer(
                            modifier =
                                Modifier.height(
                                    2.dp
                                )
                        )


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
                                    "Các tài khoản khác",
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium,
                                fontWeight =
                                    FontWeight.ExtraBold
                            )


                            Text(
                                text =
                                    "${otherAccounts.size} tài khoản",
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


                    items(
                        items =
                            otherAccounts,
                        key = {
                            it.id
                        }
                    ) {
                            account ->

                        OtherBankCard(
                            account =
                                account,
                            onClick = {

                                selectedAccount =
                                    account
                            }
                        )
                    }
                }


                /*
                |--------------------------------------------------------------------------
                | ADD BUTTON
                |--------------------------------------------------------------------------
                */

                item {

                    Button(
                        onClick = {

                            showSelectBank =
                                true
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
                                        BankGreen
                                )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Add,
                            contentDescription =
                                null
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    8.dp
                                )
                        )


                        Text(
                            text =
                                "Liên kết tài khoản ngân hàng",
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }


            /*
            |--------------------------------------------------------------------------
            | SIMULATION INFO
            |--------------------------------------------------------------------------
            */

            item {

                BankSimulationInfo()
            }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | SELECT BANK
    |--------------------------------------------------------------------------
    */

    if (
        showSelectBank
    ) {

        SelectBankDialog(
            onDismiss = {

                showSelectBank =
                    false
            },
            onBankSelected = {
                    bank ->

                selectedBank =
                    bank

                showSelectBank =
                    false
            }
        )
    }


    /*
    |--------------------------------------------------------------------------
    | LINK BANK
    |--------------------------------------------------------------------------
    */

    selectedBank?.let {
            bank ->

        LinkBankDialog(
            bank =
                bank,
            onDismiss = {

                selectedBank =
                    null
            },
            onLink = {
                    accountNumber,
                    accountName,
                    isDefault ->

                viewModel
                    .linkBankAccount(
                        bankCode =
                            bank.code,
                        bankName =
                            bank.shortName,
                        accountNumber =
                            accountNumber,
                        accountName =
                            accountName,
                        isDefault =
                            isDefault,
                        onSuccess = {

                            selectedBank =
                                null
                        }
                    )
            }
        )
    }


    /*
    |--------------------------------------------------------------------------
    | ACCOUNT OPTIONS
    |--------------------------------------------------------------------------
    */

    selectedAccount?.let {
            account ->

        BankAccountOptionsDialog(
            account =
                account,
            onDismiss = {

                selectedAccount =
                    null
            },
            onSetDefault = {

                selectedAccount =
                    null

                accountToSetDefault =
                    account
            },
            onDelete = {

                selectedAccount =
                    null

                accountToDelete =
                    account
            }
        )
    }


    /*
    |--------------------------------------------------------------------------
    | SET DEFAULT CONFIRM
    |--------------------------------------------------------------------------
    */

    accountToSetDefault?.let {
            account ->

        AlertDialog(
            onDismissRequest = {

                accountToSetDefault =
                    null
            },
            shape =
                RoundedCornerShape(
                    24.dp
                ),
            title = {

                Text(
                    text =
                        "Đặt làm tài khoản mặc định?",
                    fontWeight =
                        FontWeight.ExtraBold
                )
            },
            text = {

                Text(
                    text =
                        "${account.bankName} ${
                            account.maskedAccountNumber
                                ?: maskAccount(
                                    account.accountNumber
                                )
                        } sẽ được sử dụng làm tài khoản thanh toán mặc định."
                )
            },
            confirmButton = {

                Button(
                    onClick = {

                        viewModel.setDefault(
                            account.id
                        )

                        accountToSetDefault =
                            null
                    },
                    colors =
                        ButtonDefaults
                            .buttonColors(
                                containerColor =
                                    BankGreen
                            ),
                    shape =
                        RoundedCornerShape(
                            14.dp
                        )
                ) {

                    Text(
                        text =
                            "Đặt mặc định",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {

                        accountToSetDefault =
                            null
                    }
                ) {

                    Text(
                        "Hủy"
                    )
                }
            }
        )
    }


    /*
    |--------------------------------------------------------------------------
    | DELETE CONFIRM
    |--------------------------------------------------------------------------
    */

    accountToDelete?.let {
            account ->

        AlertDialog(
            onDismissRequest = {

                accountToDelete =
                    null
            },
            shape =
                RoundedCornerShape(
                    24.dp
                ),
            icon = {

                Icon(
                    imageVector =
                        Icons.Default.Delete,
                    contentDescription =
                        null,
                    tint =
                        BankDanger
                )
            },
            title = {

                Text(
                    text =
                        "Hủy liên kết tài khoản?",
                    fontWeight =
                        FontWeight.ExtraBold
                )
            },
            text = {

                Text(
                    text =
                        "Bạn có chắc muốn hủy liên kết ${account.bankName} ${
                            account.maskedAccountNumber
                                ?: maskAccount(
                                    account.accountNumber
                                )
                        }?"
                )
            },
            confirmButton = {

                Button(
                    onClick = {

                        viewModel
                            .unlinkBankAccount(
                                account.id
                            )

                        accountToDelete =
                            null
                    },
                    colors =
                        ButtonDefaults
                            .buttonColors(
                                containerColor =
                                    BankDanger
                            ),
                    shape =
                        RoundedCornerShape(
                            14.dp
                        )
                ) {

                    Text(
                        text =
                            "Hủy liên kết",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {

                        accountToDelete =
                            null
                    }
                ) {

                    Text(
                        "Đóng"
                    )
                }
            }
        )
    }
}


/*
|--------------------------------------------------------------------------
| HEADER
|--------------------------------------------------------------------------
*/

@Composable
private fun BankAccountHeader(
    onBack: () -> Unit,
    onAdd: () -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 16.dp,
                    bottom = 10.dp
                ),
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
                    "Tài khoản ngân hàng",
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
                    "Quản lý tài khoản thanh toán mô phỏng",
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
                Modifier.size(
                    42.dp
                ),
            shape =
                RoundedCornerShape(
                    14.dp
                ),
            color =
                BankGreen
        ) {

            IconButton(
                onClick =
                    onAdd
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Add,
                    contentDescription =
                        "Thêm tài khoản",
                    tint =
                        Color.White
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| SUMMARY
|--------------------------------------------------------------------------
*/

@Composable
private fun BankAccountSummaryCard(
    accountCount: Int,
    defaultAccount: BankAccount?
) {

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
                    BankGreen.copy(
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
                        54.dp
                    ),
                shape =
                    RoundedCornerShape(
                        18.dp
                    ),
                color =
                    BankGreen.copy(
                        alpha = 0.14f
                    )
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Outlined
                                .AccountBalance,
                        contentDescription =
                            null,
                        tint =
                            BankGreen,
                        modifier =
                            Modifier.size(
                                29.dp
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
                            accountCount == 0
                        ) {

                            "Chưa liên kết tài khoản"

                        } else {

                            "$accountCount tài khoản đã liên kết"
                        },
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
                        if (
                            defaultAccount != null
                        ) {

                            "Mặc định: ${defaultAccount.bankName} ${
                                defaultAccount.maskedAccountNumber
                                    ?: maskAccount(
                                        defaultAccount.accountNumber
                                    )
                            }"

                        } else {

                            "Chưa thiết lập tài khoản thanh toán mặc định"
                        },
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                    maxLines =
                        2,
                    overflow =
                        TextOverflow.Ellipsis
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| DEFAULT BANK CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun DefaultBankCard(
    account: BankAccount,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick
                ),
        shape =
            RoundedCornerShape(
                22.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    BankGreen.copy(
                        alpha = 0.08f
                    )
            ),
        border =
            BorderStroke(
                width =
                    1.dp,
                color =
                    BankGreen.copy(
                        alpha = 0.55f
                    )
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        17.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            BankLogo(
                bankCode =
                    account.bankCode
            )


            Spacer(
                modifier =
                    Modifier.width(
                        13.dp
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
                        account.bankName,
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


                Spacer(
                    modifier =
                        Modifier.height(
                            2.dp
                        )
                )


                Text(
                    text =
                        account.accountName,
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow.Ellipsis
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            5.dp
                        )
                )


                Text(
                    text =
                        account.maskedAccountNumber
                            ?: maskAccount(
                                account.accountNumber
                            ),
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier =
                    Modifier.width(
                        8.dp
                    )
            )


            Surface(
                shape =
                    RoundedCornerShape(
                        50.dp
                    ),
                color =
                    BankGreen
            ) {

                Row(
                    modifier =
                        Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Check,
                        contentDescription =
                            null,
                        tint =
                            Color.White,
                        modifier =
                            Modifier.size(
                                14.dp
                            )
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                4.dp
                            )
                    )


                    Text(
                        text =
                            "Mặc định",
                        color =
                            Color.White,
                        style =
                            MaterialTheme
                                .typography
                                .labelSmall,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| OTHER BANK
|--------------------------------------------------------------------------
*/

@Composable
private fun OtherBankCard(
    account: BankAccount,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick
                ),
        shape =
            RoundedCornerShape(
                20.dp
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

            BankLogo(
                bankCode =
                    account.bankCode
            )


            Spacer(
                modifier =
                    Modifier.width(
                        13.dp
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
                        account.bankName,
                    style =
                        MaterialTheme
                            .typography
                            .titleSmall,
                    fontWeight =
                        FontWeight.ExtraBold,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow.Ellipsis
                )


                Text(
                    text =
                        account.accountName,
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
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
                        account.maskedAccountNumber
                            ?: maskAccount(
                                account.accountNumber
                            ),
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    fontWeight =
                        FontWeight.SemiBold
                )
            }


            Icon(
                imageVector =
                    Icons.Default
                        .KeyboardArrowRight,
                contentDescription =
                    "Xem tùy chọn",
                tint =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| NO DEFAULT
|--------------------------------------------------------------------------
*/

@Composable
private fun NoDefaultBankCard() {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                18.dp
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
                    16.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Default.Star,
                contentDescription =
                    null,
                tint =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )


            Spacer(
                modifier =
                    Modifier.width(
                        10.dp
                    )
            )


            Text(
                text =
                    "Chưa có tài khoản mặc định. Chạm vào một tài khoản bên dưới để thiết lập.",
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
| BANK LOGO
|--------------------------------------------------------------------------
*/

@Composable
private fun BankLogo(
    bankCode: String
) {

    val background =
        when (
            bankCode.uppercase()
        ) {

            "VCB" ->
                Color(
                    0xFFE7F7EC
                )

            "BIDV" ->
                Color(
                    0xFFE5F6F7
                )

            "CTG" ->
                Color(
                    0xFFE5F0FF
                )

            "MBB" ->
                Color(
                    0xFFE8EFFF
                )

            "TCB" ->
                Color(
                    0xFFFFECEC
                )

            "TPB" ->
                Color(
                    0xFFFFF1E6
                )

            "ACB" ->
                Color(
                    0xFFE9F4FF
                )

            "STB" ->
                Color(
                    0xFFFFEEF5
                )

            else ->
                Color(
                    0xFFF0EEFF
                )
        }


    val foreground =
        when (
            bankCode.uppercase()
        ) {

            "VCB" ->
                Color(
                    0xFF087A3B
                )

            "BIDV" ->
                Color(
                    0xFF15979F
                )

            "CTG" ->
                Color(
                    0xFF1670C5
                )

            "MBB" ->
                Color(
                    0xFF2448DC
                )

            "TCB" ->
                Color(
                    0xFFE21A22
                )

            "TPB" ->
                Color(
                    0xFFF39223
                )

            "ACB" ->
                Color(
                    0xFF1B6FB8
                )

            "STB" ->
                Color(
                    0xFFC22664
                )

            else ->
                Color(
                    0xFF5F4ACB
                )
        }


    Surface(
        modifier =
            Modifier.size(
                50.dp
            ),
        shape =
            CircleShape,
        color =
            background
    ) {

        Box(
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    bankCode
                        .take(
                            3
                        )
                        .uppercase(),
                color =
                    foreground,
                style =
                    MaterialTheme
                        .typography
                        .labelMedium,
                fontWeight =
                    FontWeight.ExtraBold
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| EMPTY
|--------------------------------------------------------------------------
*/

@Composable
private fun EmptyBankCard(
    onAdd: () -> Unit
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
                        28.dp
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Surface(
                modifier =
                    Modifier.size(
                        78.dp
                    ),
                shape =
                    RoundedCornerShape(
                        25.dp
                    ),
                color =
                    BankGreen.copy(
                        alpha = 0.10f
                    )
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Outlined
                                .AccountBalance,
                        contentDescription =
                            null,
                        tint =
                            BankGreen,
                        modifier =
                            Modifier.size(
                                38.dp
                            )
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        17.dp
                    )
            )


            Text(
                text =
                    "Chưa có tài khoản ngân hàng",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                fontWeight =
                    FontWeight.ExtraBold,
                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )


            Text(
                text =
                    "Liên kết tài khoản mô phỏng để sử dụng khi thanh toán QR.",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            Button(
                onClick =
                    onAdd,
                shape =
                    RoundedCornerShape(
                        16.dp
                    ),
                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                BankGreen
                        )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Add,
                    contentDescription =
                        null
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            7.dp
                        )
                )


                Text(
                    text =
                        "Liên kết tài khoản",
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| INFORMATION
|--------------------------------------------------------------------------
*/

@Composable
private fun BankSimulationInfo() {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        color =
            MaterialTheme
                .colorScheme
                .primary
                .copy(
                    alpha = 0.07f
                ),
        shape =
            RoundedCornerShape(
                18.dp
            )
    ) {

        Row(
            modifier =
                Modifier.padding(
                    16.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Outlined
                        .AccountBalance,
                contentDescription =
                    null,
                tint =
                    MaterialTheme
                        .colorScheme
                        .primary
            )


            Spacer(
                modifier =
                    Modifier.width(
                        12.dp
                    )
            )


            Text(
                text =
                    "Tài khoản ngân hàng trong Tích Phú chỉ phục vụ mô phỏng. Ứng dụng không yêu cầu mật khẩu ngân hàng, mã PIN hoặc OTP.",
                modifier =
                    Modifier.weight(
                        1f
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
| SELECT BANK DIALOG
|--------------------------------------------------------------------------
*/

@Composable
private fun SelectBankDialog(
    onDismiss: () -> Unit,
    onBankSelected: (SupportedBank) -> Unit
) {

    var search by remember {
        mutableStateOf("")
    }


    val filteredBanks =
        supportedBanks.filter {

            it.code.contains(
                search,
                ignoreCase = true
            ) ||

                    it.shortName.contains(
                        search,
                        ignoreCase = true
                    ) ||

                    it.name.contains(
                        search,
                        ignoreCase = true
                    )
        }


    AlertDialog(
        onDismissRequest =
            onDismiss,
        shape =
            RoundedCornerShape(
                24.dp
            ),
        title = {

            Text(
                text =
                    "Chọn ngân hàng",
                fontWeight =
                    FontWeight.ExtraBold
            )
        },
        text = {

            Column {

                Text(
                    text =
                        "Chọn ngân hàng bạn muốn liên kết với Tích Phú.",
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
                            12.dp
                        )
                )


                OutlinedTextField(
                    value =
                        search,
                    onValueChange = {

                        search =
                            it
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    placeholder = {

                        Text(
                            "Tìm ngân hàng..."
                        )
                    },
                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Outlined.Search,
                            contentDescription =
                                null
                        )
                    },
                    singleLine =
                        true,
                    shape =
                        RoundedCornerShape(
                            15.dp
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )


                if (
                    filteredBanks.isEmpty()
                ) {

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    120.dp
                                ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                "Không tìm thấy ngân hàng.",
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }

                } else {

                    LazyColumn(
                        modifier =
                            Modifier.height(
                                390.dp
                            )
                    ) {

                        items(
                            items =
                                filteredBanks,
                            key = {
                                it.code
                            }
                        ) {
                                bank ->

                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {

                                            onBankSelected(
                                                bank
                                            )
                                        }
                                        .padding(
                                            vertical = 11.dp
                                        ),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                BankLogo(
                                    bankCode =
                                        bank.code
                                )


                                Spacer(
                                    modifier =
                                        Modifier.width(
                                            12.dp
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
                                            bank.shortName,
                                        fontWeight =
                                            FontWeight.Bold
                                    )


                                    Text(
                                        text =
                                            bank.name,
                                        style =
                                            MaterialTheme
                                                .typography
                                                .labelSmall,
                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .onSurfaceVariant,
                                        maxLines =
                                            1,
                                        overflow =
                                            TextOverflow.Ellipsis
                                    )
                                }


                                Icon(
                                    imageVector =
                                        Icons.Default
                                            .KeyboardArrowRight,
                                    contentDescription =
                                        null,
                                    tint =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )
                            }


                            HorizontalDivider(
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .outlineVariant
                                        .copy(
                                            alpha = 0.5f
                                        )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {

            TextButton(
                onClick =
                    onDismiss
            ) {

                Text(
                    "Đóng"
                )
            }
        }
    )
}


/*
|--------------------------------------------------------------------------
| LINK BANK DIALOG
|--------------------------------------------------------------------------
*/

@Composable
private fun LinkBankDialog(
    bank: SupportedBank,
    onDismiss: () -> Unit,
    onLink: (
        String,
        String,
        Boolean
    ) -> Unit
) {

    var accountNumber by remember {
        mutableStateOf("")
    }


    var accountName by remember {
        mutableStateOf("")
    }


    var isDefault by remember {
        mutableStateOf(
            true
        )
    }


    var error by remember {
        mutableStateOf<String?>(
            null
        )
    }


    AlertDialog(
        onDismissRequest =
            onDismiss,
        shape =
            RoundedCornerShape(
                24.dp
            ),
        title = {

            Text(
                text =
                    "Thông tin tài khoản",
                fontWeight =
                    FontWeight.ExtraBold
            )
        },
        text = {

            Column {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    BankLogo(
                        bankCode =
                            bank.code
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                12.dp
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
                                bank.shortName,
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.ExtraBold
                        )


                        Text(
                            text =
                                bank.name,
                            style =
                                MaterialTheme
                                    .typography
                                    .labelSmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant,
                            maxLines =
                                2
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            20.dp
                        )
                )


                OutlinedTextField(
                    value =
                        accountNumber,
                    onValueChange = {

                        accountNumber =
                            it.filter {
                                    char ->
                                char.isDigit()
                            }

                        error =
                            null
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {

                        Text(
                            "Số tài khoản"
                        )
                    },
                    placeholder = {

                        Text(
                            "Nhập số tài khoản"
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


                Spacer(
                    modifier =
                        Modifier.height(
                            13.dp
                        )
                )


                OutlinedTextField(
                    value =
                        accountName,
                    onValueChange = {

                        accountName =
                            it.uppercase()

                        error =
                            null
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {

                        Text(
                            "Tên chủ tài khoản"
                        )
                    },
                    placeholder = {

                        Text(
                            "Ví dụ: NGUYEN VAN A"
                        )
                    },
                    singleLine =
                        true,
                    shape =
                        RoundedCornerShape(
                            15.dp
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )


                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),
                    color =
                        BankGreen.copy(
                            alpha = 0.07f
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

                        Column(
                            modifier =
                                Modifier.weight(
                                    1f
                                )
                        ) {

                            Text(
                                text =
                                    "Đặt làm tài khoản mặc định",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium,
                                fontWeight =
                                    FontWeight.Bold
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(
                                        2.dp
                                    )
                            )


                            Text(
                                text =
                                    "Sử dụng tài khoản này khi thanh toán QR",
                                style =
                                    MaterialTheme
                                        .typography
                                        .labelSmall,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                            )
                        }


                        Switch(
                            checked =
                                isDefault,
                            onCheckedChange = {

                                isDefault =
                                    it
                            }
                        )
                    }
                }


                if (
                    !error.isNullOrBlank()
                ) {

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
                                13.dp
                            ),
                        color =
                            MaterialTheme
                                .colorScheme
                                .errorContainer
                    ) {

                        Text(
                            text =
                                error
                                    ?: "",
                            modifier =
                                Modifier.padding(
                                    11.dp
                                ),
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onErrorContainer,
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        confirmButton = {

            Button(
                onClick = {

                    when {

                        accountNumber.length <
                                6 -> {

                            error =
                                "Số tài khoản không hợp lệ."
                        }


                        accountName
                            .isBlank() -> {

                            error =
                                "Vui lòng nhập tên chủ tài khoản."
                        }


                        else -> {

                            onLink(
                                accountNumber,
                                accountName
                                    .trim(),
                                isDefault
                            )
                        }
                    }
                },
                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                BankGreen
                        ),
                shape =
                    RoundedCornerShape(
                        14.dp
                    )
            ) {

                Text(
                    text =
                        "Liên kết tài khoản",
                    fontWeight =
                        FontWeight.Bold
                )
            }
        },
        dismissButton = {

            TextButton(
                onClick =
                    onDismiss
            ) {

                Text(
                    "Hủy"
                )
            }
        }
    )
}


/*
|--------------------------------------------------------------------------
| ACCOUNT OPTIONS
|--------------------------------------------------------------------------
*/

@Composable
private fun BankAccountOptionsDialog(
    account: BankAccount,
    onDismiss: () -> Unit,
    onSetDefault: () -> Unit,
    onDelete: () -> Unit
) {

    AlertDialog(
        onDismissRequest =
            onDismiss,
        shape =
            RoundedCornerShape(
                24.dp
            ),
        title = {

            Text(
                text =
                    "Thông tin tài khoản",
                fontWeight =
                    FontWeight.ExtraBold
            )
        },
        text = {

            Column {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    BankLogo(
                        bankCode =
                            account.bankCode
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                12.dp
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
                                account.bankName,
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.ExtraBold
                        )


                        Text(
                            text =
                                account.accountName,
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
                                    2.dp
                                )
                        )


                        Text(
                            text =
                                account.maskedAccountNumber
                                    ?: maskAccount(
                                        account.accountNumber
                                    ),
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            18.dp
                        )
                )


                if (
                    account.isDefault
                ) {

                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        color =
                            BankGreen.copy(
                                alpha = 0.10f
                            ),
                        shape =
                            RoundedCornerShape(
                                14.dp
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

                            Icon(
                                imageVector =
                                    Icons.Default.Check,
                                contentDescription =
                                    null,
                                tint =
                                    BankGreen
                            )


                            Spacer(
                                modifier =
                                    Modifier.width(
                                        8.dp
                                    )
                            )


                            Text(
                                text =
                                    "Tài khoản thanh toán mặc định",
                                color =
                                    BankGreenDark,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }

                } else {

                    OutlinedButton(
                        onClick =
                            onSetDefault,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    50.dp
                                ),
                        shape =
                            RoundedCornerShape(
                                15.dp
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Check,
                            contentDescription =
                                null
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    7.dp
                                )
                        )


                        Text(
                            text =
                                "Đặt làm tài khoản mặc định",
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            9.dp
                        )
                )


                OutlinedButton(
                    onClick =
                        onDelete,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(
                                50.dp
                            ),
                    shape =
                        RoundedCornerShape(
                            15.dp
                        ),
                    colors =
                        ButtonDefaults
                            .outlinedButtonColors(
                                contentColor =
                                    BankDanger
                            )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Delete,
                        contentDescription =
                            null
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                7.dp
                            )
                    )


                    Text(
                        text =
                            "Hủy liên kết",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {

            TextButton(
                onClick =
                    onDismiss
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Close,
                    contentDescription =
                        null
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            4.dp
                        )
                )


                Text(
                    "Đóng"
                )
            }
        }
    )
}


/*
|--------------------------------------------------------------------------
| MESSAGE
|--------------------------------------------------------------------------
*/

@Composable
private fun BankMessageCard(
    text: String,
    error: Boolean
) {

    val background =
        if (
            error
        ) {

            MaterialTheme
                .colorScheme
                .errorContainer

        } else {

            BankGreen.copy(
                alpha = 0.10f
            )
        }


    val foreground =
        if (
            error
        ) {

            MaterialTheme
                .colorScheme
                .onErrorContainer

        } else {

            BankGreenDark
        }


    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        color =
            background,
        shape =
            RoundedCornerShape(
                15.dp
            )
    ) {

        Text(
            text =
                text,
            modifier =
                Modifier.padding(
                    13.dp
                ),
            color =
                foreground,
            style =
                MaterialTheme
                    .typography
                    .bodySmall,
            fontWeight =
                FontWeight.SemiBold
        )
    }
}


/*
|--------------------------------------------------------------------------
| MASK ACCOUNT
|--------------------------------------------------------------------------
*/

private fun maskAccount(
    accountNumber: String
): String {

    if (
        accountNumber.length <=
        4
    ) {

        return accountNumber
    }


    return "•••• " +
            accountNumber.takeLast(
                4
            )
}