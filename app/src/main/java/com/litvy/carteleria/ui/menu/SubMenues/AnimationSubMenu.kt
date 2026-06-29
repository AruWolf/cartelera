package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.litvy.carteleria.R
import com.litvy.carteleria.ui.menu.MenuItemView

@Composable
fun AnimationSubMenu(
    selectedIndex: Int,
    activeAnimation: String
) {

    // Lista de animaciones disponibles (Depende de las que esten implementadas en TvTransitions)
    val animations = listOf(
        "random" to stringResource(R.string.animation_random),
        "fade" to stringResource(R.string.animation_fade),
        "scale" to stringResource(R.string.animation_scale),
        "left" to stringResource(R.string.animation_slide_left),
        "up" to stringResource(R.string.animation_slide_up),
        "right" to stringResource(R.string.animation_slide_right),
        "down" to stringResource(R.string.animation_slide_down)
    )

    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0 && selectedIndex < animations.size) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
            .padding(24.dp)
    ) {

        itemsIndexed(animations) { index, (key, label) ->

            val isFocused = selectedIndex == index
            val isActive = key == activeAnimation

            val prefix = buildString {
                if (isFocused) append("\u25B6 ")
                if (isActive) append("\u2714 ")
            }

            MenuItemView(
                text = prefix + label,
                selected = isFocused,
                onClick = {}
            )
        }
    }
}