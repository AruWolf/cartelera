package com.litvy.carteleria.ui.menu

import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.slides.SlideSpeed
import com.litvy.carteleria.ui.menu.SubMenues.AnimationSubMenu
import com.litvy.carteleria.ui.menu.SubMenues.ContentSubMenu
import com.litvy.carteleria.ui.menu.SubMenues.ExternalContentSubMenu
import com.litvy.carteleria.ui.menu.SubMenues.SpeedSubMenu

@Composable
fun SideMenu(
    currentAnimation: String,
    currentSpeed: SlideSpeed,
    folders: List<String>,
    externalFolders: List<String>,
    currentFolder: String,
    currentExternalFolder: String,
    onAnimationSelected: (String) -> Unit,
    onSpeedSelected: (SlideSpeed) -> Unit,
    onFolderSelected: (String) -> Unit,
    onExternalFolderSelected: (String) -> Unit,
    onPickExternalFolder: () -> Unit,
    onShowQr: () -> Unit,
    onClose: () -> Unit
) {

    var subMenu by remember { mutableStateOf(SubMenu.NONE) }

    val firstItemFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        firstItemFocusRequester.requestFocus()
        subMenu = SubMenu.CONTENT
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .onPreviewKeyEvent { event ->
                if (event.nativeKeyEvent.action == KeyEvent.ACTION_UP &&
                    event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_BACK
                ) {
                    onClose()
                    true
                } else {
                    false
                }
            }
    ) {

        Column(
            modifier = Modifier
                .width(260.dp)
                .padding(24.dp)
        ) {

            MenuItemView(
                text = "Contenido",
                onFocus = { subMenu = SubMenu.CONTENT },
                onClick = {},
                focusRequester = firstItemFocusRequester
            )

            MenuItemView(
                text = "Contenido Externo",
                onFocus = { subMenu = SubMenu.EXTERNAL_CONTENT },
                onClick = {}
            )

            MenuItemView(
                text = "Animación",
                onFocus = { subMenu = SubMenu.ANIMATION },
                onClick = {}
            )

            MenuItemView(
                text = "Velocidad",
                onFocus = { subMenu = SubMenu.SPEED },
                onClick = {}
            )

            MenuItemView(
                text = "Cargar contenido (QR)",
                onFocus = { subMenu = SubMenu.NONE },
                onClick = { onShowQr() }
            )

            MenuItemView(
                text = "Cerrar",
                onFocus = { subMenu = SubMenu.NONE },
                onClick = onClose
            )
        }

        AnimatedVisibility(visible = subMenu == SubMenu.ANIMATION) {
            AnimationSubMenu(
                selected = currentAnimation,
                onSelect = {
                    onAnimationSelected(it)
                    subMenu = SubMenu.NONE
                }
            )
        }

        AnimatedVisibility(visible = subMenu == SubMenu.CONTENT) {
            ContentSubMenu(
                folders = folders,
                selected = currentFolder,
                onSelect = {
                    onFolderSelected(it)
                    subMenu = SubMenu.NONE
                }
            )
        }

        AnimatedVisibility(visible = subMenu == SubMenu.EXTERNAL_CONTENT) {
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

        AnimatedVisibility(visible = subMenu == SubMenu.SPEED) {
            SpeedSubMenu(
                selected = currentSpeed,
                onSelect = {
                    onSpeedSelected(it)
                    subMenu = SubMenu.NONE
                }
            )
        }
    }
}
