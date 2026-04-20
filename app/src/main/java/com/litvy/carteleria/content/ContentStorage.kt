package com.litvy.carteleria.content

import android.content.Context
import java.io.File

object ContentStorage {

    private const val RESOURCES_DIR_NAME = "resources"
    private const val DEFAULT_FOLDER_NAME = "demo"

    fun rootDirectory(context: Context): File =
        File(context.filesDir, RESOURCES_DIR_NAME)

    fun ensureRootDirectory(context: Context): File =
        rootDirectory(context).apply {
            if (!exists()) mkdirs()
        }

    fun defaultFolder(context: Context): File =
        File(ensureRootDirectory(context), DEFAULT_FOLDER_NAME)
}
