package com.litvy.carteleria.util.usb

import android.content.Context
import kotlinx.coroutines.*
import java.io.File

// Lector de archivos usb --- Lee lo que esté dentro de la carpeta "Carteleria"

class UsbContentManager(
    private val context: Context,
    private val onContentUpdated: () -> Unit
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val imageExtensions = listOf("png", "jpg", "jpeg", "webp")

    // Metodo para iniciar el escaneo periodico de usb
    // Se utiliza en caso de que el tv no lea automaticamente el usb
    fun startPeriodicScan() {
        scope.launch {
            while (isActive) {
                scanUsbStorage()
                delay(60000) // temporizador de 60 segundos para iniciar escaneo
            }
        }
    }

    // Metodo para forzar el escaneo del usb
    fun forceScan() {
        scope.launch {
            scanUsbStorage()
        }
    }

    // Metodo para escanear el usb y extraer los archivos que estén dentro de la carpeta "Carteleria"
    private fun scanUsbStorage() {

        // Carpeta base para buscar el usb
        val storageRoot = File("/storage")

        val possibleUsbDirs = storageRoot.listFiles()
            ?.filter { it.isDirectory }
            ?.filterNot {
                it.name == "emulated" ||
                        it.name == "self"
            } ?: return

        possibleUsbDirs.forEach { usbRoot ->

            val carteleriaDir = File(usbRoot, "Carteleria") // Carpeta seleccionada para extraer archivos

            if (carteleriaDir.exists() && carteleriaDir.isDirectory) {
                importContent(carteleriaDir)
            }
        }
    }

    // Metodo para importar los archivos que estén dentro de la carpeta "Carteleria"
    private fun importContent(sourceRoot: File) {
        // Carpeta destino para importar los archivos
        val destRoot = File(context.filesDir, "resources")
        if (!destRoot.exists()) destRoot.mkdirs()

        var somethingImported = false

        // Recorrer los archivos de la carpeta "Carteleria"
        sourceRoot.listFiles()?.forEach { sourceFolder ->

            if (!sourceFolder.isDirectory) return@forEach

            val destFolder = File(destRoot, sourceFolder.name)

            if (!destFolder.exists()) {
                destFolder.mkdirs()
            }
            sourceFolder.listFiles()?.forEach { file ->

                if (!file.isFile) return@forEach
                if (file.extension.lowercase() !in imageExtensions) return@forEach

                val destFile = File(destFolder, file.name)

                // Si el archivo no existe, se importa. Si existe se cargan los archivos que no existan.
                if (!destFile.exists()) {
                    file.copyTo(destFile)
                    somethingImported = true
                }
            }
        }
        // Si se importaron archivos, se llama al metodo onContentUpdated()
        if (somethingImported) {
            onContentUpdated()
        }
    }
    // Metodo para detener el escaneo del usb
    fun stop() {
        scope.cancel()
    }
}