package com.litvy.carteleria.ui.slideshow

import android.content.Context
import android.net.Uri
import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litvy.carteleria.R
import com.litvy.carteleria.data.CartelConfig
import com.litvy.carteleria.data.CartelPreferences
import com.litvy.carteleria.data.ContentSource
import com.litvy.carteleria.domain.importing.AndroidMediaImporter
import com.litvy.carteleria.domain.server.CartelServer
import com.litvy.carteleria.domain.usb.UsbImporter
import com.litvy.carteleria.slides.AppStorageSlideProvider
import com.litvy.carteleria.slides.ImageSlideDurations
import com.litvy.carteleria.util.storage.ContentDirectoryObserver
import com.litvy.carteleria.util.usb.UsbScanResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import java.io.File

class SlideShowViewModel(
    private val externalProvider: AppStorageSlideProvider,
    private val prefs: CartelPreferences,
    private val server: CartelServer,
    private val usbImporter: UsbImporter,
    private val context: Context
) : ViewModel() {

    private companion object {
        const val TAG = "SlideShowViewModel"
        const val SLIDES_BETWEEN_ADVERTISING = 20
    }

    private val _uiState = MutableStateFlow(SlideShowUiState())
    val uiState = _uiState.asStateFlow()

    private val _serverUrl = MutableStateFlow("")
    val serverUrl = _serverUrl.asStateFlow()

    private val mediaImporter = AndroidMediaImporter(context)
    private var contentRefreshJob: Job? = null
    private val contentObserver = ContentDirectoryObserver(
        root = com.litvy.carteleria.content.ContentStorage.ensureRootDirectory(context),
        onChanged = { scheduleContentRefresh() }
    )

    init {
        observePreferences()
        contentObserver.start()
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
                    slides = slides,
                    currentIndex = _uiState.value.currentIndex.coerceAtMost(slides.lastIndex.coerceAtLeast(0)),
                    contentRevision = _uiState.value.contentRevision + 1
                )

                _uiState.value = _uiState.value.copy(
                    currentAnimation = config.animation,
                    globalImageDurationMs = config.globalImageDurationMs
                )
            }
        }
    }

    fun selectExternalFolder(folder: File) {
        if (_uiState.value.isAdvertisingShowing) return

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
        if (_uiState.value.isAdvertisingShowing) return
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
        if (state.slides.isEmpty() || state.isAdvertisingShowing) return

        _uiState.value = state.copy(
            currentIndex = (state.currentIndex + 1) % state.slides.size,
            showSlideIndicator = true
        )
    }

    fun previousSlide() {
        val state = _uiState.value
        if (state.slides.isEmpty() || state.isAdvertisingShowing) return

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
        if (_uiState.value.isAdvertisingShowing) return

        _uiState.value = _uiState.value.copy(
            isPaused = !_uiState.value.isPaused
        )
    }

    fun toggleMenu() {
        if (_uiState.value.isAdvertisingShowing) return

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

        if (state.isAdvertisingShowing) {
            Log.d(TAG, "Advertising slide finished")
            Log.d(TAG, "Advertising counter reset")
            _uiState.value = state.copy(
                isAdvertisingShowing = false,
                currentIndex = (state.currentIndex + 1) % state.slides.size,
                slidesShownSinceLastAdvertising = 0
            )
            return
        }

        val completedCount = state.slidesShownSinceLastAdvertising + 1
        if (completedCount >= SLIDES_BETWEEN_ADVERTISING) {
            Log.d(TAG, "Advertising slide scheduled")
            _uiState.value = state.copy(
                slidesShownSinceLastAdvertising = completedCount,
                isAdvertisingShowing = true,
                menuVisible = false,
                showSlideIndicator = false
            )
            return
        }

        _uiState.value = state.copy(
            currentIndex = (state.currentIndex + 1) % state.slides.size,
            slidesShownSinceLastAdvertising = completedCount
        )
    }

    // --- MANEJO DE SERVIDOR LAN ---
    // Deshabilitado temporalmente: el codigo queda disponible para futuras versiones.
    fun startServer() {
        _serverUrl.value = ""
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
        if (_uiState.value.isAdvertisingShowing) return
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
            usbMessage = context.getString(R.string.usb_searching)
        )

        when (val result = usbImporter.forceScan()) {

            is UsbScanResult.Imported -> {
                _uiState.value = _uiState.value.copy(
                    usbMessage = context.resources.getQuantityString(R.plurals.usb_files_imported, result.count, result.count),
                    isUsbLoading = false
                )
                delay(3000)
                clearUsbMessage()
                return true
            }

            UsbScanResult.NoChanges -> {
                _uiState.value = _uiState.value.copy(
                    usbMessage = context.getString(R.string.usb_no_changes),
                    isUsbLoading = false
                )
                delay(3000)
                clearUsbMessage()
                return false
            }

            else -> {
                _uiState.value = _uiState.value.copy(
                    usbMessage = context.getString(R.string.usb_not_found),
                    isUsbLoading = false
                )
                delay(3000)
                clearUsbMessage()
                return false
            }
        }
    }

    fun importMedia(uris: List<Uri>) {
        if (uris.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUsbLoading = true,
                usbMessage = context.getString(R.string.importing_content)
            )

            val importedCount = mediaImporter.importUris(
                uris = uris,
                targetFolder = _uiState.value.selectedExternalFolder
            )

            reloadExternalFolderPreservingCurrentIndex()
            _uiState.value = _uiState.value.copy(
                isUsbLoading = false,
                usbMessage = context.resources.getQuantityString(
                    R.plurals.media_files_imported,
                    importedCount,
                    importedCount
                ),
                contentRevision = _uiState.value.contentRevision + 1
            )
            delay(3000)
            clearUsbMessage()
        }
    }

    private fun scheduleContentRefresh() {
        contentRefreshJob?.cancel()
        contentRefreshJob = viewModelScope.launch {
            delay(250)
            reloadExternalFolderPreservingCurrentIndex()
            _uiState.value = _uiState.value.copy(contentRevision = _uiState.value.contentRevision + 1)
        }
    }

    override fun onCleared() {
        contentObserver.stop()
        server.stop()
        super.onCleared()
    }

}

