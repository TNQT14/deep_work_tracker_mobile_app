package com.deepworktracker.dashboard.presentation.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepworktracker.dashboard.presentation.chart.AggregateBarChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onNavigateToCategoryDetail: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddRule by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Category") },
                actions = {
                    androidx.compose.material3.IconButton(
                        onClick = { viewModel.refresh() },
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading && uiState.categories.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Top categories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(uiState.categories) { item ->
                CategoryRow(
                    item = item,
                    selected = uiState.selectedCategory == item.category,
                    onSelect = { viewModel.selectCategory(item.category) },
                    onOpen = { onNavigateToCategoryDetail(item.category) }
                )
            }

            uiState.selectedCategory?.let { selected ->
                item {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Overview: $selected",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    AggregateBarChart(
                        title = "Trend theo tuần",
                        subtitle = "Phút focus theo tuần (12 tuần gần nhất)",
                        valuesMinutes = uiState.selectedWeekTrendMinutes
                    )
                }

                item {
                    AggregateBarChart(
                        title = "Trend theo tháng",
                        subtitle = "Phút focus theo tháng (12 tháng gần nhất)",
                        valuesMinutes = uiState.selectedMonthTrendMinutes
                    )
                }

                item {
                    HeatmapCard(heatmap = uiState.selectedHeatmapMinutes)
                }
            }

            item {
                RulesSection(
                    rules = uiState.rules,
                    onAdd = { showAddRule = true },
                    onDelete = { id -> viewModel.deleteRule(id) }
                )
            }
        }
    }

    if (showAddRule) {
        AddRuleDialog(
            defaultCategory = uiState.selectedCategory ?: uiState.categories.firstOrNull()?.category.orEmpty(),
            onDismiss = { showAddRule = false },
            onConfirm = { keyword, startHour, endHour, category, tag, priority ->
                viewModel.addRule(
                    keyword = keyword,
                    startHour = startHour,
                    endHour = endHour,
                    category = category,
                    tag = tag,
                    priority = priority
                )
                showAddRule = false
            }
        )
    }
}

@Composable
private fun CategoryRow(
    item: CategorySummary,
    selected: Boolean,
    onSelect: () -> Unit,
    onOpen: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${(item.shareRatio * 100).toInt()}% share",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${item.totalMinutes}m",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Detail",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onOpen() }
                )
            }
        }
    }
}

@Composable
private fun HeatmapCard(heatmap: List<List<Long>>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Heatmap theo thứ/giờ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Mỗi ô = phút focus. Đậm hơn = nhiều hơn.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
            Spacer(Modifier.height(12.dp))

            if (heatmap.isEmpty() || heatmap.all { row -> row.all { it == 0L } }) {
                Text(
                    text = "No data",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
                return@Column
            }

            val max = heatmap.maxOf { row -> row.maxOrNull() ?: 0L }.coerceAtLeast(1L)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                heatmap.take(7).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        row.take(24).forEach { v ->
                            val a = (v.toFloat() / max).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        color = lerpColor(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.colorScheme.primary,
                                            a
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RulesSection(
    rules: List<com.deepworktracker.domain.model.CategoryRule>,
    onAdd: () -> Unit,
    onDelete: (String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Auto-assign rules",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onAdd) { Text("Add") }
            }

            if (rules.isEmpty()) {
                Text(
                    text = "Chưa có rule. Thêm rule để auto-gợi ý/auto-gán category theo keyword/giờ.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            } else {
                rules.forEach { r ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${r.category}${r.tag?.let { " · #$it" } ?: ""}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(4.dp))
                                val kw = r.keyword?.let { "kw=\"$it\"" } ?: "kw=*"
                                val hr = if (r.startHour != null && r.endHour != null) "h=${r.startHour}-${r.endHour}" else "h=*"
                                Text(
                                    text = "$kw · $hr · p=${r.priority}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            TextButton(onClick = { onDelete(r.id) }) { Text("Delete") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddRuleDialog(
    defaultCategory: String,
    onDismiss: () -> Unit,
    onConfirm: (keyword: String?, startHour: Int?, endHour: Int?, category: String, tag: String?, priority: Int) -> Unit,
) {
    var keyword by remember { mutableStateOf("") }
    var startHourText by remember { mutableStateOf("") }
    var endHourText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(defaultCategory) }
    var tag by remember { mutableStateOf("") }
    var priority by remember { mutableIntStateOf(10) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add auto-assign rule") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = keyword,
                    onValueChange = { keyword = it },
                    label = { Text("Keyword (optional, contains)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = startHourText,
                        onValueChange = { startHourText = it },
                        label = { Text("Start hour") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endHourText,
                        onValueChange = { endHourText = it },
                        label = { Text("End hour") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Tag (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priority.toString(),
                    onValueChange = { priority = it.toIntOrNull() ?: priority },
                    label = { Text("Priority (higher wins)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val sh = startHourText.toIntOrNull()?.coerceIn(0, 23)
                    val eh = endHourText.toIntOrNull()?.coerceIn(0, 23)
                    onConfirm(
                        keyword.takeIf { it.isNotBlank() },
                        sh,
                        eh,
                        category,
                        tag.takeIf { it.isNotBlank() },
                        priority
                    )
                },
                enabled = category.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun lerpColor(a: Color, b: Color, t: Float): Color {
    val tt = t.coerceIn(0f, 1f)
    return Color(
        red = a.red + (b.red - a.red) * tt,
        green = a.green + (b.green - a.green) * tt,
        blue = a.blue + (b.blue - a.blue) * tt,
        alpha = 1f
    )
}

