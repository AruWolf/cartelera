package com.litvy.carteleria.domain.external.usecase

data class ExternalContentUseCases(
    val listFolders: ListExternalFoldersUseCase,
    val listFiles: ListExternalFilesUseCase,
    val deleteFile: DeleteExternalFileUseCase,
    val deleteFolder: DeleteExternalFolderUseCase,
    val copyFile: CopyExternalFileUseCase,
    val moveFile: MoveExternalFileUseCase,
)
