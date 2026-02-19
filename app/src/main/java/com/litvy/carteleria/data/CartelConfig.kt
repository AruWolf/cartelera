package com.litvy.carteleria.data

import com.litvy.carteleria.slides.SlideSpeed

data class CartelConfig(
    val source: ContentSource,
    val animation: String,
    val speed: SlideSpeed
)

sealed class ContentSource {
    data class Internal(val folder: String) : ContentSource()
    data class External(val path: String) : ContentSource()
}
