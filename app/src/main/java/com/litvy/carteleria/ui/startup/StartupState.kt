package com.litvy.carteleria.ui.startup

sealed interface StartupState {
    data object Loading: StartupState
    data object Ready: StartupState
    data class Error(
        val stage: StartupStage,
        val throwable: Throwable
    ) : StartupState
}