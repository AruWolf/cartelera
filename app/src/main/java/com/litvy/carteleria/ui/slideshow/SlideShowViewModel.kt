package com.litvy.carteleria.ui.slideshow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litvy.carteleria.data.CartelConfig
import com.litvy.carteleria.data.CartelPreferences
import com.litvy.carteleria.data.ContentSource
import com.litvy.carteleria.domain.server.CartelServer
import com.litvy.carteleria.domain.usb.UsbImporter
import com.litvy.carteleria.slides.AssetSlideProvider
import com.litvy.carteleria.slides.AppStorageSlideProvider
import com.litvy.carteleria.slides.SlideSpeed
import com.litvy.carteleria.util.usb.UsbScanResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.litvy.carteleria.ui.menu.model.ClipboardItem
import kotlinx.coroutines.flow.update
import java.io.File

class SlideShowViewModel(
    private val assetProvider: AssetSlideProvider,
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

                when (config.source) {

                    is ContentSource.Internal -> {
                        val slides = assetProvider.loadFrom(config.source.folder)
                        _uiState.value = _uiState.value.copy(
                            contentMode = ContentMode.INTERNAL,
                            selectedInternalFolder = config.source.folder,
                            selectedExternalFolder = null,
                            slides = slides
                        )
                    }

                    is ContentSource.External -> {
                        val folder = File(config.source.path)
                        if (folder.exists()) {
                            val slides = externalProvider.loadFromFolder(folder)
                            _uiState.value = _uiState.value.copy(
                                contentMode = ContentMode.EXTERNAL,
                                selectedExternalFolder = folder,
                                selectedInternalFolder = null,
                                slides = slides
                            )
                        }
                    }
                }

                _uiState.value = _uiState.value.copy(
                    currentAnimation = config.animation,
                    slideSpeed = config.speed
                )
            }
        }
    }

    fun selectInternalFolder(folder: String) {
        val slides = assetProvider.loadFrom(folder)

        _uiState.value = _uiState.value.copy(
            contentMode = ContentMode.INTERNAL,
            selectedInternalFolder = folder,
            selectedExternalFolder = null,
            slides = slides,
            currentIndex = 0
        )

        saveConfig()
    }

    fun selectExternalFolder(folder: File) {
        val slides = externalProvider.loadFromFolder(folder)

        _uiState.value = _uiState.value.copy(
            contentMode = ContentMode.EXTERNAL,
            selectedExternalFolder = folder,
            selectedInternalFolder = null,
            slides = slides,
            currentIndex = 0
        )

        saveConfig()
    }

    fun changeAnimation(animation: String) {
        _uiState.value = _uiState.value.copy(currentAnimation = animation)
        saveConfig()
    }

    fun changeSpeed(speed: SlideSpeed) {
        _uiState.value = _uiState.value.copy(slideSpeed = speed)
        saveConfig()
    }

    private fun saveConfig() {
        viewModelScope.launch {

            val state = _uiState.value

            val source = when (state.contentMode) {
                ContentMode.INTERNAL ->
                    ContentSource.Internal(state.selectedInternalFolder ?: return@launch)

                ContentMode.EXTERNAL ->
                    ContentSource.External(state.selectedExternalFolder?.absolutePath ?: return@launch)
            }

            prefs.saveConfig(
                CartelConfig(
                    source = source,
                    animation = state.currentAnimation,
                    speed = state.slideSpeed
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

    fun forceUsbScan() {
        viewModelScope.launch {

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
                    return@launch
                }

                UsbScanResult.NoChanges -> {
                    _uiState.value = _uiState.value.copy(
                        usbMessage = "📁 No hay cambios para importar",
                        isUsbLoading = false
                    )
                    delay(3000)
                    clearUsbMessage()
                    return@launch
                }

                else -> { }
            }

            _uiState.value = _uiState.value.copy(
                usbMessage = "🔍 Escaneando vía sistema..."
            )

            when (val mediaResult = usbImporter.scanViaMediaStore()) {

                is UsbScanResult.Imported -> {
                    _uiState.value = _uiState.value.copy(
                        usbMessage = "✅ Se importaron ${mediaResult.count} archivos",
                        isUsbLoading = false
                    )
                    delay(3000)
                    clearUsbMessage()
                    return@launch
                }

                UsbScanResult.NoChanges -> {
                    _uiState.value = _uiState.value.copy(
                        usbMessage = "📁 No hay cambios para importar",
                        isUsbLoading = false
                    )
                    delay(3000)
                    clearUsbMessage()
                    return@launch
                }

                else -> {
                    _uiState.value = _uiState.value.copy(
                        usbMessage = "⚠️ Este dispositivo no permite acceso directo al USB.\nUtilice la carga por red (QR).",
                        isUsbLoading = false
                    )
                    delay(5000)
                    clearUsbMessage()
                }
            }
        }
    }

    private fun clearUsbMessage() {
        _uiState.value = _uiState.value.copy(usbMessage = null)
    }

    fun openMenu() {
        _uiState.update { it.copy(menuVisible = true) }
    }

    fun closeMenu() {
        _uiState.update { it.copy(menuVisible = false) }
    }

}