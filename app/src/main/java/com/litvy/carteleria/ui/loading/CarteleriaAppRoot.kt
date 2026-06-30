package com.litvy.carteleria.ui.loading

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.litvy.carteleria.ui.slideshow.SlideShowScreen
import kotlinx.coroutines.delay

@Composable
fun CarteleriaAppRoot(
    onShowTouchRemote: () -> Unit = {}
) {
    var showStartupLoading by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(LoadingUiDefaults.STARTUP_VISIBLE_MS)
        showStartupLoading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SlideShowScreen(
            onShowTouchRemote = onShowTouchRemote
        )

        AnimatedVisibility(
            visible = showStartupLoading,
            exit = fadeOut(tween(durationMillis = LoadingUiDefaults.STARTUP_FADE_OUT_MS))
        ) {
            AppLoadingSurface(
                backgroundName = AppVisualAssets.STARTUP_LOADING_BACKGROUND
            )
        }
    }
}
