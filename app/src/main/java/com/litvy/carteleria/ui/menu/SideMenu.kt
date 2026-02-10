package com.litvy.carteleria.ui.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.ui.menu.SubMenues.AnimationSubMenu
import com.litvy.carteleria.ui.menu.SubMenues.ContentSubMenu
import com.litvy.carteleria.ui.menu.SubMenues.ExternalContentSubMenu

@Composable
fun SideMenu(
    currentAnimation: String,
    folders: List<String>,
    externalFolders: List<String>,
    currentFolder: String,
    currentExternalFolder: String,
    onAnimationSelected: (String) -> Unit,
    onFolderSelected: (String) -> Unit,
    onExternalFolderSelected: (String) -> Unit,
    onPickExternalFolder: () -> Unit,
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
                text = "Contenido",
                onClick = { subMenu = SubMenu.CONTENT }
            )

            MenuItemView(
                text = "Contenido Externo",
                onClick = { subMenu = SubMenu.EXTERNAL_CONTENT }
            )

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

        AnimatedVisibility(
            visible = subMenu == SubMenu.CONTENT
        ) {
            ContentSubMenu(
                folders = folders,
                selected = currentFolder,
                onSelect = {
                    onFolderSelected(it)
                    subMenu = SubMenu.NONE
                }
            )
        }

        AnimatedVisibility(
            visible = subMenu == SubMenu.EXTERNAL_CONTENT
        ) {
            ExternalContentSubMenu(
                folders = externalFolders,
                selected = currentExternalFolder,
                onSelect = {
                    onExternalFolderSelected(it)
                    subMenu = SubMenu.NONE
                },
                onPickFolder = onPickExternalFolder
            )
        }




    }
}
