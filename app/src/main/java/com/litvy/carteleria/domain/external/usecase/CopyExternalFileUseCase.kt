package com.litvy.carteleria.domain.external.usecase

import com.litvy.carteleria.domain.external.ExternalContentRepository

class CopyExternalFileUseCase(
    private val repository: ExternalContentRepository
) {
    operator fun invoke(sourcePath: String, targetFolderPath: String) {
        repository.copyFile(sourcePath, targetFolderPath)
    }
}