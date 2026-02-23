package com.litvy.carteleria.slides

import android.content.Context
import java.io.File

class AppStorageSlideProvider(
    private val context: Context
) {

    private val imageExtensions = listOf("png", "jpg", "jpeg", "webp")

    private val resourcesDir: File by lazy {
        File(context.filesDir, "resources").apply {
            if (!exists()) mkdirs()
        }
    }

    // LISTADO DE CARPETAS

    fun listFolders(): List<File> {
        return resourcesDir.listFiles()
            ?.filter { it.isDirectory }
            ?.sortedBy { it.name.lowercase() }
            ?: emptyList()
    }

    fun loadFromFolder(folder: File): List<Slide> {
        return folder.listFiles()
            ?.filter { it.isFile }
            ?.filter { it.extension.lowercase() in imageExtensions }
            ?.sortedBy { it.name.lowercase() }
            ?.mapIndexed { index, file ->
                ExternalImageSlide(
                    id = "external-$index-${file.name}",
                    file = file,
                    durationMs = 5000L,
                    transitionKey = "fade"
                )
            } ?: emptyList()
    }

    // MODIFICACIÓN DE CARPETAS

    //TODO: Implementar creación de carpetas. Concepto: Opción " + Crear Carpeta " en menú de contenido externo, por encima de todas las carpetas.
    fun createFolder(name: String): Boolean {
        if (name.isBlank()) return false

        val newFolder = File(resourcesDir, name.trim())
        if (newFolder.exists()) return false

        return newFolder.mkdirs()
    }

    // Eliminar carpeta
    fun deleteFolder(folder: File): Boolean {
        if (!folder.exists() || !folder.isDirectory) return false
        return folder.deleteRecursively()
    }

    //TODO: Implementar renombrado de carpetas. Concepto: Opción ya existente en menu de acciones de carpeta en ExternalContentSubMenu.
    fun renameFolder(folder: File, newName: String): Boolean {
        if (newName.isBlank()) return false

        val target = File(resourcesDir, newName.trim())
        if (target.exists()) return false

        return folder.renameTo(target)
    }

    // MODIFICACION DE ARCHIVOS

    //  Eliminar archivos
    fun deleteFile(file: File): Boolean {
        if (!file.exists() || !file.isFile) return false
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

        return source.renameTo(finalTarget)
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