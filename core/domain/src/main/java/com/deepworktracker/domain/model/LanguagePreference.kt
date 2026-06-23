package com.deepworktracker.domain.model

sealed class LanguagePreference {
    data object System : LanguagePreference()
    data class Fixed(val localeTag: String): LanguagePreference()

    val storageKey : String
        get() = when(this){
            is System -> STORAGE_SYSTEM
            is Fixed -> localeTag
        }

    companion object {
        const val STORAGE_SYSTEM = "system"

        fun fromStorage(value: String?): LanguagePreference {
            if (value.isNullOrBlank() || value == STORAGE_SYSTEM) return System
            return Fixed(value)
        }
    }
}