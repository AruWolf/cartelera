package com.litvy.carteleria.ui.slideshow.input

import android.view.KeyEvent
import com.litvy.carteleria.ui.slideshow.RemoteInputHandler
import com.litvy.carteleria.ui.slideshow.exit.ExitHandler

object SlideInputDefaults {
    const val SWIPE_THRESHOLD_PX = 120f
}

class SlideInputHandler(
    private val actions: SlideShowActions,
    private val isAdvertisingShowing: () -> Boolean,
    private val isMenuVisible: () -> Boolean,
    private val isPlaybackPaused: () -> Boolean,
    private val onTemporaryPauseChanged: (Boolean) -> Unit,
    private val exitHandler: ExitHandler,
    private val isMobile: Boolean
) {
    private var wasPlayingBeforePress = false
    private var ignoreNextCenter = false

    fun onKeyEvent(event: KeyEvent): Boolean {
        if (isAdvertisingShowing()) return event.keyCode in ADVERTISING_CONSUMED_KEYS
        if (isMenuVisible()) return false
        if (ignoreNextCenter) {
            ignoreNextCenter = false
            return true
        }
        if (event.action != KeyEvent.ACTION_UP) return false

        return onKeyCode(event.keyCode)
    }

    fun onRemoteKeyEvent(keyCode: Int): Boolean {
        if (isAdvertisingShowing()) return keyCode in ADVERTISING_CONSUMED_KEYS
        if (isMenuVisible()) return false
        if (ignoreNextCenter) {
            ignoreNextCenter = false
            return true
        }

        return onKeyCode(keyCode)
    }

    private fun onKeyCode(keyCode: Int): Boolean = when (keyCode) {
        KeyEvent.KEYCODE_0,
        KeyEvent.KEYCODE_1,
        KeyEvent.KEYCODE_2,
        KeyEvent.KEYCODE_3,
        KeyEvent.KEYCODE_4,
        KeyEvent.KEYCODE_5,
        KeyEvent.KEYCODE_6,
        KeyEvent.KEYCODE_7,
        KeyEvent.KEYCODE_8,
        KeyEvent.KEYCODE_9 -> {
            RemoteInputHandler.numberFromKeyCode(keyCode)?.let(actions::playShortcut) ?: false
        }

        KeyEvent.KEYCODE_DPAD_RIGHT -> {
            actions.nextSlide()
            true
        }

        KeyEvent.KEYCODE_DPAD_LEFT -> {
            actions.previousSlide()
            true
        }

        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
        KeyEvent.KEYCODE_DPAD_UP -> {
            actions.togglePlayback()
            true
        }

        KeyEvent.KEYCODE_DPAD_CENTER -> {
            actions.openMenu()
            true
        }

        KeyEvent.KEYCODE_BACK -> exitHandler.handleBack()
        else -> false
    }

    fun onTap() {
        if (canHandlePlaybackInput()) actions.togglePlayback()
    }

    fun onSwipe(horizontalDistance: Float) {
        if (!canHandlePlaybackInput() || kotlin.math.abs(horizontalDistance) < SlideInputDefaults.SWIPE_THRESHOLD_PX) {
            return
        }

        if (horizontalDistance < 0) actions.nextSlide() else actions.previousSlide()
    }

    fun onPress() {
        wasPlayingBeforePress = canHandlePlaybackInput() && !isPlaybackPaused()
        if (wasPlayingBeforePress) {
            onTemporaryPauseChanged(true)
            actions.pausePlayback()
        }
    }

    fun onRelease() {
        if (wasPlayingBeforePress) {
            actions.resumePlayback()
            onTemporaryPauseChanged(false)
        }
        wasPlayingBeforePress = false
    }

    fun openMenu() {
        if (!isAdvertisingShowing()) actions.openMenu()
    }

    fun closeMenu() {
        if (isMobile){
            ignoreNextCenter = false
        }
        ignoreNextCenter = true
        actions.closeMenu()
    }

    private fun canHandlePlaybackInput(): Boolean = !isAdvertisingShowing() && !isMenuVisible()

    private companion object {
        val ADVERTISING_CONSUMED_KEYS = setOf(
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_MEDIA_NEXT,
            KeyEvent.KEYCODE_MEDIA_PREVIOUS,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_0,
            KeyEvent.KEYCODE_1,
            KeyEvent.KEYCODE_2,
            KeyEvent.KEYCODE_3,
            KeyEvent.KEYCODE_4,
            KeyEvent.KEYCODE_5,
            KeyEvent.KEYCODE_6,
            KeyEvent.KEYCODE_7,
            KeyEvent.KEYCODE_8,
            KeyEvent.KEYCODE_9
        )
    }
}
