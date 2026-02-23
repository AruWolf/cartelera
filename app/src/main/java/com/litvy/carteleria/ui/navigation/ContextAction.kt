package com.litvy.carteleria.ui.navigation

sealed class ContextAction(val label: String) {

    object Cancel : ContextAction("Cancelar")
    object Delete : ContextAction("Eliminar")
    object Rename : ContextAction("Renombrar")

    // Carpetas
    object OpenFolder : ContextAction("Abrir")
    object PlayFolder : ContextAction("Reproducir")

    // Archivos
    object Preview : ContextAction("Previsualizar")
    object Copy : ContextAction("Copiar")
    object Cut : ContextAction("Cortar")

    fun buildContextOptions(target: ContextTarget?): List<ContextAction> {

        return when (target) {

            is ContextTarget.Folder -> listOf(
                ContextAction.OpenFolder,
                ContextAction.PlayFolder,
                ContextAction.Rename,
                ContextAction.Delete,
                ContextAction.Cancel
            )

            is ContextTarget.FileItem -> listOf(
                ContextAction.Preview,
                ContextAction.Rename,
                ContextAction.Copy,
                ContextAction.Cut,
                ContextAction.Delete,
                ContextAction.Cancel
            )

            null -> emptyList()
        }
    }

}