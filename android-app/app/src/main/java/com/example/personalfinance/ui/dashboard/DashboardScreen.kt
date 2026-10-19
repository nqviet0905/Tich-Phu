package com.example.personalfinance.ui.dashboard


import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinance.R
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalContext
import com.example.personalfinance.ui.theme.ThemeMode
import com.example.personalfinance.ui.theme.ThemePreference
import com.example.personalfinance.ui.theme.BrandGold
import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.ui.theme.BrandPurpleDark
import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.ui.theme.IncomeGreen
import com.example.personalfinance.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.ui.unit.sp


@Composable
fun DashboardScreen(

    onLogout: () -> Unit,

    onTransactionsClick: () -> Unit,

    onDepositClick: () -> Unit,

    onTransferClick: () -> Unit,

    onQrScanClick: () -> Unit,

    onBudgetClick: () -> Unit,

    onPlanClick: () -> Unit,

    onGoalClick: () -> Unit,

    onStatisticsClick: () -> Unit,

    onNotificationClick: () -> Unit,

    onProfileClick: () -> Unit,

    /*
    |--------------------------------------------------------------------------
    | TÌM KIẾM
    |--------------------------------------------------------------------------
    */

    onSearchClick: () -> Unit = {},

    dashboardViewModel: DashboardViewModel = viewModel()

) {

    val state by
    dashboardViewModel.uiState.collectAsState()


    val formatter =
        remember {

            NumberFormat.getNumberInstance(
                Locale("vi", "VN")
            )
        }


    var balanceVisible by remember {
        mutableStateOf(true)
    }


    var showMoreActions by remember {
        mutableStateOf(false)
    }


    /*
    |--------------------------------------------------------------------------
    | TỰ REFRESH KHI QUAY VỀ HOME
    |--------------------------------------------------------------------------
    */

    val lifecycleOwner =
        LocalLifecycleOwner.current


    var firstResume by remember {
        mutableStateOf(true)
    }


    DisposableEffect(lifecycleOwner) {

        val observer =
            LifecycleEventObserver { _, event ->

                if (
                    event ==
                    Lifecycle.Event.ON_RESUME
                ) {

                    if (firstResume) {

                        firstResume = false

                    } else {

                        dashboardViewModel.refresh()
                    }
                }
            }


        lifecycleOwner.lifecycle
            .addObserver(observer)


        onDispose {

            lifecycleOwner.lifecycle
                .removeObserver(observer)
        }
    }


    Box(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                )
    ) {

        when {


            /*
            |--------------------------------------------------------------------------
            | LOADING
            |--------------------------------------------------------------------------
            */

            state.isLoading &&
                    state.data == null -> {

                Box(

                    modifier =
                        Modifier.fillMaxSize(),

                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color =
                            BrandPurple
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | ERROR
            |--------------------------------------------------------------------------
            */

            state.error != null &&
                    state.data == null -> {

                Column(

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp),

                    verticalArrangement =
                        Arrangement.Center,

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(

                        text =
                            state.error
                                ?: "Đã xảy ra lỗi.",

                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )


                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )


                    Button(

                        onClick = {
                            dashboardViewModel.refresh()
                        }

                    ) {

                        Text("Thử lại")
                    }
                }
            }


            /*
            |--------------------------------------------------------------------------
            | DASHBOARD
            |--------------------------------------------------------------------------
            */

            state.data != null -> {

                val data =
                    state.data!!


                val budgetProgress =
                    (
                            data.budget_usage_percentage
                                .toFloat() / 100f
                            ).coerceIn(
                            0f,
                            1f
                        )


                LazyColumn(

                    modifier =
                        Modifier.fillMaxSize(),

                    contentPadding =
                        PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 14.dp,
                            bottom = 115.dp
                        ),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            16.dp
                        )

                ) {


                    /*
                    |--------------------------------------------------------------------------
                    | HEADER
                    |--------------------------------------------------------------------------
                    */

                    item {

                        TichPhuHomeHeader(

                            unreadNotificationCount =
                                data.unread_notification_count,

                            onSearchClick =
                                onSearchClick,

                            onNotificationClick =
                                onNotificationClick
                        )
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | TOTAL ASSET
                    |--------------------------------------------------------------------------
                    */

                    item {

                        HomeBalanceCard(

                            balance =
                                if (balanceVisible) {

                                    "${
                                        formatter.format(
                                            data.balance
                                        )
                                    } đ"

                                } else {

                                    "••••••••• đ"
                                },

                            balanceValue =
                                data.balance,

                            savingRate =
                                data.saving_rate,

                            balanceVisible =
                                balanceVisible,

                            onToggleBalance = {

                                balanceVisible =
                                    !balanceVisible
                            }
                        )
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | QUICK ACTION
                    |--------------------------------------------------------------------------
                    */

                    item {

                        Row(

                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            HomeAction(
                                title = "Nạp tiền",
                                icon = Icons.Default.Add,
                                accentColor = IncomeGreen,
                                onClick = onDepositClick,
                                modifier = Modifier.weight(1f),
                                titleSize = 8.sp
                            )


                            HomeAction(
                                title = "Chuyển tiền",
                                icon = Icons.Default.Send,
                                accentColor = BrandPurple,
                                onClick = onTransferClick,
                                modifier = Modifier.weight(1f),
                                titleSize = 8.sp
                            )


                            HomeAction(
                                title = "Quét QR",
                                icon = Icons.Default.QrCodeScanner,
                                accentColor = BrandGold,
                                onClick = onQrScanClick,
                                modifier = Modifier.weight(1f),
                                titleSize = 8.sp
                            )


                            HomeAction(
                                title = "Xem thêm",
                                icon = Icons.Default.MoreHoriz,
                                accentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                onClick = {
                                    showMoreActions = !showMoreActions
                                },
                                modifier = Modifier.weight(1f),
                                titleSize = 8.sp
                            )
                        }
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | XEM THÊM
                    |--------------------------------------------------------------------------
                    */

                    if (showMoreActions) {

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
                                    )
                            ) {

                                Row(

                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                vertical = 14.dp
                                            )
                                ) {

                                    HomeAction(

                                        title =
                                            "Ngân sách",

                                        icon =
                                            Icons.Default
                                                .AccountBalanceWallet,

                                        accentColor =
                                            IncomeGreen,

                                        onClick =
                                            onBudgetClick,

                                        modifier =
                                            Modifier.weight(1f)
                                    )


                                    HomeAction(

                                        title =
                                            "Kế hoạch",

                                        icon =
                                            Icons.Default
                                                .EventNote,

                                        accentColor =
                                            BrandPurple,

                                        onClick =
                                            onPlanClick,

                                        modifier =
                                            Modifier.weight(1f)
                                    )


                                    HomeAction(

                                        title =
                                            "Mục tiêu",

                                        icon =
                                            Icons.Default.Flag,

                                        accentColor =
                                            BrandGold,

                                        onClick =
                                            onGoalClick,

                                        modifier =
                                            Modifier.weight(1f)
                                    )


                                    HomeAction(

                                        title =
                                            "Thống kê",

                                        icon =
                                            Icons.Default.BarChart,

                                        accentColor =
                                            ExpenseRed,

                                        onClick =
                                            onStatisticsClick,

                                        modifier =
                                            Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | MỤC TIÊU
                    |--------------------------------------------------------------------------
                    */

                    item {

                        HomeSectionHeader(

                            title =
                                "Mục tiêu tài chính",

                            action =
                                "Xem tất cả",

                            onClick =
                                onGoalClick
                        )
                    }


                    item {

                        HomeGoalCard(
                            activeGoals = data.active_goals,
                            goalName = data.goal_name,
                            currentAmount = data.goal_current_amount,
                            targetAmount = data.goal_target_amount,
                            progressValue = data.goal_progress,
                            onClick = onGoalClick
                        )
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | NGÂN SÁCH
                    |--------------------------------------------------------------------------
                    */

                    item {

                        HomeSectionHeader(

                            title =
                                "Ngân sách tháng này",

                            action =
                                "Chi tiết",

                            onClick =
                                onBudgetClick
                        )
                    }


                    item {

                        HomeBudgetCard(

                            totalBudget =
                                "${
                                    formatter.format(
                                        data.total_budget
                                    )
                                } đ",

                            totalExpense =
                                "${
                                    formatter.format(
                                        data.total_expense
                                    )
                                } đ",

                            usage =
                                data
                                    .budget_usage_percentage,

                            progress =
                                budgetProgress,

                            onClick =
                                onBudgetClick
                        )
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | TỔNG QUAN THÁNG
                    |--------------------------------------------------------------------------
                    */

                    item {

                        Text(

                            text =
                                "Tổng quan tháng",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,

                            fontWeight =
                                FontWeight.ExtraBold
                        )
                    }


                    item {

                        HomeMonthSummary(

                            income =
                                "${
                                    formatter.format(
                                        data.total_income
                                    )
                                } đ",

                            expense =
                                "${
                                    formatter.format(
                                        data.total_expense
                                    )
                                } đ",

                            transactionCount =
                                data.transaction_count,

                            onStatisticsClick =
                                onStatisticsClick
                        )
                    }
                }
            }


            else -> {

                Box(

                    modifier =
                        Modifier.fillMaxSize(),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        "Chưa có dữ liệu tài chính."
                    )
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| HEADER
|--------------------------------------------------------------------------
*/

@Composable
private fun TichPhuHomeHeader(

    unreadNotificationCount: Int,

    onSearchClick: () -> Unit,

    onNotificationClick: () -> Unit

) {
    val context =
        LocalContext.current

    val themeMode =
        ThemePreference.get(
            context
        )

    val systemDark =
        isSystemInDarkTheme()

    val isDarkTheme =
        when (themeMode) {

            ThemeMode.LIGHT ->
                false

            ThemeMode.DARK ->
                true

            ThemeMode.SYSTEM ->
                systemDark
        }

    val logoRes =
        if (isDarkTheme) {

            R.drawable.tich_phu_icon_dark

        } else {

            R.drawable.tich_phu_icon_light
        }

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {


        Image(

            painter =
                painterResource(
                    id = logoRes
                ),

            contentDescription =
                "Tích Phú",

            modifier =
                Modifier
                    .size(48.dp)
                    .clip(
                        RoundedCornerShape(
                            15.dp
                        )
                    ),

            contentScale =
                ContentScale.Crop
        )


        Spacer(
            modifier =
                Modifier.width(10.dp)
        )


        Column(

            modifier =
                Modifier.weight(1f)
        ) {

            Text(

                text =
                    "Tích Phú",

                style =
                    MaterialTheme
                        .typography
                        .titleLarge,

                fontWeight =
                    FontWeight.ExtraBold
            )
        }


        /*
        |--------------------------------------------------------------------------
        | SEARCH
        |--------------------------------------------------------------------------
        */

        HeaderIconButton(

            icon =
                Icons.Default.Search,

            contentDescription =
                "Tìm kiếm",

            onClick =
                onSearchClick
        )


        Spacer(
            modifier =
                Modifier.width(7.dp)
        )


        /*
        |--------------------------------------------------------------------------
        | NOTIFICATION
        |--------------------------------------------------------------------------
        */

        Box {

            HeaderIconButton(

                icon =
                    Icons.Default.Notifications,

                contentDescription =
                    "Thông báo",

                onClick =
                    onNotificationClick
            )


            if (unreadNotificationCount > 0) {

                Box(

                    modifier =
                        Modifier
                            .size(18.dp)
                            .align(
                                Alignment.TopEnd
                            )
                            .background(
                                ExpenseRed,
                                CircleShape
                            ),

                    contentAlignment =
                        Alignment.Center

                ) {

                    Text(

                        text =
                            if (unreadNotificationCount > 9) {
                                "9+"
                            } else {
                                unreadNotificationCount
                                    .toString()
                            },

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


@Composable
private fun HeaderIconButton(

    icon: ImageVector,

    contentDescription: String,

    onClick: () -> Unit

) {

    Surface(

        modifier =
            Modifier.size(43.dp),

        shape =
            CircleShape,

        color =
            MaterialTheme
                .colorScheme
                .surface,

        tonalElevation =
            2.dp
    ) {

        IconButton(
            onClick =
                onClick
        ) {

            Icon(

                imageVector =
                    icon,

                contentDescription =
                    contentDescription,

                modifier =
                    Modifier.size(24.dp)
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| BALANCE CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun HomeBalanceCard(

    balance: String,

    balanceValue: Double,

    savingRate: Double,

    balanceVisible: Boolean,

    onToggleBalance: () -> Unit

) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                25.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.Transparent
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    5.dp
            )

    ) {

        Box(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(

                        brush =
                            Brush.linearGradient(

                                colors =
                                    listOf(

                                        Color(
                                            0xFF102A9C
                                        ),

                                        BrandPurpleDark,

                                        BrandPurple,

                                        Color(
                                            0xFF9658FF
                                        )
                                    )
                            )
                    )
                    .padding(
                        horizontal = 18.dp,
                        vertical = 17.dp
                    )
        ) {


            /*
            |--------------------------------------------------------------------------
            | TEXT
            |--------------------------------------------------------------------------
            */

            Column(

                modifier =
                    Modifier
                        .fillMaxWidth(
                            0.73f
                        )
            ) {

                Row(

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(

                        text =
                            "Tổng tài sản",

                        color =
                            Color.White.copy(
                                alpha = 0.88f
                            ),

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium
                    )


                    IconButton(

                        onClick =
                            onToggleBalance,

                        modifier =
                            Modifier.size(31.dp)

                    ) {

                        Icon(

                            imageVector =
                                if (balanceVisible) {

                                    Icons.Default
                                        .Visibility

                                } else {

                                    Icons.Default
                                        .VisibilityOff
                                },

                            contentDescription =
                                null,

                            tint =
                                Color.White,

                            modifier =
                                Modifier.size(
                                    18.dp
                                )
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(1.dp)
                )


                Text(

                    text =
                        balance,

                    color =
                        Color.White,

                    style =
                        MaterialTheme
                            .typography
                            .headlineLarge,

                    fontWeight =
                        FontWeight.ExtraBold,

                    maxLines = 1,

                    overflow =
                        TextOverflow.Ellipsis
                )


                Spacer(
                    modifier =
                        Modifier.height(17.dp)
                )


                Row(

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Surface(

                        shape =
                            RoundedCornerShape(
                                50.dp
                            ),

                        color =
                            Color(
                                0xFF0D9B70
                            )
                    ) {

                        Text(

                            text =
                                "↑ ${
                                    formatPercent(
                                        savingRate
                                    )
                                }%",

                            modifier =
                                Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 5.dp
                                ),

                            color =
                                Color(
                                    0xFF7CFFB8
                                ),

                            style =
                                MaterialTheme
                                    .typography
                                    .labelMedium,

                            fontWeight =
                                FontWeight.ExtraBold
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )


                    Text(

                        text =
                            "tỷ lệ tiết kiệm",

                        color =
                            Color.White.copy(
                                alpha = 0.78f
                            ),

                        style =
                            MaterialTheme
                                .typography
                                .labelSmall
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | GOLD COIN + PLANT
            |--------------------------------------------------------------------------
            */

            Image(

                painter =
                    painterResource(
                        id = R.drawable.welcome_hero
                    ),

                contentDescription =
                    "Minh họa tài chính",

                contentScale =
                    ContentScale.Fit,

                modifier =
                    Modifier
                        .size(
                            width = 145.dp,
                            height = 125.dp
                        )
                        .align(
                            Alignment.BottomEnd
                        )
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| GOLD COIN ILLUSTRATION
|--------------------------------------------------------------------------
*/




/*
|--------------------------------------------------------------------------
| QUICK ACTION
|--------------------------------------------------------------------------
*/

@Composable
private fun HomeAction(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleSize: androidx.compose.ui.unit.TextUnit = 10.sp
) {

    Column(

        modifier =
            modifier
                .clickable {
                    onClick()
                }
                .padding(
                    vertical = 5.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Surface(

            modifier =
                Modifier.size(55.dp),

            shape =
                RoundedCornerShape(
                    18.dp
                ),

            color =
                accentColor.copy(
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
                        title,

                    tint =
                        accentColor,

                    modifier =
                        Modifier.size(
                            27.dp
                        )
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(7.dp)
        )


        Text(
            text = title,
            fontSize = titleSize,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


/*
|--------------------------------------------------------------------------
| SECTION HEADER
|--------------------------------------------------------------------------
*/

@Composable
private fun HomeSectionHeader(

    title: String,

    action: String,

    onClick: () -> Unit

) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically,

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(

            text =
                title,

            style =
                MaterialTheme
                    .typography
                    .titleMedium,

            fontWeight =
                FontWeight.ExtraBold
        )


        Text(

            text =
                "$action ›",

            color =
                BrandPurple,

            style =
                MaterialTheme
                    .typography
                    .bodySmall,

            fontWeight =
                FontWeight.Bold,

            modifier =
                Modifier.clickable {
                    onClick()
                }
        )
    }
}


/*
|--------------------------------------------------------------------------
| GOAL
|--------------------------------------------------------------------------
*/

@Composable
private fun HomeGoalCard(

    activeGoals: Int,

    goalName: String?,

    currentAmount: Double,

    targetAmount: Double,

    progressValue: Double,

    onClick: () -> Unit

) {

    val formatter =
        remember {
            NumberFormat.getNumberInstance(
                Locale("vi", "VN")
            )
        }


    val progress =
        (
                progressValue.toFloat() / 100f
                ).coerceIn(
                0f,
                1f
            )


    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },

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
                defaultElevation = 2.dp
            )

    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            /*
            |--------------------------------------------------------------------------
            | ICON
            |--------------------------------------------------------------------------
            */

            Surface(

                modifier =
                    Modifier.size(52.dp),

                shape =
                    RoundedCornerShape(
                        17.dp
                    ),

                color =
                    ExpenseRed.copy(
                        alpha = 0.10f
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
                            ExpenseRed,

                        modifier =
                            Modifier.size(27.dp)
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.width(13.dp)
            )


            /*
            |--------------------------------------------------------------------------
            | NỘI DUNG
            |--------------------------------------------------------------------------
            */

            Column(

                modifier =
                    Modifier.weight(1f)

            ) {

                /*
                |--------------------------------------------------------------------------
                | KHÔNG CÓ MỤC TIÊU
                |--------------------------------------------------------------------------
                */

                if (
                    activeGoals <= 0 ||
                    goalName.isNullOrBlank()
                ) {

                    Text(

                        text =
                            "Chưa có mục tiêu tài chính",

                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )
                    return@Column
                }


                /*
                |--------------------------------------------------------------------------
                | TÊN MỤC TIÊU
                |--------------------------------------------------------------------------
                */

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Text(

                        text =
                            goalName,

                        modifier =
                            Modifier.weight(1f),

                        fontWeight =
                            FontWeight.ExtraBold,

                        maxLines = 1,

                        overflow =
                            TextOverflow.Ellipsis
                    )


                    Text(

                        text =
                            "${formatPercent(progressValue)}%",

                        color =
                            BrandPurple,

                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )


                /*
                |--------------------------------------------------------------------------
                | SỐ TIỀN
                |--------------------------------------------------------------------------
                */

                Text(

                    text =
                        "${
                            formatter.format(
                                currentAmount
                            )
                        } đ / ${
                            formatter.format(
                                targetAmount
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

                    fontWeight =
                        FontWeight.SemiBold
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                /*
                |--------------------------------------------------------------------------
                | PROGRESS
                |--------------------------------------------------------------------------
                */

                LinearProgressIndicator(

                    progress = {
                        progress
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(
                                RoundedCornerShape(
                                    20.dp
                                )
                            ),

                    color =
                        BrandPurple,

                    trackColor =
                        MaterialTheme
                            .colorScheme
                            .surfaceVariant
                )


                /*
                |--------------------------------------------------------------------------
                | SỐ MỤC TIÊU KHÁC
                |--------------------------------------------------------------------------
                */

                if (activeGoals > 1) {

                    Spacer(
                        modifier =
                            Modifier.height(7.dp)
                    )


                    Text(

                        text =
                            "+${activeGoals - 1} mục tiêu khác đang thực hiện",

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
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| BUDGET
|--------------------------------------------------------------------------
*/

@Composable
private fun HomeBudgetCard(

    totalBudget: String,

    totalExpense: String,

    usage: Double,

    progress: Float,

    onClick: () -> Unit

) {

    val progressColor =
        when {

            usage >= 90 ->
                ExpenseRed

            usage >= 70 ->
                BrandGold

            else ->
                BrandPurple
        }


    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },

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
                Modifier.padding(16.dp)
        ) {

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Column {

                    Text(

                        text =
                            "Đã chi",

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
                            Modifier.height(4.dp)
                    )


                    Text(

                        text =
                            totalExpense,

                        color =
                            ExpenseRed,

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,

                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }


                Column(

                    horizontalAlignment =
                        Alignment.End
                ) {

                    Text(

                        text =
                            "Ngân sách",

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
                            Modifier.height(4.dp)
                    )


                    Text(

                        text =
                            totalBudget,

                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(15.dp)
            )


            Row(

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                LinearProgressIndicator(

                    progress = {
                        progress
                    },

                    modifier =
                        Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(
                                RoundedCornerShape(
                                    20.dp
                                )
                            ),

                    color =
                        progressColor,

                    trackColor =
                        MaterialTheme
                            .colorScheme
                            .surfaceVariant
                )


                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )


                Text(

                    text =
                        "${
                            formatPercent(
                                usage
                            )
                        }%",

                    color =
                        progressColor,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| MONTH SUMMARY
|--------------------------------------------------------------------------
*/

@Composable
private fun HomeMonthSummary(
    income: String,
    expense: String,
    transactionCount: Int,
    onStatisticsClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {

        MonthSummaryCard(
            title = "Thu nhập",
            value = income,
            icon = "↓",
            accentColor = IncomeGreen,
            modifier = Modifier.weight(1f),
            onClick = onStatisticsClick
        )

        MonthSummaryCard(
            title = "Chi tiêu",
            value = expense,
            icon = "↑",
            accentColor = ExpenseRed,
            modifier = Modifier.weight(1f),
            onClick = onStatisticsClick
        )

        MonthSummaryCard(
            title = "Giao dịch",
            value = transactionCount.toString(),
            icon = "▥",
            accentColor = BrandPurple,
            modifier = Modifier.weight(1f),
            onClick = onStatisticsClick
        )
    }
}


@Composable
private fun MonthSummaryCard(
    title: String,
    value: String,
    icon: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .height(125.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme
                .colorScheme
                .surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            // ICON
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(11.dp),
                color = accentColor.copy(alpha = 0.11f)
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = icon,
                        color = accentColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // TIÊU ĐỀ
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            // GIÁ TRỊ
            Text(
                text = value,
                color = accentColor,
                style = MaterialTheme
                    .typography
                    .bodySmall,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| FORMAT %
|--------------------------------------------------------------------------
*/

private fun formatPercent(
    value: Double
): String {

    return if (
        value % 1.0 == 0.0
    ) {

        value.toInt()
            .toString()

    } else {

        String.format(
            Locale("vi", "VN"),
            "%.1f",
            value
        )
    }
}