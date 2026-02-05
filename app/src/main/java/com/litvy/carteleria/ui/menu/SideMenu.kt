package com.litvy.carteleria.ui.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.ui.menu.SubMenues.AnimationSubMenu

@Composable
fun SideMenu(
    currentAnimation: String,
    onAnimationSelected: (String) -> Unit,
    onClose: () -> Unit
) {
    var subMenu by remember { mutableStateOf(SubMenu.NONE) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
    ) {

        Column(
            modifier = Modifier
                .width(260.dp)
                .padding(24.dp)
        ) {
            MenuItemView(
                text = "Animación",
                onClick = { subMenu = SubMenu.ANIMATION }
            )

            MenuItemView(
                text = "Velocidad",
                onClick = { /* futuro */ }
            )

            MenuItemView(
                text = "Cerrar",
                onClick = onClose
            )
        }

        AnimatedVisibility(
            visible = subMenu == SubMenu.ANIMATION
        ) {
            AnimationSubMenu(
                selected = currentAnimation,
                onSelect = {
                    onAnimationSelected(it)
                    subMenu = SubMenu.NONE
                }
            )
        }
    }
}
