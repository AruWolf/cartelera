package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.slides.AppStorageSlideProvider
import com.litvy.carteleria.ui.menu.MenuItemView
import com.litvy.carteleria.ui.menu.model.ClipboardItem
import com.litvy.carteleria.ui.navigation.*
import com.litvy.carteleria.ui.navigation.ContextAction.Cancel.buildContextOptions
import java.io.File

@Composable
fun ExternalContentSubMenu(
    folders: List<File>,
    storageProvider: AppStorageSlideProvider,
    navigation: ExternalNavigationController,
    clipboardItem: ClipboardItem?,
    onClipboardChange: (ClipboardItem?) -> Unit,
    onExternalContentChanged: () -> Unit,
    onSelectFolder: (File) -> Unit,
    isPreviewMode: Boolean
) {

    val navState = navigation.state
    val exploredFolder = navState.exploredFolder

    val files = exploredFolder?.listFiles()
        ?.filter { it.isFile }
        ?.sortedBy { it.name.lowercase() }
        ?: emptyList()

    Box(
        modifier = Modifier
            .width(420.dp)
            .fillMaxHeight()
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
        ) {

            // ===================================================
            // SI HAY CARPETA EXPLORADA → MOSTRAR ARCHIVOS
            // ===================================================

            if (exploredFolder != null) {

                MenuItemView(
                    text = "📁 ${exploredFolder.name}",
                    selected = false,
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (clipboardItem != null) {

                    val isSelected =
                        !isPreviewMode && navState.fileIndex == 0

                    MenuItemView(
                        text = if (isSelected)
                            "▶ 📋 Pegar aquí"
                        else
                            "📋 Pegar aquí",
                        selected = isSelected,
                        onClick = {}
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                val hasClipboard = clipboardItem != null

                val volverIndex = if (hasClipboard) 1 else 0

                MenuItemView(
                    text = "< Volver",
                    selected = !isPreviewMode && navState.fileIndex == volverIndex,
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(16.dp))

                val offset = if (clipboardItem != null) 2 else 1

                files.forEachIndexed { index, file ->

                    val globalIndex = index + offset
                    val isSelected =
                        !isPreviewMode && navState.fileIndex == globalIndex

                    MenuItemView(
                        text = if (isSelected)
                            "▶ ${file.name}"
                        else
                            file.name,
                        selected = isSelected,
                        onClick = {}
                    )
                }

            } else {

                // ===================================================
                // NO HAY CARPETA EXPLORADA → MOSTRAR CARPETAS
                // ===================================================

                MenuItemView(
                    text = "📱 Cargar contenido (QR)",
                    selected = !isPreviewMode && navState.folderIndex == 0,
                    onClick = {}
                )

                MenuItemView(
                    text = "🔄 Actualizar desde USB",
                    selected = !isPreviewMode && navState.folderIndex == 1,
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(16.dp))

                folders.forEachIndexed { index, folder ->

                    val globalIndex = index + 2
                    val isSelected =
                        !isPreviewMode && navState.folderIndex == globalIndex

                    MenuItemView(
                        text = if (isSelected)
                            "▶ ${folder.name}"
                        else
                            folder.name,
                        selected = isSelected,
                        onClick = {}
                    )
                }
            }
        }

        // ===================================================
        // CONTEXT MENU (OVERLAY)
        // ===================================================

        if (navState.level == 2 && !isPreviewMode) {

            val options = buildContextOptions(navState.contextTarget)

            Column(
                modifier = Modifier
                    .offset(x = 300.dp)
                    .width(220.dp)
                    .background(Color.Black.copy(alpha = 0.95f))
                    .padding(16.dp)
            ) {

                options.forEachIndexed { index, action ->

                    val isSelected =
                        navState.contextIndex == index

                    MenuItemView(
                        text = if (isSelected)
                            "▶ ${action.label}"
                        else
                            action.label,
                        selected = isSelected,
                        onClick = {}
                    )
                }
            }
        }
    }
}