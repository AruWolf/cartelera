package com.litvy.carteleria.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.litvy.carteleria.slides.SlideSpeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "cartel_prefs")

class CartelPreferences(private val context: Context) {

    companion object {
        private val SOURCE_TYPE = stringPreferencesKey("source_type")
        private val SOURCE_VALUE = stringPreferencesKey("source_value")
        private val ANIMATION = stringPreferencesKey("animation")
        private val SPEED = stringPreferencesKey("speed")

        private const val INTERNAL = "INTERNAL"
        private const val EXTERNAL = "EXTERNAL"
    }

    val preferencesFlow: Flow<CartelConfig> =
        context.dataStore.data.map { prefs ->

            val type = prefs[SOURCE_TYPE] ?: INTERNAL
            val value = prefs[SOURCE_VALUE] ?: ""

            val source = when (type) {
                EXTERNAL -> ContentSource.External(value)
                else -> ContentSource.Internal(value)
            }

            CartelConfig(
                source = source,
                animation = prefs[ANIMATION] ?: "fade",
                speed = SlideSpeed.valueOf(
                    prefs[SPEED] ?: SlideSpeed.NORMAL.name
                )
            )
        }

    suspend fun saveConfig(config: CartelConfig) {
        context.dataStore.edit { prefs ->

            when (config.source) {
                is ContentSource.Internal -> {
                    prefs[SOURCE_TYPE] = INTERNAL
                    prefs[SOURCE_VALUE] = config.source.folder
                }

                is ContentSource.External -> {
                    prefs[SOURCE_TYPE] = EXTERNAL
                    prefs[SOURCE_VALUE] = config.source.path
                }
            }

            prefs[ANIMATION] = config.animation
            prefs[SPEED] = config.speed.name
        }
    }
}
