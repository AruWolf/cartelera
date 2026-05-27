package com.litvy.carteleria.ui.touchremote

import android.app.Activity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.litvy.carteleria.R

class TouchRemoteOverlayController(
    private val activity: Activity,
    private val dispatchKeyEvent: (Int) -> Unit = { keyCode -> RemoteKeyEventBus.send(keyCode) }
) {

    private var overlayView: View? = null
    private var tripleTapDetector: TripleTapDetector? = null

    fun attach() {
        if (!TouchDeviceDetector.shouldShowTouchRemote(activity)) {
            return
        }

        val contentRoot = activity.findViewById<FrameLayout>(android.R.id.content)
        val overlay = LayoutInflater.from(activity)
            .inflate(R.layout.view_touch_remote_overlay, contentRoot, false)

        overlay.findViewById<View>(R.id.touchRemoteUp)
            .setOnClickListener { dispatchKeyEvent(KeyEvent.KEYCODE_DPAD_UP) }
        overlay.findViewById<View>(R.id.touchRemoteDown)
            .setOnClickListener { dispatchKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN) }
        overlay.findViewById<View>(R.id.touchRemoteLeft)
            .setOnClickListener { dispatchKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT) }
        overlay.findViewById<View>(R.id.touchRemoteRight)
            .setOnClickListener { dispatchKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT) }
        overlay.findViewById<View>(R.id.touchRemoteOk)
            .setOnClickListener { dispatchKeyEvent(KeyEvent.KEYCODE_DPAD_CENTER) }
        overlay.findViewById<View>(R.id.touchRemoteClose)
            .setOnClickListener { hide() }

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.BOTTOM or android.view.Gravity.END
            marginEnd = activity.resources.getDimensionPixelSize(R.dimen.touch_remote_margin_end)
            bottomMargin = activity.resources.getDimensionPixelSize(R.dimen.touch_remote_margin_bottom)
        }

        contentRoot.addView(overlay, params)
        overlayView = overlay
        tripleTapDetector = TripleTapDetector { show() }
    }

    fun onTouchEvent(event: MotionEvent) {
        tripleTapDetector?.onTouchEvent(event)
    }

    fun show() {
        overlayView?.let {
            it.visibility = View.VISIBLE
            it.bringToFront()
        }
    }

    fun hide() {
        overlayView?.visibility = View.GONE
    }

    fun detach() {
        val contentRoot = activity.findViewById<FrameLayout>(android.R.id.content)
        overlayView?.let { contentRoot.removeView(it) }
        overlayView = null
        tripleTapDetector = null
    }
}
