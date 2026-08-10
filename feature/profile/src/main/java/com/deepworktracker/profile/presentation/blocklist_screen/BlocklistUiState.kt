package com.deepworktracker.profile.presentation.blocklist_screen

data class BlocklistUiState(
    val apps: List<InstalledApp> = emptyList(),
    val blocklist: Set<String> = emptySet(),
    val query: String = "",
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
) {
    val filteredApps: List<InstalledApp>
        get() = if (query.isBlank()) apps else apps.filter {
            it.label.contains(
                query,
                ignoreCase = true
            )
        }
}