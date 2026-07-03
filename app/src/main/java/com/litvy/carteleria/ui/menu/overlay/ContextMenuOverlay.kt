package com.litvy.carteleria.ui.menu.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.R
import com.litvy.carteleria.ui.menu.MenuItemView
import com.litvy.carteleria.ui.navigation.ContextAction
import com.litvy.carteleria.ui.navigation.ContextTarget
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect

data class ContextMenuState(
    val isVisible: Boolean = false,
    val target: ContextTarget? = null,
    val selectedIndex: Int = 0
)

@Composable
fun BoxScope.ContextMenuOverlay(
    state: ContextMenuState,
    options: List<ContextAction>,
    onActionSelected: (ContextAction) -> Unit
) {

    val listState = rememberLazyListState()

    LaunchedEffect(state.selectedIndex) {
        listState.animateScrollToItem(state.selectedIndex)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .align(Alignment.CenterStart)
            .offset(x = 340.dp)
            .width(220.dp)
            .background(Color.Black.copy(alpha = 0.95f))
            .padding(16.dp)
    ) {

        itemsIndexed(options) { index, action ->

            val isSelected = state.selectedIndex == index
            val label = action.localizedLabel()

            MenuItemView(
                text = if (isSelected) "\u25B6 $label" else label,
                selected = isSelected,
                onClick = {}
            )
        }
    }
}

@Composable
private fun ContextAction.localizedLabel(): String {
    return when (this) {
        ContextAction.Cancel -> stringResource(R.string.context_cancel)
        ContextAction.Delete -> stringResource(R.string.context_delete)
        ContextAction.OpenFolder -> stringResource(R.string.context_open_folder)
        ContextAction.PlayFolder -> stringResource(R.string.context_play_folder)
        ContextAction.NumericShortcut -> stringResource(R.string.context_numeric_shortcut)
        ContextAction.ApplyGlobalDuration -> stringResource(R.string.context_apply_global_duration)
        ContextAction.Duration -> stringResource(R.string.context_duration)
        ContextAction.Copy -> stringResource(R.string.context_copy)
        ContextAction.Cut -> stringResource(R.string.context_cut)
        ContextAction.Hide -> stringResource(R.string.context_hide)
        ContextAction.Show -> stringResource(R.string.context_show)
    }
}