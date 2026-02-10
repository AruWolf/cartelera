package com.litvy.carteleria.ui.slideshow

import android.content.Intent
import android.view.KeyEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.litvy.carteleria.slides.Slide
import com.litvy.carteleria.ui.menu.SideMenu

enum class ContentMode {
    INTERNAL,
    EXTERNAL
}

@Composable
fun SlideShowScreen() {

    var menuVisible by remember { mutableStateOf(false) }
    var selectedAnimation by remember { mutableStateOf("fade") }

    var contentMode by remember { mutableStateOf(ContentMode.INTERNAL) }

    var selectedFolder by remember { mutableStateOf("promos") }
    var selectedExternalFolder by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { uri ->
            uri ?: return@rememberLauncherForActivityResult

            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            selectedExternalFolder = uri.toString()
            contentMode = ContentMode.EXTERNAL
            menuVisible = false
        }
    )


    /* ---------- PROVIDER ---------- */

    val assetProvider = remember(context) {
        AssetSlideProvider(context)
    }

    val internalFolders = remember(assetProvider) {
        assetProvider.listFolders()
    }

    /* ---------- SLIDES ---------- */

    val slides = remember(contentMode, selectedFolder, selectedExternalFolder) {
        when (contentMode) {
            ContentMode.INTERNAL ->
                AssetPropaganda(assetProvider, selectedFolder).slides()

            ContentMode.EXTERNAL ->
                emptyList() // se completa cuando implementemos SAF / USB
        }
    }

    if (slides.isEmpty()) return

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
            SideMenu(
                currentAnimation = selectedAnimation,
                folders = internalFolders,
                externalFolders = emptyList(), // se llena después
                currentFolder = selectedFolder,
                currentExternalFolder = selectedExternalFolder ?: "",
                onAnimationSelected = {
                    selectedAnimation = it
                    menuVisible = false
                },
                onFolderSelected = {
                    contentMode = ContentMode.INTERNAL
                    selectedFolder = it
                    menuVisible = false
                },
                onExternalFolderSelected = {
                    contentMode = ContentMode.EXTERNAL
                    selectedExternalFolder = it
                    menuVisible = false
                },
                onPickExternalFolder = {
                    folderPickerLauncher.launch(null)
                },
                onClose = { menuVisible = false }
            )
        }
    }
}
