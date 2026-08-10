package com.deepworktracker.domain.model

/**
 * User-chosen Focus Shield configuration (persisted).
 *
 * @param dndEnabled auto-enable Do Not Disturb while a focus session is running.
 * @param blocklist package names treated as distractions during a session.
 */
data class FocusShieldConfig(
    val dndEnabled: Boolean,
    val blocklist: Set<String>,
) {
    companion object {
        val DEFAULT = FocusShieldConfig(dndEnabled = false, blocklist = emptySet())
    }
}
