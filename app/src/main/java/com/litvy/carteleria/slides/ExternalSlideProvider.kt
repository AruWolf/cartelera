package com.litvy.carteleria.slides

import android.content.Context
import java.io.File

class ExternalSlideProvider(
    private val context: Context
) {
    fun listFolders(root: File): List<File>{
        return root.listFiles()
            ?.filter { it.isDirectory }
            ?.filter { dir ->
                dir.listFiles()?.any { file ->
                    file.extension.lowercase() in listOf("png", "jpg", "jpeg", "webp")
                } == true
            }
            ?: emptyList()
    }

    fun loadFrom(folder: File): List<Slide>{
        return folder.listFiles()
            ?.filter { it.isFile }
            ?.filter { it.extension.lowercase() in listOf("png", "jpg", "jpeg", "webp")}
            ?.sortedBy { it.name }
            ?.mapIndexed { index, file ->
                ExternalImageSlide(
                    id = "${folder.name}-$index",
                    file = file,
                    durationMs = 5000L,
                    transitionKey = "Fade"
                )
            }
            ?: emptyList()
    }
}