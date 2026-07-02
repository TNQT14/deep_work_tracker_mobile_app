plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.kapt) apply false
}

// Unit-test sources in this project do not use Hilt/Room kapt annotations.
// Skipping kapt on unit-test variants avoids noisy "options were not recognized"
// warnings when Hilt passes processor args but no processor runs on test sources.
subprojects {
    pluginManager.withPlugin("org.jetbrains.kotlin.kapt") {
        tasks.configureEach {
            if ((name.startsWith("kapt") || name.startsWith("kaptGenerateStubs")) &&
                name.contains("UnitTest", ignoreCase = true)
            ) {
                enabled = false
            }
        }
    }
}