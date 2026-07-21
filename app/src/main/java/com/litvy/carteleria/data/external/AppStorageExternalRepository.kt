package com.litvy.carteleria.data.external

import com.litvy.carteleria.domain.external.*
import com.litvy.carteleria.domain.slides.AppStorageSlideProvider
import java.io.File

class AppStorageExternalRepository(
    private val provider: AppStorageSlideProvider,
    private val hiddenManager: HiddenFileManager,
    private val durationManager: ImageDurationManager,
    private val shortcutManager: FolderShortcutManager
) : ExternalContentRepository {

    // Formatos aceptados
    private val imageExtensions = setOf("png", "jpg", "jpeg", "webp")

    // Metodo de listado de carpetas
    override fun listFolders(): List<ExternalFolder> {
        val folders = provider.listFolders().map {
            ExternalFolder(
                name = it.name,
                path = it.absolutePath
            )
        }

        return shortcutManager.applyPersistedShortcuts(folders)
    }

    // Metodo de creacion de carpetas
    override fun createFolder(name: String): ExternalFolder? {
        return provider.createFolder(name)?.let {
            ExternalFolder(
                name = it.name,
                path = it.absolutePath
            )
        }
    }

    // Metodo de listado de archivos en una carpeta
    override fun listFiles(folderPath: String): List<ExternalFile> {

        val folder = File(folderPath)

        return folder.listFiles()
            ?.filter { it.isFile }
            ?.map { // Aplica propiedades para el proceso posterior de reproduccion
                val isImage = it.extension.lowercase() in imageExtensions
                ExternalFile(
                    name = it.name,
                    path = it.absolutePath,
                    isHidden = hiddenManager.isHidden(it.absolutePath),
                    isImage = isImage,
                    customDurationMs = if (isImage) durationManager.getDuration(it.absolutePath) else null
                )
            } // Ordenamiento de archivos alfabeticamente
            ?.sortedWith(
                compareBy<ExternalFile> { it.isHidden }
                    .thenBy(String.CASE_INSENSITIVE_ORDER) { it.name }
            )
            ?: emptyList()
    }

    // Metodo para eliminar archivos
    override fun deleteFile(path: String) {
        provider.deleteFile(File(path))
    }

    // Metodo para eliminar carpetas
    override fun deleteFolder(path: String) {
        provider.deleteFolder(File(path))
        shortcutManager.clearShortcut(path)
    }

    // Metodo para aplicar atajo numerico a una carpeta
    override fun setFolderShortcut(path: String, shortcutNumber: Int?) {
        shortcutManager.setShortcut(path, shortcutNumber)
    }

    // Metodo para copiar un archivo
    override fun copyFile(sourcePath: String, targetFolderPath: String) {
        provider.duplicateFileToFolder(
            File(sourcePath),
            File(targetFolderPath)
        )
    }

    // Metodo para cortar o mover un archivo
    override fun moveFile(sourcePath: String, targetFolderPath: String) {
        provider.moveFileToFolder(
            File(sourcePath),
            File(targetFolderPath)
        )
    }

    // Metodo para ocultar un archivo en reproduccion
    override fun hideFile(path: String) {
        hiddenManager.hide(path)
    }

    // Metodo para volver a mostrar un archivo en reproduccion
    override fun showFile(path: String) {
        hiddenManager.show(path)
    }

}
