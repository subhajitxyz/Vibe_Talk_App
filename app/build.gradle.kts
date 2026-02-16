plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id ("kotlin-kapt")
    id("com.google.dagger.hilt.android")

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.real.vibechat"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.real.vibechat"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    implementation("androidx.navigation:navigation-compose:2.9.6")

    // For hilt Implementation
    implementation("com.google.dagger:hilt-android:2.57.1")
    kapt("com.google.dagger:hilt-compiler:2.57.1")

    // Core coroutine library for general use
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    // Android-specific coroutine support and Dispatchers.Main
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // Lifecycle-aware components (lifecycleScope, viewModelScope)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")



    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:34.9.0"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
//    implementation("com.google.firebase:firebase-firestore-ktx")

    implementation("io.coil-kt:coil-compose:2.6.0")


}