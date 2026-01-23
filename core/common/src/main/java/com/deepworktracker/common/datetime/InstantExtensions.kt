package com.deepworktracker.common.datetime

import kotlinx.datetime.Instant

/**
 * Extension function to convert Instant to epoch milliseconds
 * kotlinx.datetime.Instant uses epochSeconds internally, so we convert to milliseconds
 */
fun Instant.toEpochMilliseconds(): Long {
    return this.epochSeconds * 1000 + (this.nanosecondsOfSecond / 1_000_000)
}
