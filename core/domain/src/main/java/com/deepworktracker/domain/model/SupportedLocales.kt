package com.deepworktracker.domain.model

object SupportedLocales {
    const val ENGLISH ="en"
    const val VIETNAMESE= "vi"

    val ALL: Set<String> = setOf(ENGLISH, VIETNAMESE)

    fun isSupported(tag: String): Boolean = tag in ALL

    fun normalize(tag: String): String =
        tag.lowercase().substringBefore('-').substringBefore('_')

}