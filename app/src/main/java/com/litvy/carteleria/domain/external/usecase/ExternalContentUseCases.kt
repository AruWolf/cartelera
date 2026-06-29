package com.litvy.carteleria.domain.external.usecase

data class ExternalContentUseCases(
    val listFolders: ListExternalFoldersUseCase,
    val listFiles: ListExternalFilesUseCase,
    val createFolder: CreateExternalFolderUseCase,
    val deleteFile: DeleteExternalFileUseCase,
    val deleteFolder: DeleteExternalFolderUseCase,
    val setFolderShortcut: SetFolderShortcutUseCase,
    val copyFile: CopyExternalFileUseCase,
    val moveFile: MoveExternalFileUseCase,
    val hideFile: HideExternalFileUseCase,
    val showFile: ShowExternalFileUseCase
)
