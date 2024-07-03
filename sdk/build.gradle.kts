plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("maven-publish")
    id("signing")
}

group = "kz.tarlanpayments.storage.androidsdk"
version = "1.0.0"

android {
    buildFeatures.buildConfig = true
    namespace = "kz.tarlanpayments.storage.androidsdk"
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
    val composeBom = platform("androidx.compose:compose-bom:2024.02.02")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.1")

    implementation("androidx.compose.material:material")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.squareup.okhttp3:okhttp:4.11.0")
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


publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["release"])

            groupId = "kz.tarlanpayments.storage"
            artifactId = "androidsdk"
            version = "1.0.0"

            pom {
                name.set("tarlan-android")
                description.set("Tarlan Android SDK")
                url.set("https://github.com/your-repo/your-library")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("your-id")
                        name.set("Your Name")
                        email.set("your-email@example.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/your-repo/your-library.git")
                    developerConnection.set("scm:git:ssh://github.com:your-repo/your-library.git")
                    url.set("https://github.com/your-repo/your-library")
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(System.getenv("GPG_SIGNING_KEY"), System.getenv("GPG_SIGNING_PASSWORD"))
    sign(publishing.publications["release"])
}