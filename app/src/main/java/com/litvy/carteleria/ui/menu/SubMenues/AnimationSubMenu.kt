package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.ui.menu.MenuItemView

@Composable
fun AnimationSubMenu(
    selectedIndex: Int,
    activeAnimation: String
) {

    // Lista de animaciones disponibles (Depende de las que esten implementadas en TvTransitions)
    val animations = listOf(
        "random" to "Aleatorio",
        "fade" to "Fade",
        "scale" to "Scale",
        "left" to "Deslizar Izq",
        "up" to "Deslizar Arriba",
        "right" to "Deslizar Der",
        "down" to "Deslizar Abajo"
    )

    Column(
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
            .padding(24.dp)
    ) {

        animations.forEachIndexed { index, (key, label) ->

            val isFocused = selectedIndex == index
            val isActive = key == activeAnimation

            val prefix = buildString {
                if (isFocused) append("▶ ")
                if (isActive) append("✔ ")
            }

            MenuItemView(
                text = prefix + label,
                selected = isFocused,
                onClick = {}
            )
        }
    }
}