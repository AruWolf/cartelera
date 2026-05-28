package com.litvy.carteleria.ui.touchremote

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object RemoteKeyEventBus {
    private val _keyEvents = MutableSharedFlow<Int>(
        extraBufferCapacity = 8
    )

    val keyEvents = _keyEvents.asSharedFlow()

    fun send(keyCode: Int) {
        _keyEvents.tryEmit(keyCode)
    }
}
