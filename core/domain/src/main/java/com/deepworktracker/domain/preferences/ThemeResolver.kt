package com.deepworktracker.domain.preferences

import com.deepworktracker.domain.model.DeviceEnviroment
import com.deepworktracker.domain.model.ThemePreference
import com.deepworktracker.domain.model.preferences.ResolvedTheme

object ThemeResolver {
    fun resolve(preference: ThemePreference, device: DeviceEnviroment): ResolvedTheme =
        when (preference) {
            ThemePreference.SYSTEM -> ResolvedTheme(
                isDark = device.isSystemDark,
                userChoice = preference,
                followsSystem = true,
            )

            ThemePreference.LIGHT -> ResolvedTheme(
                isDark = false,
                userChoice = preference,
                followsSystem = false,
            )

            ThemePreference.DARK -> ResolvedTheme(
                isDark = true,
                userChoice = preference,
                followsSystem = false,
            )

        }
}