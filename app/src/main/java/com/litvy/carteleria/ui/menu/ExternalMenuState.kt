package com.litvy.carteleria.ui.menu.external

import com.litvy.carteleria.domain.external.ExternalFile
import com.litvy.carteleria.domain.external.ExternalFolder

data class ExternalMenuState(
    val folders: List<ExternalFolder> = emptyList(),
    val files: List<ExternalFile> = emptyList(),
    val currentFolderPath: String? = null,
    val clipboardPath: String? = null,
    val isCut: Boolean = false,
    val isInFolder: Boolean = false,

    val renameTargetPath: String? = null,
    val renameInitialName: String = ""
)