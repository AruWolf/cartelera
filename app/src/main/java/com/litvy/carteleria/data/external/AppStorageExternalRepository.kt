package com.litvy.carteleria.data.external

import com.litvy.carteleria.domain.external.*
import com.litvy.carteleria.slides.AppStorageSlideProvider
import java.io.File

class AppStorageExternalRepository(
    private val provider: AppStorageSlideProvider
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
            ?.sortedBy { it.name.lowercase() }
            ?.map {
                ExternalFile(
                    name = it.name,
                    path = it.absolutePath
                )
            }
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

    override fun rename(path: String, newName: String) {
        val file = File(path)

        val extension = file.extension
        val finalName =
            if (extension.isNotEmpty())
                "$newName.$extension"
            else
                newName

        val newFile = File(file.parent, finalName)

        if (!newFile.exists()) {
            file.renameTo(newFile)
        }
    }
}