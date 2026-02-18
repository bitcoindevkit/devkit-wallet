import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("com.android.application") version "8.13.2"
    id("org.jetbrains.kotlin.android") version "2.3.10"
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.10"
    id("com.google.protobuf") version "0.9.6"
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
}

// This is the version of the app that is displayed in the UI on the drawer.
val variantName = "Version 0.1.0/Kyoto"

android {
    namespace = "org.bitcoindevkit.devkitwallet"
    compileSdk = 36

    buildFeatures {
        viewBinding = true
        compose = true
    }

    defaultConfig {
        applicationId = "org.bitcoindevkit.devkitwallet"
        minSdk = 33
        targetSdk = 36
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
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    // Basic android dependencies
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.datastore:datastore:1.2.0")
    implementation("com.google.protobuf:protobuf-javalite:4.33.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
    implementation("androidx.core:core-splashscreen:1.2.0")

    // Jetpack Compose
    // Adding the Bill of Materials synchronizes dependencies in the androidx.compose namespace
    // You can remove the library version in your dependency declarations
    implementation(platform("androidx.compose:compose-bom:2026.02.00"))
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.navigation:navigation-compose:2.9.7")

    // Icons
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("com.composables:icons-lucide:1.1.0")

    // Bitcoin Development Kit
    implementation("org.bitcoindevkit:bdk-android:2.2.0")

    // QR codes
    implementation("com.google.zxing:core:3.5.4")

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
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
    version = "1.8.0"
    ignoreFailures = false
    reporters {
        reporter(ReporterType.PLAIN).apply { outputToConsole = true }
    }
}
