package com.litvy.carteleria.ui.menu

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.slides.SlideSpeed
import com.litvy.carteleria.ui.menu.SubMenues.*
import com.litvy.carteleria.ui.menu.external.ExternalMenuViewModel
import com.litvy.carteleria.ui.menu.overlay.ContextMenuOverlay
import com.litvy.carteleria.ui.menu.overlay.ContextMenuState
import com.litvy.carteleria.ui.menu.overlay.RenameOverlay
import com.litvy.carteleria.ui.navigation.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SideMenu(
    currentAnimation: String,
    currentSpeed: SlideSpeed,
    folders: List<String>,
    currentFolder: String,
    externalMenuViewModel: ExternalMenuViewModel,
    onAnimationSelected: (String) -> Unit,
    onSpeedSelected: (SlideSpeed) -> Unit,
    onFolderSelected: (String) -> Unit,
    onPlayExternalFolder: (String) -> Unit,
    onShowQr: () -> Unit,
    onClose: () -> Unit,
    onForceUsbScan: () -> Unit,
) {

    val navigation = remember { TvNavigationController() }
    val navState = navigation.state
    val externalNavigation = remember { ExternalNavigationController() }
    val externalState by externalMenuViewModel.state.collectAsState()

    val containerFocusRequester = remember { FocusRequester() }

    var contextMenuState by remember { mutableStateOf(ContextMenuState()) }

    val mainMenuItems = listOf(
        "Contenido",
        "Contenido Externo",
        "Animación",
        "Velocidad",
        "Cerrar"
    )

    LaunchedEffect(Unit) {
        containerFocusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .focusRequester(containerFocusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->

                val native = event.nativeKeyEvent

                if (native.action != KeyEvent.ACTION_DOWN || native.repeatCount > 0)
                    return@onPreviewKeyEvent false

                // 🔒 BLOQUEO TOTAL si Rename está activo
                if (externalState.renameTargetPath != null) {
                    return@onPreviewKeyEvent true
                }

                // ================= CONTEXT MENU =================
                if (contextMenuState.isVisible) {

                    val options = ContextAction.Cancel
                        .buildContextOptions(contextMenuState.target)

                    when (native.keyCode) {

                        KeyEvent.KEYCODE_DPAD_UP -> {
                            contextMenuState = contextMenuState.copy(
                                selectedIndex = (contextMenuState.selectedIndex - 1)
                                    .coerceAtLeast(0)
                            )
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            contextMenuState = contextMenuState.copy(
                                selectedIndex = (contextMenuState.selectedIndex + 1)
                                    .coerceAtMost(options.lastIndex)
                            )
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_BACK,
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            contextMenuState = ContextMenuState()
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_DPAD_CENTER -> {

                            val action =
                                options.getOrNull(contextMenuState.selectedIndex)

                            val target = contextMenuState.target

                            if (action != null && target != null) {

                                when (target) {

                                    is ContextTarget.Folder -> {
                                        when (action) {
                                            ContextAction.OpenFolder -> {
                                                externalMenuViewModel.openFolder(target.path)
                                                externalNavigation.resetFileIndex()
                                            }

                                            ContextAction.PlayFolder ->
                                                onPlayExternalFolder(target.path)

                                            ContextAction.Delete ->
                                                externalMenuViewModel.deleteFolder(target.path)

                                            ContextAction.Rename ->
                                                externalMenuViewModel.startRename(target.path)

                                            else -> {}
                                        }
                                    }

                                    is ContextTarget.FileItem -> {
                                        when (action) {
                                            ContextAction.Copy ->
                                                externalMenuViewModel.copyFile(target.path)

                                            ContextAction.Cut ->
                                                externalMenuViewModel.cutFile(target.path)

                                            ContextAction.Delete ->
                                                externalMenuViewModel.deleteFile(target.path)

                                            ContextAction.Rename ->
                                                externalMenuViewModel.startRename(target.path)

                                            else -> {}
                                        }
                                    }
                                }
                            }

                            contextMenuState = ContextMenuState()
                            return@onPreviewKeyEvent true
                        }
                    }
                }

                // ================= NAVEGACIÓN ORIGINAL =================
                when (native.keyCode) {

                    KeyEvent.KEYCODE_DPAD_UP -> {

                        when (navState.section) {

                            FocusSection.MAIN_MENU ->
                                navigation.moveMainUp()

                            FocusSection.SUBMENU_CONTENT,
                            FocusSection.SUBMENU_ANIMATION,
                            FocusSection.SUBMENU_SPEED ->
                                navigation.moveSubUp()

                            FocusSection.SUBMENU_EXTERNAL -> {

                                if (!externalState.isInFolder)
                                    externalNavigation.moveFolderUp()
                                else
                                    externalNavigation.moveFileUp()
                            }

                            else -> {}
                        }

                        true
                    }

                    KeyEvent.KEYCODE_DPAD_DOWN -> {

                        when (navState.section) {

                            FocusSection.MAIN_MENU ->
                                navigation.moveMainDown(mainMenuItems.lastIndex)

                            FocusSection.SUBMENU_CONTENT ->
                                navigation.moveSubDown(folders.lastIndex)

                            FocusSection.SUBMENU_ANIMATION ->
                                navigation.moveSubDown(6)

                            FocusSection.SUBMENU_SPEED ->
                                navigation.moveSubDown(SlideSpeed.entries.lastIndex)

                            FocusSection.SUBMENU_EXTERNAL -> {

                                if (!externalState.isInFolder) {
                                    val max = externalState.folders.size + 1
                                    externalNavigation.moveFolderDown(max)
                                } else {
                                    val extra =
                                        if (externalState.clipboardPath != null) 1 else 0
                                    val total =
                                        externalState.files.size + extra
                                    externalNavigation.moveFileDown(total)
                                }
                            }

                            else -> {}
                        }

                        true
                    }

                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {

                        when (navState.section) {

                            FocusSection.MAIN_MENU -> {

                                when (navState.mainIndex) {
                                    0 -> navigation.enterSubMenu(FocusSection.SUBMENU_CONTENT)
                                    1 -> navigation.enterSubMenu(FocusSection.SUBMENU_EXTERNAL)
                                    2 -> navigation.enterSubMenu(FocusSection.SUBMENU_ANIMATION)
                                    3 -> navigation.enterSubMenu(FocusSection.SUBMENU_SPEED)
                                    4 -> onClose()
                                }
                            }

                            FocusSection.SUBMENU_EXTERNAL -> {

                                if (!externalState.isInFolder) {

                                    val index =
                                        externalNavigation.state.folderIndex

                                    when (index) {

                                        0 -> onShowQr()
                                        1 -> onForceUsbScan()

                                        else -> {
                                            val folder =
                                                externalState.folders
                                                    .getOrNull(index - 2)

                                            folder?.let {
                                                contextMenuState =
                                                    ContextMenuState(
                                                        isVisible = true,
                                                        target = ContextTarget.Folder(
                                                            it.name,
                                                            it.path
                                                        )
                                                    )
                                            }
                                        }
                                    }

                                } else {

                                    val fileIndex =
                                        externalNavigation.state.fileIndex

                                    val hasClipboard =
                                        externalState.clipboardPath != null

                                    if (hasClipboard && fileIndex == 0) {
                                        externalMenuViewModel.paste()
                                    } else {

                                        val backIndex =
                                            if (hasClipboard) 1 else 0

                                        if (fileIndex == backIndex) {
                                            externalMenuViewModel.goBack()
                                            externalNavigation.resetFileIndex()
                                        } else {

                                            val offset =
                                                if (hasClipboard) 2 else 1

                                            val file =
                                                externalState.files
                                                    .getOrNull(fileIndex - offset)

                                            file?.let {
                                                contextMenuState =
                                                    ContextMenuState(
                                                        isVisible = true,
                                                        target = ContextTarget.FileItem(
                                                            it.name,
                                                            it.path
                                                        )
                                                    )
                                            }
                                        }
                                    }
                                }
                            }

                            FocusSection.SUBMENU_CONTENT -> {
                                val selected =
                                    folders.getOrNull(navState.subIndex)
                                selected?.let { onFolderSelected(it) }
                            }

                            FocusSection.SUBMENU_ANIMATION -> {
                                val animations = listOf(
                                    "random", "fade", "scale",
                                    "left", "up", "right", "down"
                                )
                                animations.getOrNull(navState.subIndex)
                                    ?.let { onAnimationSelected(it) }
                            }

                            FocusSection.SUBMENU_SPEED -> {
                                SlideSpeed.entries
                                    .getOrNull(navState.subIndex)
                                    ?.let { onSpeedSelected(it) }
                            }

                            else -> {}
                        }

                        true
                    }

                    KeyEvent.KEYCODE_BACK,
                    KeyEvent.KEYCODE_DPAD_LEFT -> {

                        when (navState.section) {

                            FocusSection.SUBMENU_EXTERNAL -> {

                                if (externalState.isInFolder) {
                                    externalMenuViewModel.goBack()
                                    externalNavigation.resetFileIndex()
                                } else {
                                    navigation.backToMain()
                                }
                            }

                            else -> {
                                if (navState.section != FocusSection.MAIN_MENU)
                                    navigation.backToMain()
                                else
                                    onClose()
                            }
                        }

                        true
                    }

                    else -> false
                }
            }
    ) {

        Row {

            LazyColumn(
                modifier = Modifier
                    .width(260.dp)
                    .padding(24.dp)
            ) {

                items(mainMenuItems.size) { index ->

                    val isSelected =
                        navState.section == FocusSection.MAIN_MENU &&
                                navState.mainIndex == index

                    MenuItemView(
                        text = mainMenuItems[index],
                        selected = isSelected,
                        onClick = {}
                    )
                }
            }

            val isPreviewMode =
                navState.section == FocusSection.MAIN_MENU

            val sectionToRender =
                if (isPreviewMode) {
                    when (navState.mainIndex) {
                        0 -> FocusSection.SUBMENU_CONTENT
                        1 -> FocusSection.SUBMENU_EXTERNAL
                        2 -> FocusSection.SUBMENU_ANIMATION
                        3 -> FocusSection.SUBMENU_SPEED
                        else -> null
                    }
                } else navState.section

            when (sectionToRender) {

                FocusSection.SUBMENU_ANIMATION ->
                    AnimationSubMenu(
                        selectedIndex =
                            if (isPreviewMode) -1 else navState.subIndex,
                        activeAnimation = currentAnimation
                    )

                FocusSection.SUBMENU_CONTENT ->
                    ContentSubMenu(
                        folders = folders,
                        selectedIndex =
                            if (isPreviewMode) -1 else navState.subIndex,
                        activeFolder = currentFolder
                    )

                FocusSection.SUBMENU_SPEED ->
                    SpeedSubMenu(
                        selectedIndex =
                            if (isPreviewMode) -1 else navState.subIndex,
                        activeSpeed = currentSpeed
                    )

                FocusSection.SUBMENU_EXTERNAL ->
                    ExternalContentSubMenu(
                        viewModel = externalMenuViewModel,
                        navigation = externalNavigation,
                        isPreviewMode = isPreviewMode,
                        onPlayFolder = onPlayExternalFolder
                    )

                else -> {}
            }
        }

        if (contextMenuState.isVisible) {
            ContextMenuOverlay(
                state = contextMenuState,
                onActionSelected = {}
            )
        }

        if (externalState.renameTargetPath != null) {
            RenameOverlay(
                initialName = externalState.renameInitialName,
                onConfirm = { newName ->
                    externalMenuViewModel.confirmRename(newName)
                },
                onCancel = {
                    externalMenuViewModel.cancelRename()
                }
            )
        }
    }
}