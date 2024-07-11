val appGroupId = "kz.tarlanpayments.storage"
val appArtifactId = "androidsdk"
val appVersion = "1.0.6"

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

group = "${appGroupId}.${appArtifactId}"
version = appVersion

android {
    buildFeatures.buildConfig = true
    namespace = "${appGroupId}.${appArtifactId}"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            buildConfigField("String", "BASE_URL", "\"https://prapi.tarlanpayments.kz\"")
            buildConfigField("String", "MERCHANT_GATEWAY", "\"tarlanpayments\"")
            buildConfigField("String", "MERCHANT_GATEWAY_ID", "\"BCR2DN4T7C37TEZS\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://prapi.tarlanpayments.kz\"")
            buildConfigField("String", "MERCHANT_GATEWAY", "\"tarlanpayments\"")
            buildConfigField("String", "MERCHANT_GATEWAY_ID", "\"BCR2DN4T7C37TEZS\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("com.google.android.material:material:1.12.0")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")

    implementation("androidx.compose.material:material:1.6.8")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.github.terrakok:cicerone:7.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.28.0")
    implementation("com.github.SmartToolFactory:Compose-Screenshot:1.0.3")
    implementation("com.google.android.gms:play-services-wallet:19.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}