package com.litvy.carteleria.ui.menu

import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
    onClose: () -> Unit,
    onForceUsbScan: () -> Unit,
    ) {

    var subMenu by remember { mutableStateOf(SubMenu.NONE) }

    val contentRequester = remember { FocusRequester() }
    val externalRequester = remember { FocusRequester() }
    val animationRequester = remember { FocusRequester() }
    val speedRequester = remember { FocusRequester() }
    val qrRequester = remember { FocusRequester() }
    val usbRequester = remember { FocusRequester() }
    val closeRequester = remember { FocusRequester() }

    val menuListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        subMenu = SubMenu.CONTENT
        kotlinx.coroutines.delay(100)
        contentRequester.requestFocus()
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

        LazyColumn(
            state = menuListState,
            modifier = Modifier
                .width(260.dp)
                .padding(24.dp)
        ) {
            item {
                MenuItemView(
                    text = "Contenido",
                    onFocus = { subMenu = SubMenu.CONTENT },
                    onClick = {},
                    focusRequester = contentRequester
                )
            }

            item {
                MenuItemView(
                    text = "Contenido Externo",
                    onFocus = { subMenu = SubMenu.EXTERNAL_CONTENT },
                    onClick = {},
                    focusRequester = externalRequester
                )
            }

            item {
                MenuItemView(
                    text = "Animación",
                    onFocus = { subMenu = SubMenu.ANIMATION },
                    onClick = {},
                    focusRequester = animationRequester
                )
            }

            item {
                MenuItemView(
                    text = "Velocidad",
                    onFocus = { subMenu = SubMenu.SPEED },
                    onClick = {},
                    focusRequester = speedRequester
                )
            }

            item {
                MenuItemView(
                    text = "Cargar contenido (QR)",
                    onFocus = { subMenu = SubMenu.NONE },
                    onClick = { onShowQr() },
                    focusRequester = qrRequester
                )
            }

            item {
                MenuItemView(
                    text = "Actualizar desde USB",
                    onFocus = { subMenu = SubMenu.NONE },
                    onClick = {
                        onForceUsbScan()
                    },
                    focusRequester = usbRequester
                )
            }

            item {
                MenuItemView(
                    text = "Cerrar",
                    onFocus = { subMenu = SubMenu.NONE },
                    onClick = onClose,
                    focusRequester = closeRequester
                )
            }
        }

        AnimatedVisibility(visible = subMenu == SubMenu.ANIMATION) {
            AnimationSubMenu(
                selected = currentAnimation,
                onSelect = {
                    onAnimationSelected(it)
                    subMenu = SubMenu.NONE
                    animationRequester.requestFocus()
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
                    contentRequester.requestFocus()
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
                    externalRequester.requestFocus()
                },
                onShowQr = {
                    subMenu = SubMenu.NONE
                    externalRequester.requestFocus()
                    onShowQr()
                }
            )
        }

        AnimatedVisibility(visible = subMenu == SubMenu.SPEED) {
            SpeedSubMenu(
                selected = currentSpeed,
                onSelect = {
                    onSpeedSelected(it)
                    subMenu = SubMenu.NONE
                    speedRequester.requestFocus()
                }
            )
        }
    }
}