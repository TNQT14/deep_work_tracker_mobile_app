package com.deepworktracker.domain.error

sealed class DeepWorkError : Throwable() {
    object SessionNotFound : DeepWorkError() {
        override val message: String = "Session not found"
    }
    object ActiveSessionExists : DeepWorkError() {
        override val message: String = "An active session already exists"
    }
    object NoActiveSession : DeepWorkError() {
        override val message: String = "No active session"
    }
    object DatabaseError : DeepWorkError() {
        override val message: String = "Database error"
    }
    object NetworkError : DeepWorkError() {
        override val message: String = "Network error"
    }
    data class UnknownError(override val message: String) : DeepWorkError()
}
