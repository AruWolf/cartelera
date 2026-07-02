package com.litvy.carteleria.ui.menu

import android.view.KeyEvent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.litvy.carteleria.R
import com.litvy.carteleria.slides.ImageSlideDurations
import com.litvy.carteleria.ui.menu.SubMenues.localizedDurationLabel
import com.litvy.carteleria.util.DeviceUtils
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

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

    val context = LocalContext.current
    val isTv = DeviceUtils.isTv(context)

    val options = buildList {
        if (includeGlobalOption) add(DurationOption(stringResource(R.string.global_duration), null))
        addAll(
            ImageSlideDurations.allowedValuesMs.map { durationMs ->
                DurationOption(localizedDurationLabel(durationMs), durationMs)
            }
        )
    }
    val labels = options.map { it.label }.toTypedArray()
    val initialIndex = options.indexOfFirst { it.durationMs == initialDurationMs }
        .takeIf { it >= 0 }
        ?: 0

    var selectedIndex by remember { mutableIntStateOf(initialIndex) }

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

            DurationWheel(
                options = labels.toList(),
                selectedIndex = selectedIndex,
                onSelectedIndexChange = {
                    selectedIndex = it
                },
                onConfirm = {
                    onDurationSelected(options[selectedIndex].durationMs)
                },
                onDismiss = onDismiss
            )

            if (!isTv) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MenuItemView(
                        text = stringResource(R.string.cancel),
                        onClick = onDismiss,
                        modifier = Modifier.width(130.dp),
                        enableTouch = !isTv,
                        textColor = Color.White
                    )

                    MenuItemView(
                        text = stringResource(R.string.confirm),
                        onClick = {
                            onDurationSelected(
                                options[selectedIndex].durationMs
                            )
                        },
                        modifier = Modifier.width(130.dp),
                        enableTouch = !isTv,
                        textColor = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun DurationWheel(
    options: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (options.isEmpty()) return

    val focusRequester = remember { FocusRequester() }
    val scrollPosition = remember { Animatable(selectedIndex.toFloat()) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val itemHeight = 34.dp
    val wheelHeight = itemHeight * 7f
    val itemHeightPx = with(density) { itemHeight.toPx() }
    val wheelSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )

    fun wheelIndex(index: Int): Int {
        return (index % options.size + options.size) % options.size
    }

    var targetPosition by remember {
        mutableFloatStateOf(selectedIndex.toFloat())
    }
    var committedStep by remember {
        mutableIntStateOf(selectedIndex)
    }

    fun commitStep(step: Int) {
        committedStep = step
        onSelectedIndexChange(wheelIndex(step))
    }

    fun commitCrossedItems(position: Float) {
        while (position >= committedStep + 1) {
            commitStep(committedStep + 1)
        }

        while (position <= committedStep - 1) {
            commitStep(committedStep - 1)
        }
    }

    fun animateToTarget() {
        scope.launch {
            scrollPosition.animateTo(targetPosition, wheelSpring) {
                commitCrossedItems(value)
            }
            commitCrossedItems(scrollPosition.value)
        }
    }

    fun addTargetSteps(steps: Int) {
        targetPosition += steps
        animateToTarget()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(selectedIndex, options.size) {
        val normalizedIndex = wheelIndex(committedStep)
        if (selectedIndex != normalizedIndex) {
            committedStep = selectedIndex
            targetPosition = selectedIndex.toFloat()
            scrollPosition.snapTo(targetPosition)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(wheelHeight)
            .focusRequester(focusRequester)
            .focusable()
            .pointerInput(options.size) {
                detectVerticalDragGestures(
                    onDragStart = {
                        scope.launch {
                            scrollPosition.stop()
                            targetPosition = scrollPosition.value
                        }
                    },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            val nextPosition = scrollPosition.value - dragAmount / itemHeightPx
                            targetPosition = nextPosition
                            scrollPosition.snapTo(nextPosition)
                            commitCrossedItems(nextPosition)
                        }
                    },
                    onDragEnd = {
                        targetPosition = scrollPosition.value.roundToInt().toFloat()
                        animateToTarget()
                    },
                    onDragCancel = {
                        targetPosition = scrollPosition.value.roundToInt().toFloat()
                        animateToTarget()
                    }
                )
            }
            .onPreviewKeyEvent { event ->

                val native = event.nativeKeyEvent

                if (native.action != KeyEvent.ACTION_DOWN) {
                    return@onPreviewKeyEvent false
                }

                when (native.keyCode) {

                    KeyEvent.KEYCODE_DPAD_UP -> {
                        val repeatBoost = (native.repeatCount / 4).coerceAtMost(3)
                        addTargetSteps(-(1 + repeatBoost))
                        true
                    }

                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        val repeatBoost = (native.repeatCount / 4).coerceAtMost(3)
                        addTargetSteps(1 + repeatBoost)
                        true
                    }

                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER,
                    KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                        onConfirm()
                        true
                    }

                    KeyEvent.KEYCODE_BACK -> {
                        onDismiss()
                        true
                    }

                    else -> false
                }
            }
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight)
                .background(Color.White.copy(alpha = 0.045f), RoundedCornerShape(6.dp))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.28f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.18f))
            )
        }

        val centerStep = scrollPosition.value.roundToInt()

        (centerStep - 3..centerStep + 3).forEach { itemStep ->
            val optionIndex = wheelIndex(itemStep)
            val itemOffsetPx = (itemStep - scrollPosition.value) * itemHeightPx
            val distanceFromCenter = abs(itemOffsetPx / itemHeightPx)
            val centerProgress = (1f - distanceFromCenter / 3.45f).coerceIn(0f, 1f)
            val scale = 0.80f + centerProgress * 0.20f
            val itemAlpha = centerProgress * centerProgress
            val fontSize = 15f + centerProgress * 7f
            val rotation = (itemOffsetPx / itemHeightPx).coerceIn(-3.5f, 3.5f) * -9f

            Text(
                text = options[optionIndex],
                color = Color.White,
                fontSize = fontSize.sp,
                fontWeight = if (distanceFromCenter < 0.35f) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(itemHeight)
                    .zIndex(4f - distanceFromCenter)
                    .graphicsLayer {
                        translationY = itemOffsetPx
                        alpha = itemAlpha
                        scaleX = scale
                        scaleY = scale
                        rotationX = rotation
                        cameraDistance = 12f * density.density
                    }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(76.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF111111),
                            Color(0x00111111)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(76.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00111111),
                            Color(0xFF111111)
                        )
                    )
                )
        )
    }
}
