package com.litvy.carteleria.util.storage
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.storage.StorageManager
import android.os.storage.StorageVolume

class StorageManagerUsbLocator : UsbLocator {

    override suspend fun locate(
        context: Context
    ): Uri? {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return null
        }

        val storageManager =
            context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

        val removableVolume = storageManager.storageVolumes.firstOrNull {
            isUsbVolume(it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return removableVolume?.directory?.let(Uri::fromFile)
        }

        return null
    }

    private fun isUsbVolume(
        volume: StorageVolume
    ): Boolean {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return false
        }

        if (!volume.isRemovable)
            return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            volume.state == android.os.Environment.MEDIA_MOUNTED
        } else {
            true
        }
    }
}

