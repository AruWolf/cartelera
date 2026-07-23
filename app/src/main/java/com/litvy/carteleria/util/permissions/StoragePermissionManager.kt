package com.litvy.carteleria.util.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class StoragePermissionManager(
    private val context: Context
) {

    fun hasStoragePermission(): Boolean {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        val imagePermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED

        val videoPermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED

        return imagePermission && videoPermission
    }

    fun requiredPermissions(): Array<String> {

        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {

            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

        } else {

            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        }
    }
}