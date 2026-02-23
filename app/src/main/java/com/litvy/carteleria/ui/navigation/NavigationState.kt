package com.litvy.carteleria.ui.navigation

// Indice de posiciones de foco dentro de cada sección del menu.
data class NavigationState(
    val section: FocusSection = FocusSection.MAIN_MENU,
    val mainIndex: Int = 0,
    val subIndex: Int = 0,
    val contextIndex: Int = 0
)

