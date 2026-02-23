package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.slides.SlideSpeed
import com.litvy.carteleria.ui.menu.MenuItemView

@Composable
fun SpeedSubMenu(
    selectedIndex: Int,
    activeSpeed: SlideSpeed
) {
    val speeds = SlideSpeed.entries

    Column(
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
            .padding(24.dp)
    ) {

        speeds.forEachIndexed { index, speed ->

            val label = when (speed) {
                SlideSpeed.SLOW -> "Lento"
                SlideSpeed.NORMAL -> "Normal"
                SlideSpeed.FAST -> "Rápido"
            }

            val isFocused = selectedIndex == index
            val isActive = speed == activeSpeed

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