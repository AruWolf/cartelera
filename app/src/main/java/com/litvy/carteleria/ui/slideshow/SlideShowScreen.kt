package com.litvy.carteleria.ui.slideshow

import android.content.Context
import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import com.litvy.carteleria.animations.TvTransitions
import com.litvy.carteleria.domain.propaganda.AssetPropaganda
import com.litvy.carteleria.engine.EvokeSlide
import com.litvy.carteleria.slides.AssetSlideProvider
import com.litvy.carteleria.slides.DcimSlideProvider
import com.litvy.carteleria.slides.ExternalSlideProvider
import com.litvy.carteleria.slides.Slide
import com.litvy.carteleria.ui.menu.SideMenu
import java.io.File

enum class ContentMode {
    INTERNAL,
    EXTERNAL
}

@Composable
fun SlideShowScreen() {

    var menuVisible by remember { mutableStateOf(false) }
    var selectedAnimation by remember { mutableStateOf("fade") }

    var contentMode by remember { mutableStateOf(ContentMode.INTERNAL) }
    val context: Context = LocalContext.current

    var selectedFolder by remember { mutableStateOf("promos") }

    var externalFolders by remember { mutableStateOf<List<File>>(emptyList()) }
    var selectedExternalFolder by remember { mutableStateOf<File?>(null) }

    /* ---------- PROVIDERS ---------- */

    val assetProvider = remember {
        AssetSlideProvider(context)
    }

    val dcimProvider = remember {
        DcimSlideProvider()
    }

    val internalFolders = remember {
        assetProvider.listFolders()
    }

    /* ---------- SLIDES ---------- */

    val slides = remember(contentMode, selectedFolder, selectedExternalFolder) {
        when (contentMode) {
            ContentMode.INTERNAL ->
                AssetPropaganda(assetProvider, selectedFolder).slides()

            ContentMode.EXTERNAL ->
                selectedExternalFolder?.let {
                    dcimProvider.loadFromFolder(it)
                } ?: emptyList()
        }
    }

    /* ---------- TRANSITIONS ---------- */

    val transitions = remember {
        mapOf(
            "fade" to TvTransitions.fade<Slide>(),
            "scale" to TvTransitions.scale<Slide>(),
            "left" to TvTransitions.slideLeft<Slide>(),
            "up" to TvTransitions.slideUp<Slide>()
        )
    }

    val engine = remember(
        selectedAnimation,
        contentMode,
        selectedFolder,
        selectedExternalFolder
    ) {
        EvokeSlide(
            slides = slides,
            transition = transitions[selectedAnimation]!!
        )
    }

    /* ---------- UI ---------- */

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
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
                    KeyEvent.KEYCODE_MENU,
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        menuVisible = !menuVisible
                        true
                    }

                    KeyEvent.KEYCODE_BACK -> {
                        if (menuVisible) {
                            menuVisible = false
                            true
                        } else false
                    }

                    else -> false
                }
            }
    ) {

        engine.Render(Modifier.fillMaxSize())

        if (menuVisible) {

            val currentExternalFolderName =
                selectedExternalFolder?.name ?: ""

            SideMenu(
                currentAnimation = selectedAnimation,
                folders = internalFolders,
                externalFolders = externalFolders.map { it.name },
                currentFolder = selectedFolder,
                currentExternalFolder = currentExternalFolderName,

                onAnimationSelected = {
                    selectedAnimation = it
                    menuVisible = false
                },

                onFolderSelected = {
                    contentMode = ContentMode.INTERNAL
                    selectedFolder = it
                    menuVisible = false
                },

                onExternalFolderSelected = { folderName ->
                    val folder = externalFolders.find { it.name == folderName }
                    folder?.let {
                        selectedExternalFolder = it
                        contentMode = ContentMode.EXTERNAL
                    }
                    menuVisible = false
                },

                onPickExternalFolder = {

                    val debug = dcimProvider.debugInfo()

                    Toast.makeText(
                        context,
                        debug,
                        Toast.LENGTH_LONG
                    ).show()

                    externalFolders = dcimProvider.listFolders()

                    // Si no hay carpetas pero hay imágenes sueltas:
                    if (externalFolders.isEmpty()) {
                        val slidesFromRoot = dcimProvider.loadSlidesFromRoot()

                        if (slidesFromRoot.isNotEmpty()) {
                            selectedExternalFolder = dcimProvider.getRoot()
                            contentMode = ContentMode.EXTERNAL
                        }
                    }
                },


                        onClose = { menuVisible = false }
            )
        }
    }
}

