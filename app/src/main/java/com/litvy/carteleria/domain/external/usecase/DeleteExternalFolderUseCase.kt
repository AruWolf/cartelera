package com.litvy.carteleria.domain.external.usecase

import com.litvy.carteleria.domain.external.ExternalContentRepository

class DeleteExternalFolderUseCase(
    private val repository: ExternalContentRepository
) {
    operator fun invoke(path: String) {
        repository.deleteFolder(path)
    }
}