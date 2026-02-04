package com.litvy.carteleria.slides

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.litvy.carteleria.R

class HeadphonesSlide(
    private val priceText: String = "Auriculares: $25.000",
    override val durationMs: Long = 2500L,
    override val transitionKey: String? = "scale"
) : Slide {

    override val id: String = "headphones"

    @Composable
    override fun Render() {
        Box(modifier = Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(R.drawable.hp),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            Text(
                text = priceText,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
