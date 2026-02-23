package com.litvy.carteleria.ui.navigation

import java.io.File

data class ExternalNavigationState(

    // 0 = carpetas
    // 1 = archivos
    // 2 = contexto
    val level: Int = 0,

    val folderIndex: Int = 0,
    val fileIndex: Int = 0,
    val contextIndex: Int = 0,

    val exploredFolder: File? = null,

    val previousLevel: Int = 0,

    val contextTarget: ContextTarget? = null
)