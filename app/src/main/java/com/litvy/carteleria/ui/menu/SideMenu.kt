package com.litvy.carteleria.ui.menu

import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.R
import com.litvy.carteleria.ui.menu.SubMenues.AnimationSubMenu
import com.litvy.carteleria.ui.menu.SubMenues.AboutSubMenu
import com.litvy.carteleria.ui.menu.SubMenues.DurationSubMenu
import com.litvy.carteleria.ui.menu.SubMenues.ExternalContentSubMenu
import com.litvy.carteleria.ui.menu.overlay.ContextMenuOverlay
import com.litvy.carteleria.ui.menu.overlay.ContextMenuState
import com.litvy.carteleria.ui.menu.preview.FilePreviewPanel
import com.litvy.carteleria.ui.navigation.ContextAction
import com.litvy.carteleria.ui.navigation.ContextTarget
import com.litvy.carteleria.ui.navigation.ExternalNavigationController
import com.litvy.carteleria.ui.navigation.FocusSection
import com.litvy.carteleria.ui.navigation.TvNavigationController
import com.litvy.carteleria.ui.touchremote.RemoteKeyEventBus
import com.litvy.carteleria.util.DeviceUtils

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SideMenu(
    currentAnimation: String,
    currentGlobalImageDurationMs: Long,
    externalMenuViewModel: ExternalMenuViewModel,
    onAnimationSelected: (String) -> Unit,
    onGlobalImageDurationSelected: (Long) -> Unit,
    onImageDurationSelected: (String, Long) -> Unit,
    onUseGlobalImageDuration: (String) -> Unit,
    onUseGlobalDurationForFolder: (String) -> Unit,
    onUseGlobalDurationForAllImages: () -> Unit,
    onPlayExternalFolder: (String) -> Unit,
    canImportFromDevice: Boolean,
    onImportFromFiles: (String?) -> Unit,
    onImportFromGallery: (String?) -> Unit,
    onClose: () -> Unit,
    onForceUsbScan: () -> Unit,
    onVisibilityChanged: () -> Unit
) {

    val context = LocalContext.current
    val isTv = remember { DeviceUtils.isTv(context) }
    val navigation = remember { TvNavigationController() }
    val navState = navigation.state
    val externalNavigation = remember { ExternalNavigationController() }
    val externalState by externalMenuViewModel.state.collectAsState()

    val containerFocusRequester = remember { FocusRequester() }
    var contextMenuState by remember { mutableStateOf(ContextMenuState()) }
    var imageDurationTargetPath by remember { mutableStateOf<String?>(null) }
    var imageDurationInitialMs by remember { mutableStateOf<Long?>(null) }
    var showGlobalDurationDialog by remember { mutableStateOf(false) }
    var confirmAllDurations by remember { mutableStateOf(false) }
    var confirmFolderDurationPath by remember { mutableStateOf<String?>(null) }
    var shortcutTargetPath by remember { mutableStateOf<String?>(null) }
    var shortcutConflict by remember { mutableStateOf<Pair<String, Int>?>(null) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }

    val mainMenuItems = listOf(
        stringResource(R.string.menu_content),
        stringResource(R.string.menu_animation),
        stringResource(R.string.menu_duration),
        stringResource(R.string.menu_about),
        stringResource(R.string.menu_close)
    )
    val importActionCount = if (canImportFromDevice) 2 else 0
    val newFolderIndex = importActionCount
    val usbActionIndex = importActionCount + 1
    val firstFolderIndex = importActionCount + 2
    val folderImportActionCount = if (canImportFromDevice) 2 else 0

    LaunchedEffect(Unit) {
        containerFocusRequester.requestFocus()
    }

    fun focusFolder(path: String) {
        val folderIndex = externalMenuViewModel.state.value.folders.indexOfFirst { it.path == path }
        if (folderIndex >= 0) {
            externalNavigation.setFolderIndex(firstFolderIndex + folderIndex)
        }
    }

    fun createAutomaticFolder() {
        externalMenuViewModel.createAutomaticFolder()?.let { createdFolder ->
            focusFolder(createdFolder.path)
        }
    }

    fun createNamedFolder(name: String): Boolean {
        val createdFolder = externalMenuViewModel.createFolder(name) ?: return false
        focusFolder(createdFolder.path)
        return true
    }

    fun requestCreateFolder() {
        if (isTv) {
            createAutomaticFolder()
        } else {
            showCreateFolderDialog = true
        }
    }

    fun handleRootExternalSelection() {
        val index = externalNavigation.state.folderIndex

        when {
            canImportFromDevice && index == 0 -> onImportFromFiles(null)
            canImportFromDevice && index == 1 -> onImportFromGallery(null)
            index == newFolderIndex -> requestCreateFolder()
            index == usbActionIndex -> onForceUsbScan()
            else -> {
                val folder = externalState.folders.getOrNull(index - firstFolderIndex)
                folder?.let {
                    contextMenuState = ContextMenuState(
                        isVisible = true,
                        target = ContextTarget.Folder(it.name, it.path)
                    )
                }
            }
        }
    }

    fun handleFolderExternalSelection() {
        val fileIndex = externalNavigation.state.fileIndex
        val currentFolderPath = externalState.currentFolderPath

        if (canImportFromDevice && fileIndex == 0) {
            onImportFromFiles(currentFolderPath)
            return
        }

        if (canImportFromDevice && fileIndex == 1) {
            onImportFromGallery(currentFolderPath)
            return
        }

        val hasClipboard = externalState.clipboardPath != null
        val pasteIndex = folderImportActionCount

        if (hasClipboard && fileIndex == pasteIndex) {
            externalMenuViewModel.paste()
            return
        }

        val backIndex = folderImportActionCount + if (hasClipboard) 1 else 0

        if (fileIndex == backIndex) {
            externalMenuViewModel.goBack()
            externalNavigation.resetFileIndex()
            return
        }

        val offset = folderImportActionCount + if (hasClipboard) 2 else 1
        val file = externalState.files.getOrNull(fileIndex - offset)

        file?.let {
            contextMenuState = ContextMenuState(
                isVisible = true,
                target = ContextTarget.FileItem(it.name, it.path)
            )
        }
    }

    val contextOptions = remember(contextMenuState, externalState) {
        when (val target = contextMenuState.target) {
            is ContextTarget.FileItem -> {
                val file = externalState.files.find { it.path == target.path }
                val visibilityAction =
                    if (file?.isHidden == true) ContextAction.Show else ContextAction.Hide

                buildList {
                    if (file?.isImage == true) add(ContextAction.Duration)
                    add(visibilityAction)
                    add(ContextAction.Copy)
                    add(ContextAction.Cut)
                    add(ContextAction.Delete)
                    add(ContextAction.Cancel)
                }
            }

            is ContextTarget.Folder -> listOf(
                ContextAction.OpenFolder,
                ContextAction.PlayFolder,
                ContextAction.NumericShortcut,
                ContextAction.ApplyGlobalDuration,
                ContextAction.Delete,
                ContextAction.Cancel
            )

            else -> emptyList()
        }
    }

    fun handleMenuRemoteKey(keyCode: Int): Boolean {
        if (contextMenuState.isVisible) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> {
                    contextMenuState = contextMenuState.copy(
                        selectedIndex = (contextMenuState.selectedIndex - 1).coerceAtLeast(0)
                    )
                    return true
                }

                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    contextMenuState = contextMenuState.copy(
                        selectedIndex = (contextMenuState.selectedIndex + 1)
                            .coerceAtMost(contextOptions.lastIndex)
                    )
                    return true
                }

                KeyEvent.KEYCODE_BACK,
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    contextMenuState = ContextMenuState()
                    return true
                }

                KeyEvent.KEYCODE_DPAD_CENTER -> {
                    val action = contextOptions.getOrNull(contextMenuState.selectedIndex)
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

                                    ContextAction.NumericShortcut ->
                                        shortcutTargetPath = target.path

                                    ContextAction.ApplyGlobalDuration ->
                                        confirmFolderDurationPath = target.path

                                    ContextAction.Delete ->
                                        externalMenuViewModel.deleteFolder(target.path)

                                    else -> Unit
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

                                    ContextAction.Hide -> {
                                        externalMenuViewModel.hideFile(target.path)
                                        onVisibilityChanged()
                                    }

                                    ContextAction.Show -> {
                                        externalMenuViewModel.showFile(target.path)
                                        onVisibilityChanged()
                                    }

                                    ContextAction.Duration -> {
                                        val file = externalState.files.find { it.path == target.path }
                                        if (file?.isImage == true) {
                                            imageDurationTargetPath = target.path
                                            imageDurationInitialMs = file.customDurationMs
                                        }
                                    }

                                    else -> Unit
                                }
                            }
                        }
                    }

                    contextMenuState = ContextMenuState()
                    return true
                }
            }
        }

        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                when (navState.section) {
                    FocusSection.MAIN_MENU ->
                        navigation.moveMainUp()

                    FocusSection.SUBMENU_ANIMATION,
                    FocusSection.SUBMENU_DURATION ->
                        navigation.moveSubUp()

                    FocusSection.SUBMENU_ABOUT ->
                        Unit

                    FocusSection.SUBMENU_EXTERNAL -> {
                        if (!externalState.isInFolder) {
                            externalNavigation.moveFolderUp()
                        } else {
                            externalNavigation.moveFileUp()
                        }
                    }

                    else -> Unit
                }

                true
            }

            KeyEvent.KEYCODE_DPAD_DOWN -> {
                when (navState.section) {
                    FocusSection.MAIN_MENU ->
                        navigation.moveMainDown(mainMenuItems.lastIndex)

                    FocusSection.SUBMENU_ANIMATION ->
                        navigation.moveSubDown(6)

                    FocusSection.SUBMENU_DURATION ->
                        navigation.moveSubDown(1)

                    FocusSection.SUBMENU_ABOUT ->
                        Unit

                    FocusSection.SUBMENU_EXTERNAL -> {
                        if (!externalState.isInFolder) {
                            val max = externalState.folders.size + importActionCount + 1
                            externalNavigation.moveFolderDown(max)
                        } else {
                            val extra = folderImportActionCount + if (externalState.clipboardPath != null) 1 else 0
                            val total = externalState.files.size + extra
                            externalNavigation.moveFileDown(total)
                        }
                    }

                    else -> Unit
                }

                true
            }

            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                when (navState.section) {
                    FocusSection.MAIN_MENU -> {
                        when (navState.mainIndex) {
                            0 -> navigation.enterSubMenu(FocusSection.SUBMENU_EXTERNAL)
                            1 -> navigation.enterSubMenu(FocusSection.SUBMENU_ANIMATION)
                            2 -> navigation.enterSubMenu(FocusSection.SUBMENU_DURATION)
                            3 -> navigation.enterSubMenu(FocusSection.SUBMENU_ABOUT)
                            4 -> onClose()
                        }
                    }

                    FocusSection.SUBMENU_EXTERNAL -> {
                        if (!externalState.isInFolder) {
                            handleRootExternalSelection()
                        } else {
                            handleFolderExternalSelection()
                        }
                    }

                    FocusSection.SUBMENU_ANIMATION -> {
                        val animations = listOf(
                            "random",
                            "fade",
                            "scale",
                            "left",
                            "up",
                            "right",
                            "down"
                        )

                        animations.getOrNull(navState.subIndex)
                            ?.let { onAnimationSelected(it) }
                    }

                    FocusSection.SUBMENU_DURATION -> {
                        if (navState.subIndex == 0) {
                            showGlobalDurationDialog = true
                        } else {
                            confirmAllDurations = true
                        }
                    }

                    FocusSection.SUBMENU_ABOUT -> Unit

                    else -> Unit
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
                        if (navState.section != FocusSection.MAIN_MENU) {
                            navigation.backToMain()
                        } else {
                            onClose()
                        }
                    }
                }

                true
            }

            else -> false
        }
    }

    LaunchedEffect(
        contextMenuState,
        externalState,
        navState.section,
        navState.mainIndex,
        navState.subIndex,
        externalNavigation.state.folderIndex,
        externalNavigation.state.fileIndex
    ) {
        RemoteKeyEventBus.keyEvents.collect { keyCode ->
            handleMenuRemoteKey(keyCode)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .focusRequester(containerFocusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                val native = event.nativeKeyEvent

                if (native.action != KeyEvent.ACTION_DOWN || native.repeatCount > 0) {
                    return@onPreviewKeyEvent false
                }

                if (contextMenuState.isVisible) {
                    when (native.keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            contextMenuState = contextMenuState.copy(
                                selectedIndex = (contextMenuState.selectedIndex - 1).coerceAtLeast(0)
                            )
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            contextMenuState = contextMenuState.copy(
                                selectedIndex = (contextMenuState.selectedIndex + 1)
                                    .coerceAtMost(contextOptions.lastIndex)
                            )
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_BACK,
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            contextMenuState = ContextMenuState()
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_DPAD_CENTER -> {
                            val action = contextOptions.getOrNull(contextMenuState.selectedIndex)
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

                                            ContextAction.NumericShortcut ->
                                                shortcutTargetPath = target.path

                                            ContextAction.ApplyGlobalDuration ->
                                                confirmFolderDurationPath = target.path

                                            ContextAction.Delete ->
                                                externalMenuViewModel.deleteFolder(target.path)

                                            else -> Unit
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

                                            ContextAction.Hide -> {
                                                externalMenuViewModel.hideFile(target.path)
                                                onVisibilityChanged()
                                            }

                                            ContextAction.Show -> {
                                                externalMenuViewModel.showFile(target.path)
                                                onVisibilityChanged()
                                            }

                                            ContextAction.Duration -> {
                                                val file = externalState.files.find { it.path == target.path }
                                                if (file?.isImage == true) {
                                                    imageDurationTargetPath = target.path
                                                    imageDurationInitialMs = file.customDurationMs
                                                }
                                            }

                                            else -> Unit
                                        }
                                    }
                                }
                            }

                            contextMenuState = ContextMenuState()
                            return@onPreviewKeyEvent true
                        }
                    }
                }

                when (native.keyCode) {
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        when (navState.section) {
                            FocusSection.MAIN_MENU ->
                                navigation.moveMainUp()

                            FocusSection.SUBMENU_ANIMATION,
                            FocusSection.SUBMENU_DURATION ->
                                navigation.moveSubUp()

                            FocusSection.SUBMENU_ABOUT ->
                                Unit

                            FocusSection.SUBMENU_EXTERNAL -> {
                                if (!externalState.isInFolder) {
                                    externalNavigation.moveFolderUp()
                                } else {
                                    externalNavigation.moveFileUp()
                                }
                            }

                            else -> Unit
                        }

                        true
                    }

                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        when (navState.section) {
                            FocusSection.MAIN_MENU ->
                                navigation.moveMainDown(mainMenuItems.lastIndex)

                            FocusSection.SUBMENU_ANIMATION ->
                                navigation.moveSubDown(6)

                            FocusSection.SUBMENU_DURATION ->
                                navigation.moveSubDown(1)

                            FocusSection.SUBMENU_ABOUT ->
                                Unit

                            FocusSection.SUBMENU_EXTERNAL -> {
                                if (!externalState.isInFolder) {
                                    val max = externalState.folders.size + importActionCount + 1
                                    externalNavigation.moveFolderDown(max)
                                } else {
                                    val extra = folderImportActionCount + if (externalState.clipboardPath != null) 1 else 0
                                    val total = externalState.files.size + extra
                                    externalNavigation.moveFileDown(total)
                                }
                            }

                            else -> Unit
                        }

                        true
                    }

                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        when (navState.section) {
                            FocusSection.MAIN_MENU -> {
                                when (navState.mainIndex) {
                                    0 -> navigation.enterSubMenu(FocusSection.SUBMENU_EXTERNAL)
                                    1 -> navigation.enterSubMenu(FocusSection.SUBMENU_ANIMATION)
                                    2 -> navigation.enterSubMenu(FocusSection.SUBMENU_DURATION)
                                    3 -> navigation.enterSubMenu(FocusSection.SUBMENU_ABOUT)
                                    4 -> onClose()
                                }
                            }

                            FocusSection.SUBMENU_EXTERNAL -> {
                                if (!externalState.isInFolder) {
                                    handleRootExternalSelection()
                                } else {
                                    handleFolderExternalSelection()
                                }
                            }

                            FocusSection.SUBMENU_ANIMATION -> {
                                val animations = listOf(
                                    "random",
                                    "fade",
                                    "scale",
                                    "left",
                                    "up",
                                    "right",
                                    "down"
                                )

                                animations.getOrNull(navState.subIndex)
                                    ?.let { onAnimationSelected(it) }
                            }

                            FocusSection.SUBMENU_DURATION -> {
                                if (navState.subIndex == 0) {
                                    showGlobalDurationDialog = true
                                } else {
                                    confirmAllDurations = true
                                }
                            }

                            FocusSection.SUBMENU_ABOUT -> Unit

                            else -> Unit
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
                                if (navState.section != FocusSection.MAIN_MENU) {
                                    navigation.backToMain()
                                } else {
                                    onClose()
                                }
                            }
                        }

                        true
                    }

                    else -> false
                }
            }
    ) {
        val selectedFile =
            if (navState.section == FocusSection.SUBMENU_EXTERNAL && externalState.isInFolder) {
                val hasClipboard = externalState.clipboardPath != null
                val offset = folderImportActionCount + if (hasClipboard) 2 else 1
                externalState.files.getOrNull(externalNavigation.state.fileIndex - offset)
            } else {
                null
            }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val windowSizeClass = when {
                maxWidth < 600.dp -> MenuWindowSizeClass.Compact
                maxWidth < 840.dp -> MenuWindowSizeClass.Medium
                else -> MenuWindowSizeClass.Expanded
            }
            val compact = windowSizeClass == MenuWindowSizeClass.Compact
            val mainMenuWidth = when (windowSizeClass) {
                MenuWindowSizeClass.Compact -> 190.dp
                MenuWindowSizeClass.Medium -> 220.dp
                MenuWindowSizeClass.Expanded -> 260.dp
            }
            val sidePadding = if (compact) 12.dp else 24.dp

            Row(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .width(mainMenuWidth)
                        .padding(sidePadding)
                ) {
                items(mainMenuItems.size) { index ->
                    val isSelected =
                        navState.section == FocusSection.MAIN_MENU && navState.mainIndex == index

                    MenuItemView(
                        text = mainMenuItems[index],
                        selected = isSelected,
                        onClick = {}
                    )
                }
            }

            val isPreviewMode = navState.section == FocusSection.MAIN_MENU

            val sectionToRender = if (isPreviewMode) {
                when (navState.mainIndex) {
                    0 -> FocusSection.SUBMENU_EXTERNAL
                    1 -> FocusSection.SUBMENU_ANIMATION
                    2 -> FocusSection.SUBMENU_DURATION
                    3 -> FocusSection.SUBMENU_ABOUT
                    else -> null
                }
            } else {
                navState.section
            }

            when (sectionToRender) {
                FocusSection.SUBMENU_ANIMATION ->
                    AnimationSubMenu(
                        selectedIndex = if (isPreviewMode) -1 else navState.subIndex,
                        activeAnimation = currentAnimation
                    )

                FocusSection.SUBMENU_DURATION ->
                    DurationSubMenu(
                        selectedIndex = if (isPreviewMode) -1 else navState.subIndex,
                        activeGlobalImageDurationMs = currentGlobalImageDurationMs
                    )

                FocusSection.SUBMENU_EXTERNAL ->
                    ExternalContentSubMenu(
                        viewModel = externalMenuViewModel,
                        navigation = externalNavigation,
                        isPreviewMode = isPreviewMode,
                        canImportFromDevice = canImportFromDevice,
                        onImportFromFiles = { onImportFromFiles(externalState.currentFolderPath) },
                        onImportFromGallery = { onImportFromGallery(externalState.currentFolderPath) },
                        onCreateFolder = { requestCreateFolder() },
                        onForceUsbScan = onForceUsbScan,
                        onFolderSelected = { folder ->
                            contextMenuState = ContextMenuState(
                                isVisible = true,
                                target = ContextTarget.Folder(folder.name, folder.path)
                            )
                        }
                    )

                FocusSection.SUBMENU_ABOUT ->
                    AboutSubMenu()

                else -> Unit
            }

                if (selectedFile != null && !compact) {
                    Spacer(modifier = Modifier.width(24.dp))
                    FilePreviewPanel(selectedFile)
                }
            }
        }

        if (contextMenuState.isVisible) {
            ContextMenuOverlay(
                state = contextMenuState,
                options = contextOptions,
                onActionSelected = {}
            )
        }

        if (showGlobalDurationDialog) {
            DurationPickerDialog(
                title = stringResource(R.string.menu_duration),
                initialDurationMs = currentGlobalImageDurationMs,
                includeGlobalOption = false,
                onDurationSelected = { durationMs ->
                    durationMs?.let { onGlobalImageDurationSelected(it) }
                    showGlobalDurationDialog = false
                },
                onDismiss = { showGlobalDurationDialog = false }
            )
        }

        imageDurationTargetPath?.let { targetPath ->
            DurationPickerDialog(
                title = stringResource(R.string.menu_duration),
                initialDurationMs = imageDurationInitialMs,
                includeGlobalOption = true,
                onDurationSelected = { durationMs ->
                    if (durationMs == null) {
                        onUseGlobalImageDuration(targetPath)
                    } else {
                        onImageDurationSelected(targetPath, durationMs)
                    }
                    imageDurationTargetPath = null
                    imageDurationInitialMs = null
                },
                onDismiss = {
                    imageDurationTargetPath = null
                    imageDurationInitialMs = null
                }
            )
        }

        if (confirmAllDurations) {
            ConfirmationDialog(
                text = stringResource(R.string.confirm_apply_global_duration_all),
                onConfirm = {
                    onUseGlobalDurationForAllImages()
                    Toast.makeText(
                        context,
                        context.getString(R.string.toast_global_duration_applied_all),
                        Toast.LENGTH_SHORT
                    ).show()
                    confirmAllDurations = false
                },
                onDismiss = { confirmAllDurations = false }
            )
        }

        confirmFolderDurationPath?.let { folderPath ->
            ConfirmationDialog(
                text = stringResource(R.string.confirm_apply_global_duration_folder),
                onConfirm = {
                    onUseGlobalDurationForFolder(folderPath)
                    Toast.makeText(
                        context,
                        context.getString(R.string.toast_global_duration_applied_folder),
                        Toast.LENGTH_SHORT
                    ).show()
                    confirmFolderDurationPath = null
                },
                onDismiss = { confirmFolderDurationPath = null }
            )
        }

        shortcutTargetPath?.takeIf { shortcutConflict == null }?.let { targetPath ->
            val targetFolder = externalState.folders.find { it.path == targetPath }

            FolderShortcutDialog(
                currentShortcut = targetFolder?.shortcutNumber,
                onShortcutSelected = { selectedShortcut ->
                    if (selectedShortcut == null) {
                        externalMenuViewModel.setFolderShortcut(targetPath, null)
                        shortcutTargetPath = null
                    } else {
                        val conflictFolder = externalState.folders.firstOrNull {
                            it.path != targetPath && it.shortcutNumber == selectedShortcut
                        }

                        if (conflictFolder != null) {
                            shortcutConflict = conflictFolder.path to selectedShortcut
                        } else {
                            externalMenuViewModel.setFolderShortcut(targetPath, selectedShortcut)
                            shortcutTargetPath = null
                        }
                    }
                },
                onDismiss = { shortcutTargetPath = null }
            )
        }

        if (showCreateFolderDialog) {
            NewFolderNameDialog(
                folderExists = { externalMenuViewModel.folderNameExists(it) },
                onCreate = { folderName ->
                    if (createNamedFolder(folderName)) {
                        showCreateFolderDialog = false
                    }
                },
                onDismiss = { showCreateFolderDialog = false }
            )
        }

        shortcutConflict?.let { (conflictPath, selectedShortcut) ->
            val conflictFolder = externalState.folders.find { it.path == conflictPath }
            val targetFolder = shortcutTargetPath?.let { targetPath ->
                externalState.folders.find { it.path == targetPath }
            }

            ConfirmationDialog(
                text = stringResource(
                    R.string.confirm_replace_shortcut,
                    selectedShortcut,
                    conflictFolder?.name.orEmpty(),
                    targetFolder?.name.orEmpty()
                ),
                onConfirm = {
                    val targetPath = shortcutTargetPath
                    if (targetPath != null) {
                        externalMenuViewModel.setFolderShortcut(targetPath, selectedShortcut)
                    }
                    shortcutConflict = null
                    shortcutTargetPath = null
                },
                onDismiss = { shortcutConflict = null }
            )
        }
    }
}

private enum class MenuWindowSizeClass {
    Compact,
    Medium,
    Expanded
}
