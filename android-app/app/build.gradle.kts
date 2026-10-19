plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.personalfinance"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.personalfinance"

        minSdk = 29
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        release {

            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility =
            JavaVersion.VERSION_11

        targetCompatibility =
            JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("androidx.compose.material:material-icons-extended")

    /*
|--------------------------------------------------------------------------
| CAMERA X - QUÉT QR
|--------------------------------------------------------------------------
*/

    implementation(
        "androidx.camera:camera-core:1.6.2"
    )

    implementation(
        "androidx.camera:camera-camera2:1.6.2"
    )

    implementation(
        "androidx.camera:camera-lifecycle:1.6.2"
    )

    implementation(
        "androidx.camera:camera-view:1.6.2"
    )

    /*
    |--------------------------------------------------------------------------
    | ML KIT - BARCODE / QR CODE SCANNER
    |--------------------------------------------------------------------------
    */

    implementation(
        "com.google.mlkit:barcode-scanning:17.3.0"
    )
    /*
    |--------------------------------------------------------------------------
    | FIREBASE
    |--------------------------------------------------------------------------
    */

    implementation(
        platform(
            "com.google.firebase:firebase-bom:34.3.0"
        )
    )

    implementation(
        "com.google.firebase:firebase-auth"
    )

    implementation(
        "com.google.firebase:firebase-messaging"
    )

    /*
    |--------------------------------------------------------------------------
    | GOOGLE SIGN-IN
    | CREDENTIAL MANAGER
    |--------------------------------------------------------------------------
    */

    implementation(
        "androidx.credentials:credentials:1.3.0"
    )

    implementation(
        "androidx.credentials:credentials-play-services-auth:1.3.0"
    )

    implementation(
        "com.google.android.libraries.identity.googleid:googleid:1.1.1"
    )

    /*
    |--------------------------------------------------------------------------
    | RETROFIT
    |--------------------------------------------------------------------------
    */

    implementation(
        "com.squareup.retrofit2:retrofit:2.11.0"
    )

    implementation(
        "com.squareup.retrofit2:converter-gson:2.11.0"
    )

    /*
    |--------------------------------------------------------------------------
    | OKHTTP
    |--------------------------------------------------------------------------
    */

    implementation(
        "com.squareup.okhttp3:logging-interceptor:4.12.0"
    )

    /*
    |--------------------------------------------------------------------------
    | NAVIGATION
    |--------------------------------------------------------------------------
    */

    implementation(
        "androidx.navigation:navigation-compose:2.9.3"
    )

    /*
    |--------------------------------------------------------------------------
    | COROUTINES + GOOGLE TASKS
    |--------------------------------------------------------------------------
    */

    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2"
    )

    /*
    |--------------------------------------------------------------------------
    | MATERIAL ICONS
    |--------------------------------------------------------------------------
    */

    implementation(
        "androidx.compose.material:material-icons-extended"
    )

    /*
    |--------------------------------------------------------------------------
    | ANDROIDX
    |--------------------------------------------------------------------------
    */

    implementation(
        libs.androidx.core.ktx
    )

    implementation(
        libs.androidx.lifecycle.runtime.ktx
    )

    implementation(
        libs.androidx.activity.compose
    )

    /*
    |--------------------------------------------------------------------------
    | COMPOSE
    |--------------------------------------------------------------------------
    */

    implementation(
        platform(
            libs.androidx.compose.bom
        )
    )

    implementation(
        libs.androidx.ui
    )

    implementation(
        libs.androidx.ui.graphics
    )

    implementation(
        libs.androidx.ui.tooling.preview
    )

    implementation(
        libs.androidx.material3
    )

    /*
    |--------------------------------------------------------------------------
    | TEST
    |--------------------------------------------------------------------------
    */

    testImplementation(
        libs.junit
    )

    androidTestImplementation(
        libs.androidx.junit
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        platform(
            libs.androidx.compose.bom
        )
    )

    androidTestImplementation(
        libs.androidx.ui.test.junit4
    )

    /*
    |--------------------------------------------------------------------------
    | DEBUG
    |--------------------------------------------------------------------------
    */

    debugImplementation(
        libs.androidx.ui.tooling
    )

    debugImplementation(
        libs.androidx.ui.test.manifest
    )
}