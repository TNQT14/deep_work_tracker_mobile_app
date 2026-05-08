package com.deepworktracker.common.datetime

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Extension function to convert Instant to epoch milliseconds
 * kotlinx.datetime.Instant uses epochSeconds internally, so we convert to milliseconds
 */
fun Instant.toEpochMilliseconds(): Long {
    return this.epochSeconds * 1000 + (this.nanosecondsOfSecond / 1_000_000)
}

fun Instant.toDdMmYyyyCompact(zone: TimeZone = TimeZone.currentSystemDefault(),):String{
    val date = this.toLocalDateTime(zone).date
    val d = date.dayOfMonth.toString().padStart(2, '0')
    val m = date.monthNumber.toString().padStart(2, '0')
    val y = date.year
    return "$d/$m/$y"
}

fun Instant?.toDdMmYyyyCompactOrNull(
    zone: TimeZone = TimeZone.currentSystemDefault(),
): String? = this?.toDdMmYyyyCompact(zone)
