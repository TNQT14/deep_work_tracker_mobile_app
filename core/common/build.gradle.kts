plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.deepworktracker.common"
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
}

dependencies {
    // Date/Time
    implementation(libs.kotlinx.datetime)
    
    // Pure Kotlin utilities - no Android dependencies needed
    // Result and TimeFormatter are pure Kotlin classes
}