package com.litvy.carteleria.services.usb

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import com.litvy.carteleria.data.content.ContentStorage
import com.litvy.carteleria.domain.usb.UsbImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import android.util.Log

/**
 * Importa contenido elegido por el usuario mediante Storage Access Framework.
 *
 * No se intenta recorrer /storage ni otras rutas físicas: desde Android 11 ese
 * acceso directo deja de estar disponible para una aplicación normal. El URI
 * entregado por el selector es la autorización explícita para leer el pendrive.
 */
class UsbContentManager(
    private val context: Context
) : UsbImporter {

    override suspend fun forceScan(): UsbScanResult = withContext(Dispatchers.IO) {

        val roots = listOf(
            File("/storage"),
            File("/mnt/media_rw"),
            File("/mnt/runtime/default"),
            File("/mnt/runtime/read"),
            File("/mnt/runtime/write")
        )

        val destinationRoot = ContentStorage.ensureRootDirectory(context)

        var importedCount = 0
        var carteleriaFound = false

        roots.forEach { root ->

            val children = try {
                root.listFiles()
            } catch (e: Exception) {
                return@forEach
            } ?: return@forEach

            children.forEach { device ->

                if (!device.isDirectory)
                    return@forEach

                val carteleria =
                    File(device, "Carteleria")

                if (!carteleria.exists() || !carteleria.isDirectory)
                    return@forEach

                carteleriaFound = true

                val folders = try {
                    carteleria.listFiles()
                } catch (e: Exception) {
                    null
                }

                folders?.forEach { sourceFolder ->

                    if (!sourceFolder.isDirectory)
                        return@forEach

                    val destinationFolder =
                        File(destinationRoot, sourceFolder.name)

                    destinationFolder.mkdirs()

                    val files = try {
                        sourceFolder.listFiles()
                    } catch (e: Exception) {
                        null
                    }

                    files?.forEach { file ->

                        if (!file.isFile)
                            return@forEach

                        val extension =
                            file.extension.lowercase()

                        if (extension !in SUPPORTED_MEDIA_EXTENSIONS)
                            return@forEach

                        val destination =
                            File(destinationFolder, file.name)

                        if (!destination.exists()) {
                            try {
                                file.copyTo(destination)
                                importedCount++
                            } catch (_: Exception) {
                            }
                        }
                    }
                }
            }
        }

        when {
            importedCount > 0 ->
                UsbScanResult.Imported(importedCount)

            carteleriaFound ->
                UsbScanResult.NoChanges

            else -> {
                scanViaMediaStore()
            }
        }
    }

    override suspend fun importFromUri(uri: Uri): UsbScanResult = withContext(Dispatchers.IO) {
        val selectedRoot = DocumentFile.fromTreeUri(context, uri)
            ?: return@withContext UsbScanResult.NoUsbFound

        // Conserva la estructura histórica: si existe Carteleria en la carpeta
        // elegida, esa carpeta es la fuente. Si no existe, se importa la carpeta
        // que el usuario eligió, sin exigir ningún nombre determinado.
        val sourceRoot = selectedRoot.carteleriaChildOrSelf()
        val destinationRoot = ContentStorage.ensureRootDirectory(context)
        val destinationFolder = File(destinationRoot, sourceRoot.safeName())
            .apply { mkdirs() }

        val importedCount = copyMediaFiles(
            directory = sourceRoot,
            destinationFolder = destinationFolder,
            depth = ROOT_DEPTH
        )

        if (importedCount > 0) {
            UsbScanResult.Imported(importedCount)
        } else {
            UsbScanResult.NoChanges
        }
    }

    /**
     * Conservado por compatibilidad con el contrato anterior. MediaStore no es
     * apropiado para descubrir automáticamente el contenido de un pendrive:
     * sólo debe leerse el árbol que el usuario seleccionó con SAF.
     */
    override suspend fun scanViaMediaStore(): UsbScanResult = withContext(Dispatchers.IO) {

        val projection = arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.RELATIVE_PATH
        )

        val cursor = context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            null,
            null,
            null
        ) ?: return@withContext UsbScanResult.NoUsbFound

        val builder = StringBuilder()

        cursor.use {

            val nameColumn =
                it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)

            val pathColumn =
                it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH)

            var count = 0

            while (it.moveToNext() && count < 30) {

                builder.appendLine(
                    "${it.getString(pathColumn)} -> ${it.getString(nameColumn)}"
                )

                count++
            }
        }

        UsbScanResult.Debug(builder.toString())
    }

    private fun DocumentFile.carteleriaChildOrSelf(): DocumentFile = when {
        name.equals(CARTELERIA_DIRECTORY_NAME, ignoreCase = true) -> this
        else -> findFile(CARTELERIA_DIRECTORY_NAME)?.takeIf { it.isDirectory } ?: this
    }

    private fun copyMediaFiles(
        directory: DocumentFile,
        destinationFolder: File,
        depth: Int
    ): Int {
        var importedCount = 0

        directory.listFiles().forEach { entry ->
            when {
                entry.isFile && entry.isSupportedMedia() -> {
                    val fileName = entry.name ?: return@forEach
                    val destination = File(destinationFolder, fileName)

                    // El comportamiento se mantiene idempotente: una nueva
                    // importación no pisa un archivo que ya usa ese nombre.
                    if (!destination.exists() && copyToAppStorage(entry, destination)) {
                        importedCount++
                    }
                }

                // depth = 0 corresponde a la carpeta elegida. Se admiten sus
                // subcarpetas en los niveles 1, 2 y 3, pero no se baja más.
                entry.isDirectory && depth < MAX_DIRECTORY_DEPTH -> {
                    importedCount += copyMediaFiles(entry, destinationFolder, depth + 1)
                }
            }
        }

        return importedCount
    }

    private fun copyToAppStorage(source: DocumentFile, destination: File): Boolean = try {
        context.contentResolver.openInputStream(source.uri)?.use { input ->
            destination.outputStream().use { output -> input.copyTo(output) }
        } != null
    } catch (_: SecurityException) {
        false
    } catch (_: java.io.IOException) {
        // Puede ocurrir si se desconecta el pendrive durante la copia.
        destination.delete()
        false
    }

    private fun DocumentFile.isSupportedMedia(): Boolean {
        val extension = name?.substringAfterLast('.', missingDelimiterValue = "")?.lowercase()
        return extension in SUPPORTED_MEDIA_EXTENSIONS
    }

    private fun DocumentFile.safeName(): String = name
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?.replace(Regex("[\\\\/:*?\"<>|]"), "_")
        ?: DEFAULT_IMPORT_FOLDER_NAME

    private companion object {
        const val CARTELERIA_DIRECTORY_NAME = "Carteleria"
        const val DEFAULT_IMPORT_FOLDER_NAME = "Contenido USB"
        const val ROOT_DEPTH = 0
        const val MAX_DIRECTORY_DEPTH = 3
        val SUPPORTED_MEDIA_EXTENSIONS = setOf(
            "png", "jpg", "jpeg", "webp", "gif", "bmp",
            "mp4", "webm", "mkv", "3gp", "mov"
        )
    }
}

sealed class UsbScanResult {
    data object NoUsbFound : UsbScanResult()
    data object NoCarteleriaFolder : UsbScanResult()
    data object NoChanges : UsbScanResult()
    data class Imported(val count: Int) : UsbScanResult()
    data class Debug(val message: String): UsbScanResult()
}
