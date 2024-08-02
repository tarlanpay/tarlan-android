val appGroupId = "kz.tarlanpayments.storage"
val appArtifactId = "androidsdk.noui"
val appVersion = "1.1.2"

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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.fragment:fragment-ktx:1.8.2")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}