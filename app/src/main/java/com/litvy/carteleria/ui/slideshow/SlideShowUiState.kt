package com.litvy.carteleria.ui.slideshow

import com.litvy.carteleria.slides.Slide
import com.litvy.carteleria.slides.SlideSpeed
import com.litvy.carteleria.ui.menu.model.ClipboardItem
import java.io.File

data class SlideShowUiState(
    val contentMode: ContentMode = ContentMode.INTERNAL,
    val selectedInternalFolder: String? = null,
    val selectedExternalFolder: File? = null,
    val currentAnimation: String = "fade",
    val slideSpeed: SlideSpeed = SlideSpeed.NORMAL,
    val slides: List<Slide> = emptyList(),
    val isPaused: Boolean = false,
    val currentIndex: Int = 0,
    val showSlideIndicator: Boolean = false,
    val showQr: Boolean = false,
    val menuVisible: Boolean = false,
    val usbMessage: String? = null,
    val isUsbLoading: Boolean = false,
    val clipboardItem: ClipboardItem? = null
)