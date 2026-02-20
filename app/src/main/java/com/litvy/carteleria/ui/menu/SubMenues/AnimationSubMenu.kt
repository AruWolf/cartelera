package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
fun AnimationSubMenu(
    selected: String,
    parentFocusRequester: FocusRequester,
    onSelect: (String) -> Unit
) {

    val animations = listOf(
        "random" to "Aleatorio",
        "fade" to "fade",
        "scale" to "scale",
        "left" to "Deslizar Izq",
        "up" to "Deslizar Arriba",
        "right" to "Deslizar Der",
        "down" to "Deslizar Abajo"
    )

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
            .padding(24.dp)
    ) {
        itemsIndexed(animations) { index, (key, label) ->
            MenuItemView(
                text = if (key == selected) "▶ $label" else label,
                onClick = { onSelect(key) },
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