package com.litvy.carteleria.data.external

import android.content.Context

// Gestor de duracion de imagenes
class ImageDurationManager(context: Context) {

    private val prefs = context.getSharedPreferences("image_slide_durations", Context.MODE_PRIVATE)

    // Metodo para obtener la duracion de una imagen
    fun getDuration(path: String): Long? {
        return if (prefs.contains(path)) prefs.getLong(path, 0L) else null
    }

    // Metodo para establecer la duracion de una imagen
    fun setDuration(path: String, durationMs: Long) {
        prefs.edit().putLong(path, durationMs).apply()
    }

    // Metodo para eliminar la duracion de una imagen
    fun clearDuration(path: String) {
        prefs.edit().remove(path).apply()
    }
}
