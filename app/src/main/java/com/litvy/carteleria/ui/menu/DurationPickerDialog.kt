package com.litvy.carteleria.ui.menu

import android.view.KeyEvent
import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.litvy.carteleria.slides.ImageSlideDurations

@Composable
fun DurationPickerDialog(
    title: String,
    initialDurationMs: Long?,
    includeGlobalOption: Boolean,
    onDurationSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    data class DurationOption(
        val label: String,
        val durationMs: Long?
    )

    val options = buildList {
        if (includeGlobalOption) add(DurationOption("Duraci\u00f3n global", null))
        addAll(
            ImageSlideDurations.allowedValuesMs.map { durationMs ->
                DurationOption(ImageSlideDurations.labelFor(durationMs), durationMs)
            }
        )
    }
    val labels = options.map { it.label }.toTypedArray()
    val initialIndex = options.indexOfFirst { it.durationMs == initialDurationMs }
        .takeIf { it >= 0 }
        ?: 0

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .background(Color(0xFF111111), RoundedCornerShape(12.dp))
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                color = Color.White
            )

            AndroidView(
                factory = { context ->
                    NumberPicker(context).apply {
                        minValue = 0
                        maxValue = options.lastIndex
                        displayedValues = labels
                        value = initialIndex
                        wrapSelectorWheel = true
                        descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                        setOnKeyListener { _, keyCode, event ->
                            if (
                                event.action == KeyEvent.ACTION_DOWN &&
                                (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                                    keyCode == KeyEvent.KEYCODE_ENTER ||
                                    keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
                            ) {
                                onDurationSelected(options[value].durationMs)
                                true
                            } else {
                                false
                            }
                        }
                        post { requestFocus() }
                    }
                },
                update = { picker ->
                    picker.displayedValues = null
                    picker.minValue = 0
                    picker.maxValue = options.lastIndex
                    picker.displayedValues = labels
                }
            )
        }
    }
}
