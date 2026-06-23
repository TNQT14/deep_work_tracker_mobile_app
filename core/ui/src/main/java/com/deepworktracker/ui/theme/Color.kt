package com.deepworktracker.ui.theme

import androidx.compose.ui.graphics.Color
import com.deepworktracker.ui.theme.tokens.RawColors

// Backward-compatible aliases — feature modules đang import các val này vẫn compile
val Primary = RawColors.Indigo500
val PrimaryVariant = RawColors.Indigo600
val Secondary = RawColors.Emerald500
val Background = RawColors.Gray50
val Surface = RawColors.White
val Error = RawColors.Red500
val OnPrimary = RawColors.White
val OnSecondary = RawColors.White
val OnBackground = RawColors.Gray900
val OnSurface = RawColors.Gray900

// Legacy
val Purple80 = RawColors.Purple80
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = RawColors.Purple40
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)