package com.litvy.carteleria.ui.menu.external

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.litvy.carteleria.domain.external.usecase.ExternalContentUseCases

class ExternalMenuViewModel(
    private val useCases: ExternalContentUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(ExternalMenuState())
    val state = _state.asStateFlow()

    init {
        loadFolders()
    }

    fun loadFolders() {
        _state.value = _state.value.copy(
            folders = useCases.listFolders(),
            isInFolder = false,
            currentFolderPath = null
        )
    }

    fun openFolder(path: String) {
        _state.value = _state.value.copy(
            files = useCases.listFiles(path),
            currentFolderPath = path,
            isInFolder = true
        )
    }

    fun goBack() {
        if (_state.value.isInFolder) {
            loadFolders()
        }
    }

    fun deleteFile(path: String) {
        useCases.deleteFile(path)
        refreshFolder()
    }

    fun deleteFolder(path: String) {
        useCases.deleteFolder(path)
        loadFolders()
    }

    fun copyFile(path: String) {
        _state.value = _state.value.copy(
            clipboardPath = path,
            isCut = false
        )
    }

    fun cutFile(path: String) {
        _state.value = _state.value.copy(
            clipboardPath = path,
            isCut = true
        )
    }

    fun paste() {
        val clip = _state.value.clipboardPath ?: return
        val target = _state.value.currentFolderPath ?: return

        if (_state.value.isCut) {
            useCases.moveFile(clip, target)
        } else {
            useCases.copyFile(clip, target)
        }

        _state.value = _state.value.copy(
            clipboardPath = null,
            isCut = false
        )

        refreshFolder()
    }

    private fun refreshFolder() {
        _state.value.currentFolderPath?.let {
            openFolder(it)
        }
    }

    fun hideFile(path: String) {
        useCases.hideFile(path)
        refreshFolder()
    }

    fun showFile(path: String) {
        useCases.showFile(path)
        refreshFolder()
    }

    fun reloadCurrentView() {
        if (_state.value.isInFolder) {
            refreshFolder()
        } else {
            loadFolders()
        }
    }

}