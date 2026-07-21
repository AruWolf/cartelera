package com.litvy.carteleria

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import com.litvy.carteleria.ui.loading.CarteleriaAppRoot
import com.litvy.carteleria.ui.touchremote.TouchDeviceDetector
import com.litvy.carteleria.ui.touchremote.TouchRemoteOverlayController
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.litvy.carteleria.ui.startup.StartupScreen
import com.litvy.carteleria.services.update.InAppUpdateManager


class MainActivity : ComponentActivity() {
    private var touchRemoteOverlayController: TouchRemoteOverlayController? = null
    private lateinit var inAppUpdateManager: InAppUpdateManager

    private val updateLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->

            if (result.resultCode != RESULT_OK) {
                Log.w(
                    "InAppUpdate",
                    "Actualización cancelada o fallida."
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (TouchDeviceDetector.shouldShowTouchRemote(this)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())

            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        inAppUpdateManager =
            InAppUpdateManager(
                activity = this,
                launcher = updateLauncher
            )

        inAppUpdateManager.registerListener()

        setContent {

            StartupScreen {

                CarteleriaAppRoot(
                    onShowTouchRemote = {
                        touchRemoteOverlayController?.show()
                    }
                )

            }

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
        inAppUpdateManager.unregisterListener()

        touchRemoteOverlayController?.detach()
        touchRemoteOverlayController = null
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            WindowInsetsControllerCompat(window, window.decorView).apply {
                hide(WindowInsetsCompat.Type.systemBars())

                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    override fun onStart() {
        super.onStart()

        inAppUpdateManager.checkForUpdates()
    }

    override fun onStop(){
        super.onStop()

    }
}