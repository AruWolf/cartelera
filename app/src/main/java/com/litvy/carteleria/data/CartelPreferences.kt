package com.litvy.carteleria.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.litvy.carteleria.data.content.ContentStorage
import com.litvy.carteleria.domain.slides.ImageSlideDurations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "cartel_prefs")

class CartelPreferences(private val context: Context) {

    companion object {
        private val SOURCE_TYPE = stringPreferencesKey("source_type")
        private val SOURCE_VALUE = stringPreferencesKey("source_value")
        private val ANIMATION = stringPreferencesKey("animation")
        private val GLOBAL_IMAGE_DURATION_MS = longPreferencesKey("global_image_duration_ms")

        private const val EXTERNAL = "EXTERNAL"
    }

    val preferencesFlow: Flow<CartelConfig> =
        context.dataStore.data.map { prefs ->

            val type = prefs[SOURCE_TYPE] ?: EXTERNAL
            val value = prefs[SOURCE_VALUE] ?: ""
            val rootDirectory = ContentStorage.ensureRootDirectory(context)
            val firstFolder = rootDirectory.listFiles()
                ?.firstOrNull { it.isDirectory }
                ?: rootDirectory

            val source = when (type) {
                EXTERNAL -> ContentSource.External(value.ifBlank { firstFolder.absolutePath })
                else -> ContentSource.External(firstFolder.absolutePath)
            }

            CartelConfig(
                source = source,
                animation = prefs[ANIMATION] ?: "fade",
                globalImageDurationMs = prefs[GLOBAL_IMAGE_DURATION_MS]
                    ?.takeIf { ImageSlideDurations.isAllowed(it) }
                    ?: ImageSlideDurations.DEFAULT_GLOBAL_DURATION_MS
                )
        }

    suspend fun saveConfig(config: CartelConfig) {
        context.dataStore.edit { prefs ->

            when (config.source) {
                is ContentSource.External -> {
                    prefs[SOURCE_TYPE] = EXTERNAL
                    prefs[SOURCE_VALUE] = config.source.path
                }

                else -> {}
            }

            prefs[ANIMATION] = config.animation
            if (ImageSlideDurations.isAllowed(config.globalImageDurationMs)) {
                prefs[GLOBAL_IMAGE_DURATION_MS] = config.globalImageDurationMs
            }
        }
    }
}
