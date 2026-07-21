package com.litvy.carteleria.ui.menu

import com.litvy.carteleria.domain.external.ExternalFile
import com.litvy.carteleria.domain.external.ExternalFolder

sealed interface ExternalMenuEntry {
    data object ImportFiles: ExternalMenuEntry
    data object ImportGallery: ExternalMenuEntry
    data object CreateFolder: ExternalMenuEntry
    data object ForceUsbScan: ExternalMenuEntry
    data object Paste: ExternalMenuEntry
    data object Back: ExternalMenuEntry
    data class Folder(val folder: ExternalFolder): ExternalMenuEntry
    data class File(val file: ExternalFile): ExternalMenuEntry
}