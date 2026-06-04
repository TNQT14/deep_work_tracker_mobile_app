package com.deepworktracker.data.remote.model.request

data class ForgotPasswordResetRequest(
    val email: String,
    val password: String,
)