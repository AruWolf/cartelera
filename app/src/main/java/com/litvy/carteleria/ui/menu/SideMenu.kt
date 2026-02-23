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
import com.litvy.carteleria.slides.AppStorageSlideProvider
import com.litvy.carteleria.slides.SlideSpeed
import com.litvy.carteleria.ui.menu.SubMenues.*
import com.litvy.carteleria.ui.menu.model.ClipboardItem
import com.litvy.carteleria.ui.navigation.*
import java.io.File

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SideMenu(
    currentAnimation: String,
    currentSpeed: SlideSpeed,
    folders: List<String>,
    externalFolders: List<File>,
    currentFolder: String,
    currentExternalFolder: String,
    externalStorageProvider: AppStorageSlideProvider,
    clipboardItem: ClipboardItem?,
    onClipboardChange: (ClipboardItem?) -> Unit,
    onExternalContentChanged: () -> Unit,
    onAnimationSelected: (String) -> Unit,
    onSpeedSelected: (SlideSpeed) -> Unit,
    onFolderSelected: (String) -> Unit,
    onExternalFolderSelected: (File) -> Unit,
    onShowQr: () -> Unit,
    onClose: () -> Unit,
    onForceUsbScan: () -> Unit,
) {

    val navigation = remember { TvNavigationController() }
    val navState = navigation.state
    val externalNavigation = remember { ExternalNavigationController() }
    val containerFocusRequester = remember { FocusRequester() }

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

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .focusRequester(containerFocusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->

                val native = event.nativeKeyEvent
                if (native.action != KeyEvent.ACTION_DOWN || native.repeatCount > 0)
                    return@onPreviewKeyEvent false

                when (native.keyCode) {

                    // ================= UP =================
                    KeyEvent.KEYCODE_DPAD_UP -> {

                        when (navState.section) {

                            FocusSection.MAIN_MENU ->
                                navigation.moveMainUp()

                            FocusSection.SUBMENU_SPEED,
                            FocusSection.SUBMENU_ANIMATION,
                            FocusSection.SUBMENU_CONTENT ->
                                navigation.moveSubUp()

                            FocusSection.SUBMENU_EXTERNAL -> {

                                when (externalNavigation.state.level) {

                                    0 -> {
                                        val max = 1 + externalFolders.size + 1
                                        externalNavigation.moveUp(max)
                                    }

                                    1 -> {
                                        val folder = externalNavigation.state.exploredFolder
                                        val files = folder?.listFiles()
                                            ?.filter { it.isFile }
                                            ?.sortedBy { it.name.lowercase() }
                                            ?: emptyList()

                                        val extra = if (clipboardItem != null) 1 else 0
                                        val total = files.size + 1 + extra
                                        externalNavigation.moveUp(total)
                                    }

                                    2 -> {
                                        val options =
                                            ContextAction.Cancel.buildContextOptions(
                                                externalNavigation.state.contextTarget
                                            )
                                        externalNavigation.moveUp(options.lastIndex)
                                    }
                                }
                            }

                            else -> {}
                        }
                        true
                    }

                    // ================= DOWN =================
                    KeyEvent.KEYCODE_DPAD_DOWN -> {

                        when (navState.section) {

                            FocusSection.MAIN_MENU ->
                                navigation.moveMainDown(mainMenuItems.lastIndex)

                            FocusSection.SUBMENU_SPEED ->
                                navigation.moveSubDown(SlideSpeed.entries.lastIndex)

                            FocusSection.SUBMENU_ANIMATION ->
                                navigation.moveSubDown(6)

                            FocusSection.SUBMENU_CONTENT ->
                                navigation.moveSubDown(folders.lastIndex)

                            FocusSection.SUBMENU_EXTERNAL -> {

                                when (externalNavigation.state.level) {

                                    0 -> {
                                        val max = 1 + externalFolders.size + 1
                                        externalNavigation.moveDown(max)
                                    }

                                    1 -> {
                                        val folder = externalNavigation.state.exploredFolder
                                        val files = folder?.listFiles()
                                            ?.filter { it.isFile }
                                            ?.sortedBy { it.name.lowercase() }
                                            ?: emptyList()

                                        val extra = if (clipboardItem != null) 1 else 0
                                        val total = files.size + 1 + extra
                                        externalNavigation.moveDown(total)
                                    }

                                    2 -> {
                                        val options =
                                            ContextAction.Cancel.buildContextOptions(
                                                externalNavigation.state.contextTarget
                                            )
                                        externalNavigation.moveDown(options.lastIndex)
                                    }
                                }
                            }

                            else -> {}
                        }
                        true
                    }

                    // ================= OK =================
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

                            FocusSection.SUBMENU_CONTENT -> {

                                val selected = folders.getOrNull(navState.subIndex)

                                selected?.let {
                                    onFolderSelected(it)
                                }
                            }

                            FocusSection.SUBMENU_EXTERNAL -> {

                                when (externalNavigation.state.level) {

                                    // ========= CARPETAS =========
                                    0 -> {

                                        val index = externalNavigation.state.folderIndex

                                        when (index) {
                                            0 -> onShowQr()
                                            1 -> onForceUsbScan()
                                            else -> {
                                                val folder =
                                                    externalFolders.getOrNull(index - 2)
                                                folder?.let {
                                                    externalNavigation.openContextMenu(
                                                        ContextTarget.Folder(it)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // ========= ARCHIVOS =========
                                    1 -> {

                                        val folder = externalNavigation.state.exploredFolder
                                        val files = folder?.listFiles()
                                            ?.filter { it.isFile }
                                            ?.sortedBy { it.name.lowercase() }
                                            ?: emptyList()

                                        val fileIndex =
                                            externalNavigation.state.fileIndex

                                        val hasClipboard = clipboardItem != null

                                        when {

                                            // PEGAR
                                            hasClipboard && fileIndex == 0 -> {

                                                val clip = clipboardItem
                                                if (clip != null && folder != null) {

                                                    if (clip.isCut) {
                                                        externalStorageProvider.moveFileToFolder(
                                                            clip.file,
                                                            folder
                                                        )
                                                    } else {
                                                        externalStorageProvider.duplicateFileToFolder(
                                                            clip.file,
                                                            folder
                                                        )
                                                    }

                                                    onClipboardChange(null)
                                                    onExternalContentChanged()
                                                }
                                            }

                                            // VOLVER
                                            (!hasClipboard && fileIndex == 0) ||
                                                    (hasClipboard && fileIndex == 1) -> {
                                                externalNavigation.back()
                                            }

                                            // ARCHIVOS
                                            else -> {

                                                val offset =
                                                    if (hasClipboard) 2 else 1

                                                val file =
                                                    files.getOrNull(fileIndex - offset)

                                                file?.let {
                                                    externalNavigation.openContextMenu(
                                                        ContextTarget.FileItem(it)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // ========= CONTEXTO =========
                                    2 -> {

                                        val target =
                                            externalNavigation.state.contextTarget

                                        val options =
                                            ContextAction.Cancel.buildContextOptions(
                                                target
                                            )

                                        val action =
                                            options.getOrNull(
                                                externalNavigation.state.contextIndex
                                            )

                                        var closeContext = true

                                        when (target) {

                                            is ContextTarget.Folder -> {

                                                when (action) {

                                                    ContextAction.OpenFolder -> {
                                                        externalNavigation.enterFiles(
                                                            target.folder
                                                        )
                                                        closeContext = false
                                                    }

                                                    ContextAction.PlayFolder -> {
                                                        onExternalFolderSelected(
                                                            target.folder
                                                        )
                                                    }

                                                    ContextAction.Delete -> {
                                                        externalStorageProvider.deleteFolder(
                                                            target.folder
                                                        )
                                                        onExternalContentChanged()
                                                    }

                                                    else -> {}
                                                }
                                            }

                                            is ContextTarget.FileItem -> {

                                                when (action) {

                                                    ContextAction.Preview -> {
                                                        onExternalFolderSelected(
                                                            target.file
                                                        )
                                                    }

                                                    ContextAction.Copy -> {
                                                        onClipboardChange(
                                                            ClipboardItem(
                                                                file = target.file,
                                                                isCut = false
                                                            )
                                                        )
                                                    }

                                                    ContextAction.Cut -> {
                                                        onClipboardChange(
                                                            ClipboardItem(
                                                                file = target.file,
                                                                isCut = true
                                                            )
                                                        )
                                                    }

                                                    ContextAction.Delete -> {
                                                        externalStorageProvider.deleteFile(
                                                            target.file
                                                        )
                                                        onExternalContentChanged()
                                                    }

                                                    else -> {}
                                                }
                                            }

                                            null -> {}
                                        }

                                        if (closeContext) {
                                            externalNavigation.back()
                                        }
                                    }
                                }
                            }

                            else -> {}
                        }
                        true
                    }

                    // ================= BACK =================
                    KeyEvent.KEYCODE_BACK,
                    KeyEvent.KEYCODE_DPAD_LEFT -> {

                        when (navState.section) {

                            FocusSection.SUBMENU_EXTERNAL -> {
                                if (!externalNavigation.back()) {
                                    navigation.backToMain()
                                    externalNavigation.reset()
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

        val isPreviewMode = navState.section == FocusSection.MAIN_MENU

        when (if (isPreviewMode) {
            when (navState.mainIndex) {
                0 -> FocusSection.SUBMENU_CONTENT
                1 -> FocusSection.SUBMENU_EXTERNAL
                2 -> FocusSection.SUBMENU_ANIMATION
                3 -> FocusSection.SUBMENU_SPEED
                else -> null
            }
        } else navState.section) {

            FocusSection.SUBMENU_ANIMATION ->
                AnimationSubMenu(
                    selectedIndex = if (isPreviewMode) -1 else navState.subIndex,
                    activeAnimation = currentAnimation
                )

            FocusSection.SUBMENU_CONTENT ->
                ContentSubMenu(
                    folders = folders,
                    selectedIndex = if (isPreviewMode) -1 else navState.subIndex,
                    activeFolder = currentFolder
                )

            FocusSection.SUBMENU_SPEED ->
                SpeedSubMenu(
                    selectedIndex = if (isPreviewMode) -1 else navState.subIndex,
                    activeSpeed = currentSpeed
                )

            FocusSection.SUBMENU_EXTERNAL ->
                ExternalContentSubMenu(
                    folders = externalFolders,
                    storageProvider = externalStorageProvider,
                    navigation = externalNavigation,
                    clipboardItem = clipboardItem,
                    onClipboardChange = onClipboardChange,
                    onExternalContentChanged = onExternalContentChanged,
                    onSelectFolder = onExternalFolderSelected,
                    isPreviewMode = isPreviewMode
                )

            else -> {}
        }
    }
}