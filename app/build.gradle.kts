import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("com.android.application") version "8.7.1"
    id("org.jetbrains.kotlin.android") version "2.1.10"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
    id("com.google.protobuf") version "0.9.4"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
}

// This is the version of the app that is displayed in the UI on the drawer.
val variantName = "Version 0.1.0/Kyoto"

android {
    namespace = "org.bitcoindevkit.devkitwallet"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
        compose = true
    }

    defaultConfig {
        applicationId = "org.bitcoindevkit.devkitwallet"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "v0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "VARIANT_NAME", "\"$variantName\"")
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Basic android dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.datastore:datastore:1.1.1")
    implementation("com.google.protobuf:protobuf-javalite:3.18.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Jetpack Compose
    // Adding the Bill of Materials synchronizes dependencies in the androidx.compose namespace
    // You can remove the library version in your dependency declarations
    implementation(platform("androidx.compose:compose-bom:2025.02.00"))
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.23.1")

    // Icons
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("com.composables:icons-lucide:1.0.0")

    // Toolbar
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Bitcoin Development Kit
    implementation("org.bitcoindevkit:bdk-android:1.2.0")

    // QR codes
    implementation("com.google.zxing:core:3.5.3")

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.0"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

ktlint {
    version = "1.5.0"
    ignoreFailures = false
    reporters {
        reporter(ReporterType.PLAIN).apply { outputToConsole = true }
    }
}
