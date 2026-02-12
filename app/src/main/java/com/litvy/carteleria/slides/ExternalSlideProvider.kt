package com.litvy.carteleria.slides

import java.io.File

class ExternalSlideProvider {

    fun detectUsbRoots(): List<File> {

        val storageDir = File("/storage")

        val candidates = storageDir.listFiles()
            ?.filter { it.isDirectory }
            ?: emptyList()

        return candidates.filter { dir ->
            try {
                val files = dir.listFiles()
                files != null
            } catch (e: Exception) {
                false
            }
        }
    }



    fun listFolders(usbRoot: File): List<File> {

        return try {
            usbRoot.listFiles()
                ?.filter { it.isDirectory }
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }


    fun loadFromFolder(folder: File): List<Slide> {
        return folder.listFiles()
            ?.filter { it.isFile }
            ?.filter {
                it.extension.lowercase() in listOf("png", "jpg", "jpeg", "webp")
            }
            ?.sortedBy { it.name }
            ?.mapIndexed { index, file ->
                ExternalImageSlide(
                    id = "external-$index",
                    file = file,
                    durationMs = 5000L,
                    transitionKey = "fade"
                )
            } ?: emptyList()
    }
}
