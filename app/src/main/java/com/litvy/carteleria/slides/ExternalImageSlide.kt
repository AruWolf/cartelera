package com.litvy.carteleria.slides

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import java.io.File
import android.graphics.BitmapFactory
import androidx.compose.runtime.remember

class ExternalImageSlide(
    override val id: String,
    val file: File,
    override val durationMs: Long,
    override val transitionKey: String?
) : Slide {

    @Composable
    override fun Render() {
        val bitmap = remember(file) {
            BitmapFactory.decodeFile(file.absolutePath)
        }

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}