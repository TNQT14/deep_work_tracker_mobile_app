package com.deepworktracker.session.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepworktracker.common.time.TimeFormatter
import com.deepworktracker.ui.theme.DeepWorkTrackerTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SessionScreen(
    viewModel: SessionViewModel = hiltViewModel(),
    onNavigateToDashboard: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    DeepWorkTrackerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopAppBar(
                    title = { Text("Deep Work Tracker") },
                    actions = {
                        IconButton(onClick = onNavigateToDashboard) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Dashboard"
                            )
                        }
                    }
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                val session = uiState.session
                if (uiState.isTracking && session != null) {
                    Text(
                        text = session.goal,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    
                    Text(
                        text = TimeFormatter.formatDuration(uiState.elapsedTime),
                        style = MaterialTheme.typography.displayLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 48.dp)
                    )
                    
                    Button(
                        onClick = { viewModel.endSession() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("End Session")
                    }
                } else {
                    var goalText by remember { mutableStateOf("") }
                    val recentGoals = uiState.recentSession
                    Text(
                        text = "What are you focusing on?",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    OutlinedTextField(
                        value = goalText,
                        onValueChange = { goalText = it },
                        label = { Text("Goal") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        singleLine = true
                    )

                    Text(
                        text = "Quick choose",
                        style = MaterialTheme.typography.bodySmall,
                        color =  MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                recentGoals.take(4).forEach { preset ->
                                    FilterChip(
                                        selected = goalText == preset,
                                        onClick = { goalText = preset },
                                        label = { Text(preset) }
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                recentGoals.drop(4).forEach { preset ->
                                    FilterChip(
                                        selected = goalText == preset,
                                        onClick = { goalText = preset },
                                        label = { Text(preset) }
                                    )
                                }
                            }
                        }
                    }
                    
                    Button(
                        onClick = { 
                            if (goalText.isNotBlank()) {
                                viewModel.startSession(goalText)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = goalText.isNotBlank() && !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        } else {
                            Text("Start Deep Work")
                        }
                    }
                }
                
                // Error message
                uiState.error?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error.message ?: "An error occurred",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}}
