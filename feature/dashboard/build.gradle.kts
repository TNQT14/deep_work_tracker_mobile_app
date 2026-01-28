plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.deepworktracker.dashboard"
    compileSdk = 36

    defaultConfig {
        minSdk = 33
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
    // Core
    implementation(libs.androidx.core.ktx)
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    
    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.compiler)
    
    // Domain
    implementation(project(":core:domain"))
    
    // Data
    implementation(project(":core:data"))
    
    // UI
    implementation(project(":core:ui"))
    
    // Common
    implementation(project(":core:common"))
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    
    // DateTime
    implementation(libs.kotlinx.datetime)

    // Charts
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.core)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
