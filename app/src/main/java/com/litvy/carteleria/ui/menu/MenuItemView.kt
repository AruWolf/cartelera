package com.litvy.carteleria.ui.menu

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuItemView(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit,
    onFocus: (() -> Unit)? = null,
    focusRequester: FocusRequester? = null,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
    textColor: Color =  Color.White,
    isHidden: Boolean = false,
    trailingText: String? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .background(
                if (selected) Color.Gray.copy(alpha = 0.3f)
                else Color.Transparent
            )
            .clickable{
                onClick()
            }
            .then(
                if (focusRequester != null)
                    Modifier.focusRequester(focusRequester)
                else Modifier
            )
            .onFocusChanged {
                if (it.isFocused) {
                    onFocus?.invoke()
                }
            }
            .focusable()
            .onPreviewKeyEvent { event ->

                if (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                    event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER
                ) {

                    // LONG PRESS
                    if (event.nativeKeyEvent.action == KeyEvent.ACTION_DOWN &&
                        event.nativeKeyEvent.isLongPress &&
                        onLongPress != null
                    ) {
                        onLongPress()
                        return@onPreviewKeyEvent true
                    }

                    // CLICK NORMAL
                    if (event.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                            onClick()
                        return@onPreviewKeyEvent true
                    }
                }

                false
            }
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            BasicText(
                text = text,
                style = TextStyle(
                    color = textColor,
                    fontSize = 22.sp,
                    textDecoration = if (isHidden)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None
                ),
                modifier = Modifier.weight(1f)
            )

            if (trailingText != null) {
                Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                BasicText(
                    text = trailingText,
                    style = TextStyle(
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 18.sp
                    )
                )
            }
        }
    }
}
