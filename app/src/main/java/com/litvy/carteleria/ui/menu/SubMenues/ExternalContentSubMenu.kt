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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.R
import com.litvy.carteleria.domain.external.ExternalFolder
import com.litvy.carteleria.ui.menu.ExternalMenuViewModel
import com.litvy.carteleria.ui.menu.MenuItemView
import com.litvy.carteleria.ui.navigation.ExternalNavigationController
import com.litvy.carteleria.util.DeviceUtils

@Composable
fun ExternalContentSubMenu(
    viewModel: ExternalMenuViewModel,
    navigation: ExternalNavigationController,
    isPreviewMode: Boolean,
    canImportFromDevice: Boolean,
    onImportFromFiles: () -> Unit,
    onImportFromGallery: () -> Unit,
    onCreateFolder: () -> Unit,
    onForceUsbScan: () -> Unit,
    onFolderSelected: (ExternalFolder) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val isTv = DeviceUtils.isTv(context)
    val enableTouch = !isTv

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
            var nextIndex = 0

            if (canImportFromDevice) {
                val filesIndex = nextIndex++
                item {
                    MenuItemView(
                        text = "[+] ${stringResource(R.string.import_from_files)}",
                        selected = !isPreviewMode && navigation.state.folderIndex == filesIndex,
                        onClick = {
                            navigation.setFolderIndex(filesIndex)
                            onImportFromFiles()
                        },
                        enableTouch = enableTouch
                    )
                }

                val galleryIndex = nextIndex++
                item {
                    MenuItemView(
                        text = "[+] ${stringResource(R.string.import_from_gallery)}",
                        selected = !isPreviewMode && navigation.state.folderIndex == galleryIndex,
                        onClick = {
                            navigation.setFolderIndex(galleryIndex)
                            onImportFromGallery()
                        },
                        enableTouch = enableTouch
                    )
                }
            }

            val newFolderIndex = nextIndex++
            item {
                MenuItemView(
                    text = "[+] ${stringResource(R.string.new_folder)}",
                    selected = !isPreviewMode && navigation.state.folderIndex == newFolderIndex,
                    onClick = {
                        navigation.setFolderIndex(newFolderIndex)
                        onCreateFolder()
                    },
                    enableTouch = enableTouch
                )
            }

            if (isTv) {
                val usbIndex = nextIndex++
                item {
                    MenuItemView(
                        text = "${stringResource(R.string.update_from_usb)}",
                        selected = !isPreviewMode && navigation.state.folderIndex == usbIndex,
                        onClick = {
                            navigation.setFolderIndex(usbIndex)
                            onForceUsbScan()
                        },
                        enableTouch = enableTouch
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            itemsIndexed(state.folders) { index, folder ->
                val globalIndex = index + nextIndex
                val isSelected = !isPreviewMode && navigation.state.folderIndex == globalIndex

                MenuItemView(
                    text = if (isSelected) "> ${folder.name}" else folder.name,
                    selected = isSelected,
                    trailingText = folder.shortcutNumber?.let { "[$it]" },
                    onClick = {
                        navigation.setFolderIndex(globalIndex)
                        onFolderSelected(folder)
                    },
                    enableTouch = enableTouch
                )
            }
        } else {
            var nextIndex = 0

            if (canImportFromDevice) {
                val filesIndex = nextIndex++
                item {
                    MenuItemView(
                        text = "[+] ${stringResource(R.string.import_from_files)}",
                        selected = !isPreviewMode && navigation.state.fileIndex == filesIndex,
                        onClick = {
                            navigation.setFileIndex(filesIndex)
                            onImportFromFiles()
                        },
                        enableTouch = enableTouch
                    )
                }

                val galleryIndex = nextIndex++
                item {
                    MenuItemView(
                        text = "[+] ${stringResource(R.string.import_from_gallery)}",
                        selected = !isPreviewMode && navigation.state.fileIndex == galleryIndex,
                        onClick = {
                            navigation.setFileIndex(galleryIndex)
                            onImportFromGallery()
                        },
                        enableTouch = enableTouch
                    )
                }
            }

            val hasClipboard = state.clipboardPath != null

            if (hasClipboard) {
                val pasteIndex = nextIndex++
                item {
                    val isSelected = !isPreviewMode && navigation.state.fileIndex == pasteIndex
                    val pasteHere = "[Paste] ${stringResource(R.string.paste_here)}"
                    MenuItemView(
                        text = if (isSelected) "> $pasteHere" else pasteHere,
                        selected = isSelected,
                        onClick = { navigation.setFileIndex(pasteIndex) },
                        enableTouch = enableTouch
                    )
                }
            }

            val backIndex = nextIndex++
            item {
                MenuItemView(
                    text = "< ${stringResource(R.string.back)}",
                    selected = !isPreviewMode && navigation.state.fileIndex == backIndex,
                    onClick = { navigation.setFileIndex(backIndex) },
                    enableTouch = enableTouch
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            itemsIndexed(state.files) { index, file ->
                val globalIndex = index + nextIndex
                val isSelected = !isPreviewMode && navigation.state.fileIndex == globalIndex
                val durationIndicator = if (file.isImage) {
                    val label = file.customDurationMs?.let { localizedDurationLabel(it) }
                        ?: stringResource(R.string.global)
                    "  [$label]"
                } else {
                    ""
                }

                MenuItemView(
                    text = file.name + durationIndicator,
                    selected = isSelected,
                    isHidden = file.isHidden,
                    textColor = if (file.isHidden) Color(0xFFFF5555) else Color.White,
                    onClick = { navigation.setFileIndex(globalIndex) },
                    enableTouch = enableTouch
                )
            }
        }
    }
}
