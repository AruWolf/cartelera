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
fun ContentSubMenu(
    folders: List<String>,
    selectedIndex: Int,
    activeFolder: String
) {

    Column(
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
            .padding(24.dp)
    ) {

        folders.forEachIndexed { index, folder ->

            val isFocused = selectedIndex == index
            val isActive = folder == activeFolder

            val prefix = buildString {
                if (isFocused) append("▶ ")
                if (isActive) append("✔ ")
            }

            MenuItemView(
                text = prefix + folder,
                selected = isFocused,
                onClick = {}
            )
        }
    }
}