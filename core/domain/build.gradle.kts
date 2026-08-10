plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.deepworktracker.domain"
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
    // Coroutines Flow
    implementation(libs.kotlinx.coroutines.core)

    // Date/Time
    implementation(libs.kotlinx.datetime)

    // Testing
    testImplementation(libs.junit)
}
