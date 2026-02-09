package com.litvy.carteleria.slides

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

class AssetImageSlide(
    override val id: String,
    private val assetPath: String,
    override val durationMs: Long,
    override val transitionKey: String?
): Slide {

    @Composable
    override fun Render(){
        val context = LocalContext.current



        val bitmap = remember(assetPath) {
            runCatching {
                context.assets.open(assetPath).use {
                    BitmapFactory.decodeStream(it)
                }
            }.getOrNull()
        }

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }



    }
}