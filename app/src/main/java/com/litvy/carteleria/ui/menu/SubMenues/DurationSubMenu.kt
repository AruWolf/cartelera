package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.R
import com.litvy.carteleria.slides.ImageSlideDurations
import com.litvy.carteleria.ui.menu.MenuItemView

@Composable
fun DurationSubMenu(
    selectedIndex: Int,
    activeGlobalImageDurationMs: Long
) {
    val context = LocalContext.current
    val activeDurationLabel = localizedDurationLabel(activeGlobalImageDurationMs)
    val items = listOf(
        stringResource(R.string.custom_duration_with_value, activeDurationLabel),
        stringResource(R.string.apply_global_duration_all_images)
    )

    Column(
        modifier = Modifier
            .width(340.dp)
            .fillMaxHeight()
            .padding(24.dp)
    ) {
        items.forEachIndexed { index, label ->
            val isFocused = selectedIndex == index
            MenuItemView(
                text = if (isFocused) "\u25B6 $label" else label,
                selected = isFocused,
                onClick = {}
            )
        }
    }
}

@Composable
internal fun localizedDurationLabel(durationMs: Long): String {
    val context = LocalContext.current
    val seconds = (durationMs / 1000L).toInt()
    return if (seconds >= 60 && seconds % 60 == 0) {
        val minutes = seconds / 60
        context.resources.getQuantityString(R.plurals.duration_minutes, minutes, minutes)
    } else {
        context.resources.getQuantityString(R.plurals.duration_seconds, seconds, seconds)
    }
}