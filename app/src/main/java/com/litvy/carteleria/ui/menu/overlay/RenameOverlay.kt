package com.litvy.carteleria.ui.menu.overlay

import android.view.KeyEvent
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
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.RenameOverlay(
    initialName: String,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {

    var text by remember { mutableStateOf(initialName) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showKeyboard by remember { mutableStateOf(false) }

    val textFocus = remember { FocusRequester() }
    val confirmFocus = remember { FocusRequester() }
    val cancelFocus = remember { FocusRequester() }

    var focusIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        textFocus.requestFocus()
    }

    LaunchedEffect(focusIndex, showKeyboard) {
        if (!showKeyboard) {
            when (focusIndex) {
                0 -> textFocus.requestFocus()
                1 -> confirmFocus.requestFocus()
                2 -> cancelFocus.requestFocus()
            }
        }
    }

    Box(
        modifier = Modifier
            .align(Alignment.Center)
            .fillMaxSize()
    ) {

        // Desplaza el overlay al estar activo el teclado.
        val overlayAlignment =
            if (showKeyboard) Alignment.TopCenter
            else Alignment.Center

        Column(
            modifier = Modifier
                .align(overlayAlignment)
                .padding(top = if (showKeyboard) 80.dp else 0.dp)
                .width(520.dp)
                .background(
                    Color(0xFF1E1E1E),
                    RoundedCornerShape(20.dp)
                )
                .padding(28.dp)
                .focusable()
                .onPreviewKeyEvent { event ->

                    if (showKeyboard) return@onPreviewKeyEvent true

                    val native = event.nativeKeyEvent

                    if (native.action != KeyEvent.ACTION_DOWN) {
                        return@onPreviewKeyEvent false
                    }

                    val unicode = native.unicodeChar

                    if (!showKeyboard && unicode != 0 && !Character.isISOControl(unicode)) {
                        text += unicode.toChar()
                        return@onPreviewKeyEvent true
                    }

                    when (native.keyCode) {

                        KeyEvent.KEYCODE_BACK -> {
                            onCancel()
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            focusIndex = (focusIndex + 1).coerceAtMost(2)
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_UP -> {
                            focusIndex = (focusIndex - 1).coerceAtLeast(0)
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            if (focusIndex == 2) focusIndex = 1
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            if (focusIndex == 1) focusIndex = 2
                            true
                        }

                        KeyEvent.KEYCODE_DEL -> {
                            if (text.isNotEmpty()) {
                                text = text.dropLast(1)
                            }
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER -> {

                            when (focusIndex) {

                                0 -> showKeyboard = true

                                1 -> {
                                    if (text.isBlank()) {
                                        errorMessage = "El nombre no puede estar vacío."
                                    } else {
                                        errorMessage = null
                                        onConfirm(text.trim())
                                    }
                                }

                                2 -> onCancel()
                            }

                            true
                        }

                        else -> false
                    }
                }
        ) {

            Text(
                text = "Renombrar",
                color = Color.White
            )

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(textFocus)
                    .focusable()
                    .background(
                        if (focusIndex == 0)
                            Color(0xFF505050)
                        else
                            Color(0xFF2E2E2E),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(14.dp)
            ) {
                Text(text = text, color = Color.White)
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(confirmFocus)
                        .focusable()
                        .background(
                            if (focusIndex == 1)
                                Color(0xFF505050)
                            else
                                Color(0xFF333333),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Confirmar", color = Color.White)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(cancelFocus)
                        .focusable()
                        .background(
                            if (focusIndex == 2)
                                Color(0xFF505050)
                            else
                                Color(0xFF333333),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        }

        if (showKeyboard) {
            TvKeyboardOverlay(
                onKeyPress = { key ->
                    when (key) {
                        "⌫" -> if (text.isNotEmpty()) text = text.dropLast(1)
                        "ESP" -> text += " "
                        else -> text += key
                    }
                },
                onDismiss = {
                    showKeyboard = false
                    textFocus.requestFocus()
                }
            )
        }
    }
}