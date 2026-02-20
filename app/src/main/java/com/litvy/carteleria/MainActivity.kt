package com.litvy.carteleria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.litvy.carteleria.ui.slideshow.SlideShowScreen


// TODO: Deseleccionar images
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SlideShowScreen()
            //SlideShow()
        }
    }
}
/*
@Composable
fun SlideShow() {
    val playlist = Propaganda1().slides()

    val engine = EvokeSlide(
        slides = playlist,
        transitions = mapOf(
            "fade" to TvTransitions.fade(ms = 700),
            "scale" to TvTransitions.scale(ms = 700),
            "left" to TvTransitions.slideLeft(ms = 700),
            "up" to TvTransitions.slideUp(ms = 700),
        ),
        defaultTransition = TvTransitions.fade(ms = 700),
        transitionMs = 700
    )

    engine.Render()
}*/
