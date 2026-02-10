package com.litvy.carteleria.ui.menu.SubMenues

import com.litvy.carteleria.ui.menu.MenuItemView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExternalContentSubMenu(
    folders: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onPickFolder: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(260.dp)
            .padding(24.dp)
    ) {

        MenuItemView(
            text = "Seleccionar carpeta USB",
            onClick = onPickFolder
        )

        Spacer(Modifier.height(16.dp))

        folders.forEach { folder ->
            MenuItemView(
                text = folder,
                onClick = { onSelect(folder) }
            )
        }
    }
}