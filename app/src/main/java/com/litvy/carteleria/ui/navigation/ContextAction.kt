package com.litvy.carteleria.ui.navigation

// Clase utilizada para contextualizar el menu de acciones de archivos y carpetas
sealed class ContextAction(val label: String) {

    object Cancel : ContextAction("Cancelar")
    object Delete : ContextAction("Eliminar")

    // Carpetas
    object OpenFolder : ContextAction("Abrir")
    object PlayFolder : ContextAction("Reproducir")

    // Archivos
    object Preview : ContextAction("Previsualizar")
    object Copy : ContextAction("Copiar")
    object Cut : ContextAction("Cortar")

    object Hide : ContextAction("Ocultar")
    object Show : ContextAction("Mostrar")
}