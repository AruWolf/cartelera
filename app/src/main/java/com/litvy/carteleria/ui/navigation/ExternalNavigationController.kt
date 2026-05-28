package com.litvy.carteleria.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ExternalNavigationController {

    var state by mutableStateOf(ExternalNavigationState())
        private set

    // -------- FOLDERS --------

    fun moveFolderUp() {
        state = state.copy(
            folderIndex = (state.folderIndex - 1).coerceAtLeast(0)
        )
    }

    fun moveFolderDown(maxIndex: Int) {
        state = state.copy(
            folderIndex = (state.folderIndex + 1)
                .coerceAtMost(maxIndex)
        )
    }

    // -------- FILES --------

    fun moveFileUp() {
        state = state.copy(
            fileIndex = (state.fileIndex - 1).coerceAtLeast(0)
        )
    }

    fun moveFileDown(maxIndex: Int) {
        state = state.copy(
            fileIndex = (state.fileIndex + 1)
                .coerceAtMost(maxIndex)
        )
    }

    fun resetFileIndex() {
        state = state.copy(fileIndex = 0)
    }

    fun reset() {
        state = ExternalNavigationState()
    }
}