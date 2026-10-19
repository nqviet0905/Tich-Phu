package com.example.personalfinance.ui.payment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.personalfinance.data.model.QrPaymentData
import com.example.personalfinance.data.model.QrPaymentParser
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

private val ScannerPurple = Color(0xFF6236FF)
private val ScannerGold = Color(0xFFF2B93B)

private fun scanQrFromImage(
    context: Context,
    uri: Uri,
    onQrDetected: (QrPaymentData) -> Unit,
    onInvalidQr: () -> Unit,
    onError: (String) -> Unit
) {
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    val scanner = BarcodeScanning.getClient(options)

    try {
        val inputImage = InputImage.fromFilePath(context, uri)

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->

                val rawValue = barcodes
                    .firstOrNull {
                        !it.rawValue.isNullOrBlank()
                    }
                    ?.rawValue
                    ?.trim()

                if (rawValue.isNullOrBlank()) {
                    onInvalidQr()
                    return@addOnSuccessListener
                }

                val qrData = QrPaymentParser.parse(rawValue)

                if (qrData != null) {
                    onQrDetected(qrData)
                } else {
                    onInvalidQr()
                }
            }
            .addOnFailureListener {
                onError(
                    "Không thể đọc mã QR từ ảnh. Vui lòng chọn ảnh rõ hơn."
                )
            }
            .addOnCompleteListener {
                scanner.close()
            }

    } catch (exception: Exception) {

        scanner.close()

        onError(
            "Không thể mở ảnh đã chọn."
        )
    }
}

