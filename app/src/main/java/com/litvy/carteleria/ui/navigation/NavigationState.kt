package com.litvy.carteleria.ui.navigation

// Indice de posiciones de foco dentro de cada sección del menu.
data class NavigationState(
    val section: FocusSection = FocusSection.MAIN_MENU, // Seccion actual
    val mainIndex: Int = 0, // Indice de la opcion seleccionada en el menu principal
    val subIndex: Int = 0, // Indice de la opcion seleccionada en el subMenu
)

