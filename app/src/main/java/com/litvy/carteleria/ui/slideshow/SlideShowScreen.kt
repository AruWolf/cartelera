package com.litvy.carteleria.ui.slideshow

import android.view.KeyEvent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreviewKeyEvent
import com.litvy.carteleria.animations.TvTransitions
import com.litvy.carteleria.domain.propaganda.Propaganda1
import com.litvy.carteleria.engine.EvokeSlide
import com.litvy.carteleria.ui.menu.MenuAction
import com.litvy.carteleria.ui.menu.SideMenu
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester


@Composable
fun SlideShowScreen() {

    //Estado del menu - cerrado por defecto
    var menuVisible by remember { mutableStateOf(false) }
    var currentAnimation by remember {mutableStateOf("fade")}

    val rootFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        rootFocusRequester.requestFocus()
    }

    val playlist = remember { Propaganda1().slides()}

    val engine = remember(currentAnimation) {
        EvokeSlide(
            slides = playlist,
            transitions = mapOf(
                "fade" to TvTransitions.fade(ms = 700),
                "scale" to TvTransitions.scale(ms = 700),
                "left" to TvTransitions.slideLeft(ms = 700),
                "up" to TvTransitions.slideUp(ms = 700),
            ),
            defaultTransition = TvTransitions.fade(ms = 700)
            )
    }

    // Contenedor Raiz(Slides + menu) + Manejo con control remoto
    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(rootFocusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.nativeKeyEvent.action != KeyEvent.ACTION_UP) {
                    return@onPreviewKeyEvent false
                }

                when (event.nativeKeyEvent.keyCode) {

                    // MENU físico (si existe)
                    KeyEvent.KEYCODE_MENU -> {
                        menuVisible = !menuVisible
                        true
                    }

                    // Flecha izquierda
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        menuVisible = !menuVisible
                        true
                    }

                    // BACK cierra menú
                    KeyEvent.KEYCODE_BACK -> {
                        if (menuVisible) {
                            menuVisible = false
                            true
                        } else {
                            false
                        }
                    }

                    else -> false
                }

            }
    ){
        engine.Render(modifier = Modifier.fillMaxSize())
        if (menuVisible) {
            SideMenu(
                onAction = { action ->
                    when (action) {
                        MenuAction.ChangeAnimation -> {
                            currentAnimation = when (currentAnimation) {
                                "fade" -> "scale"
                                "scale" -> "left"
                                "left" -> "up"
                                else -> "fade"
                            }
                        }

                        MenuAction.ChangeSpeed -> {
                            // lo vemos después
                        }

                        MenuAction.Restart -> {
                            // lo vemos después
                        }
                    }
                },
                onClose = { menuVisible = false }
            )
        }
    }

}