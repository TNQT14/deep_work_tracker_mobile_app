package com.deepworktracker.ui.theme.tokens

import androidx.compose.ui.graphics.Color

object SemanticColors {
    object Light {
        val backgroundPrimary = RawColors.Gray50
        val surfacePrimary = RawColors.White
        val textPrimary = RawColors.Gray900
        val textSecondary = RawColors.Gray900.copy(alpha = 0.7f)
        val borderDefault = RawColors.Gray900.copy(alpha = 0.12f)
        val brandPrimary = RawColors.Indigo500
        val brandSecondary = RawColors.Emerald500
        val feedbackError = RawColors.Red500
        val feedbackSuccess = RawColors.Green600
    }

    object Dark {
        val backgroundPrimary = RawColors.Gray900
        val surfacePrimary = RawColors.Gray800
        val textPrimary = RawColors.Gray50
        val textSecondary = RawColors.Gray50.copy(alpha = 0.7f)
        val borderDefault = RawColors.Gray50.copy(alpha = 0.24f)
        val brandPrimary = RawColors.Indigo500
        val brandSecondary = RawColors.Emerald500
        val feedbackError = RawColors.Red400
        val feedbackSuccess = RawColors.Green600
    }
}

object ComponentColors {
    val todoInProgress = RawColors.Blue600
    val todoPaused = RawColors.Amber600
    val todoDone = RawColors.Green600

    fun todoInProgressBackground(): Color = todoInProgress.copy(alpha = 0.2f)
    fun todoPausedBackground(): Color = todoPaused.copy(alpha = 0.2f)
    fun todoDoneBackground(): Color = todoDone.copy(alpha = 0.2f)
    val focusAccent = RawColors.Indigo500
    val focusBreak = RawColors.Emerald500

    fun focusBreakBackground(): Color = focusBreak.copy(alpha = 0.15f)
}