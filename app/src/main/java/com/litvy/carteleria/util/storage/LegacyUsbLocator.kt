package com.litvy.carteleria.util.storage

import android.content.Context
import android.net.Uri

class LegacyUsbLocator: UsbLocator {

    override suspend fun locate(
        context: Context
    ): Uri? {

        return null
    }
}