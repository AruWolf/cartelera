package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.ui.menu.MenuItemView
import kotlinx.coroutines.launch

@Composable
fun ContentSubMenu(
    folders: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(folders, selected) {
        val selectedIndex = folders.indexOf(selected)
        if (selectedIndex >= 0) {
            listState.scrollToItem(selectedIndex)
        }
    }

    if (folders.isEmpty()) {
        Column(
            modifier = Modifier
                .width(240.dp)
                .fillMaxHeight()
                .padding(24.dp)
        ) {
            MenuItemView(
                text = "No hay carpetas disponibles",
                onClick = {}
            )
        }
        return
    }


    LazyColumn(
        state = listState,
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
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
