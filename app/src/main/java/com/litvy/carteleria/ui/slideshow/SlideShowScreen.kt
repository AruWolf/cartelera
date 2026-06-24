package com.litvy.carteleria.ui.slideshow

import com.litvy.carteleria.R
import android.app.Activity
import android.graphics.Bitmap
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
import com.litvy.carteleria.ui.menu.SideMenu
import com.litvy.carteleria.ui.touchremote.RemoteKeyEventBus
import com.litvy.carteleria.util.network.LocalCartelServer
import com.litvy.carteleria.util.qr.generateQrCode
import com.litvy.carteleria.util.usb.UsbContentManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun SlideShowScreen() {
    val context = LocalContext.current
    var backPressedOnce by remember { mutableStateOf(false) }

    val hiddenManager = remember { HiddenFileManager(context) }
    val imageDurationManager = remember { ImageDurationManager(context) }
    val folderShortcutManager = remember { FolderShortcutManager(context) }

    val viewModel = remember {
        SlideShowViewModel(
            externalProvider = AppStorageSlideProvider(context, hiddenManager, imageDurationManager),
            prefs = CartelPreferences(context),
            server = LocalCartelServer(context),
            usbImporter = UsbContentManager(context),
            context = context
        )
    }
    val state by viewModel.uiState.collectAsState()
    val serverUrl by viewModel.serverUrl.collectAsState()

    val focusRequester = remember { FocusRequester() }
    var showQr by remember { mutableStateOf(false) }
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
        if (state.isAdvertisingShowing) return true

        if (state.menuVisible) return false

        if (ignoreNextCenter) {
            ignoreNextCenter = false
            return true
        }

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
                if (showQr) {
                    showQr = false
                    true
                } else if (!backPressedOnce) {
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
        viewModel.startServer()
    }

    LaunchedEffect(
        state.menuVisible,
        state.isAdvertisingShowing,
        showQr,
        ignoreNextCenter,
        backPressedOnce
    ) {
        RemoteKeyEventBus.keyEvents.collect { keyCode ->
            handleSlideshowRemoteKey(keyCode)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopServer()
            shortcutPlaybackJob?.cancel()
            shortcutOverlayManager.clear()
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
            showQr = false
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
                        .align(Alignment.BottomStart)
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

        if (state.menuVisible && !state.isAdvertisingShowing) {
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
                onShowQr = {
                    showQr = true
                    viewModel.toggleMenu()
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

        if (showQr && serverUrl.isNotEmpty() && !state.isAdvertisingShowing) {
            val qrBitmap: Bitmap = remember(serverUrl) {
                generateQrCode(serverUrl)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
            ) {
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
                            text = stringResource(R.string.scan_to_upload_content),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = stringResource(R.string.qr_content_description),
                            modifier = Modifier.size(220.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = serverUrl,
                            color = Color.Gray
                        )
                    }
                }
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
        )
    }
}

