package com.litvy.carteleria.slides

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.litvy.carteleria.R

class ChargersSlide(
    override val durationMs: Long = 2500L,
    override val transitionKey: String? = "fade"
) : Slide {

    override val id: String = "chargers"

    @Composable
    override fun Render() {
        Image(
            painter = painterResource(id = R.drawable.charger),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
