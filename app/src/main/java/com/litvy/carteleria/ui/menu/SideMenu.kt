package com.litvy.carteleria.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SideMenu(
    onAction: (MenuAction) -> Unit,
    onClose: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    // Pedimos foco al abrir
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color.Black.copy(alpha = 0.75f))
            .focusRequester(focusRequester)
            .focusable()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            MenuItemView(
                text = "Animación",
                onClick = { onAction(MenuAction.ChangeAnimation) }
            )
            MenuItemView(
                text = "Velocidad",
                onClick = { onAction(MenuAction.ChangeSpeed) }
            )
            MenuItemView(
                text = "Reiniciar",
                onClick = { onAction(MenuAction.Restart) }
            )
        }
    }
}
