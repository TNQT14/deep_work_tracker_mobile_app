package com.deepworktracker.profile.presentation.blocklist_screen

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.deepworktracker.profile.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlocklistScreen(
    uiState: BlocklistUiState,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onToggle: (String, Boolean) -> Unit,
    onClearError: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.errorMsg) {
        uiState.errorMsg?.let { snackbarHostState.showSnackbar(it); onClearError() }
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasUsage by remember { mutableStateOf(hasUsageAccess(context)) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) hasUsage = hasUsageAccess(context)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.blocklist_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (!hasUsage) {
                Text(
                    stringResource(R.string.blocklist_usage_access_needed),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                TextButton(
                    onClick = { context.startActivity(usageAccessIntent()) },
                    modifier = Modifier.padding(horizontal = 8.dp),
                ) { Text(stringResource(R.string.blocklist_grant_usage_access)) }
            }

            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                singleLine = true,
                label = { Text(stringResource(R.string.blocklist_search)) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )

            if (uiState.isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(uiState.filteredApps, key = { it.packageName }) { app ->
                        Row(
                            Modifier.fillMaxWidth()
                                .clickable { onToggle(app.packageName, app.packageName !in uiState.blocklist) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(app.label, Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge)
                            Checkbox(
                                checked = app.packageName in uiState.blocklist,
                                onCheckedChange = { onToggle(app.packageName, it) },
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun hasUsageAccess(context: Context): Boolean {
    val appOps = context.getSystemService(AppOpsManager::class.java)
    val mode = appOps.unsafeCheckOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName,
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

private fun usageAccessIntent(): Intent =
    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)