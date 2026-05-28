package com.litvy.carteleria.data.external

import android.content.Context

class ImageDurationManager(context: Context) {

    private val prefs = context.getSharedPreferences("image_slide_durations", Context.MODE_PRIVATE)

    fun getDuration(path: String): Long? {
        return if (prefs.contains(path)) prefs.getLong(path, 0L) else null
    }

    fun setDuration(path: String, durationMs: Long) {
        prefs.edit().putLong(path, durationMs).apply()
    }

    fun clearDuration(path: String) {
        prefs.edit().remove(path).apply()
    }
}
