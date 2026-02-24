package com.litvy.carteleria.domain.usb

import android.net.Uri
import com.litvy.carteleria.util.usb.UsbScanResult

interface UsbImporter {
    suspend fun forceScan(): UsbScanResult
    suspend fun importFromUri(uri: Uri): UsbScanResult
    suspend fun scanViaMediaStore(): UsbScanResult
}