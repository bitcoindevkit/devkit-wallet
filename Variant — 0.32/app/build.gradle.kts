plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 34

    buildFeatures {
        viewBinding = true
        compose = true
    }

    defaultConfig {
        applicationId = "com.goldenraven.devkitwallet"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "v0.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }

    namespace = "com.goldenraven.devkitwallet"
}

dependencies {
    // basic android dependencies
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("com.google.android.material:material:1.10.0")

    // compose
    // Adding the Bill of Materials synchronizes dependencies in the androidx.compose namespace
    // You can remove the library version in your dependency declarations
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    // implementation("androidx.navigation:navigation-compose:2.4.1")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.23.1")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.23.1")

    // toolbar
    implementation("androidx.appcompat:appcompat:1.6.1")

    // bitcoindevkit
    implementation("org.bitcoindevkit:bdk-android:0.32.1")

    // qr codes
    implementation("com.google.zxing:core:3.4.1")

    // tests
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
}
