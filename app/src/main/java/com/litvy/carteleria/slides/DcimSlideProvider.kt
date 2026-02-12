package com.litvy.carteleria.slides

import android.os.Environment
import android.util.Log
import java.io.File

class DcimSlideProvider {

    private val imageExtensions = listOf("png", "jpg", "jpeg", "webp")

    private val dcimDirectory: File by lazy {
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
    }

    fun getRoot(): File? {
        return if (dcimDirectory.exists() && dcimDirectory.isDirectory) {
            dcimDirectory
        } else {
            null
        }
    }

    fun listFolders(): List<File> {
        val root = getRoot() ?: return emptyList()

        return root.listFiles()
            ?.filter { it.isDirectory }
            ?.sortedBy { it.name }
            ?: emptyList()
    }

    fun loadImagesFromDcimRoot(): List<File> {
        val root = getRoot() ?: return emptyList()

        return root.listFiles()
            ?.filter { it.isFile }
            ?.filter { it.extension.lowercase() in imageExtensions }
            ?.sortedBy { it.name }
            ?: emptyList()
    }

    fun loadFromFolder(folder: File): List<Slide> {
        return folder.listFiles()
            ?.filter { it.isFile }
            ?.filter { it.extension.lowercase() in imageExtensions }
            ?.sortedBy { it.name }
            ?.mapIndexed { index, file ->
                ExternalImageSlide(
                    id = "dcim-$index-${file.name}",
                    file = file,
                    durationMs = 5000L,
                    transitionKey = "fade"
                )
            } ?: emptyList()
    }

    fun loadSlidesFromRoot(): List<Slide> {
        val files = loadImagesFromDcimRoot()

        return files.mapIndexed { index, file ->
            ExternalImageSlide(
                id = "dcim-root-$index-${file.name}",
                file = file,
                durationMs = 5000L,
                transitionKey = "fade"
            )
        }
    }

    fun debugInfo(): String {
        val root = getRoot() ?: return "DCIM no accesible"

        val folders = listFolders()
        val rootImages = loadImagesFromDcimRoot()

        return buildString {
            appendLine("DCIM OK")
            appendLine("Carpetas: ${folders.size}")
            appendLine("Imágenes sueltas en DCIM: ${rootImages.size}")
        }
    }
}
