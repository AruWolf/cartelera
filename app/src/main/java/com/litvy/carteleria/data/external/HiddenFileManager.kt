package com.litvy.carteleria.data.external

import android.content.Context
import java.io.File

// Gestor de archivos ocultos para reproduccion
class HiddenFileManager(context: Context) {

    // Genera un txt donde almacena los archivos ocultos
    private val hiddenFile = File(context.filesDir, "hidden_files.txt")

    // Lista de ruta de archivos ocultos
    private var hiddenPaths: MutableSet<String> = load()

    // Metodo para verificar si un archivo esta oculto
    fun isHidden(path: String): Boolean {
        return hiddenPaths.contains(path)
    }

    // Metodo para ocultar un archivo
    fun hide(path: String) {
        if (hiddenPaths.add(path)) save()
    }

    // Metodo para mostrar un archivo
    fun show(path: String) {
        if (hiddenPaths.remove(path)) save()
    }

    // Metodo para limpiar la lista de archivos ocultos
    private fun save() {
        hiddenFile.writeText(hiddenPaths.joinToString("\n"))
    }

    // Metodo para cargar la lista de archivos ocultos
    private fun load(): MutableSet<String> {
        if (!hiddenFile.exists()) return mutableSetOf()
        return hiddenFile.readLines().toMutableSet()
    }
}