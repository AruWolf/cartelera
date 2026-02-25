package com.litvy.carteleria.data.external

import com.litvy.carteleria.domain.external.*
import com.litvy.carteleria.slides.AppStorageSlideProvider
import java.io.File

class AppStorageExternalRepository(
    private val provider: AppStorageSlideProvider,
    private val hiddenManager: HiddenFileManager
) : ExternalContentRepository {

    override fun listFolders(): List<ExternalFolder> {
        return provider.listFolders().map {
            ExternalFolder(
                name = it.name,
                path = it.absolutePath
            )
        }
    }

    override fun listFiles(folderPath: String): List<ExternalFile> {

        val folder = File(folderPath)

        return folder.listFiles()
            ?.filter { it.isFile }
            ?.map {
                ExternalFile(
                    name = it.name,
                    path = it.absolutePath,
                    isHidden = hiddenManager.isHidden(it.absolutePath)
                )
            }
            ?.sortedWith(
                compareBy<ExternalFile> { it.isHidden }
                    .thenBy { it.name.lowercase() }
            )
            ?: emptyList()
    }

    override fun deleteFile(path: String) {
        provider.deleteFile(File(path))
    }

    override fun deleteFolder(path: String) {
        provider.deleteFolder(File(path))
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