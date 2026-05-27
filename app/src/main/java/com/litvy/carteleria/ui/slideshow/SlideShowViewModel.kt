package com.litvy.carteleria.ui.slideshow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litvy.carteleria.data.CartelConfig
import com.litvy.carteleria.data.CartelPreferences
import com.litvy.carteleria.data.ContentSource
import com.litvy.carteleria.domain.server.CartelServer
import com.litvy.carteleria.domain.usb.UsbImporter
import com.litvy.carteleria.slides.AppStorageSlideProvider
import com.litvy.carteleria.slides.ImageSlideDurations
import com.litvy.carteleria.util.usb.UsbScanResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import java.io.File

class SlideShowViewModel(
    private val externalProvider: AppStorageSlideProvider,
    private val prefs: CartelPreferences,
    private val server: CartelServer,
    private val usbImporter: UsbImporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(SlideShowUiState())
    val uiState = _uiState.asStateFlow()

    private val _serverUrl = MutableStateFlow("")
    val serverUrl = _serverUrl.asStateFlow()

    init {
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            prefs.preferencesFlow.collect { config ->
                val folder = when (val source = config.source) {
                    is ContentSource.External -> File(source.path)
                    is ContentSource.Internal -> File(source.folder)
                }
                val slides = if (folder.exists()) {
                    externalProvider.loadFromFolder(folder)
                } else {
                    emptyList()
                }

                _uiState.value = _uiState.value.copy(
                    selectedExternalFolder = folder.takeIf { it.exists() },
                    slides = slides
                )

                _uiState.value = _uiState.value.copy(
                    currentAnimation = config.animation,
                    globalImageDurationMs = config.globalImageDurationMs
                )
            }
        }
    }

    fun selectExternalFolder(folder: File) {
        val slides = externalProvider.loadFromFolder(folder)

        _uiState.value = _uiState.value.copy(
            selectedExternalFolder = folder,
            slides = slides,
            currentIndex = 0
        )

        saveConfig()
    }

    fun changeAnimation(animation: String) {
        _uiState.value = _uiState.value.copy(currentAnimation = animation)
        saveConfig()
    }

    fun changeGlobalImageDuration(durationMs: Long) {
        if (!ImageSlideDurations.isAllowed(durationMs)) return
        _uiState.value = _uiState.value.copy(globalImageDurationMs = durationMs)
        saveConfig()
    }

    fun setImageCustomDuration(path: String, durationMs: Long) {
        if (!ImageSlideDurations.isAllowed(durationMs)) return
        externalProvider.setImageDuration(File(path), durationMs)
        reloadExternalFolderPreservingCurrentIndex()
    }

    fun clearImageCustomDuration(path: String) {
        externalProvider.clearImageDuration(File(path))
        reloadExternalFolderPreservingCurrentIndex()
    }

    fun clearImageDurationsInFolder(path: String) {
        externalProvider.clearImageDurationsInFolder(File(path))
        reloadExternalFolderPreservingCurrentIndex()
    }

    fun clearAllImageDurations() {
        externalProvider.clearAllImageDurations()
        reloadExternalFolderPreservingCurrentIndex()
    }

    private fun saveConfig() {
        viewModelScope.launch {

            val state = _uiState.value

            prefs.saveConfig(
                CartelConfig(
                    source = com.litvy.carteleria.data.ContentSource.External(
                        state.selectedExternalFolder?.absolutePath ?: return@launch
                    ),
                    animation = state.currentAnimation,
                    globalImageDurationMs = state.globalImageDurationMs
                )
            )
        }
    }

    fun nextSlide() {
        val state = _uiState.value
        if (state.slides.isEmpty()) return

        _uiState.value = state.copy(
            currentIndex = (state.currentIndex + 1) % state.slides.size,
            showSlideIndicator = true
        )
    }

    fun previousSlide() {
        val state = _uiState.value
        if (state.slides.isEmpty()) return

        val newIndex =
            if (state.currentIndex - 1 < 0)
                state.slides.lastIndex
            else
                state.currentIndex - 1

        _uiState.value = state.copy(
            currentIndex = newIndex,
            showSlideIndicator = true
        )
    }

    fun togglePause() {
        _uiState.value = _uiState.value.copy(
            isPaused = !_uiState.value.isPaused
        )
    }

    fun toggleMenu() {
        _uiState.value = _uiState.value.copy(
            menuVisible = !_uiState.value.menuVisible
        )
    }

    fun hideSlideIndicator() {
        _uiState.value = _uiState.value.copy(showSlideIndicator = false)
    }

    fun autoNext() {
        val state = _uiState.value
        if (state.slides.isEmpty()) return

        _uiState.value = state.copy(
            currentIndex = (state.currentIndex + 1) % state.slides.size
        )
    }

    // --- MANEJO DE SERVIDOR LAN ---
    // - Se inicia durante el ciclo de vida de la pantalla.
    fun startServer() {
        try {
            server.start()
            _serverUrl.value = server.getUrl()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // - Se apaga durante al finalizar el ciclo de vida de la pantalla.
    fun stopServer() {
        server.stop()
    }

    // --- USB ---

        private fun clearUsbMessage() {
        _uiState.value = _uiState.value.copy(usbMessage = null)
    }

    fun openMenu() {
        _uiState.update { it.copy(menuVisible = true) }
    }

    fun closeMenu() {
        _uiState.update { it.copy(menuVisible = false) }
    }

    fun reloadExternalFolderIfSelected() {

        val currentFolder = _uiState.value.selectedExternalFolder ?: return

        val slides = externalProvider.loadFromFolder(currentFolder)

        _uiState.value = _uiState.value.copy(
            slides = slides,
            currentIndex = 0
        )
    }

    private fun reloadExternalFolderPreservingCurrentIndex() {
        val currentFolder = _uiState.value.selectedExternalFolder ?: return
        val state = _uiState.value
        val slides = externalProvider.loadFromFolder(currentFolder)

        _uiState.value = state.copy(
            slides = slides,
            currentIndex = state.currentIndex.coerceAtMost(slides.lastIndex.coerceAtLeast(0))
        )
    }

    suspend fun forceUsbScanAndReturnResult(): Boolean {

        _uiState.value = _uiState.value.copy(
            isUsbLoading = true,
            usbMessage = "🔍 Buscando USB..."
        )

        when (val result = usbImporter.forceScan()) {

            is UsbScanResult.Imported -> {
                _uiState.value = _uiState.value.copy(
                    usbMessage = "✅ Se importaron ${result.count} archivos",
                    isUsbLoading = false
                )
                delay(3000)
                clearUsbMessage()
                return true
            }

            UsbScanResult.NoChanges -> {
                _uiState.value = _uiState.value.copy(
                    usbMessage = "📁 No hay cambios para importar",
                    isUsbLoading = false
                )
                delay(3000)
                clearUsbMessage()
                return false
            }

            else -> {
                _uiState.value = _uiState.value.copy(
                    usbMessage = "⚠️ No se encontró USB",
                    isUsbLoading = false
                )
                delay(3000)
                clearUsbMessage()
                return false
            }
        }
    }

}
