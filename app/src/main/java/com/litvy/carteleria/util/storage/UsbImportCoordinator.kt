package com.litvy.carteleria.util.storage

import android.content.Context
import android.net.Uri

class UsbImportCoordinator {

    private val storageLocator =
        StorageManagerUsbLocator()

    suspend fun locateUsb(
        context: Context
    ): Uri? {

        return storageLocator.locate(context)
    }
}