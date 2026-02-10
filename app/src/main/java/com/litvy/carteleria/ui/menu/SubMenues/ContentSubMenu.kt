package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.litvy.carteleria.ui.menu.MenuItemView

@Composable
fun ContentSubMenu(
    folders: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column {
        folders.forEach { folder ->
            MenuItemView(
                text = if (folder == selected) "▶ $folder" else folder,
                onClick = { onSelect(folder) }
            )
        }
    }
}
