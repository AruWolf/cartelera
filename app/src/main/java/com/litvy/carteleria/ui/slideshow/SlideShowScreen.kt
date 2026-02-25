package com.litvy.carteleria.ui.slideshow

import android.graphics.Bitmap
import android.view.KeyEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.litvy.carteleria.engine.EvokeSlide
import com.litvy.carteleria.slides.*
import com.litvy.carteleria.ui.menu.SideMenu
import com.litvy.carteleria.util.qr.generateQrCode
import com.litvy.carteleria.util.usb.UsbContentManager
import kotlinx.coroutines.delay
import com.litvy.carteleria.util.network.LocalCartelServer
import com.litvy.carteleria.data.external.AppStorageExternalRepository
import com.litvy.carteleria.domain.external.usecase.*
import com.litvy.carteleria.ui.menu.external.ExternalMenuViewModel
import java.io.File


enum class ContentMode {
    INTERNAL,
    EXTERNAL
}

@Composable
fun SlideShowScreen() {

    // --- View Model ---
    val context = LocalContext.current

    val viewModel = remember {
        SlideShowViewModel(
            assetProvider = AssetSlideProvider(context),
            externalProvider = AppStorageSlideProvider(context),
            prefs = CartelPreferences(context),
            server = LocalCartelServer(context),
            usbImporter = UsbContentManager(context)
        )
    }
    val state by viewModel.uiState.collectAsState()

    // URL servidor LAN
    val serverUrl by viewModel.serverUrl.collectAsState()

    val focusRequester = remember { FocusRequester() } // Solicitud de foco/atención en pantalla

    var showQr by remember { mutableStateOf(false) }


    // --- PROVIDERS --- (importan las imagenes)
    val assetProvider = remember { AssetSlideProvider(context) }
    val externalProvider = remember { AppStorageSlideProvider(context) }

    // --- ANIMACIONES ---
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

    // Parametros de reproducción(slides, animacion, velocidad)
    val engine = remember(state.slides, transition, state.slideSpeed) {
        if (state.slides.isNotEmpty()) {
            EvokeSlide(
                slides = state.slides,
                transition = transition,
                speed = state.slideSpeed
            )
        } else null
    }

    val externalRepository = remember {
        AppStorageExternalRepository(externalProvider)
    }

    val externalUseCases = remember {
        ExternalContentUseCases(
            listFolders = ListExternalFoldersUseCase(externalRepository),
            listFiles = ListExternalFilesUseCase(externalRepository),
            deleteFile = DeleteExternalFileUseCase(externalRepository),
            deleteFolder = DeleteExternalFolderUseCase(externalRepository),
            copyFile = CopyExternalFileUseCase(externalRepository),
            moveFile = MoveExternalFileUseCase(externalRepository),
            rename = RenameExternalNodeUseCase(externalRepository)
        )
    }

    val externalMenuViewModel = remember {
        ExternalMenuViewModel(externalUseCases)
    }


    // Arranque de servidor LAN - Al iniciar la pantalla
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        viewModel.startServer()
    }

    // Detención de servidor LAN - Al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopServer()
        }
    }

    // Ocultar indicador, numerico de slide, luego de 5 segundos
    LaunchedEffect(state.showSlideIndicator) {
        if (state.showSlideIndicator) {
            delay(5000)
            viewModel.hideSlideIndicator()
        }
    }

    // --- UI ---

    // Contenedor principal de la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->

                // 🚫 Si el menú está abierto, no manejar eventos acá
                if (state.menuVisible) return@onPreviewKeyEvent false

                if (event.nativeKeyEvent.action != KeyEvent.ACTION_UP)
                    return@onPreviewKeyEvent false

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
                        when {
                            showQr -> {
                                showQr = false
                                true
                            }
                            else -> false
                        }
                    }

                    else -> false
                }
            }
    )
    {

        // Renderización de Slides
        if (state.slides.isNotEmpty() && engine != null) {

            engine.Render(
                modifier = Modifier.fillMaxSize(),
                currentIndex = state.currentIndex,
                isPaused = state.isPaused,
                onAutoNext = {
                    viewModel.autoNext()
                }
            )

            // Indicador temporal (se activa al navegar con el control)
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

            // Mensaje al pausar (Se ejecuta al pausar la reproducción)
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

        } else { // Mensaje ejecutado al no haber contenido para reproducir
            Box(
                modifier = Modifier.fillMaxSize(),
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

        // Menu lateral
        if (state.menuVisible) {
            SideMenu(
                currentAnimation = state.currentAnimation,
                currentSpeed = state.slideSpeed,
                folders = assetProvider.listFolders(),
                currentFolder = state.selectedInternalFolder ?: "",
                externalMenuViewModel = externalMenuViewModel,

                onAnimationSelected = {
                    viewModel.changeAnimation(it)
                },

                onSpeedSelected = {
                    viewModel.changeSpeed(it)
                },

                onFolderSelected = {
                    viewModel.selectInternalFolder(it)
                    viewModel.toggleMenu()
                },

                onPlayExternalFolder = { path ->
                    viewModel.selectExternalFolder(File(path))
                    viewModel.toggleMenu()
                },

                onShowQr = {
                    showQr = true
                    viewModel.toggleMenu()
                },

                onForceUsbScan = {
                    viewModel.forceUsbScan()
                },

                onClose = {
                    viewModel.closeMenu()
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
                            text = "Escaneá para cargar contenido externo",
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

        // Mensaje de lectura de usb
        state.usbMessage?.let { message ->

            Box(
                modifier = Modifier
                    .fillMaxSize(),
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


