package com.litvy.carteleria.domain.external.usecase

import com.litvy.carteleria.domain.external.ExternalContentRepository

class RenameExternalNodeUseCase(
    private val repository: ExternalContentRepository
) {
    operator fun invoke(path: String, newName: String) {
        repository.rename(path, newName)
    }
}