package com.litvy.carteleria.util.usb

import android.content.Context
import kotlinx.coroutines.*
import java.io.File
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import android.provider.MediaStore
import com.litvy.carteleria.domain.usb.UsbImporter

// Lector de archivos usb --- Lee lo que esté dentro de la carpeta "Carteleria"
class UsbContentManager(
    private val context: Context
): UsbImporter {

    private val imageExtensions = listOf("png", "jpg", "jpeg", "webp")

    override suspend fun forceScan(): UsbScanResult = withContext(Dispatchers.IO) {

        val roots = listOf(
            File("/storage"),
            File("/mnt/media_rw"),
            File("/mnt/runtime/default")
        )

        var carteleriaFound = false
        var importedCount = 0

        roots.forEach { root ->

            val dirs = root.listFiles()?.filter { it.isDirectory } ?: return@forEach

            dirs.forEach { dir ->

                val carteleriaDir = File(dir, "Carteleria")

                if (carteleriaDir.exists() && carteleriaDir.isDirectory) {

                    carteleriaFound = true

                    val destRoot = File(context.filesDir, "resources")
                    if (!destRoot.exists()) destRoot.mkdirs()

                    carteleriaDir.listFiles()?.forEach { sourceFolder ->

                        if (!sourceFolder.isDirectory) return@forEach

                        val destFolder = File(destRoot, sourceFolder.name)
                        if (!destFolder.exists()) destFolder.mkdirs()

                        sourceFolder.listFiles()?.forEach { file ->

                            if (!file.isFile) return@forEach
                            if (file.extension.lowercase() !in imageExtensions) return@forEach

                            val destFile = File(destFolder, file.name)

                            if (!destFile.exists()) {
                                file.copyTo(destFile)
                                importedCount++
                            }
                        }
                    }
                }
            }
        }

        if (!carteleriaFound) return@withContext UsbScanResult.NoCarteleriaFolder

        if (importedCount == 0) {
            UsbScanResult.NoChanges
        } else {
            UsbScanResult.Imported(importedCount)
        }
    }

    override suspend fun importFromUri(uri: Uri): UsbScanResult = withContext(Dispatchers.IO) {

        val root = DocumentFile.fromTreeUri(context, uri)
            ?: return@withContext UsbScanResult.NoChanges

        var importedCount = 0

        val destRoot = File(context.filesDir, "resources")
        if (!destRoot.exists()) destRoot.mkdirs()

        root.listFiles()?.forEach { folder ->

            if (!folder.isDirectory) return@forEach

            val destFolder = File(destRoot, folder.name ?: return@forEach)
            if (!destFolder.exists()) destFolder.mkdirs()

            folder.listFiles()?.forEach { file ->

                if (!file.isFile) return@forEach

                val extension = file.name?.substringAfterLast(".", "")?.lowercase()
                if (extension !in imageExtensions) return@forEach

                val destFile = File(destFolder, file.name ?: return@forEach)

                if (!destFile.exists()) {

                    context.contentResolver.openInputStream(file.uri)?.use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    importedCount++
                }
            }
        }

        if (importedCount == 0) {
            UsbScanResult.NoChanges
        } else {
            UsbScanResult.Imported(importedCount)
        }
    }

    override suspend fun scanViaMediaStore(): UsbScanResult = withContext(Dispatchers.IO) {

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.RELATIVE_PATH
        )

        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%Carteleria%")

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        ) ?: return@withContext UsbScanResult.NoUsbFound

        var importedCount = 0
        val destRoot = File(context.filesDir, "resources")
        if (!destRoot.exists()) destRoot.mkdirs()

        cursor.use {

            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val pathColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)

            while (it.moveToNext()) {

                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val relativePath = it.getString(pathColumn)

                val contentUri = MediaStore.Images.Media
                    .EXTERNAL_CONTENT_URI
                    .buildUpon()
                    .appendPath(id.toString())
                    .build()

                val folderName = relativePath
                    ?.substringAfter("Carteleria/")
                    ?.substringBefore("/")
                    ?: "Default"

                val destFolder = File(destRoot, folderName)
                if (!destFolder.exists()) destFolder.mkdirs()

                val destFile = File(destFolder, name)

                if (!destFile.exists()) {

                    context.contentResolver.openInputStream(contentUri)?.use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    importedCount++
                }
            }
        }

        if (importedCount == 0) {
            UsbScanResult.NoUsbFound
        } else {
            UsbScanResult.Imported(importedCount)
        }
    }
}

sealed class UsbScanResult {
    object NoUsbFound : UsbScanResult()
    object NoCarteleriaFolder : UsbScanResult()
    object NoChanges : UsbScanResult()
    data class Imported(val count: Int) : UsbScanResult()
}