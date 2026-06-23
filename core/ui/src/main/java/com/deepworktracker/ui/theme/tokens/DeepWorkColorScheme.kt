package com.deepworktracker.ui.theme.tokens

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object DeepWorkColorScheme {

    val light = lightColorScheme(
        primary = SemanticColors.Light.brandPrimary,
        onPrimary = RawColors.White,
        primaryContainer = RawColors.Indigo600.copy(alpha = 0.12f),
        onPrimaryContainer = SemanticColors.Light.textPrimary,
        secondary = SemanticColors.Light.brandSecondary,
        onSecondary = RawColors.White,
        background = SemanticColors.Light.backgroundPrimary,
        onBackground = SemanticColors.Light.textPrimary,
        surface = SemanticColors.Light.surfacePrimary,
        onSurface = SemanticColors.Light.textPrimary,
        surfaceVariant = RawColors.Gray100,
        onSurfaceVariant = SemanticColors.Light.textSecondary,
        outline = SemanticColors.Light.borderDefault,
        error = SemanticColors.Light.feedbackError,
        onError = RawColors.White,
        errorContainer = SemanticColors.Light.feedbackError.copy(alpha = 0.12f),
        onErrorContainer = SemanticColors.Light.feedbackError,
    )

    val dark = darkColorScheme(
        primary = SemanticColors.Dark.brandPrimary,
        onPrimary = RawColors.White,
        primaryContainer = RawColors.Indigo600.copy(alpha = 0.24f),
        onPrimaryContainer = SemanticColors.Dark.textPrimary,
        secondary = SemanticColors.Dark.brandSecondary,
        onSecondary = RawColors.White,
        background = SemanticColors.Dark.backgroundPrimary,
        onBackground = SemanticColors.Dark.textPrimary,
        surface = SemanticColors.Dark.surfacePrimary,
        onSurface = SemanticColors.Dark.textPrimary,
        surfaceVariant = RawColors.Gray800,
        onSurfaceVariant = SemanticColors.Dark.textSecondary,
        outline = SemanticColors.Dark.borderDefault,
        error = SemanticColors.Dark.feedbackError,
        onError = RawColors.Gray900,
        errorContainer = SemanticColors.Dark.feedbackError.copy(alpha = 0.24f),
        onErrorContainer = SemanticColors.Dark.feedbackError,
    )

    fun fromDarkTheme(isDark: Boolean) = if (isDark) dark else light
}