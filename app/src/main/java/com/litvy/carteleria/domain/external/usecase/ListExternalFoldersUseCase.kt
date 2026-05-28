package com.litvy.carteleria.domain.external.usecase

import com.litvy.carteleria.domain.external.ExternalContentRepository
import com.litvy.carteleria.domain.external.ExternalFolder

class ListExternalFoldersUseCase(
    private val repository: ExternalContentRepository
) {
    operator fun invoke(): List<ExternalFolder> {
        return repository.listFolders()
    }
}