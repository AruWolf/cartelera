package com.litvy.carteleria.ui.touchremote

import android.view.MotionEvent

class TripleTapDetector(
    private val tapWindowMs: Long = DEFAULT_TAP_WINDOW_MS,
    private val requiredTaps: Int = REQUIRED_TAPS,
    private val onTripleTap: () -> Unit
) {

    private var firstTapTimeMs: Long = 0L
    private var tapCount: Int = 0

    fun onTouchEvent(event: MotionEvent) {
        if (event.action != MotionEvent.ACTION_UP) {
            return
        }

        val now = event.eventTime

        if (firstTapTimeMs == 0L || now - firstTapTimeMs > tapWindowMs) {
            firstTapTimeMs = now
            tapCount = 1
            return
        }

        tapCount++

        if (tapCount >= requiredTaps) {
            tapCount = 0
            firstTapTimeMs = 0L
            onTripleTap()
        }
    }

    companion object {
        private const val DEFAULT_TAP_WINDOW_MS = 600L
        private const val REQUIRED_TAPS = 3
    }
}
