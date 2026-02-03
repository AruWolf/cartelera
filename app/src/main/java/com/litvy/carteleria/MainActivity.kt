package com.litvy.carteleria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.litvy.carteleria.domain.propaganda.Propaganda1
import com.litvy.carteleria.slides.slideChargers
import com.litvy.carteleria.slides.slideHeadphones
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SlideShow()
        }
    }
}


@Composable
fun SlideShow() {
    val generator = remember {
        GenerateSlide(
            propaganda = Propaganda1(),
            TimeLength = 2500
        )
    }

    var slides = remember { generator.generate() }

    var currentSlide by remember { mutableStateOf(0) }

    // Config
    val slideDurationMs = 2500L      // cuánto dura cada slide en pantalla
    val transitionMs = 2500           // cuánto dura el fade

    LaunchedEffect(slides.size) {
        while (true) {
            delay(slideDurationMs)
            currentSlide = (currentSlide + 1) % slides.size
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Crossfade(
            targetState = currentSlide,
            animationSpec = tween(durationMillis = transitionMs),
            label = "slideshow"
        ) { index ->
            slides[index]()
        }
    }
}