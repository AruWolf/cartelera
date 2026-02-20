package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.ui.menu.MenuItemView
import kotlinx.coroutines.launch

@Composable
fun ExternalContentSubMenu(
    folders: List<String>,
    selected: String,
    parentFocusRequester: FocusRequester,
    onSelect: (String) -> Unit,
    onShowQr: () -> Unit,
    firstItemFocusRequester: FocusRequester
) {

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight()
            .padding(24.dp)
    ) {

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

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                MenuItemView(
                    text = "Cargar contenido (QR)",
                    onClick = { onShowQr() },
                    modifier = Modifier.focusProperties {
                        left = parentFocusRequester
                    }
                )
            }
        } else {

            itemsIndexed(folders) { index, folder ->
                MenuItemView(
                    focusRequester = if (index == 0) firstItemFocusRequester else null,
                    text = if (folder == selected) "▶ $folder" else folder,
                    onClick = { onSelect(folder) },
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
    }
}