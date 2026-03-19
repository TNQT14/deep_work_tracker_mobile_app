package com.deepworktracker.session.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
                    var categoryText by remember { mutableStateOf("") }
                    var tagText by remember { mutableStateOf("") }
                    val recentGoals = uiState.recentSession
                    val recentCategories = uiState.recentCategories
                    val recentTags = uiState.recentTags
                    Text(
                        text = "What are you focusing on?",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    OutlinedTextField(
                        value = goalText,
                        onValueChange = {
                            goalText = it
                            viewModel.suggestForGoal(it)
                        },
                        label = { Text("Goal") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = categoryText,
                        onValueChange = { categoryText = it },
                        label = { Text("Category (optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = tagText,
                        onValueChange = { tagText = it },
                        label = { Text("Tag (optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        singleLine = true
                    )

                    uiState.suggestedCategory?.let { suggested ->
                        if (categoryText.isBlank()) {
                            AssistChip(
                                onClick = { categoryText = suggested },
                                label = { Text("Suggested category: $suggested") },
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }
                    uiState.suggestedTag?.let { suggested ->
                        if (tagText.isBlank()) {
                            AssistChip(
                                onClick = { tagText = suggested },
                                label = { Text("Suggested tag: $suggested") },
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }

                    Text(
                        text = "Quick choose",
                        style = MaterialTheme.typography.bodySmall,
                        color =  MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 100.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(recentGoals) { preset ->
                            FilterChip(
                                selected = goalText == preset,
                                onClick = {
                                    goalText = preset
                                    viewModel.suggestForGoal(preset)
                                },
                                label = { Text(preset, maxLines = 1) }
                            )
                        }
                    }

                    if (recentCategories.isNotEmpty()) {
                        Text(
                            text = "Quick category",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 120.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 160.dp)
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(recentCategories) { preset ->
                                FilterChip(
                                    selected = categoryText == preset,
                                    onClick = { categoryText = preset },
                                    label = { Text(preset, maxLines = 1) }
                                )
                            }
                        }
                    }

                    if (recentTags.isNotEmpty()) {
                        Text(
                            text = "Quick tag",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 120.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 160.dp)
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(recentTags) { preset ->
                                FilterChip(
                                    selected = tagText == preset,
                                    onClick = { tagText = preset },
                                    label = { Text(preset, maxLines = 1) }
                                )
                            }
                        }
                    }
                    Button(
                        onClick = { 
                            if (goalText.isNotBlank()) {
                                viewModel.startSession(
                                    goal = goalText,
                                    category = categoryText.trim().takeIf { it.isNotBlank() },
                                    tag = tagText.trim().takeIf { it.isNotBlank() }
                                )
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