@Composable
fun QrScannerScreen(
    onBack: () -> Unit,
    onQrScanned: (QrPaymentData) -> Unit
) {

    val context = LocalContext.current

    /*
    |--------------------------------------------------------------------------
    | CAMERA PERMISSION
    |--------------------------------------------------------------------------
    */

    var hasCameraPermission by remember {

        mutableStateOf(

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    /*
    |--------------------------------------------------------------------------
    | QR MESSAGE
    |--------------------------------------------------------------------------
    */

    var invalidQrMessage by remember {
        mutableStateOf<String?>(null)
    }

    /*
    |--------------------------------------------------------------------------
    | GALLERY SCANNING
    |--------------------------------------------------------------------------
    */

    var isGalleryScanning by remember {
        mutableStateOf(false)
    }

    /*
    |--------------------------------------------------------------------------
    | GALLERY PICKER
    |--------------------------------------------------------------------------
    */

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri == null) {
                return@rememberLauncherForActivityResult
            }

            invalidQrMessage = null
            isGalleryScanning = true

            scanQrFromImage(

                context = context,

                uri = uri,

                onQrDetected = { qrData ->

                    isGalleryScanning = false

                    onQrScanned(
                        qrData
                    )
                },

                onInvalidQr = {

                    isGalleryScanning = false

                    invalidQrMessage =
                        "Không tìm thấy mã QR thanh toán Tích Phú trong ảnh."
                },

                onError = { message ->

                    isGalleryScanning = false

                    invalidQrMessage =
                        message
                }
            )
        }

    /*
    |--------------------------------------------------------------------------
    | CAMERA PERMISSION LAUNCHER
    |--------------------------------------------------------------------------
    */

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasCameraPermission =
                granted
        }

    /*
    |--------------------------------------------------------------------------
    | REQUEST CAMERA ON FIRST OPEN
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(Unit) {

        if (!hasCameraPermission) {

            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    /*
    |--------------------------------------------------------------------------
    | AUTO HIDE ERROR
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(
        invalidQrMessage
    ) {

        if (!invalidQrMessage.isNullOrBlank()) {

            delay(
                2200L
            )

            invalidQrMessage = null
        }
    }

    /*
    |--------------------------------------------------------------------------
    | MAIN
    |--------------------------------------------------------------------------
    */

    Box(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color.Black
                )

    ) {

        if (hasCameraPermission) {

            /*
            |--------------------------------------------------------------------------
            | CAMERA
            |--------------------------------------------------------------------------
            */

            CameraQrPreview(

                modifier =
                    Modifier.fillMaxSize(),

                onQrDetected = { qrData ->

                    invalidQrMessage = null

                    onQrScanned(
                        qrData
                    )
                },

                onInvalidQr = {

                    invalidQrMessage =
                        "Mã QR này không phải mã thanh toán Tích Phú."
                }
            )

            /*
            |--------------------------------------------------------------------------
            | DARK OVERLAY
            |--------------------------------------------------------------------------
            */

            Box(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(
                                alpha = 0.17f
                            )
                        )
            )

            /*
            |--------------------------------------------------------------------------
            | TOP BAR
            |--------------------------------------------------------------------------
            */

            ScannerTopBar(
                onBack = onBack
            )

            /*
            |--------------------------------------------------------------------------
            | CENTER CONTENT
            |--------------------------------------------------------------------------
            */

            Column(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 28.dp
                        ),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center

            ) {

                Text(

                    text =
                        "Quét mã QR",

                    color =
                        Color.White,

                    fontSize =
                        26.sp,

                    fontWeight =
                        FontWeight.ExtraBold
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            7.dp
                        )
                )

                Text(

                    text =
                        "Đưa mã QR thanh toán vào trong khung",

                    color =
                        Color.White.copy(
                            alpha = 0.85f
                        ),

                    fontSize =
                        13.sp,

                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            28.dp
                        )
                )

                /*
                |--------------------------------------------------------------------------
                | QR FRAME
                |--------------------------------------------------------------------------
                */

                ScannerFrame()

                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )

                /*
                |--------------------------------------------------------------------------
                | SELECT IMAGE
                |--------------------------------------------------------------------------
                */

                GalleryButton(

                    onClick = {

                        galleryLauncher.launch(
                            "image/*"
                        )
                    },

                    isScanning =
                        isGalleryScanning,

                    darkTheme =
                        true
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )

                /*
                |--------------------------------------------------------------------------
                | AUTO SCAN INFO
                |--------------------------------------------------------------------------
                */

                Surface(

                    shape =
                        RoundedCornerShape(
                            50.dp
                        ),

                    color =
                        Color.Black.copy(
                            alpha = 0.48f
                        )

                ) {

                    Row(

                        modifier =
                            Modifier.padding(
                                horizontal = 15.dp,
                                vertical = 10.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.QrCodeScanner,

                            contentDescription =
                                null,

                            tint =
                                ScannerGold,

                            modifier =
                                Modifier.size(
                                    19.dp
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.size(
                                    8.dp
                                )
                        )

                        Text(

                            text =
                                "Tích Phú sẽ tự đọc thông tin thanh toán",

                            color =
                                Color.White,

                            fontSize =
                                11.sp
                        )
                    }
                }

                /*
                |--------------------------------------------------------------------------
                | ERROR
                |--------------------------------------------------------------------------
                */

                if (!invalidQrMessage.isNullOrBlank()) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )

                    InvalidQrMessage(

                        message =
                            invalidQrMessage
                                ?: "",

                        darkTheme =
                            true
                    )
                }
            }

            /*
            |--------------------------------------------------------------------------
            | FOOTER
            |--------------------------------------------------------------------------
            */

            Surface(

                modifier =
                    Modifier
                        .align(
                            Alignment.BottomCenter
                        )
                        .fillMaxWidth(),

                color =
                    Color.Black.copy(
                        alpha = 0.60f
                    ),

                shape =
                    RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp
                    )

            ) {

                Column(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 24.dp,
                                vertical = 21.dp
                            ),

                    horizontalAlignment =
                        Alignment.CenterHorizontally

                ) {

                    Text(

                        text =
                            "Thanh toán thông minh",

                        color =
                            Color.White,

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                4.dp
                            )
                    )

                    Text(

                        text =
                            "Tên cửa hàng, số tiền và tài khoản nhận sẽ được đọc tự động từ mã QR.",

                        color =
                            Color.White.copy(
                                alpha = 0.67f
                            ),

                        fontSize =
                            11.sp,

                        lineHeight =
                            16.sp,

                        textAlign =
                            TextAlign.Center
                    )
                }
            }

        } else {

            /*
            |--------------------------------------------------------------------------
            | CAMERA NOT ALLOWED
            |--------------------------------------------------------------------------
            |
            | Vẫn cho phép chọn ảnh QR từ thư viện.
            |
            */

            CameraPermissionScreen(

                onBack =
                    onBack,

                onRequestPermission = {

                    permissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                },

                onSelectImage = {

                    invalidQrMessage = null

                    galleryLauncher.launch(
                        "image/*"
                    )
                },

                isGalleryScanning =
                    isGalleryScanning,

                invalidQrMessage =
                    invalidQrMessage
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| GALLERY BUTTON
|--------------------------------------------------------------------------
*/

@Composable
private fun GalleryButton(

    onClick: () -> Unit,

    isScanning: Boolean,

    darkTheme: Boolean

) {

    Button(

        onClick =
            onClick,

        enabled =
            !isScanning,

        modifier =
            Modifier
                .fillMaxWidth()
                .height(
                    50.dp
                ),

        shape =
            RoundedCornerShape(
                17.dp
            ),

        colors =
            if (darkTheme) {

                ButtonDefaults.buttonColors(

                    containerColor =
                        Color.White.copy(
                            alpha = 0.16f
                        ),

                    contentColor =
                        Color.White
                )

            } else {

                ButtonDefaults.buttonColors(

                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .secondaryContainer,

                    contentColor =
                        MaterialTheme
                            .colorScheme
                            .onSecondaryContainer
                )
            }

    ) {

        Icon(

            imageVector =
                Icons.Default.Image,

            contentDescription =
                null
        )

        Spacer(
            modifier =
                Modifier.size(
                    8.dp
                )
        )

        Text(

            text =
                if (isScanning) {

                    "Đang đọc ảnh QR..."

                } else {

                    "Chọn ảnh QR từ thư viện"
                },

            fontWeight =
                FontWeight.Bold
        )
    }
}

/*
|--------------------------------------------------------------------------
| INVALID QR MESSAGE
|--------------------------------------------------------------------------
*/

@Composable
private fun InvalidQrMessage(

    message: String,

    darkTheme: Boolean

) {

    Surface(

        shape =
            RoundedCornerShape(
                14.dp
            ),

        color =
            if (darkTheme) {

                Color(0xFFD83B3B)
                    .copy(
                        alpha = 0.90f
                    )

            } else {

                MaterialTheme
                    .colorScheme
                    .errorContainer
            }

    ) {

        Text(

            text =
                message,

            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 9.dp
                ),

            color =
                if (darkTheme) {

                    Color.White

                } else {

                    MaterialTheme
                        .colorScheme
                        .onErrorContainer
                },

            fontSize =
                11.sp,

            fontWeight =
                FontWeight.SemiBold,

            textAlign =
                TextAlign.Center
        )
    }
}

/*
|--------------------------------------------------------------------------
| CAMERA PREVIEW
|--------------------------------------------------------------------------
*/

@Composable
private fun CameraQrPreview(

    modifier: Modifier =
        Modifier,

    onQrDetected:
        (QrPaymentData) -> Unit,

    onInvalidQr:
        () -> Unit

) {

    val context =
        LocalContext.current

    val lifecycleOwner =
        LocalLifecycleOwner.current

    /*
    |--------------------------------------------------------------------------
    | ML KIT SCANNER
    |--------------------------------------------------------------------------
    */

    val scanner =
        remember {

            val options =
                BarcodeScannerOptions
                    .Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE
                    )
                    .build()

            BarcodeScanning.getClient(
                options
            )
        }

    /*
    |--------------------------------------------------------------------------
    | CAMERA EXECUTOR
    |--------------------------------------------------------------------------
    */

    val cameraExecutor =
        remember {

            Executors
                .newSingleThreadExecutor()
        }

    /*
    |--------------------------------------------------------------------------
    | PREVENT DUPLICATE QR
    |--------------------------------------------------------------------------
    */

    val paymentQrScanned =
        remember {

            AtomicBoolean(
                false
            )
        }

    /*
    |--------------------------------------------------------------------------
    | PREVENT MULTIPLE FRAME PROCESSING
    |--------------------------------------------------------------------------
    */

    val isProcessing =
        remember {

            AtomicBoolean(
                false
            )
        }

    /*
    |--------------------------------------------------------------------------
    | INVALID QR THROTTLE
    |--------------------------------------------------------------------------
    */

    val lastInvalidQrTime =
        remember {

            AtomicLong(
                0L
            )
        }

    /*
    |--------------------------------------------------------------------------
    | CAMERA PROVIDER
    |--------------------------------------------------------------------------
    */

    var cameraProvider by remember {

        mutableStateOf<
                ProcessCameraProvider?
                >(null)
    }

    /*
    |--------------------------------------------------------------------------
    | RELEASE RESOURCES
    |--------------------------------------------------------------------------
    */

    DisposableEffect(Unit) {

        onDispose {

            cameraProvider
                ?.unbindAll()

            scanner.close()

            cameraExecutor.shutdown()
        }
    }

    /*
    |--------------------------------------------------------------------------
    | CAMERA VIEW
    |--------------------------------------------------------------------------
    */

    AndroidView(

        modifier =
            modifier,

        factory = { ctx ->

            val previewView =
                PreviewView(
                    ctx
                ).apply {

                    implementationMode =
                        PreviewView
                            .ImplementationMode
                            .COMPATIBLE

                    scaleType =
                        PreviewView
                            .ScaleType
                            .FILL_CENTER
                }

            /*
            |--------------------------------------------------------------------------
            | CAMERA PROVIDER
            |--------------------------------------------------------------------------
            */

            val cameraProviderFuture =
                ProcessCameraProvider
                    .getInstance(
                        ctx
                    )

            cameraProviderFuture
                .addListener(

                    {

                        try {

                            val provider =
                                cameraProviderFuture
                                    .get()

                            cameraProvider =
                                provider

                            /*
                            |--------------------------------------------------------------------------
                            | PREVIEW
                            |--------------------------------------------------------------------------
                            */

                            val preview =
                                Preview
                                    .Builder()
                                    .build()
                                    .also {

                                        it.surfaceProvider =
                                            previewView
                                                .surfaceProvider
                                    }

                            /*
                            |--------------------------------------------------------------------------
                            | IMAGE ANALYSIS
                            |--------------------------------------------------------------------------
                            */

                            val imageAnalysis =
                                ImageAnalysis
                                    .Builder()
                                    .setBackpressureStrategy(

                                        ImageAnalysis
                                            .STRATEGY_KEEP_ONLY_LATEST
                                    )
                                    .build()

                            /*
                            |--------------------------------------------------------------------------
                            | ANALYZER
                            |--------------------------------------------------------------------------
                            */

                            imageAnalysis
                                .setAnalyzer(

                                    cameraExecutor

                                ) { imageProxy ->

                                    /*
                                    |--------------------------------------------------------------------------
                                    | QR ALREADY FOUND
                                    |--------------------------------------------------------------------------
                                    */

                                    if (
                                        paymentQrScanned
                                            .get()
                                    ) {

                                        imageProxy
                                            .close()

                                        return@setAnalyzer
                                    }

                                    /*
                                    |--------------------------------------------------------------------------
                                    | FRAME ALREADY PROCESSING
                                    |--------------------------------------------------------------------------
                                    */

                                    if (
                                        !isProcessing
                                            .compareAndSet(
                                                false,
                                                true
                                            )
                                    ) {

                                        imageProxy
                                            .close()

                                        return@setAnalyzer
                                    }

                                    /*
                                    |--------------------------------------------------------------------------
                                    | CONVERT IMAGE
                                    |--------------------------------------------------------------------------
                                    */

                                    val bitmap =
                                        try {

                                            imageProxy
                                                .toBitmap()

                                        } catch (
                                            exception:
                                            Exception
                                        ) {

                                            exception
                                                .printStackTrace()

                                            isProcessing
                                                .set(
                                                    false
                                                )

                                            imageProxy
                                                .close()

                                            return@setAnalyzer
                                        }

                                    /*
                                    |--------------------------------------------------------------------------
                                    | ML KIT INPUT
                                    |--------------------------------------------------------------------------
                                    */

                                    val inputImage =
                                        InputImage
                                            .fromBitmap(

                                                bitmap,

                                                imageProxy
                                                    .imageInfo
                                                    .rotationDegrees
                                            )

                                    /*
                                    |--------------------------------------------------------------------------
                                    | SCAN
                                    |--------------------------------------------------------------------------
                                    */

                                    scanner
                                        .process(
                                            inputImage
                                        )

                                        .addOnSuccessListener { barcodes ->

                                            val rawValue =
                                                barcodes
                                                    .firstOrNull {

                                                        !it.rawValue
                                                            .isNullOrBlank()
                                                    }
                                                    ?.rawValue
                                                    ?.trim()

                                            /*
                                            |--------------------------------------------------------------------------
                                            | NO QR
                                            |--------------------------------------------------------------------------
                                            */

                                            if (
                                                rawValue
                                                    .isNullOrBlank()
                                            ) {

                                                return@addOnSuccessListener
                                            }

                                            /*
                                            |--------------------------------------------------------------------------
                                            | PARSE TÍCH PHÚ QR
                                            |--------------------------------------------------------------------------
                                            */

                                            val qrData =
                                                QrPaymentParser
                                                    .parse(
                                                        rawValue
                                                    )

                                            /*
                                            |--------------------------------------------------------------------------
                                            | VALID QR
                                            |--------------------------------------------------------------------------
                                            */

                                            if (
                                                qrData != null
                                            ) {

                                                if (
                                                    paymentQrScanned
                                                        .compareAndSet(
                                                            false,
                                                            true
                                                        )
                                                ) {

                                                    ContextCompat
                                                        .getMainExecutor(
                                                            ctx
                                                        )
                                                        .execute {

                                                            onQrDetected(
                                                                qrData
                                                            )
                                                        }
                                                }

                                            } else {

                                                /*
                                                |--------------------------------------------------------------------------
                                                | INVALID QR
                                                |--------------------------------------------------------------------------
                                                */

                                                val now =
                                                    System
                                                        .currentTimeMillis()

                                                val lastTime =
                                                    lastInvalidQrTime
                                                        .get()

                                                if (
                                                    now -
                                                    lastTime >=
                                                    2000L
                                                ) {

                                                    if (
                                                        lastInvalidQrTime
                                                            .compareAndSet(
                                                                lastTime,
                                                                now
                                                            )
                                                    ) {

                                                        ContextCompat
                                                            .getMainExecutor(
                                                                ctx
                                                            )
                                                            .execute {

                                                                onInvalidQr()
                                                            }
                                                    }
                                                }
                                            }
                                        }

                                        .addOnFailureListener {
                                            it.printStackTrace()
                                        }

                                        .addOnCompleteListener {

                                            isProcessing
                                                .set(
                                                    false
                                                )

                                            imageProxy
                                                .close()
                                        }
                                }

                            /*
                            |--------------------------------------------------------------------------
                            | BIND CAMERA
                            |--------------------------------------------------------------------------
                            */

                            provider
                                .unbindAll()

                            provider
                                .bindToLifecycle(

                                    lifecycleOwner,

                                    CameraSelector
                                        .DEFAULT_BACK_CAMERA,

                                    preview,

                                    imageAnalysis
                                )

                        } catch (
                            exception:
                            Exception
                        ) {

                            exception
                                .printStackTrace()
                        }
                    },

                    ContextCompat
                        .getMainExecutor(
                            ctx
                        )
                )

            previewView
        }
    )
}

