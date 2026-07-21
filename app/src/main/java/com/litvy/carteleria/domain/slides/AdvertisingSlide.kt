package com.litvy.carteleria.domain.slides

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.litvy.carteleria.R
import kotlinx.coroutines.delay

object AdvertisingSlide : Slide {

    private const val TAG = "AdvertisingSlide"
    const val ADVERTISING_DURATION_MS = 5000L

    override val id: String = "litvy-advertising-slide"
    override val transitionKey: String? = null
    override val durationMs: Long = ADVERTISING_DURATION_MS
    override val customDurationMs: Long? = null

    @Composable
    override fun Render(
        isPaused: Boolean,
        onFinished: (() -> Unit)?
    ) {
        LaunchedEffect(Unit) {
            Log.d(TAG, "Advertising slide started")
            delay(ADVERTISING_DURATION_MS)
            onFinished?.invoke()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Image(
                painter = painterResource(id = R.drawable.advertising_background),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
