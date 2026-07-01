package com.litvy.carteleria.ui.slideshow

import com.litvy.carteleria.R
import android.app.Activity
import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.animations.TvTransitions
import com.litvy.carteleria.config.ExternalLinks
import com.litvy.carteleria.data.CartelPreferences
import com.litvy.carteleria.data.external.AppStorageExternalRepository
import com.litvy.carteleria.data.external.FolderShortcutManager
import com.litvy.carteleria.data.external.HiddenFileManager
import com.litvy.carteleria.data.external.ImageDurationManager
import com.litvy.carteleria.domain.external.usecase.CopyExternalFileUseCase
import com.litvy.carteleria.domain.external.usecase.CreateExternalFolderUseCase
import com.litvy.carteleria.domain.external.usecase.DeleteExternalFileUseCase
import com.litvy.carteleria.domain.external.usecase.DeleteExternalFolderUseCase
import com.litvy.carteleria.domain.external.usecase.ExternalContentUseCases
import com.litvy.carteleria.domain.external.usecase.HideExternalFileUseCase
import com.litvy.carteleria.domain.external.usecase.ListExternalFilesUseCase
import com.litvy.carteleria.domain.external.usecase.ListExternalFoldersUseCase
import com.litvy.carteleria.domain.external.usecase.MoveExternalFileUseCase
import com.litvy.carteleria.domain.external.usecase.SetFolderShortcutUseCase
import com.litvy.carteleria.domain.external.usecase.ShowExternalFileUseCase
import com.litvy.carteleria.engine.EvokeSlide
import com.litvy.carteleria.slides.AdvertisingSlide
import com.litvy.carteleria.slides.AppStorageSlideProvider
import com.litvy.carteleria.slides.Slide
import com.litvy.carteleria.ui.loading.AppLoadingSurface
import com.litvy.carteleria.ui.loading.AppVisualAssets
import com.litvy.carteleria.ui.loading.LoadingUiDefaults
import com.litvy.carteleria.ui.menu.ExternalMenuViewModel
import com.litvy.carteleria.ui.menu.ImportDestinationDialog
import com.litvy.carteleria.ui.menu.NewFolderNameDialog
import com.litvy.carteleria.ui.menu.SideMenu
import com.litvy.carteleria.ui.touchremote.RemoteKeyEventBus
import com.litvy.carteleria.util.qr.generateQrCode
import com.litvy.carteleria.ui.touchremote.TouchDeviceDetector
import com.litvy.carteleria.util.usb.UsbContentManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.litvy.carteleria.util.DeviceUtils
import androidx.compose.foundation.clickable

private enum class MediaImportSource {
    Files,
    Gallery
}

