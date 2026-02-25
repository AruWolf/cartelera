package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.ui.menu.MenuItemView
import com.litvy.carteleria.ui.menu.external.ExternalMenuViewModel
import com.litvy.carteleria.ui.navigation.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

@Composable
fun ExternalContentSubMenu(
    viewModel: ExternalMenuViewModel,
    navigation: ExternalNavigationController,
    isPreviewMode: Boolean,
    onPlayFolder: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
            .padding(24.dp)
    ) {

        if (!state.isInFolder) {

            // ---------- OPCIONES FIJAS ----------
            MenuItemView(
                text = "📱 Cargar contenido (QR)",
                selected = !isPreviewMode && navigation.state.folderIndex == 0,
                onClick = {}
            )

            MenuItemView(
                text = "🔄 Actualizar desde USB",
                selected = !isPreviewMode && navigation.state.folderIndex == 1,
                onClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---------- CARPETAS ----------
            state.folders.forEachIndexed { index, folder ->

                val globalIndex = index + 2

                val isSelected =
                    !isPreviewMode &&
                            navigation.state.folderIndex == globalIndex

                MenuItemView(
                    text = if (isSelected)
                        "▶ ${folder.name}"
                    else
                        folder.name,
                    selected = isSelected,
                    onClick = {}
                )
            }

        } else {

            val hasClipboard = state.clipboardPath != null

            if (hasClipboard) {
                MenuItemView(
                    text = if (!isPreviewMode && navigation.state.fileIndex == 0)
                        "▶ 📋 Pegar aquí"
                    else
                        "📋 Pegar aquí",
                    selected = !isPreviewMode && navigation.state.fileIndex == 0,
                    onClick = {}
                )
            }

            val backIndex = if (hasClipboard) 1 else 0

            MenuItemView(
                text = "< Volver",
                selected = !isPreviewMode &&
                        navigation.state.fileIndex == backIndex,
                onClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            val offset = if (hasClipboard) 2 else 1

            state.files.forEachIndexed { index, file ->

                val globalIndex = index + offset

                val isSelected =
                    !isPreviewMode &&
                            navigation.state.fileIndex == globalIndex

                MenuItemView(
                    text = if (isSelected)
                        "▶ ${file.name}"
                    else
                        file.name,
                    selected = isSelected,
                    onClick = {}
                )
            }
        }
    }
}