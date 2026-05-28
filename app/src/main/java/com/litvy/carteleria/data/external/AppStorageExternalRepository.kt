package com.litvy.carteleria.data.external

import com.litvy.carteleria.domain.external.*
import com.litvy.carteleria.slides.AppStorageSlideProvider
import java.io.File

class AppStorageExternalRepository(
    private val provider: AppStorageSlideProvider,
    private val hiddenManager: HiddenFileManager,
    private val durationManager: ImageDurationManager,
    private val shortcutManager: FolderShortcutManager
) : ExternalContentRepository {

    private val imageExtensions = setOf("png", "jpg", "jpeg", "webp")

    override fun listFolders(): List<ExternalFolder> {
        val folders = provider.listFolders().map {
            ExternalFolder(
                name = it.name,
                path = it.absolutePath
            )
        }

        return shortcutManager.applyPersistedShortcuts(folders)
    }

    override fun listFiles(folderPath: String): List<ExternalFile> {

        val folder = File(folderPath)

        return folder.listFiles()
            ?.filter { it.isFile }
            ?.map {
                val isImage = it.extension.lowercase() in imageExtensions
                ExternalFile(
                    name = it.name,
                    path = it.absolutePath,
                    isHidden = hiddenManager.isHidden(it.absolutePath),
                    isImage = isImage,
                    customDurationMs = if (isImage) durationManager.getDuration(it.absolutePath) else null
                )
            }
            ?.sortedWith(
                compareBy<ExternalFile> { it.isHidden }
                    .thenBy(String.CASE_INSENSITIVE_ORDER) { it.name }
            )
            ?: emptyList()
    }

    override fun deleteFile(path: String) {
        provider.deleteFile(File(path))
    }

    override fun deleteFolder(path: String) {
        provider.deleteFolder(File(path))
        shortcutManager.clearShortcut(path)
    }

    override fun setFolderShortcut(path: String, shortcutNumber: Int?) {
        shortcutManager.setShortcut(path, shortcutNumber)
    }

    override fun copyFile(sourcePath: String, targetFolderPath: String) {
        provider.duplicateFileToFolder(
            File(sourcePath),
            File(targetFolderPath)
        )
    }

    override fun moveFile(sourcePath: String, targetFolderPath: String) {
        provider.moveFileToFolder(
            File(sourcePath),
            File(targetFolderPath)
        )
    }

    override fun hideFile(path: String) {
        hiddenManager.hide(path)
    }

    override fun showFile(path: String) {
        hiddenManager.show(path)
    }

}
