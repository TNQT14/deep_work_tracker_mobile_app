package com.deepworktracker.domain.model

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromStorage(value: String?): ThemePreference = entries.find{
            it.name == value
        } ?: SYSTEM
    }
}