package com.litvy.carteleria.slides

import android.content.Context
import com.litvy.carteleria.content.ContentStorage
import java.io.File
import com.litvy.carteleria.data.external.HiddenFileManager
import com.litvy.carteleria.data.external.ImageDurationManager


class AppStorageSlideProvider(
    private val context: Context,
    private val hiddenManager: HiddenFileManager,
    private val durationManager: ImageDurationManager
) {

    // Formatos de imagen admitidos TODO: Verificar la correcta lectura de cada uno
    private val imageExtensions = listOf("png", "jpg", "jpeg", "webp")
    private val videoExtensions = listOf("mp4", "webm", "mkv")

    // Direccion de la carpeta de recursos
    private val resourcesDir: File by lazy { ContentStorage.ensureRootDirectory(context) }

    // LISTADO DE CARPETAS

    fun listFolders(): List<File> {
        return resourcesDir.listFiles()
            ?.filter { it.isDirectory }
            ?.sortedBy { it.name.lowercase() }
            ?: emptyList()
    }

    fun createFolder(name: String): File? {
        val folderName = name.trim()
        if (folderName.isBlank()) return null
        if (folderName.any { it in setOf('\\', '/', ':', '*', '?', '"', '<', '>', '|') }) return null
        if (listFolders().any { it.name.equals(folderName, ignoreCase = true) }) return null

        val folder = File(resourcesDir, folderName)
        return if (folder.mkdirs()) folder else null
    }

    fun loadFromFolder(folder: File): List<Slide> {

        return folder.listFiles()
            ?.filter { it.isFile && !hiddenManager.isHidden(it.absolutePath) }
            ?.sortedBy { it.name.lowercase() }
            ?.mapIndexedNotNull { index, file ->

                val ext = file.extension.lowercase()

                when {

                    ext in imageExtensions ->
                        ExternalImageSlide(
                            id = "external-img-$index-${file.name}",
                            file = file,
                            customDurationMs = durationManager.getDuration(file.absolutePath),
                            transitionKey = "fade"
                        )

                    ext in videoExtensions ->
                        ExternalVideoSlide(
                            id = "external-video-$index-${file.name}",
                            file = file,
                            transitionKey = "fade"
                        )

                    else -> null
                }
            }
            ?: emptyList()
    }

    // MODIFICACIÓN DE CARPETAS
    // Eliminar carpeta
    fun deleteFolder(folder: File): Boolean {
        if (!folder.exists() || !folder.isDirectory) return false
        return folder.deleteRecursively()
    }

    // MODIFICACION DE ARCHIVOS

    //  Eliminar archivos
    fun deleteFile(file: File): Boolean {
        if (!file.exists() || !file.isFile) return false
        durationManager.clearDuration(file.absolutePath)
        return file.delete()
    }
    // Copiar archivos
    fun duplicateFileToFolder(source: File, targetFolder: File): Boolean {
        if (!source.exists()) return false
        if (!targetFolder.exists()) return false

        val newFileName = generateUniqueFileName(source, targetFolder)
        val targetFile = File(targetFolder, newFileName)

        source.copyTo(
            target = targetFile,
            overwrite = false
        )

        durationManager.getDuration(source.absolutePath)?.let { duration ->
            durationManager.setDuration(targetFile.absolutePath, duration)
        }

        return true
    }
    // Cortar archivos
    fun moveFileToFolder(source: File, targetFolder: File): Boolean {
        if (!source.exists()) return false
        if (!targetFolder.exists()) return false

        // Si es la misma carpeta, no hacemos nada
        if (source.parentFile == targetFolder) return false

        val targetFile = File(targetFolder, source.name)

        // Si ya existe archivo con ese nombre, generamos uno nuevo aplicando un número al final
        val finalTarget = if (targetFile.exists()) {
            File(targetFolder, generateUniqueFileName(source, targetFolder))
        } else targetFile

        val moved = source.renameTo(finalTarget)

        if (moved) {
            durationManager.getDuration(source.absolutePath)?.let { duration ->
                durationManager.clearDuration(source.absolutePath)
                durationManager.setDuration(finalTarget.absolutePath, duration)
            }
        }

        return moved
    }

    fun setImageDuration(file: File, durationMs: Long) {
        if (!file.exists() || !file.isFile) return
        if (!ImageSlideDurations.isAllowed(durationMs)) return
        durationManager.setDuration(file.absolutePath, durationMs)
    }

    fun clearImageDuration(file: File) {
        durationManager.clearDuration(file.absolutePath)
    }

    fun clearImageDurationsInFolder(folder: File) {
        folder.listFiles()
            ?.filter { it.isFile && it.extension.lowercase() in imageExtensions }
            ?.forEach { durationManager.clearDuration(it.absolutePath) }
    }

    fun clearAllImageDurations() {
        listFolders().forEach { folder ->
            clearImageDurationsInFolder(folder)
        }
    }

    // GENERADOR DE NOMBRE REPETIDO DE ARCHIVO

    // Se encarga de aplicar un número al final del nombre del archivo para evitar sobreescritura
    // Aplica (x) al final del nombre del archivo, siendo x un numero
    // El número aplicado siempre va a ser diferente al último número usado
    private fun generateUniqueFileName(
        sourceFile: File,
        targetFolder: File
    ): String {

        val baseName = sourceFile.nameWithoutExtension
        val extension = sourceFile.extension

        val existingNames = targetFolder.listFiles()
            ?.filter { it.isFile }
            ?.map { it.nameWithoutExtension }
            ?: emptyList()

        if (!existingNames.contains(baseName)) {
            return "$baseName.$extension"
        }

        val regex = Regex("""^${Regex.escape(baseName)}\((\d+)\)$""")

        var maxNumber = 0

        existingNames.forEach { name ->

            if (name == baseName) {
                maxNumber = maxOf(maxNumber, 0)
            }

            val match = regex.find(name)
            if (match != null) {
                val number = match.groupValues[1].toIntOrNull() ?: 0
                maxNumber = maxOf(maxNumber, number)
            }
        }

        val newNumber = maxNumber + 1
        return "$baseName($newNumber).$extension"
    }
}
