package com.litvy.carteleria.slides

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

class AssetImageSlide(
    override val id: String,
    private val assetPath: String,
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
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/$assetPath")
                    .size(1920, 1080)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}