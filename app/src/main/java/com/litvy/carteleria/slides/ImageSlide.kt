package com.litvy.carteleria.slides

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

class ImageSlide(
    @DrawableRes val imageRes: Int,
    val contentScale: ContentScale = ContentScale.Crop,
) : Slide {
    @Composable
    override fun Render(){
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale
        )
    }

}