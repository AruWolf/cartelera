package com.litvy.carteleria.ui.touchremote

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration

object TouchDeviceDetector {

    fun shouldShowTouchRemote(context: Context): Boolean {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
        val isTv = uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
        val hasTouch = context.packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)

        return hasTouch && !isTv
    }

    fun isSmallTouchDevice(context: Context): Boolean {
        val configuration = context.resources.configuration
        return shouldShowTouchRemote(context) && configuration.smallestScreenWidthDp < 600
    }
}
