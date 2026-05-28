package com.litvy.carteleria.ui.slideshow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShortcutOverlayManager(
    private val scope: CoroutineScope,
    private val onTextChanged: (String?) -> Unit
) {
    private var overlayJob: Job? = null

    fun show(text: String) {
        overlayJob?.cancel()
        onTextChanged(text)
        overlayJob = scope.launch {
            delay(OVERLAY_DURATION_MS)
            onTextChanged(null)
        }
    }

    fun clear() {
        overlayJob?.cancel()
        onTextChanged(null)
    }

    private companion object {
        const val OVERLAY_DURATION_MS = 3000L
    }
}
