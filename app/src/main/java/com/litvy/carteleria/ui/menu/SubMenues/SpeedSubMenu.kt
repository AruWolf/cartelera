package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import com.litvy.carteleria.slides.SlideSpeed
import com.litvy.carteleria.ui.menu.MenuItemView

@Composable
fun SpeedSubMenu(
    selected: SlideSpeed,
    parentFocusRequester: FocusRequester,
    onSelect: (SlideSpeed) -> Unit
) {

    Column {
        SlideSpeed.values().forEach { speed ->

            val label = when (speed) {
                SlideSpeed.SLOW -> "Lento"
                SlideSpeed.NORMAL -> "Normal"
                SlideSpeed.FAST -> "Rápido"
            }

            MenuItemView(
                text = if (speed == selected) "▶ $label" else label,
                onClick = { onSelect(speed) },
                modifier = Modifier.focusProperties {
                    left = parentFocusRequester
                }
            )
        }
    }
}