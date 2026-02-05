package com.litvy.carteleria.ui.slideshow

import android.view.KeyEvent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onPreviewKeyEvent
import com.litvy.carteleria.animations.TvTransitions
import com.litvy.carteleria.domain.propaganda.Propaganda1
import com.litvy.carteleria.engine.EvokeSlide
import com.litvy.carteleria.slides.Slide
import com.litvy.carteleria.ui.menu.SideMenu

@Composable
fun SlideShowScreen() {

    var menuVisible by remember { mutableStateOf(false) }
    var selectedAnimation by remember { mutableStateOf("fade") }

    val transitions = remember {
        mapOf(
            "fade" to TvTransitions.fade<Slide>(),
            "scale" to TvTransitions.scale<Slide>(),
            "left" to TvTransitions.slideLeft<Slide>(),
            "up" to TvTransitions.slideUp<Slide>()
        )
    }

    val slides = remember { Propaganda1().slides() }

    val engine = remember(selectedAnimation) {
        EvokeSlide(
            slides = slides,
            transition = transitions[selectedAnimation]!!
        )
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.nativeKeyEvent.action != KeyEvent.ACTION_UP) return@onPreviewKeyEvent false

                when (event.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_MENU,
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        menuVisible = !menuVisible
                        true
                    }

                    KeyEvent.KEYCODE_BACK -> {
                        if (menuVisible) {
                            menuVisible = false
                            true
                        } else false
                    }

                    else -> false
                }
            }
    ) {
        engine.Render(Modifier.fillMaxSize())

        if (menuVisible) {
            SideMenu(
                currentAnimation = selectedAnimation,
                onAnimationSelected = {
                    selectedAnimation = it
                    menuVisible = false
                },
                onClose = { menuVisible = false }
            )
        }
    }
}
