package com.litvy.carteleria.ui.slideshow

import android.content.Context
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
import com.litvy.carteleria.engine.EvokeSlide
import com.litvy.carteleria.slides.*
import com.litvy.carteleria.ui.menu.SideMenu
import com.litvy.carteleria.util.generateQrCode
import com.litvy.carteleria.util.network.LocalHttpServer
import com.litvy.carteleria.util.usb.UsbContentManager
import kotlinx.coroutines.delay
import java.io.File
import java.net.Inet4Address
import java.net.NetworkInterface

enum class ContentMode {
    INTERNAL,
    EXTERNAL
}

@Composable
fun SlideShowScreen() {

    val context: Context = LocalContext.current // Contexto
    val focusRequester = remember { FocusRequester() } // Solicitud de foco/atención en pantalla

    // --- ESTADOS UI ---
    var contentMode by remember { mutableStateOf(ContentMode.INTERNAL) } // Contenido de reproducción, inicializado en Interno(Assets)

    var selectedInternalFolder by remember { mutableStateOf<String?>(null) }
    var selectedExternalFolder by remember { mutableStateOf<File?>(null) }
    var currentAnimation by remember { mutableStateOf("fade") }

    var showQr by remember { mutableStateOf(false) }
    var menuVisible by remember { mutableStateOf(false) }
    var reloadTrigger by remember { mutableStateOf(0) }

    var slideSpeed by remember { mutableStateOf(SlideSpeed.NORMAL) }

    var currentIndex by remember { mutableStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }

    var showSlideIndicator by remember { mutableStateOf(false) }

    // --- PROVIDERS --- (importan las imagenes)
    val assetProvider = remember { AssetSlideProvider(context) }
    val externalProvider = remember { AppStorageSlideProvider(context) }

    // --- MANEJO DE SERVIDOR LOCAL --- (Necesario para cargar imagenes por red LAN)
    val server = remember { LocalHttpServer(context, 8080) }
    var serverUrl by remember { mutableStateOf("") }

    // --- MANEJO DE USB --- (Necesario para escanear y cargar imagenes desde USB)
    val usbManager = remember {
        UsbContentManager(context) {
            reloadTrigger++
        }
    }

    // Arranque de servidor LAN y escaneo USB
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        usbManager.startPeriodicScan()
        try {
            server.start()
            val ip = getLocalIpAddress()
            serverUrl = "http://$ip:8080"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Selección de contenido (slides)
    val slides = remember(
        contentMode,
        selectedInternalFolder,
        selectedExternalFolder,
        reloadTrigger
    ) {
        when (contentMode) {
            ContentMode.INTERNAL -> {
                selectedInternalFolder?.let {
                    assetProvider.loadFrom(it)
                } ?: emptyList()
            }

            ContentMode.EXTERNAL -> {
                selectedExternalFolder?.let { folder ->
                    externalProvider.loadFromFolder(folder)
                } ?: emptyList()
            }
        }
    }

    //
    LaunchedEffect(slides) {
        currentIndex = 0
        isPaused = false
    }

    // --- ANIMACIONES ---
    val transition = remember(currentAnimation) {
        when (currentAnimation) {
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
    val engine = remember(slides, transition, slideSpeed) {
        if (slides.isNotEmpty()) {
            EvokeSlide(
                slides = slides,
                transition = transition,
                speed = slideSpeed
            )
        } else null
    }

    // Mostrar indicador númerico de slides
    fun showIndicatorTemporarily() {
        showSlideIndicator = true
    }

    // Metodo para desplazarse hacia el siguiente slide
    fun nextSlide() {
        if (slides.isEmpty()) return
        currentIndex = (currentIndex + 1) % slides.size
        showIndicatorTemporarily()
    }

    // Metodo para desplazarse hacia el slide anterior
    fun previousSlide() {
        if (slides.isEmpty()) return
        currentIndex =
            if (currentIndex - 1 < 0)
                slides.lastIndex
            else
                currentIndex - 1
        showIndicatorTemporarily()
    }

    // Ocultar indicador, numerico de slide, luego de 5 segundos
    LaunchedEffect(showSlideIndicator) {
        if (showSlideIndicator) {
            delay(5000) // Tiempo de visibilidad
            showSlideIndicator = false
        }
    }

    // --- UI ---

    // Contenedor principal de la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            // Detección de eventos de teclado
            .onPreviewKeyEvent { event ->

                if (event.nativeKeyEvent.action != KeyEvent.ACTION_UP)
                    return@onPreviewKeyEvent false

                when (event.nativeKeyEvent.keyCode) {

                    // Desplazamiento entre slides (Botones direccionales, izquierda, derecha)
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        nextSlide()
                        true
                    }

                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        previousSlide()
                        true
                    }

                    // Pausa/reanudación de reproducción (Botón direccional arriba o botón pausa)
                    KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        isPaused = !isPaused
                        true
                    }

                    //Apertura de menu (Botón OK/Central)
                    KeyEvent.KEYCODE_DPAD_CENTER -> {
                        if (!menuVisible) {
                            menuVisible = true
                            true
                        } else false
                    }

                    // Cierre de menu o QR (Botón Back/Retroceso)
                    KeyEvent.KEYCODE_BACK -> {
                        when {
                            showQr -> {
                                showQr = false
                                reloadTrigger++
                                true
                            }

                            menuVisible -> {
                                menuVisible = false
                                true
                            }

                            else -> false
                        }
                    }

                    else -> false
                }
            }
    ) {

        // Renderización de Slides
        if (slides.isNotEmpty() && engine != null) {

            engine.Render(
                modifier = Modifier.fillMaxSize(),
                currentIndex = currentIndex,
                isPaused = isPaused,
                onAutoNext = {
                    currentIndex = (currentIndex + 1) % slides.size
                }
            )

            // Indicador temporal (se activa al navegar con el control)
            if (showSlideIndicator) {
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
                        text = "${currentIndex + 1} / ${slides.size}",
                        color = Color.White
                    )
                }
            }

            // Mensaje al pausar (Se ejecuta al pausar la reproducción)
            if (isPaused) {
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

        // Menu lateral
        if (menuVisible) {
            SideMenu(
                currentAnimation = currentAnimation,
                currentSpeed = slideSpeed,
                folders = assetProvider.listFolders(),
                externalFolders = externalProvider.listFolders().map { it.name },
                currentFolder = selectedInternalFolder ?: "",
                currentExternalFolder = selectedExternalFolder?.name ?: "",
                onAnimationSelected = { currentAnimation = it },
                onSpeedSelected = { slideSpeed = it },
                onFolderSelected = {
                    contentMode = ContentMode.INTERNAL
                    selectedInternalFolder = it
                    menuVisible = false
                },
                onExternalFolderSelected = { folderName ->
                    val folderFile =
                        externalProvider.listFolders()
                            .firstOrNull { it.name == folderName }

                    if (folderFile != null) {
                        contentMode = ContentMode.EXTERNAL
                        selectedExternalFolder = folderFile
                    }
                    menuVisible = false
                },
                onPickExternalFolder = { },
                onShowQr = {
                    contentMode = ContentMode.EXTERNAL
                    showQr = true
                    menuVisible = false
                },
                onForceUsbScan = {
                    usbManager.forceScan()
                },
                onClose = { menuVisible = false }
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
        DisposableEffect(Unit) {
            onDispose {
                usbManager.stop() // Detener el escaneo USB
                server.stop() // Detener el servidor LAN
            }
        }
    }
}


// --- UTIL ---
fun getLocalIpAddress(): String {
    val interfaces = NetworkInterface.getNetworkInterfaces()
    for (intf in interfaces) {
        for (addr in intf.inetAddresses) {
            if (!addr.isLoopbackAddress && addr is Inet4Address) {
                return addr.hostAddress ?: ""
            }
        }
    }
    return ""
}
