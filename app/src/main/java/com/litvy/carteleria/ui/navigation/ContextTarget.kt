package com.litvy.carteleria.ui.navigation

sealed class ContextTarget {

    data class Folder(
        val name: String,
        val path: String
    ) : ContextTarget()

    data class FileItem(
        val name: String,
        val path: String
    ) : ContextTarget()
}