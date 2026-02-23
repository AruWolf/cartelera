package com.litvy.carteleria.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.File

class ExternalNavigationController {

    var state by mutableStateOf(ExternalNavigationState())
        private set

    fun moveUp(maxIndex: Int) {

        state = when (state.level) {

            0 -> state.copy(
                folderIndex = (state.folderIndex - 1)
                    .coerceAtLeast(0)
            )

            1 -> state.copy(
                fileIndex = (state.fileIndex - 1)
                    .coerceAtLeast(0)
            )

            2 -> state.copy(
                contextIndex = (state.contextIndex - 1)
                    .coerceAtLeast(0)
            )

            else -> state
        }
    }

    fun moveDown(maxIndex: Int) {

        state = when (state.level) {

            0 -> state.copy(
                folderIndex = (state.folderIndex + 1)
                    .coerceAtMost(maxIndex)
            )

            1 -> state.copy(
                fileIndex = (state.fileIndex + 1)
                    .coerceAtMost(maxIndex)
            )

            2 -> state.copy(
                contextIndex = (state.contextIndex + 1)
                    .coerceAtMost(maxIndex)
            )

            else -> state
        }
    }

    fun openContextMenu(target: ContextTarget) {

        state = state.copy(
            previousLevel = state.level,
            level = 2,
            contextIndex = 0,
            contextTarget = target
        )
    }

    fun enterFiles(folder: File) {

        state = state.copy(
            level = 1,
            exploredFolder = folder,
            fileIndex = 0,
            contextIndex = 0,
            contextTarget = null
        )
    }

    fun back(): Boolean {

        state = when (state.level) {

            2 -> state.copy(
                level = state.previousLevel,
                contextTarget = null
            )

            1 -> state.copy(
                level = 0,
                exploredFolder = null,
                fileIndex = 0
            )

            else -> return false
        }

        return true
    }

    fun reset() {
        state = ExternalNavigationState()
    }
}