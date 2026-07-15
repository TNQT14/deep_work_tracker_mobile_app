package com.deepworktracker.profile.presentation.setting_screen

import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.deepworktracker.domain.model.LanguagePreference
import com.deepworktracker.domain.model.SupportedLocales
import com.deepworktracker.domain.model.ThemePreference
import com.deepworktracker.profile.R
import android.app.NotificationManager
import android.provider.Settings
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    uiState: SettingUiState,
    onBack: () -> Unit,
    onThemeSelected: (ThemePreference) -> Unit,
    onLanguageTagSelected: (String) -> Unit,
    onDndToggle: (Boolean) -> Unit,
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

            HorizontalDivider()

            Text(
                text = stringResource(R.string.settings_focus_shield),
                style = MaterialTheme.typography.titleMedium,
            )

            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            var hasPolicyAccess by remember { mutableStateOf(hasDndAccess(context)) }
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) hasPolicyAccess = hasDndAccess(context)
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.settings_shield_dnd_label),
                        style = MaterialTheme.typography.bodyLarge)
                    Text(stringResource(R.string.settings_shield_dnd_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = uiState.shieldDndEnabled,
                    enabled = !uiState.isSaving,
                    onCheckedChange = onDndToggle,
                )
            }
            if (uiState.shieldDndEnabled && !hasPolicyAccess) {
                Text(stringResource(R.string.settings_shield_permission_needed),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error)
                TextButton(onClick = { context.startActivity(dndAccessIntent()) }) {
                    Text(stringResource(R.string.settings_shield_grant_permission))
                }
            }

        }

    }


}

private fun hasDndAccess(context: Context): Boolean =
    context.getSystemService(NotificationManager::class.java).isNotificationPolicyAccessGranted

private fun dndAccessIntent(): Intent =
    Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

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