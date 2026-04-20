package com.litvy.carteleria.ui.slideshow

import android.app.Activity
import android.graphics.Bitmap
import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.animations.TvTransitions
import com.litvy.carteleria.data.CartelPreferences
import com.litvy.carteleria.data.external.AppStorageExternalRepository
import com.litvy.carteleria.data.external.HiddenFileManager
import com.litvy.carteleria.domain.external.usecase.CopyExternalFileUseCase
import com.litvy.carteleria.domain.external.usecase.DeleteExternalFileUseCase
import com.litvy.carteleria.domain.external.usecase.DeleteExternalFolderUseCase
import com.litvy.carteleria.domain.external.usecase.ExternalContentUseCases
import com.litvy.carteleria.domain.external.usecase.HideExternalFileUseCase
import com.litvy.carteleria.domain.external.usecase.ListExternalFilesUseCase
import com.litvy.carteleria.domain.external.usecase.ListExternalFoldersUseCase
import com.litvy.carteleria.domain.external.usecase.MoveExternalFileUseCase
import com.litvy.carteleria.domain.external.usecase.ShowExternalFileUseCase
import com.litvy.carteleria.engine.EvokeSlide
import com.litvy.carteleria.slides.AppStorageSlideProvider
import com.litvy.carteleria.slides.Slide
import com.litvy.carteleria.ui.menu.ExternalMenuViewModel
import com.litvy.carteleria.ui.menu.SideMenu
import com.litvy.carteleria.util.network.LocalCartelServer
import com.litvy.carteleria.util.qr.generateQrCode
import com.litvy.carteleria.util.usb.UsbContentManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun SlideShowScreen() {

    val context = LocalContext.current
    var backPressedOnce by remember { mutableStateOf(false) }

    val hiddenManager = remember { HiddenFileManager(context) }

    val viewModel = remember {
        SlideShowViewModel(
            externalProvider = AppStorageSlideProvider(context, hiddenManager),
            prefs = CartelPreferences(context),
            server = LocalCartelServer(context),
            usbImporter = UsbContentManager(context)
        )
    }
    val state by viewModel.uiState.collectAsState()
    val serverUrl by viewModel.serverUrl.collectAsState()

    val focusRequester = remember { FocusRequester() }
    var showQr by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val externalProvider = remember {
        AppStorageSlideProvider(context, hiddenManager)
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

    val engine = remember(state.slides, transition, state.slideSpeed) {
        if (state.slides.isNotEmpty()) {
            EvokeSlide(
                slides = state.slides,
                transition = transition,
                speed = state.slideSpeed
            )
        } else {
            null
        }
    }

    val externalRepository = remember {
        AppStorageExternalRepository(
            provider = externalProvider,
            hiddenManager = hiddenManager
        )
    }

    val externalUseCases = remember {
        ExternalContentUseCases(
            listFolders = ListExternalFoldersUseCase(externalRepository),
            listFiles = ListExternalFilesUseCase(externalRepository),
            deleteFile = DeleteExternalFileUseCase(externalRepository),
            deleteFolder = DeleteExternalFolderUseCase(externalRepository),
            copyFile = CopyExternalFileUseCase(externalRepository),
            moveFile = MoveExternalFileUseCase(externalRepository),
            hideFile = HideExternalFileUseCase(externalRepository),
            showFile = ShowExternalFileUseCase(externalRepository)
        )
    }

    val externalMenuViewModel = remember {
        ExternalMenuViewModel(externalUseCases)
    }

    var ignoreNextCenter by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        viewModel.startServer()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopServer()
        }
    }

    LaunchedEffect(state.showSlideIndicator) {
        if (state.showSlideIndicator) {
            delay(5000)
            viewModel.hideSlideIndicator()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->

                if (state.menuVisible) return@onPreviewKeyEvent false

                if (ignoreNextCenter) {
                    ignoreNextCenter = false
                    return@onPreviewKeyEvent true
                }

                if (event.nativeKeyEvent.action != KeyEvent.ACTION_UP) {
                    return@onPreviewKeyEvent false
                }

                when (event.nativeKeyEvent.keyCode) {
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
                            return@onPreviewKeyEvent true
                        }

                        if (!backPressedOnce) {
                            backPressedOnce = true

                            Toast.makeText(
                                context,
                                "Presione nuevamente para salir",
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
    ) {
        if (state.slides.isNotEmpty() && engine != null) {
            engine.Render(
                modifier = Modifier.fillMaxSize(),
                currentIndex = state.currentIndex,
                isPaused = state.isPaused,
                onAutoNext = { viewModel.autoNext() }
            )

            if (state.showSlideIndicator) {
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

            if (state.isPaused) {
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
                        text = "Reproducción en pausa.\nPresione ARRIBA o el botón PAUSA para reanudar.",
                        color = Color.White
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sin contenido.\nPresione OK para abrir menú.",
                    color = Color.White
                )
            }
        }

        LaunchedEffect(state.menuVisible) {
            if (state.menuVisible) {
                delay(50)
            }
        }

        if (state.menuVisible) {
            SideMenu(
                currentAnimation = state.currentAnimation,
                currentSpeed = state.slideSpeed,
                externalMenuViewModel = externalMenuViewModel,
                onAnimationSelected = { viewModel.changeAnimation(it) },
                onSpeedSelected = { viewModel.changeSpeed(it) },
                onPlayExternalFolder = { path ->
                    viewModel.selectExternalFolder(File(path))
                    viewModel.toggleMenu()
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

        if (showQr && serverUrl.isNotEmpty()) {
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
                            text = "Escaneá para cargar contenido",
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR",
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
}
