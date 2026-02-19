package com.litvy.carteleria.ui.menu.SubMenues

import com.litvy.carteleria.ui.menu.MenuItemView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ExternalContentSubMenu(
    folders: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onShowQr: () -> Unit
) {

    var listState = rememberLazyListState()
    var coroutineScope = rememberCoroutineScope()


    LaunchedEffect(folders, selected) {
        val selectedIndex = folders.indexOf(selected)
        if (selectedIndex >= 0) {
            listState.scrollToItem(selectedIndex)
        }
    }

    if (folders.isEmpty()) {

        Column(
            modifier = Modifier
                .width(260.dp)
                .padding(24.dp)
        ) {

            MenuItemView(
                text = "No hay contenido externo",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuItemView(
                text = "Cargar contenido (QR)",
                onClick = { onShowQr() }
            )
        }

        return
    }


    LazyColumn(
        state = listState,
        modifier = Modifier
            .width(260.dp)
            .padding(24.dp)
    ) {
        itemsIndexed(folders) { index, folder ->

            MenuItemView(
                text = if (folder == selected) "▶ $folder" else folder,
                onClick = { onSelect(folder) },
                onFocus = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(index)
                    }
                }
            )
        }

    }
}