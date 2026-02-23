package com.litvy.carteleria.ui.navigation

import java.io.File

sealed class ContextTarget {

    data class Folder(val folder: File) : ContextTarget()

    data class FileItem(val file: File) : ContextTarget()
}