package com.litvy.carteleria.slides

import android.content.Context

class AssetSlideProvider(
    private val context: Context,
) {

    fun loadFrom(folder: String): List<Slide> {
        val files = context.assets.list(folder) ?: emptyArray()
    /* TODO{ Busqueda de archivos png, jpg o webp en carpeta assets.
        Se consume en propaganda1 el cual especifica la carpeta a consumir}*/
        return files
            .filter { it.endsWith("png") || it.endsWith("jpg") || it.endsWith("webp") }
            .sorted()
            .mapIndexed { index, fileName ->
                AssetImageSlide(
                    id = "$folder-$index",
                    assetPath = "$folder/$fileName",
                    durationMs = 3000,
                    transitionKey = "fade"
                )
            }
    }

}
