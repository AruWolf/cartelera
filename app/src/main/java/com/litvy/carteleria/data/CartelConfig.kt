package com.litvy.carteleria.data

data class CartelConfig(
    val source: ContentSource,
    val animation: String,
    val globalImageDurationMs: Long
)

sealed class ContentSource {
    data class Internal(val folder: String) : ContentSource()
    data class External(val path: String) : ContentSource()
}
