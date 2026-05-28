package com.litvy.carteleria.domain.external.usecase

import com.litvy.carteleria.domain.external.ExternalContentRepository

class SetFolderShortcutUseCase(
    private val repository: ExternalContentRepository
) {
    operator fun invoke(path: String, shortcutNumber: Int?) {
        repository.setFolderShortcut(path, shortcutNumber)
    }
}
