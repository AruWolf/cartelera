package com.litvy.carteleria.util

import android.content.Context
import android.content.pm.PackageManager
import android.app.UiModeManager
import android.content.res.Configuration

object DeviceUtils {

    fun isTv(context: Context): Boolean {

        val uiModeManager =
            context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

        return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION ||
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }
}