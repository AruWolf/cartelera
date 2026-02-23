package com.litvy.carteleria.ui.navigation

import java.io.File

// Clase utilizada para indicar que tipo de menu desplegar
sealed class ContextTarget {

    data class Folder(val folder: File) : ContextTarget()

    data class FileItem(val file: File) : ContextTarget()
}