package com.litvy.carteleria.ui.navigation

// Clase utilizada para contextualizar el menu de acciones de archivos y carpetas
sealed class ContextAction {

    object Cancel : ContextAction()
    object Delete : ContextAction()

    // Carpetas
    object OpenFolder : ContextAction()
    object PlayFolder : ContextAction()
    object NumericShortcut : ContextAction()
    object ApplyGlobalDuration : ContextAction()

    // Archivos
    object Preview : ContextAction()
    object Duration : ContextAction()
    object Copy : ContextAction()
    object Cut : ContextAction()

    object Hide : ContextAction()
    object Show : ContextAction()
}