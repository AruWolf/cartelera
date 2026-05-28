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

    // Manejo de posición del indice al desplazarse dentro del menu.
    fun moveMainUp() {
        state = state.copy(
            mainIndex = (state.mainIndex - 1).coerceAtLeast(0) // Decrece el indice para subir
        )
    }

    fun moveMainDown(max: Int) {
        state = state.copy(
            mainIndex = (state.mainIndex + 1).coerceAtMost(max) // Incrementa el indice para bajar
        )
    }

    // --- SUBMENU ---

    // Seteo de posición del indice al desplazarse dentro de un submenu.

    // Inicialización de posición del indice al entrar en un submenu.
    fun enterSubMenu(section: FocusSection) {
        state = state.copy(
            section = section,
            subIndex = 0
        )
    }

    fun moveSubUp() {
        state = state.copy(
            subIndex = (state.subIndex - 1).coerceAtLeast(0) // Decrece el indice para subir
        )
    }

    fun moveSubDown(max: Int) {
        state = state.copy(
            subIndex = (state.subIndex + 1).coerceAtMost(max) // Incrementa el indice para bajar
        )
    }

    // Conexión al indice padre al volver hacia atras.
    fun backToMain() {
        state = state.copy(
            section = FocusSection.MAIN_MENU
        )
    }

}