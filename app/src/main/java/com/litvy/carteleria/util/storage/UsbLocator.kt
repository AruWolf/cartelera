package com.litvy.carteleria.util.storage

import android.content.Context
import android.net.Uri

interface UsbLocator {
    suspend fun locate(context: Context): Uri?
}