package com.deepworktracker.data.remote.model.response

data class ApiEnvelope<T>(
    val success: Boolean,
    val data: T?,
)

data class MessageData(val message: String?)