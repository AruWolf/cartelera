package com.litvy.carteleria.ui.navigation

import java.io.File

data class ExternalNavigationState(

    // NIVELES
    // 0 = carpetas (SubMenu)
    // 1 = archivos (SubMenu)
    // 2 = contexto (Menu contextual)

    val level: Int = 0,

    val folderIndex: Int = 0, // Indice de carpeta seleccionada
    val fileIndex: Int = 0, // Indice de archivo seleccionado
    val contextIndex: Int = 0, // Indice de opcion de menu contextual seleccionada

    // Carpeta actual explorada
    val exploredFolder: File? = null,

    // Nivel anterior
    val previousLevel: Int = 0,

    // foco para el menu contextual
    val contextTarget: ContextTarget? = null
)