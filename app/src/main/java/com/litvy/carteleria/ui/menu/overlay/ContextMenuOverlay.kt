package com.litvy.carteleria.ui.menu.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.ui.menu.MenuItemView
import com.litvy.carteleria.ui.navigation.ContextAction
import com.litvy.carteleria.ui.navigation.ContextTarget

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

    Column(
        modifier = Modifier
            .align(Alignment.CenterStart)
            .offset(x = 340.dp)
            .width(220.dp)
            .background(Color.Black.copy(alpha = 0.95f))
            .padding(16.dp)
    ) {

        options.forEachIndexed { index, action ->

            val isSelected = state.selectedIndex == index

            MenuItemView(
                text = if (isSelected)
                    "▶ ${action.label}"
                else
                    action.label,
                selected = isSelected,
                onClick = {}
            )
        }
    }
}