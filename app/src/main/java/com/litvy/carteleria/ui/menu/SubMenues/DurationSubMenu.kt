package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.slides.ImageSlideDurations
import com.litvy.carteleria.ui.menu.MenuItemView

@Composable
fun DurationSubMenu(
    selectedIndex: Int,
    activeGlobalImageDurationMs: Long
) {
    val items = listOf(
        "Duraci\u00f3n personalizada: ${ImageSlideDurations.labelFor(activeGlobalImageDurationMs)}",
        "Aplicar duraci\u00f3n global a todas las im\u00e1genes"
    )

    Column(
        modifier = Modifier
            .width(340.dp)
            .fillMaxHeight()
            .padding(24.dp)
    ) {
        items.forEachIndexed { index, label ->
            val isFocused = selectedIndex == index
            MenuItemView(
                text = if (isFocused) "\u25B6 $label" else label,
                selected = isFocused,
                onClick = {}
            )
        }
    }
}
