package com.litvy.carteleria.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Controlador de las posiciones de navegación dentro del menu.
// *Cada sección de opciones esta asociada a un indice padre, para que,
//  en caso de volver hacia atras se posiciones el foco sobre la sección padre.
class TvNavigationController {

    var state by mutableStateOf(NavigationState())
        private set

    // --- MAIN MENU ---

    // Seteo de posición del indice al desplazarse dentro del menu.
    fun moveMainUp() {
        state = state.copy(
            mainIndex = (state.mainIndex - 1).coerceAtLeast(0)
        )
    }

    fun moveMainDown(max: Int) {
        state = state.copy(
            mainIndex = (state.mainIndex + 1).coerceAtMost(max)
        )
    }

    // --- SUBMENU ---

    // Seteo de posición del indice al desplazarse dentro de un submenu.
    fun enterSubMenu(section: FocusSection) {
        state = state.copy(
            section = section,
            subIndex = 0
        )
    }

    fun moveSubUp() {
        state = state.copy(
            subIndex = (state.subIndex - 1).coerceAtLeast(0)
        )
    }

    fun moveSubDown(max: Int) {
        state = state.copy(
            subIndex = (state.subIndex + 1).coerceAtMost(max)
        )
    }

    // Conexión al indice padre.
    fun backToMain() {
        state = state.copy(
            section = FocusSection.MAIN_MENU
        )
    }

    // -------- CONTEXT MENU --------

    // Seteo de posición del indice al desplazarse dentro del menu contextual.
    fun openContextMenu() {
        state = state.copy(
            section = FocusSection.CONTEXT_MENU,
            contextIndex = 0
        )
    }

    fun moveContextUp() {
        state = state.copy(
            contextIndex = (state.contextIndex - 1).coerceAtLeast(0)
        )
    }

    fun moveContextDown(max: Int) {
        state = state.copy(
            contextIndex = (state.contextIndex + 1).coerceAtMost(max)
        )
    }

    // Conexión con la sección padre.
    fun closeContextMenu() {
        state = state.copy(
            section = FocusSection.SUBMENU_EXTERNAL
        )
    }

    fun syncSectionWithMain() {
        state = state.copy(
            section = when (state.mainIndex) {
                0 -> FocusSection.SUBMENU_CONTENT
                1 -> FocusSection.SUBMENU_EXTERNAL
                2 -> FocusSection.SUBMENU_ANIMATION
                3 -> FocusSection.SUBMENU_SPEED
                else -> FocusSection.MAIN_MENU
            },
            subIndex = 0
        )
    }

}