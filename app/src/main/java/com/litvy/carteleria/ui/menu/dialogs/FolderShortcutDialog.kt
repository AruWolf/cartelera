package com.litvy.carteleria.ui.menu

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.litvy.carteleria.R
import com.litvy.carteleria.data.external.FolderShortcutManager
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed

@Composable
fun FolderShortcutDialog(
    currentShortcut: Int?,
    onShortcutSelected: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val options = remember { listOf<Int?>(null) + FolderShortcutManager.VALID_SHORTCUTS.toList() }
    val listState = rememberLazyListState()
    var selectedIndex by remember {
        mutableIntStateOf(options.indexOf(currentShortcut).takeIf { it >= 0 } ?: 0)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(selectedIndex) {
        val visibleItems = listState.layoutInfo.visibleItemsInfo

        if (visibleItems.isEmpty()) return@LaunchedEffect

        val first = visibleItems.first().index
        val last = visibleItems.last().index

        when {
            selectedIndex < (first + 1) ->
                listState.animateScrollToItem(selectedIndex)

            selectedIndex > (last - 1) ->
                listState.animateScrollToItem(selectedIndex)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp),
            modifier = Modifier
                .width(260.dp)
                .background(Color.Black.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent { event ->
                    val native = event.nativeKeyEvent

                    if (native.action != KeyEvent.ACTION_DOWN || native.repeatCount > 0) {
                        return@onPreviewKeyEvent false
                    }

                    when (native.keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            selectedIndex = (selectedIndex - 1).coerceAtLeast(0)
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            selectedIndex = (selectedIndex + 1).coerceAtMost(options.lastIndex)
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER,
                        KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                            onShortcutSelected(options[selectedIndex])
                            true
                        }

                        KeyEvent.KEYCODE_BACK,
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            onDismiss()
                            true
                        }

                        else -> false
                    }
                }
        ) {
            itemsIndexed(options) { index, option ->

                val isSelected = selectedIndex == index
                val label = option?.toString() ?: stringResource(R.string.no_shortcut)

                MenuItemView(
                    text = if (isSelected) "\u25B6 $label" else label,
                    selected = isSelected,
                    onClick = {
                        onShortcutSelected(option)
                    }
                )
            }
        }
    }
}
