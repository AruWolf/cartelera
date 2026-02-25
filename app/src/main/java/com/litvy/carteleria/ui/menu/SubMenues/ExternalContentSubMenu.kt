package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.ui.menu.MenuItemView
import com.litvy.carteleria.ui.menu.external.ExternalMenuViewModel
import com.litvy.carteleria.ui.navigation.*
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color

@Composable
fun ExternalContentSubMenu(
    viewModel: ExternalMenuViewModel,
    navigation: ExternalNavigationController,
    isPreviewMode: Boolean,
    onPlayFolder: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()


    LaunchedEffect(
        navigation.state.folderIndex,
        navigation.state.fileIndex,
        state.isInFolder
    ) {
        if (!state.isInFolder) {
            listState.animateScrollToItem(navigation.state.folderIndex)
        } else {
            listState.animateScrollToItem(navigation.state.fileIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
            .padding(24.dp)
    ) {

        if (!state.isInFolder) {

            item {
                MenuItemView(
                    text = "📱 Cargar contenido (QR)",
                    selected = !isPreviewMode && navigation.state.folderIndex == 0,
                    onClick = {}
                )
            }

            item {
                MenuItemView(
                    text = "🔄 Actualizar desde USB",
                    selected = !isPreviewMode && navigation.state.folderIndex == 1,
                    onClick = {}
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            items(state.folders.size) { index ->

                val folder = state.folders[index]
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
                item {
                    MenuItemView(
                        text = if (!isPreviewMode && navigation.state.fileIndex == 0)
                            "▶ 📋 Pegar aquí"
                        else
                            "📋 Pegar aquí",
                        selected = !isPreviewMode && navigation.state.fileIndex == 0,
                        onClick = {}
                    )
                }
            }

            val backIndex = if (hasClipboard) 1 else 0

            item {
                MenuItemView(
                    text = "< Volver",
                    selected = !isPreviewMode &&
                            navigation.state.fileIndex == backIndex,
                    onClick = {}
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            val offset = if (hasClipboard) 2 else 1

            items(state.files.size) { index ->

                val file = state.files[index]
                val globalIndex = index + offset

                val isSelected =
                    !isPreviewMode &&
                            navigation.state.fileIndex == globalIndex

                MenuItemView(
                    text = file.name.replace("▶ ", ""),
                    selected = isSelected,
                    isHidden = file.isHidden,
                    textColor = if (file.isHidden)
                        Color(0xFFFF5555)
                    else
                        Color.White,
                    onClick = {}
                )
            }
        }
    }
}