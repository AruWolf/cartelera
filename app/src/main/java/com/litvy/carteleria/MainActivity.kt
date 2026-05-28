package com.litvy.carteleria

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.litvy.carteleria.ui.loading.CarteleriaAppRoot
import com.litvy.carteleria.ui.touchremote.TouchDeviceDetector
import com.litvy.carteleria.ui.touchremote.TouchRemoteOverlayController


// TODO: Deseleccionar images
class MainActivity : ComponentActivity() {
    private var touchRemoteOverlayController: TouchRemoteOverlayController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (TouchDeviceDetector.shouldShowTouchRemote(this)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }

        setContent {
            CarteleriaAppRoot()
        }

        touchRemoteOverlayController = TouchRemoteOverlayController(this).also {
            it.attach()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        touchRemoteOverlayController?.onTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }

    override fun onDestroy() {
        touchRemoteOverlayController?.detach()
        touchRemoteOverlayController = null
        super.onDestroy()
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
