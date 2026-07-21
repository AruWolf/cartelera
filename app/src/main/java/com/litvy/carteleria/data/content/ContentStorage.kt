package com.litvy.carteleria.data.content

import android.content.Context
import java.io.File

// Directorio de almacenamiento de contenido
object ContentStorage {

    // Nombre de dirección de lectura de contenido
    private const val RESOURCES_DIR_NAME = "resources"
    fun rootDirectory(context: Context): File =
        File(context.filesDir, RESOURCES_DIR_NAME)

    // Crea el directorio de contenido, si no existe
    fun ensureRootDirectory(context: Context): File =
        rootDirectory(context).apply {
            if (!exists()) mkdirs()
        }
}
