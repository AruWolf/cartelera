package com.litvy.carteleria.slides

import android.content.Context

class AssetSlideProvider(
    private val context: Context
) {

    private val ROOT = "content"
    private val videoExtensions = listOf("mp4", "webm", "mkv")

    fun loadFrom(folder: String): List<Slide> {
        val path = "$ROOT/$folder"
        val files = context.assets.list(path) ?: emptyArray()

        return files
            .filter { fileName ->
                val ext = fileName.substringAfterLast('.', "")
                ext.lowercase() in listOf("png","jpg","jpeg","webp") + videoExtensions
            }
            .sorted()
            .mapIndexedNotNull { index, fileName ->

                val ext = fileName.substringAfterLast('.', "").lowercase()

                when {

                    ext in listOf("png","jpg","jpeg","webp") ->
                        AssetImageSlide(
                            id = "$folder-img-$index",
                            assetPath = "$path/$fileName",
                            durationMs = 3000,
                            transitionKey = "fade"
                        )

                    ext in videoExtensions ->
                        AssetVideoSlide(
                            id = "$folder-video-$index",
                            assetPath = "$path/$fileName",
                            transitionKey = "fade"
                        )

                    else -> null
                }
            }
    }

    fun listFolders(): List<String> {
        return context.assets.list(ROOT)
            ?.filter { folder ->
                context.assets.list("$ROOT/$folder")
                    ?.any { file ->
                        val ext = file.substringAfterLast('.', "")
                        ext.lowercase() in listOf("png","jpg","jpeg","webp") + videoExtensions
                    } == true
            }
            ?.sorted()
            ?: emptyList()
    }
}

