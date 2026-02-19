package com.litvy.carteleria.ui.slideshow

import android.content.Context
import android.graphics.Bitmap
import android.view.KeyEvent
import android.widget.Toast
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
import com.litvy.carteleria.util.network.LocalHttpServer
import com.litvy.carteleria.slides.AppStorageSlideProvider
import com.litvy.carteleria.slides.AssetSlideProvider
import com.litvy.carteleria.slides.Slide
import com.litvy.carteleria.ui.menu.SideMenu
import com.litvy.carteleria.util.generateQrCode
import java.io.File
import java.net.Inet4Address
import java.net.NetworkInterface
import com.litvy.carteleria.slides.SlideSpeed

enum class ContentMode {
    INTERNAL,
    EXTERNAL
}

@Composable
fun SlideShowScreen() {

    val context: Context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    var contentMode by remember { mutableStateOf(ContentMode.INTERNAL) }
    var selectedInternalFolder by remember { mutableStateOf<String?>(null) }
    var selectedExternalFolder by remember { mutableStateOf<File?>(null) }
    var currentAnimation by remember { mutableStateOf("fade") }

    var showQr by remember { mutableStateOf(false) }
    var menuVisible by remember { mutableStateOf(false) }
    var reloadTrigger by remember { mutableStateOf(0) }

    val assetProvider = remember { AssetSlideProvider(context) }
    val externalProvider = remember { AppStorageSlideProvider(context) }

    val server = remember { LocalHttpServer(context, 8080) }
    var serverUrl by remember { mutableStateOf("") }

    var slideSpeed by remember { mutableStateOf(SlideSpeed.NORMAL) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()

        try {
            server.start()
            val ip = getLocalIpAddress()
            serverUrl = "http://$ip:8080"

            Toast.makeText(
                context,
                "Servidor en $serverUrl",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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

    val engine = remember(slides, transition, slideSpeed) {
        if (slides.isNotEmpty()) {
            EvokeSlide(
                slides = slides,
                transition = transition,
                speed = slideSpeed
            )
        } else null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->

                if (event.nativeKeyEvent.action != KeyEvent.ACTION_UP)
                    return@onPreviewKeyEvent false

                when (event.nativeKeyEvent.keyCode) {

                    // 🔥 Abrir menú con botón central
                    KeyEvent.KEYCODE_DPAD_CENTER -> {
                        if (!menuVisible) {
                            menuVisible = true
                            true
                        } else {
                            false
                        }
                    }

                    // 🔥 BACK ahora cierra menú
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

        if (slides.isNotEmpty() && engine != null) {
            engine.Render(Modifier.fillMaxSize())
        } else {
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

        if (menuVisible) {

            val internalFolders = assetProvider.listFolders()
            val externalFolders = externalProvider.listFolders().map { it.name }

            SideMenu(
                currentAnimation = currentAnimation,
                currentSpeed = slideSpeed,
                folders = internalFolders,
                externalFolders = externalFolders,
                currentFolder = selectedInternalFolder ?: "",
                currentExternalFolder = selectedExternalFolder?.name ?: "",

                onAnimationSelected = { animationKey ->
                    currentAnimation = animationKey
                },

                onSpeedSelected = { speed ->
                    slideSpeed = speed
                },

                onFolderSelected = { folderName ->
                    contentMode = ContentMode.INTERNAL
                    selectedInternalFolder = folderName
                    menuVisible = false
                },

                onExternalFolderSelected = { folderName ->
                    val folderFile = externalProvider
                        .listFolders()
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

                onClose = {
                    menuVisible = false
                }
            )
        }
    }
}

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
