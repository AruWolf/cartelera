package com.litvy.carteleria.slides

import android.content.Context

class AssetSlideProvider(
    private val context: Context
) {

    private val ROOT = "content"

    fun loadFrom(folder: String): List<Slide> {
        val path = "$ROOT/$folder"
        val files = context.assets.list(path) ?: emptyArray()

        return files
            .filter {
                it.endsWith(".png") ||
                        it.endsWith(".jpg") ||
                        it.endsWith(".webp")
            }
            .sorted()
            .mapIndexed { index, fileName ->
                AssetImageSlide(
                    id = "$folder-$index",
                    assetPath = "$path/$fileName",
                    durationMs = 3000,
                    transitionKey = "fade"
                )
            }
    }

    fun listFolders(): List<String> {
        return context.assets.list(ROOT)
            ?.filter { folder ->
                context.assets.list("$ROOT/$folder")
                    ?.any { file ->
                        file.endsWith(".png") ||
                                file.endsWith(".jpg") ||
                                file.endsWith(".webp")
                    } == true
            }
            ?.sorted()
            ?: emptyList()
    }
}

