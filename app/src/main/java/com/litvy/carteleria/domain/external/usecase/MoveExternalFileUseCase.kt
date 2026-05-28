package com.litvy.carteleria.domain.external.usecase

import com.litvy.carteleria.domain.external.ExternalContentRepository

class MoveExternalFileUseCase(
    private val repository: ExternalContentRepository
) {
    operator fun invoke(sourcePath: String, targetFolderPath: String) {
        repository.moveFile(sourcePath, targetFolderPath)
    }
}