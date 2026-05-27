package com.litvy.carteleria.ui.loading

import android.content.Context
import androidx.annotation.RawRes

object AppVisualAssets {
    const val STARTUP_LOADING_BACKGROUND = "startup_loading_background"
    const val EMPTY_CONTENT_BACKGROUND = "empty_content_background"
    const val LOADING_INDICATOR = "loading_indicator"

    fun drawableId(context: Context, name: String): Int =
        context.resources.getIdentifier(name, "drawable", context.packageName)

    @RawRes
    fun rawId(context: Context, name: String): Int =
        context.resources.getIdentifier(name, "raw", context.packageName)
}
