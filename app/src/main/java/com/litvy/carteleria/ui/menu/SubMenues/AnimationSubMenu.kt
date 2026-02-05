package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.ui.menu.MenuItemView

@Composable
fun AnimationSubMenu(
    selected: String,
    onSelect: (String) -> Unit
)
{

    Column(
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(Color.DarkGray)
            .padding(24.dp)
    )
    {

        listOf(
            "fade" to "Fundido",
            "scale" to "Escala",
            "left" to "Deslizar Izq",
            "up" to "Deslizar Arriba"
        ).forEach { (key, label) ->
            MenuItemView(
                text = if (key == selected) "✔ $label" else label,
                onClick = { onSelect(key) }
            )
    }
}
}