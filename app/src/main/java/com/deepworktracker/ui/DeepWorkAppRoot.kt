package com.deepworktracker.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepworktracker.domain.model.UserPreferences
import com.deepworktracker.preferences.LanguageManager
import com.deepworktracker.preferences.ThemeManager
import com.deepworktracker.ui.theme.DeepWorkTrackerTheme

@Composable
fun DeepWorkAppRoot(
    themeManager: ThemeManager,
    languageManager: LanguageManager,
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val systemLocaleTag = LocalConfiguration.current.locales[0]
        ?.toLanguageTag() ?: "en"

    val preferences by themeManager.preferences.collectAsStateWithLifecycle(
        initialValue = UserPreferences.DEFAULT,
    )

    // Theme
    val resolvedTheme = themeManager.resolveTheme(
        preferences = preferences,
        isSystemDark = isSystemDark,
    )

    // Language
    val resolvedLocale = languageManager.resolveLocale(
        preferences = preferences,
        systemLocaleTag = systemLocaleTag,
    )

    DeepWorkTrackerTheme(darkTheme = resolvedTheme.isDark) {
        ProvideAppLocale(localeTag = resolvedLocale.localeTag) {
            content()
        }
    }
}
