package com.litvy.carteleria.slides

import android.content.Context
import java.io.File

class AppStorageSlideProvider(
    private val context: Context
) {

    private val imageExtensions = listOf("png", "jpg", "jpeg", "webp")

    private val resourcesDir: File by lazy {
        File(context.filesDir, "resources").apply {
            if (!exists()) mkdirs()
        }
    }

    fun listFolders(): List<File> {
        return resourcesDir.listFiles()
            ?.filter { it.isDirectory }
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
                    id = "internal-$index-${file.name}",
                    file = file,
                    durationMs = 5000L,
                    transitionKey = "fade"
                )
            } ?: emptyList()
    }
}
