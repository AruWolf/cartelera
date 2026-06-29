package com.litvy.carteleria.domain.external.usecase

import com.litvy.carteleria.domain.external.ExternalContentRepository
import com.litvy.carteleria.domain.external.ExternalFolder

class CreateExternalFolderUseCase(
    private val repository: ExternalContentRepository
) {
    operator fun invoke(name: String): ExternalFolder? {
        return repository.createFolder(name)
    }
}
