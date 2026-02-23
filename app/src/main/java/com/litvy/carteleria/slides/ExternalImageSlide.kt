package com.litvy.carteleria.slides

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.io.File

class ExternalImageSlide(
    override val id: String,
    val file: File,
    override val durationMs: Long,
    override val transitionKey: String?
) : Slide {

    @Composable
    override fun Render(
        isPaused: Boolean,
        onFinished: (() -> Unit)?
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AsyncImage(
                model = file,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}