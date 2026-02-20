package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.slides.AppStorageSlideProvider
import com.litvy.carteleria.ui.menu.MenuItemView
import com.litvy.carteleria.ui.menu.model.ClipboardItem
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ExternalContentSubMenu(
    folders: List<File>,
    storageProvider: AppStorageSlideProvider,
    onContentChanged: () -> Unit,
    selected: String,
    parentFocusRequester: FocusRequester,
    onSelect: (File) -> Unit,
    onShowQr: () -> Unit,
    firstItemFocusRequester: FocusRequester,
    clipboardItem: ClipboardItem?,
    onClipboardChange: (ClipboardItem?) -> Unit,
) {

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var exploredFolder by remember { mutableStateOf<File?>(null) }

    var contextTarget by remember { mutableStateOf<File?>(null) }
    var isContextForFolder by remember { mutableStateOf(false) }
    val contextFirstItemRequester = remember { FocusRequester() }
    var consumeNextUp by remember { mutableStateOf(false) }
    var contextIndex by remember { mutableStateOf<Int?>(null) }

    // ----------------------------
    // ARCHIVOS DEL NIVEL ACTUAL
    // ----------------------------
    val files = exploredFolder?.listFiles()
        ?.filter { it.isFile }
        ?.sortedBy { it.name.lowercase() }
        ?: emptyList()

    val dynamicRequesters = remember(files.size) {
        List(files.size) { FocusRequester() }
    }

    // 🔥 Foco al entrar a carpeta
    LaunchedEffect(exploredFolder) {
        if (exploredFolder != null && dynamicRequesters.isNotEmpty()) {
            kotlinx.coroutines.delay(50)
            dynamicRequesters.first().requestFocus()
        }
    }

    // 🔥 Foco al abrir menú contextual
    LaunchedEffect(contextTarget) {
        if (contextTarget != null) {
            kotlinx.coroutines.delay(50)
            contextFirstItemRequester.requestFocus()
        }
    }

    Box {

        LazyColumn(
            state = listState,
            modifier = Modifier
                .width(260.dp)
                .fillMaxHeight()
                .padding(24.dp)
        ) {

            // =========================
            // NIVEL 1 → CARPETAS
            // =========================
            if (exploredFolder == null) {

                if (folders.isEmpty()) {
                    item {
                        MenuItemView(
                            text = "No hay contenido externo",
                            onClick = {},
                            modifier = Modifier.focusProperties {
                                left = parentFocusRequester
                            }
                        )
                    }
                }

                itemsIndexed(folders) { index, folder ->

                    MenuItemView(
                        focusRequester = if (index == 0) firstItemFocusRequester else null,
                        text = if (folder.name == selected)
                            "▶ ${folder.name}"
                        else
                            folder.name,
                        onClick = {
                            exploredFolder = folder
                        },
                        onLongPress = {
                            consumeNextUp = true
                            contextTarget = folder
                            contextIndex = index
                            isContextForFolder = true
                        },
                        onFocus = {
                            scope.launch {
                                listState.animateScrollToItem(index)
                            }
                        },
                        modifier = Modifier.focusProperties {
                            left = parentFocusRequester
                        }
                    )
                }
            }

            // =========================
            // NIVEL 2 → ARCHIVOS
            // =========================
            else {

                item {
                    MenuItemView(
                        text = "< Volver",
                        onClick = {
                            exploredFolder = null
                            parentFocusRequester.requestFocus()
                        },
                        modifier = Modifier.focusProperties {
                            left = parentFocusRequester
                        }
                    )
                }

                if (clipboardItem != null) {
                    item {
                        MenuItemView(
                            text = "📋 Pegar",
                            onClick = {

                                val targetFolder = exploredFolder!!

                                val success = if (clipboardItem.isCut) {
                                    storageProvider.moveFileToFolder(
                                        clipboardItem.file,
                                        targetFolder
                                    )
                                } else {
                                    storageProvider.duplicateFileToFolder(
                                        clipboardItem.file,
                                        targetFolder
                                    )
                                }

                                if (success && clipboardItem.isCut) {
                                    onClipboardChange(null)
                                }

                                onContentChanged()
                            }
                        )
                    }
                }

                itemsIndexed(files) { index, file ->

                    MenuItemView(
                        focusRequester = dynamicRequesters.getOrNull(index),
                        text = file.name,
                        onClick = {},
                        onLongPress = {
                            contextTarget = file
                            contextIndex = index
                            isContextForFolder = false
                        },
                        onFocus = {
                            scope.launch {
                                listState.animateScrollToItem(index + 1)
                            }
                        },
                        modifier = Modifier.focusProperties {
                            left = parentFocusRequester
                        }
                    )
                }
            }
        }

        // =========================
        // CONTEXT MENU
        // =========================
        if (contextTarget != null) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(contextFirstItemRequester)
                    .focusable()
                    .onPreviewKeyEvent { event ->
                        if (consumeNextUp &&
                            event.nativeKeyEvent.action == android.view.KeyEvent.ACTION_UP
                        ) {
                            consumeNextUp = false
                            true
                        } else false
                    }
            ) {

                Column(
                    modifier = Modifier
                        .width(260.dp)
                        .background(Color.DarkGray)
                        .padding(16.dp)
                ) {

                    if (isContextForFolder) {

                        MenuItemView(
                            text = "Reproducir carpeta",
                            focusRequester = contextFirstItemRequester,
                            onClick = {
                                onSelect(contextTarget!!)
                                contextTarget = null
                            }
                        )

                        MenuItemView(
                            text = "Eliminar carpeta",
                            onClick = {
                                storageProvider.deleteFolder(contextTarget!!)
                                contextTarget = null
                                exploredFolder = null
                                onContentChanged()
                            }
                        )

                    } else {

                        MenuItemView(
                            text = "Copiar",
                            focusRequester = contextFirstItemRequester,
                            onClick = {
                                onClipboardChange(
                                    ClipboardItem(contextTarget!!, false)
                                )
                                contextTarget = null
                            }
                        )

                        MenuItemView(
                            text = "Cortar",
                            onClick = {
                                onClipboardChange(
                                    ClipboardItem(contextTarget!!, true)
                                )
                                contextTarget = null
                            }
                        )

                        MenuItemView(
                            text = "Eliminar",
                            onClick = {

                                val removedIndex = contextIndex

                                storageProvider.deleteFile(contextTarget!!)
                                contextTarget = null
                                onContentChanged()

                                removedIndex?.let { index ->
                                    val newIndex =
                                        if (index < files.lastIndex) index
                                        else files.lastIndex

                                    dynamicRequesters
                                        .getOrNull(newIndex)
                                        ?.requestFocus()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    MenuItemView(
                        text = "Cancelar",
                        onClick = { contextTarget = null }
                    )
                }
            }
        }
    }
}