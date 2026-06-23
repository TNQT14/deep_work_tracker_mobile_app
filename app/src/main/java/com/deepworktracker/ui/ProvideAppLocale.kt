package com.deepworktracker.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import java.util.Locale

/**
 * Applies the resolved app locale at the Compose layer, mirroring how
 * [com.deepworktracker.ui.theme.DeepWorkTrackerTheme] applies colors.
 *
 * Overriding [LocalContext] and [LocalConfiguration] makes `stringResource()`
 * re-read the correct `values/` / `values-vi/` strings on recomposition, so the
 * UI language switches instantly without recreating the Activity.
 */
@Composable
fun ProvideAppLocale(
    localeTag: String,
    content: @Composable () -> Unit,
) {
    val baseContext = LocalContext.current
    val baseConfiguration = LocalConfiguration.current

    val localizedConfiguration = remember(baseConfiguration, localeTag) {
        Configuration(baseConfiguration).apply {
            setLocale(Locale.forLanguageTag(localeTag))
        }
    }
    val localizedResources = remember(baseContext, localizedConfiguration) {
        baseContext.createConfigurationContext(localizedConfiguration).resources
    }
    CompositionLocalProvider(
        LocalConfiguration provides localizedConfiguration,
        LocalResources provides localizedResources,
    ) {
        content()
    }
}
