package com.deepworktracker.domain.model

data class CategoryRule(
    val id: String,
    val keyword: String?,
    val startHour: Int?, // 0..23
    val endHour: Int?, // 0..23
    val category: String,
    val tag: String?,
    val priority: Int = 0,
)

