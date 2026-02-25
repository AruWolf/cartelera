package com.litvy.carteleria.domain.external.usecase

import com.litvy.carteleria.domain.external.ExternalContentRepository
import com.litvy.carteleria.domain.external.ExternalFile

class ListExternalFilesUseCase(
    private val repository: ExternalContentRepository
) {
    operator fun invoke(folderPath: String): List<ExternalFile> {
        return repository.listFiles(folderPath)
    }
}