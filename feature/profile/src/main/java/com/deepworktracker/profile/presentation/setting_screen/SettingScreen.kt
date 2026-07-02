package com.deepworktracker.profile.presentation.setting_screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.deepworktracker.domain.model.LanguagePreference
import com.deepworktracker.domain.model.SupportedLocales
import com.deepworktracker.domain.model.ThemePreference
import com.deepworktracker.profile.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    uiState: SettingUiState,
    onBack: () -> Unit,
    onThemeSelected: (ThemePreference) -> Unit,
    onLanguageTagSelected: (String) -> Unit,
    onClearError: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMsg) {
        uiState.errorMsg?.let { message ->
            snackbarHostState.showSnackbar(message)
            onClearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_appearance),
                style = MaterialTheme.typography.titleMedium,
            )
            ThemePreference.entries.forEach { option ->
                PreferenceRadioRow(
                    label = option.name,
                    selected = option == uiState.theme,
                    enabled = !uiState.isSaving,
                    onClick = { onThemeSelected(option) }
                )
            }

            HorizontalDivider()

            Text(
                text = stringResource(R.string.settings_language),
                style = MaterialTheme.typography.titleMedium,
            )
            languageOptions().forEach { (tag, label) ->
                Log.d("SettingScreen", "Language option: $tag, $label")
                PreferenceRadioRow(
                    label = label,
                    selected = isLanguageSelected(uiState.language, tag),
                    enabled = !uiState.isSaving,
                    onClick = { onLanguageTagSelected(tag) }
                )

            }
            if (uiState.isSaving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

    }


}

@Composable
private fun PreferenceRadioRow(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, enabled = enabled)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null, enabled = enabled)
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}


@Composable
private fun themeLabel(theme: ThemePreference): String = when (theme) {
    ThemePreference.SYSTEM -> stringResource(R.string.settings_theme_system)
    ThemePreference.LIGHT -> stringResource(R.string.settings_theme_light)
    ThemePreference.DARK -> stringResource(R.string.settings_theme_dark)
}

@Composable
private fun languageOptions(): List<Pair<String, String>> = listOf(
    SettingViewModel.LANGUAGE_TAG_SYSTEM to stringResource(R.string.settings_language_system),
    SupportedLocales.ENGLISH to stringResource(R.string.settings_language_english),
    SupportedLocales.VIETNAMESE to stringResource(R.string.settings_language_vietnamese),
)

private fun isLanguageSelected(language: LanguagePreference, tag: String): Boolean =
    when (tag) {
        SettingViewModel.LANGUAGE_TAG_SYSTEM -> language is LanguagePreference.System
        SupportedLocales.ENGLISH -> language == LanguagePreference.Fixed(SupportedLocales.ENGLISH)
        SupportedLocales.VIETNAMESE -> language == LanguagePreference.Fixed(SupportedLocales.VIETNAMESE)
        else -> false
    }