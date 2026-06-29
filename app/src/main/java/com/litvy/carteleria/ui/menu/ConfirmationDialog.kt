package com.litvy.carteleria.ui.menu

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.R
import androidx.compose.ui.window.Dialog
import com.litvy.carteleria.util.DeviceUtils

@Composable
fun ConfirmationDialog(
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var selectedIndex by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val context = LocalContext.current
    val isTv = DeviceUtils.isTv(context)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(420.dp)
                .background(Color.Black.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent { event ->
                    val native = event.nativeKeyEvent

                    if (native.action != KeyEvent.ACTION_DOWN || native.repeatCount > 0) {
                        return@onPreviewKeyEvent false
                    }

                    when (native.keyCode) {
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            selectedIndex = 0
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            selectedIndex = 1
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER,
                        KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                            if (selectedIndex == 0) onDismiss() else onConfirm()
                            true
                        }

                        KeyEvent.KEYCODE_BACK -> {
                            onDismiss()
                            true
                        }

                        else -> false
                    }
                }
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = text,
                color = Color.White
            )

            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MenuItemView(
                    text = stringResource(R.string.cancel),
                    selected = selectedIndex == 0,
                    onClick = onDismiss,
                    modifier = Modifier.width(150.dp),
                    textColor = Color.White,
                    enableTouch = !isTv
                )

                MenuItemView(
                    text = stringResource(R.string.confirm),
                    selected = selectedIndex == 1,
                    onClick = onConfirm,
                    modifier = Modifier.width(190.dp),
                    textColor = Color.White,
                    enableTouch = !isTv
                )
            }
        }
    }
}
