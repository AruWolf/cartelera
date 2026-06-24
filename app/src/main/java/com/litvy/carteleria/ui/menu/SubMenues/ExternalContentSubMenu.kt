package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.R
import com.litvy.carteleria.ui.menu.ExternalMenuViewModel
import com.litvy.carteleria.ui.menu.MenuItemView
import com.litvy.carteleria.ui.navigation.ExternalNavigationController

@Composable
fun ExternalContentSubMenu(
    viewModel: ExternalMenuViewModel,
    navigation: ExternalNavigationController,
    isPreviewMode: Boolean
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
                    text = "\uD83D\uDCF1 ${stringResource(R.string.load_content_qr)}",
                    selected = !isPreviewMode && navigation.state.folderIndex == 0,
                    onClick = {}
                )
            }

            item {
                MenuItemView(
                    text = "\uD83D\uDD04 ${stringResource(R.string.update_from_usb)}",
                    selected = !isPreviewMode && navigation.state.folderIndex == 1,
                    onClick = {}
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            itemsIndexed(state.folders) { index, folder ->
                val globalIndex = index + 2
                val isSelected = !isPreviewMode && navigation.state.folderIndex == globalIndex

                MenuItemView(
                    text = if (isSelected) "\u25B6 ${folder.name}" else folder.name,
                    selected = isSelected,
                    trailingText = folder.shortcutNumber?.let { "[$it]" },
                    onClick = {}
                )
            }
        } else {
            val hasClipboard = state.clipboardPath != null

            if (hasClipboard) {
                item {
                    val isSelected = !isPreviewMode && navigation.state.fileIndex == 0
                    val pasteHere = "\uD83D\uDCCB ${stringResource(R.string.paste_here)}"
                    MenuItemView(
                        text = if (isSelected) "\u25B6 $pasteHere" else pasteHere,
                        selected = isSelected,
                        onClick = {}
                    )
                }
            }

            val backIndex = if (hasClipboard) 1 else 0

            item {
                MenuItemView(
                    text = "< ${stringResource(R.string.back)}",
                    selected = !isPreviewMode && navigation.state.fileIndex == backIndex,
                    onClick = {}
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            itemsIndexed(state.files) { index, file ->
                val offset = if (hasClipboard) 2 else 1
                val globalIndex = index + offset
                val isSelected = !isPreviewMode && navigation.state.fileIndex == globalIndex
                val durationIndicator = if (file.isImage) {
                    val label = file.customDurationMs?.let { localizedDurationLabel(it) } ?: stringResource(R.string.global)
                    "  \u23F1 $label"
                } else {
                    ""
                }

                MenuItemView(
                    text = file.name + durationIndicator,
                    selected = isSelected,
                    isHidden = file.isHidden,
                    textColor = if (file.isHidden) Color(0xFFFF5555) else Color.White,
                    onClick = {}
                )
            }
        }
    }
}