@Composable
fun SlideShowScreen(
    onShowTouchRemote: () -> Unit = {}
) {
    val context = LocalContext.current
    var backPressedOnce by remember { mutableStateOf(false) }

    val hiddenManager = remember { HiddenFileManager(context) }
    val imageDurationManager = remember { ImageDurationManager(context) }
    val folderShortcutManager = remember { FolderShortcutManager(context) }

    val isMobile = !DeviceUtils.isTv(context)


    val viewModel = remember {
        SlideShowViewModel(
            externalProvider = AppStorageSlideProvider(context, hiddenManager, imageDurationManager),
            prefs = CartelPreferences(context),
            usbImporter = UsbContentManager(context),
            context = context
        )
    }
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val focusRequester = remember { FocusRequester() }
    var pendingImportTargetPath by remember { mutableStateOf<String?>(null) }
    var pendingImportSource by remember { mutableStateOf<MediaImportSource?>(null) }
    var destinationFolderPath by remember { mutableStateOf<String?>(null) }
    var showDestinationDialog by remember { mutableStateOf(false) }
    var showDestinationNewFolderDialog by remember { mutableStateOf(false) }

    val importFromFilesLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        val targetPath = pendingImportTargetPath
        pendingImportTargetPath = null
        if (targetPath != null) {
            viewModel.importMedia(uris, File(targetPath))
        }
    }
    val importFromGalleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        val targetPath = pendingImportTargetPath
        pendingImportTargetPath = null
        if (targetPath != null) {
            viewModel.importMedia(uris, File(targetPath))
        }
    }
    val canUseTouchImports = remember { TouchDeviceDetector.shouldShowTouchRemote(context) }
    val scope = rememberCoroutineScope()
    var isFolderLoading by remember { mutableStateOf(false) }

    val externalProvider = remember {
        AppStorageSlideProvider(context, hiddenManager, imageDurationManager)
    }

    val transition = remember(state.currentAnimation) {
        when (state.currentAnimation) {
            "fade" -> TvTransitions.fade<Slide>()
            "scale" -> TvTransitions.scale<Slide>()
            "left" -> TvTransitions.slideLeft<Slide>()
            "up" -> TvTransitions.slideUp<Slide>()
            "right" -> TvTransitions.slideRight<Slide>()
            "down" -> TvTransitions.slideDown<Slide>()
            "fadeScale" -> TvTransitions.fadeScale<Slide>()
            "fadeSlideLeft" -> TvTransitions.fadeSlideLeft<Slide>()
            "random" -> TvTransitions.random<Slide>()
            else -> TvTransitions.fade()
        }
    }

    val engine = remember(state.slides, transition, state.globalImageDurationMs) {
        if (state.slides.isNotEmpty()) {
            EvokeSlide(
                slides = state.slides,
                transition = transition,
                globalImageDurationMs = state.globalImageDurationMs
            )
        } else {
            null
        }
    }

    val externalRepository = remember {
        AppStorageExternalRepository(
            provider = externalProvider,
            hiddenManager = hiddenManager,
            durationManager = imageDurationManager,
            shortcutManager = folderShortcutManager
        )
    }

    val externalUseCases = remember {
        ExternalContentUseCases(
            listFolders = ListExternalFoldersUseCase(externalRepository),
            listFiles = ListExternalFilesUseCase(externalRepository),
            createFolder = CreateExternalFolderUseCase(externalRepository),
            deleteFile = DeleteExternalFileUseCase(externalRepository),
            deleteFolder = DeleteExternalFolderUseCase(externalRepository),
            setFolderShortcut = SetFolderShortcutUseCase(externalRepository),
            copyFile = CopyExternalFileUseCase(externalRepository),
            moveFile = MoveExternalFileUseCase(externalRepository),
            hideFile = HideExternalFileUseCase(externalRepository),
            showFile = ShowExternalFileUseCase(externalRepository)
        )
    }

    val externalMenuViewModel = remember {
        ExternalMenuViewModel(externalUseCases)
    }
    val externalMenuState by externalMenuViewModel.state.collectAsState()

    fun launchImport(source: MediaImportSource, targetPath: String) {
        pendingImportTargetPath = targetPath
        when (source) {
            MediaImportSource.Files -> importFromFilesLauncher.launch(arrayOf("image/*", "video/*"))
            MediaImportSource.Gallery -> {
                if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)) {
                    importFromGalleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    )
                } else {
                    importFromFilesLauncher.launch(arrayOf("image/*", "video/*"))
                }
            }
        }
    }

    fun requestImport(source: MediaImportSource, targetPath: String?) {
        if (targetPath != null) {
            launchImport(source, targetPath)
            viewModel.toggleMenu()
        } else {
            pendingImportSource = source
            destinationFolderPath = externalMenuState.folders.firstOrNull()?.path
            showDestinationDialog = true
        }
    }

    LaunchedEffect(state.contentRevision) {
        externalMenuViewModel.reloadCurrentView()
    }

    var ignoreNextCenter by remember { mutableStateOf(false) }
    var shortcutOverlayText by remember { mutableStateOf<String?>(null) }
    var shortcutPlaybackJob by remember { mutableStateOf<Job?>(null) }
    val shortcutOverlayManager = remember(scope) {
        ShortcutOverlayManager(scope) { text -> shortcutOverlayText = text }
    }

    fun playFolderByShortcut(number: Int): Boolean {
        if (state.isAdvertisingShowing) return true

        val folder = externalMenuState.folders.firstOrNull { it.shortcutNumber == number }
            ?: return false

        shortcutPlaybackJob?.cancel()
        shortcutPlaybackJob = scope.launch {
            isFolderLoading = true
            delay(LoadingUiDefaults.FOLDER_SELECTION_LOADING_MS)
            viewModel.selectExternalFolder(File(folder.path))
            isFolderLoading = false
            shortcutOverlayManager.show("[$number] ${folder.name}")
        }

        return true
    }

    fun handleSlideshowRemoteKey(keyCode: Int): Boolean {
        if (state.isAdvertisingShowing) {
            return when (keyCode) {

                KeyEvent.KEYCODE_DPAD_RIGHT,
                KeyEvent.KEYCODE_DPAD_LEFT,
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                KeyEvent.KEYCODE_DPAD_UP,
                KeyEvent.KEYCODE_0,
                KeyEvent.KEYCODE_1,
                KeyEvent.KEYCODE_2,
                KeyEvent.KEYCODE_3,
                KeyEvent.KEYCODE_4,
                KeyEvent.KEYCODE_5,
                KeyEvent.KEYCODE_6,
                KeyEvent.KEYCODE_7,
                KeyEvent.KEYCODE_8,
                KeyEvent.KEYCODE_9 -> true

                else -> false
            }
        }

        if (state.menuVisible) return false

        /*if (ignoreNextCenter && keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            ignoreNextCenter = false
            return true
        }*/

        ignoreNextCenter = false

        return when (keyCode) {
            KeyEvent.KEYCODE_0,
            KeyEvent.KEYCODE_1,
            KeyEvent.KEYCODE_2,
            KeyEvent.KEYCODE_3,
            KeyEvent.KEYCODE_4,
            KeyEvent.KEYCODE_5,
            KeyEvent.KEYCODE_6,
            KeyEvent.KEYCODE_7,
            KeyEvent.KEYCODE_8,
            KeyEvent.KEYCODE_9 -> {
                val number = RemoteInputHandler.numberFromKeyCode(keyCode)
                number?.let { playFolderByShortcut(it) } ?: false
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                viewModel.nextSlide()
                true
            }

            KeyEvent.KEYCODE_DPAD_LEFT -> {
                viewModel.previousSlide()
                true
            }

            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_DPAD_UP -> {
                viewModel.togglePause()
                true
            }

            KeyEvent.KEYCODE_DPAD_CENTER -> {
                viewModel.openMenu()
                true
            }

            KeyEvent.KEYCODE_BACK -> {
                if (!backPressedOnce) {
                    backPressedOnce = true

                    Toast.makeText(
                        context,
                        context.getString(R.string.press_again_to_exit),
                        Toast.LENGTH_SHORT
                    ).show()

                    scope.launch {
                        delay(2000)
                        backPressedOnce = false
                    }

                    true
                } else {
                    (context as? Activity)?.finishAffinity()
                    true
                }
            }

            else -> false
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(
        state.menuVisible,
        state.isAdvertisingShowing,
        ignoreNextCenter,
        backPressedOnce
    ) {
        RemoteKeyEventBus.keyEvents.collect { keyCode ->
            handleSlideshowRemoteKey(keyCode)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            shortcutPlaybackJob?.cancel()
            shortcutOverlayManager.clear()
        }
    }

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->

            when (event) {

                Lifecycle.Event.ON_STOP -> {
                    viewModel.pausePlayback()
                }

                Lifecycle.Event.ON_RESUME -> {
                    viewModel.resumePlayback()
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(state.showSlideIndicator) {
        if (state.showSlideIndicator) {
            delay(5000)
            viewModel.hideSlideIndicator()
        }
    }

    LaunchedEffect(state.isAdvertisingShowing) {
        if (state.isAdvertisingShowing) {
            shortcutOverlayManager.clear()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (state.isAdvertisingShowing) {
                    return@onPreviewKeyEvent event.nativeKeyEvent.keyCode in setOf(
                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_DPAD_LEFT,
                        KeyEvent.KEYCODE_DPAD_RIGHT,
                        KeyEvent.KEYCODE_DPAD_UP,
                        KeyEvent.KEYCODE_DPAD_DOWN,
                        KeyEvent.KEYCODE_BACK,
                        KeyEvent.KEYCODE_MEDIA_NEXT,
                        KeyEvent.KEYCODE_MEDIA_PREVIOUS
                    )
                }

                if (state.menuVisible) return@onPreviewKeyEvent false

                if (ignoreNextCenter) {
                    ignoreNextCenter = false
                    return@onPreviewKeyEvent true
                }

                if (event.nativeKeyEvent.action != KeyEvent.ACTION_UP) {
                    return@onPreviewKeyEvent false
                }

                handleSlideshowRemoteKey(event.nativeKeyEvent.keyCode)
            }
    ) {
        if (state.slides.isNotEmpty() && engine != null) {
            engine.Render(
                modifier = Modifier.fillMaxSize(),
                currentIndex = state.currentIndex,
                currentSlideOverride = if (state.isAdvertisingShowing) AdvertisingSlide else null,
                isPaused = state.isPaused,
                onAutoNext = { viewModel.autoNext() }
            )

            if (state.showSlideIndicator && !state.isAdvertisingShowing) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${state.currentIndex + 1} / ${state.slides.size}",
                        color = Color.White
                    )
                }
            }

            if (state.isPaused && !state.isAdvertisingShowing) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp)
                        .background(
                            Color.Black.copy(alpha = 0.75f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.playback_paused_message),
                        color = Color.White
                    )
                }
            }
        } else {
            AppLoadingSurface(
                backgroundName = AppVisualAssets.EMPTY_CONTENT_BACKGROUND,
                fallbackBackgroundColor = Color.DarkGray,
                showLoadingIndicator = false
            )

            val userManualQrBitmap = remember {
                generateQrCode(ExternalLinks.USER_MANUAL_URL, size = 360).asImageBitmap()
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.manual_privacy_title),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Image(
                        bitmap = userManualQrBitmap,
                        contentDescription = stringResource(R.string.qr_manual_content_description),
                        modifier = Modifier.size(172.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.scan_for_help),
                        color = Color.Gray
                    )
                }
            }
        }

        LaunchedEffect(state.menuVisible) {
            if (state.menuVisible && !state.isAdvertisingShowing) {
                delay(50)
            }
        }

        if (state.menuVisible) {
            SideMenu(
                currentAnimation = state.currentAnimation,
                currentGlobalImageDurationMs = state.globalImageDurationMs,
                externalMenuViewModel = externalMenuViewModel,
                onAnimationSelected = { viewModel.changeAnimation(it) },
                onGlobalImageDurationSelected = { viewModel.changeGlobalImageDuration(it) },
                onImageDurationSelected = { path, durationMs ->
                    viewModel.setImageCustomDuration(path, durationMs)
                    externalMenuViewModel.reloadCurrentView()
                },
                onUseGlobalImageDuration = { path ->
                    viewModel.clearImageCustomDuration(path)
                    externalMenuViewModel.reloadCurrentView()
                },
                onUseGlobalDurationForFolder = { path ->
                    viewModel.clearImageDurationsInFolder(path)
                    externalMenuViewModel.reloadCurrentView()
                },
                onUseGlobalDurationForAllImages = {
                    viewModel.clearAllImageDurations()
                    externalMenuViewModel.reloadCurrentView()
                },
                onPlayExternalFolder = { path ->
                    viewModel.toggleMenu()
                    scope.launch {
                        isFolderLoading = true
                        delay(LoadingUiDefaults.FOLDER_SELECTION_LOADING_MS)
                        viewModel.selectExternalFolder(File(path))
                        isFolderLoading = false
                    }
                },
                canImportFromDevice = canUseTouchImports,
                onImportFromFiles = { targetPath ->
                    requestImport(MediaImportSource.Files, targetPath)
                },
                onImportFromGallery = { targetPath ->
                    requestImport(MediaImportSource.Gallery, targetPath)
                },
                onClose = {
                    ignoreNextCenter = true
                    viewModel.closeMenu()
                },
                onForceUsbScan = {
                    scope.launch {
                        val imported = viewModel.forceUsbScanAndReturnResult()
                        if (imported) {
                            externalMenuViewModel.reloadCurrentView()
                        }
                    }
                },
                onVisibilityChanged = {
                    viewModel.reloadExternalFolderIfSelected()
                }
            )
        }

        if (showDestinationDialog) {
            ImportDestinationDialog(
                folders = externalMenuState.folders,
                selectedFolderPath = destinationFolderPath,
                onFolderSelected = { destinationFolderPath = it },
                onCreateFolderRequested = { showDestinationNewFolderDialog = true },
                onContinue = {
                    val source = pendingImportSource
                    val targetPath = destinationFolderPath
                    if (source != null && targetPath != null) {
                        showDestinationDialog = false
                        pendingImportSource = null
                        launchImport(source, targetPath)
                        viewModel.toggleMenu()
                    }
                },
                onDismiss = {
                    showDestinationDialog = false
                    pendingImportSource = null
                    destinationFolderPath = null
                }
            )
        }

        if (showDestinationNewFolderDialog) {
            NewFolderNameDialog(
                folderExists = { externalMenuViewModel.folderNameExists(it) },
                onCreate = { folderName ->
                    externalMenuViewModel.createFolder(folderName)?.let { createdFolder ->
                        destinationFolderPath = createdFolder.path
                        showDestinationNewFolderDialog = false
                    }
                },
                onDismiss = { showDestinationNewFolderDialog = false }
            )
        }

        AnimatedVisibility(
            visible = shortcutOverlayText != null && !state.isAdvertisingShowing,
            enter = fadeIn(tween(durationMillis = 160)),
            exit = fadeOut(tween(durationMillis = 160)),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .background(
                        Color.Black.copy(alpha = 0.82f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 22.dp, vertical = 12.dp)
            ) {
                Text(
                    text = shortcutOverlayText.orEmpty(),
                    color = Color.White
                )
            }
        }

        state.usbMessage?.let { message ->
            if (state.isUsbLoading) {
                AppLoadingSurface(
                    backgroundName = AppVisualAssets.EMPTY_CONTENT_BACKGROUND,
                    message = message,
                    fallbackBackgroundColor = Color.Black,
                    showOverlayScrim = true
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Box(
                        modifier = Modifier
                            .padding(32.dp)
                            .background(
                                Color(0xFF1E1E1E),
                                RoundedCornerShape(18.dp)
                            )
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = message,
                            color = Color.White
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isFolderLoading && !state.isAdvertisingShowing,
            enter = fadeIn(tween(durationMillis = LoadingUiDefaults.FOLDER_SELECTION_FADE_MS)),
            exit = fadeOut(tween(durationMillis = LoadingUiDefaults.FOLDER_SELECTION_FADE_MS))
        ) {
            AppLoadingSurface(
                backgroundName = AppVisualAssets.EMPTY_CONTENT_BACKGROUND,
                message = stringResource(R.string.loading_content),
                fallbackBackgroundColor = Color.Black,
                showOverlayScrim = true
            )
        }

        Image(
            painter = painterResource(id = R.drawable.litvy_letter),
            contentDescription = stringResource(R.string.logo_litvy_content_description),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .height(45.dp)
                .alpha(0.5f)
                .then(
                    if (isMobile){
                        Modifier.clickable {
                            viewModel.openMenu()
                            onShowTouchRemote()
                        }
                    } else {
                        Modifier
                    }
                )
        )
    }
}

