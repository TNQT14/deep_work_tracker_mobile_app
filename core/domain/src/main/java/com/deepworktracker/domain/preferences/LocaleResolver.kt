package com.deepworktracker.domain.preferences

import com.deepworktracker.domain.model.DeviceEnviroment
import com.deepworktracker.domain.model.LanguagePreference
import com.deepworktracker.domain.model.ResolvedLocale
import com.deepworktracker.domain.model.SupportedLocales
import com.deepworktracker.domain.model.TextDirection

object LocaleResolver {

    private val RTL_PREFIXES = setOf("ar", "he", "fa", "ur")

    fun resolve(
        preference: LanguagePreference,
        device: DeviceEnviroment,
    ): ResolvedLocale {
        val rawTag = when (preference) {
            is LanguagePreference.System -> device.systemLocaleTag
            is LanguagePreference.Fixed -> preference.localeTag
        }

        val normalized = SupportedLocales.normalize(rawTag)
        val effectiveTag = if (SupportedLocales.isSupported(normalized)) {
            normalized
        } else {
            SupportedLocales.ENGLISH
        }

        return ResolvedLocale(
            localeTag = effectiveTag,
            textDirection = if (RTL_PREFIXES.any { effectiveTag.startsWith(it) }) {
                TextDirection.RTL
            } else {
                TextDirection.LTR
            },
            followsSystem = preference is LanguagePreference.System,
        )
    }
}