/*
|--------------------------------------------------------------------------
| TOP BAR
|--------------------------------------------------------------------------
*/

@Composable
private fun ScannerTopBar(

    onBack:
        () -> Unit

) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 18.dp,
                    top = 16.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically

    ) {

        Surface(

            shape =
                CircleShape,

            color =
                Color.Black.copy(
                    alpha = 0.43f
                )

        ) {

            IconButton(

                onClick =
                    onBack

            ) {

                Icon(

                    imageVector =
                        Icons.Default.ArrowBack,

                    contentDescription =
                        "Quay lại",

                    tint =
                        Color.White
                )
            }
        }

        Spacer(
            modifier =
                Modifier.weight(
                    1f
                )
        )

        Surface(

            shape =
                RoundedCornerShape(
                    50.dp
                ),

            color =
                Color.Black.copy(
                    alpha = 0.43f
                )

        ) {

            Text(

                text =
                    "TÍCH PHÚ PAY",

                modifier =
                    Modifier.padding(
                        horizontal = 13.dp,
                        vertical = 8.dp
                    ),

                color =
                    ScannerGold,

                fontSize =
                    10.sp,

                fontWeight =
                    FontWeight.ExtraBold,

                letterSpacing =
                    1.sp
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| SCANNER FRAME
|--------------------------------------------------------------------------
*/

@Composable
private fun ScannerFrame() {

    Box(

        modifier =
            Modifier.size(
                270.dp
            ),

        contentAlignment =
            Alignment.Center

    ) {

        Surface(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        10.dp
                    ),

            shape =
                RoundedCornerShape(
                    28.dp
                ),

            color =
                Color.White.copy(
                    alpha = 0.06f
                )

        ) {}

        Canvas(

            modifier =
                Modifier.fillMaxSize()

        ) {

            val stroke =
                7.dp.toPx()

            val corner =
                52.dp.toPx()

            val width =
                size.width

            val height =
                size.height

            /*
            |--------------------------------------------------------------------------
            | TOP LEFT
            |--------------------------------------------------------------------------
            */

            drawLine(

                color =
                    ScannerGold,

                start =
                    Offset(
                        0f,
                        corner
                    ),

                end =
                    Offset(
                        0f,
                        0f
                    ),

                strokeWidth =
                    stroke,

                cap =
                    StrokeCap.Round
            )

            drawLine(

                color =
                    ScannerGold,

                start =
                    Offset(
                        0f,
                        0f
                    ),

                end =
                    Offset(
                        corner,
                        0f
                    ),

                strokeWidth =
                    stroke,

                cap =
                    StrokeCap.Round
            )

            /*
            |--------------------------------------------------------------------------
            | TOP RIGHT
            |--------------------------------------------------------------------------
            */

            drawLine(

                color =
                    ScannerGold,

                start =
                    Offset(
                        width - corner,
                        0f
                    ),

                end =
                    Offset(
                        width,
                        0f
                    ),

                strokeWidth =
                    stroke,

                cap =
                    StrokeCap.Round
            )

            drawLine(

                color =
                    ScannerGold,

                start =
                    Offset(
                        width,
                        0f
                    ),

                end =
                    Offset(
                        width,
                        corner
                    ),

                strokeWidth =
                    stroke,

                cap =
                    StrokeCap.Round
            )

            /*
            |--------------------------------------------------------------------------
            | BOTTOM LEFT
            |--------------------------------------------------------------------------
            */

            drawLine(

                color =
                    ScannerGold,

                start =
                    Offset(
                        0f,
                        height - corner
                    ),

                end =
                    Offset(
                        0f,
                        height
                    ),

                strokeWidth =
                    stroke,

                cap =
                    StrokeCap.Round
            )

            drawLine(

                color =
                    ScannerGold,

                start =
                    Offset(
                        0f,
                        height
                    ),

                end =
                    Offset(
                        corner,
                        height
                    ),

                strokeWidth =
                    stroke,

                cap =
                    StrokeCap.Round
            )

            /*
            |--------------------------------------------------------------------------
            | BOTTOM RIGHT
            |--------------------------------------------------------------------------
            */

            drawLine(

                color =
                    ScannerGold,

                start =
                    Offset(
                        width - corner,
                        height
                    ),

                end =
                    Offset(
                        width,
                        height
                    ),

                strokeWidth =
                    stroke,

                cap =
                    StrokeCap.Round
            )

            drawLine(

                color =
                    ScannerGold,

                start =
                    Offset(
                        width,
                        height
                    ),

                end =
                    Offset(
                        width,
                        height - corner
                    ),

                strokeWidth =
                    stroke,

                cap =
                    StrokeCap.Round
            )

            /*
            |--------------------------------------------------------------------------
            | SCANNER LINE
            |--------------------------------------------------------------------------
            */

            drawLine(

                color =
                    ScannerPurple,

                start =
                    Offset(
                        28.dp.toPx(),
                        height / 2
                    ),

                end =
                    Offset(
                        width -
                                28.dp.toPx(),
                        height / 2
                    ),

                strokeWidth =
                    3.dp.toPx(),

                cap =
                    StrokeCap.Round
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| CAMERA PERMISSION SCREEN
|--------------------------------------------------------------------------
*/

@Composable
private fun CameraPermissionScreen(

    onBack:
        () -> Unit,

    onRequestPermission:
        () -> Unit,

    onSelectImage:
        () -> Unit,

    isGalleryScanning:
    Boolean,

    invalidQrMessage:
    String?

) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                )
                .padding(
                    24.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center

    ) {

        /*
        |--------------------------------------------------------------------------
        | ICON
        |--------------------------------------------------------------------------
        */

        Surface(

            modifier =
                Modifier.size(
                    90.dp
                ),

            shape =
                CircleShape,

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
                        Icons.Default
                            .QrCodeScanner,

                    contentDescription =
                        null,

                    tint =
                        MaterialTheme
                            .colorScheme
                            .primary,

                    modifier =
                        Modifier.size(
                            44.dp
                        )
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(
                    20.dp
                )
        )

        /*
        |--------------------------------------------------------------------------
        | TITLE
        |--------------------------------------------------------------------------
        */

        Text(

            text =
                "Cho phép sử dụng Camera",

            fontSize =
                23.sp,

            fontWeight =
                FontWeight.ExtraBold,

            color =
                MaterialTheme
                    .colorScheme
                    .onBackground,

            textAlign =
                TextAlign.Center
        )

        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )

        /*
        |--------------------------------------------------------------------------
        | DESCRIPTION
        |--------------------------------------------------------------------------
        */

        Text(

            text =
                "Tích Phú cần quyền Camera để quét mã QR thanh toán.",

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant,

            fontSize =
                13.sp,

            lineHeight =
                18.sp,

            textAlign =
                TextAlign.Center
        )

        Spacer(
            modifier =
                Modifier.height(
                    24.dp
                )
        )

        /*
        |--------------------------------------------------------------------------
        | CAMERA PERMISSION
        |--------------------------------------------------------------------------
        */

        Button(

            onClick =
                onRequestPermission,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        54.dp
                    ),

            shape =
                RoundedCornerShape(
                    17.dp
                ),

            colors =
                ButtonDefaults
                    .buttonColors(

                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .primary,

                        contentColor =
                            MaterialTheme
                                .colorScheme
                                .onPrimary
                    )

        ) {

            Icon(

                imageVector =
                    Icons.Default
                        .QrCodeScanner,

                contentDescription =
                    null
            )

            Spacer(
                modifier =
                    Modifier.size(
                        8.dp
                    )
            )

            Text(

                text =
                    "Cho phép Camera",

                fontWeight =
                    FontWeight.Bold
            )
        }

        Spacer(
            modifier =
                Modifier.height(
                    10.dp
                )
        )

        /*
        |--------------------------------------------------------------------------
        | GALLERY
        |--------------------------------------------------------------------------
        */

        GalleryButton(

            onClick =
                onSelectImage,

            isScanning =
                isGalleryScanning,

            darkTheme =
                false
        )

        /*
        |--------------------------------------------------------------------------
        | ERROR
        |--------------------------------------------------------------------------
        */

        if (!invalidQrMessage.isNullOrBlank()) {

            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )

            InvalidQrMessage(

                message =
                    invalidQrMessage,

                darkTheme =
                    false
            )
        }

        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )

        /*
        |--------------------------------------------------------------------------
        | BACK
        |--------------------------------------------------------------------------
        */

        Button(

            onClick =
                onBack,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        50.dp
                    ),

            shape =
                RoundedCornerShape(
                    17.dp
                ),

            colors =
                ButtonDefaults
                    .buttonColors(

                        containerColor =
                            Color.Transparent,

                        contentColor =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )

        ) {

            Text(

                text =
                    "Quay lại",

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}