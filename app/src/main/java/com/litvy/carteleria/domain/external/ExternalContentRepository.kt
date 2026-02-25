package com.litvy.carteleria.domain.external

interface ExternalContentRepository {

    fun listFolders(): List<ExternalFolder>

    fun listFiles(folderPath: String): List<ExternalFile>

    fun deleteFile(path: String)

    fun deleteFolder(path: String)

    fun copyFile(sourcePath: String, targetFolderPath: String)

    fun moveFile(sourcePath: String, targetFolderPath: String)

    fun hideFile(path: String)

    fun showFile(path: String)
}