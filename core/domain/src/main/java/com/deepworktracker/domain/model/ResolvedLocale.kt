package com.deepworktracker.domain.model

enum class TextDirection{
    LTR,
    RTL
}

data class ResolvedLocale (
    val localeTag: String,
    val textDirection: TextDirection,
    val followsSystem: Boolean
){
    val isRtl: Boolean get() = textDirection == TextDirection.RTL
}