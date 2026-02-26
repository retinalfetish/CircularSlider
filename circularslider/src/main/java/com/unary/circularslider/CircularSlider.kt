package com.unary.circularslider

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.scrollBy
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.unary.circularslider.internal.arcRect
import com.unary.circularslider.internal.coerceAngle
import com.unary.circularslider.internal.isTouchOnArc
import com.unary.circularslider.internal.offsetToProgress
import com.unary.circularslider.internal.progressToOffset
import kotlinx.coroutines.launch

/**
 * A circular slider that allows selecting a value within a specified angular range.
 *
 * Displays a track, progress arc, and draggable thumb, and supports RTL layouts.
 * This overload uses a [CircularSliderState] to hold and update the current value.
 *
 * @param state State to hold the current value.
 * @param onValueChange Callback invoked for value changes.
 * @param modifier Modifier to be applied.
 * @param enabled Whether the slider is enabled.
 * @param onValueChangeFinished Callback invoked when interaction ends.
 * @param startAngle The start angle in degrees.
 * @param sweepAngle The sweep angle in degrees.
 * @param trackThickness Thickness of the circular track.
 * @param thumbRadius The radius of the draggable thumb.
 * @param colors Colors used to draw the slider.
 * @param interactionSource Interaction source for the slider.
 */
@Composable
fun CircularSlider(
    state: CircularSliderState,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChangeFinished: () -> Unit = {},
    startAngle: Float = 90f,
    sweepAngle: Float = 360f,
    trackThickness: Dp = CircularSliderDefaults.trackThickness,
    thumbRadius: Dp = CircularSliderDefaults.thumbRadius,
    colors: CircularSliderColors = CircularSliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    CircularSlider(
        value = state.value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        onValueChangeFinished = onValueChangeFinished,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        trackThickness = trackThickness,
        thumbRadius = thumbRadius,
        colors = colors,
        interactionSource = interactionSource
    )
}

/**
 * A circular slider that allows selecting a value within a specified angular range.
 *
 * Displays a track, progress arc, and draggable thumb, and supports RTL layouts.
 *
 * @param value Current value between 0f and 1f.
 * @param onValueChange Callback invoked for value changes.
 * @param modifier Modifier to be applied.
 * @param enabled Whether the slider is enabled.
 * @param onValueChangeFinished Callback invoked when interaction ends.
 * @param startAngle The start angle in degrees.
 * @param sweepAngle The sweep angle in degrees.
 * @param trackThickness Thickness of the circular track.
 * @param thumbRadius The radius of the draggable thumb.
 * @param colors Colors used to draw the slider.
 * @param interactionSource Interaction source for the slider.
 */
@Composable
fun CircularSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChangeFinished: () -> Unit = {},
    startAngle: Float = 90f,
    sweepAngle: Float = 360f,
    trackThickness: Dp = CircularSliderDefaults.trackThickness,
    thumbRadius: Dp = CircularSliderDefaults.thumbRadius,
    colors: CircularSliderColors = CircularSliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val isRtl = layoutDirection == LayoutDirection.Rtl

    val resolvedStartAngle = if (isRtl) (180f - startAngle).coerceAngle else startAngle
    val resolvedSweepAngle = if (isRtl) -sweepAngle else sweepAngle

    val scope = rememberCoroutineScope()

    val trackThicknessPx = remember(key1 = density, key2 = trackThickness) {
        with(receiver = density) { trackThickness.toPx() }
    }
    val thumbRadiusPx = remember(key1 = density, key2 = thumbRadius) {
        with(receiver = density) { thumbRadius.toPx() }
    }
    val stateLayerRadiusPx = remember(key1 = density, key2 = thumbRadius) {
        with(receiver = density) { maxOf(a = 20.dp.toPx(), b = thumbRadiusPx + 8.dp.toPx()) }
    }
    val minTouchRadiusPx = remember(key1 = density) {
        with(receiver = density) { 24.dp.toPx() }
    }

    val inset = maxOf(
        a = trackThicknessPx / 2f,
        b = stateLayerRadiusPx,
        c = minTouchRadiusPx
    )

    val isPressed by interactionSource.collectIsPressedAsState()

    val trackColor by colors.trackColor(enabled = enabled)
    val progressColor by colors.progressColor(enabled = enabled)
    val thumbColor by colors.thumbColor(enabled = enabled)

    val animatedStateAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.12f else 0f,
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        label = "AlphaFloatAnimation"
    )
    val animatedStateRadius by animateFloatAsState(
        targetValue = if (isPressed) stateLayerRadiusPx else thumbRadiusPx,
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        label = "RadiusFloatAnimation"
    )

    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = CircularSliderDefaults.minWidth,
                minHeight = CircularSliderDefaults.minHeight
            )
            .focusable(enabled, interactionSource)
            .semantics(mergeDescendants = true) {
                // role = Role.Slider
                contentDescription = "Slider"
                stateDescription = "${(value * 100).toInt()}%"
                progressBarRangeInfo = ProgressBarRangeInfo(
                    current = value.coerceIn(range = 0f..1f),
                    range = 0f..1f,
                    steps = 0
                )

                if (!enabled) disabled()

                // Internal function
                fun updateValueIfChanged(value: Float): Boolean {
                    val newValue = value.coerceIn(range = 0f..1f)

                    return if (newValue != value) {
                        onValueChange(newValue)
                        onValueChangeFinished()
                        true
                    } else false
                }

                setProgress { targetValue ->
                    updateValueIfChanged(value = targetValue)
                }
                scrollBy { x, y ->
                    val scrollDelta = x + y
                    val adjustedDelta = if (isRtl) -scrollDelta else scrollDelta
                    val scrollScalar = 0.001f

                    updateValueIfChanged(value = value + adjustedDelta * scrollScalar)
                }
            }
            .pointerInput(enabled, isRtl, interactionSource) {
                if (!enabled) return@pointerInput

                // Internal function
                fun updateValueFromPointer(offset: Offset) {
                    val change = offsetToProgress(
                        offset = offset,
                        size = size,
                        startAngle = resolvedStartAngle,
                        sweepAngle = resolvedSweepAngle,
                        inset = inset
                    )

                    onValueChange(change)
                }

                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val isTouchOnArc = isTouchOnArc(
                        offset = down.position,
                        size = size,
                        startAngle = resolvedStartAngle,
                        sweepAngle = resolvedSweepAngle,
                        inset = inset
                    )

                    if (!isTouchOnArc) return@awaitEachGesture

                    down.consume()
                    updateValueFromPointer(offset = down.position)

                    val press = PressInteraction.Press(pressPosition = down.position)
                    scope.launch { interactionSource.emit(interaction = press) }

                    val result = drag(pointerId = down.id) { change ->
                        change.consume()
                        updateValueFromPointer(offset = change.position)
                    }

                    scope.launch {
                        interactionSource.emit(
                            interaction =
                                if (result) PressInteraction.Release(press)
                                else PressInteraction.Cancel(press)
                        )
                    }

                    onValueChangeFinished()
                }
            }
            .drawWithCache {
                val arcRect = arcRect(
                    size = size,
                    inset = inset
                )

                onDrawBehind {
                    // Internal function
                    fun drawArcSegment(color: Color, sweepAngle: Float) {
                        drawArc(
                            color = color,
                            startAngle = resolvedStartAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = arcRect.topLeft,
                            size = arcRect.size,
                            style = Stroke(width = trackThicknessPx, cap = StrokeCap.Round)
                        )
                    }

                    drawArcSegment(
                        color = trackColor,
                        sweepAngle = resolvedSweepAngle
                    )
                    drawArcSegment(
                        color = progressColor,
                        sweepAngle = resolvedSweepAngle * value
                    )

                    val thumbCenter = progressToOffset(
                        value = value,
                        size = size,
                        startAngle = resolvedStartAngle,
                        sweepAngle = resolvedSweepAngle,
                        inset = inset
                    )

                    if (animatedStateAlpha > 0f) {
                        drawCircle(
                            color = thumbColor.copy(alpha = animatedStateAlpha),
                            radius = animatedStateRadius,
                            center = thumbCenter
                        )
                    }
                    drawCircle(
                        color = thumbColor,
                        radius = thumbRadiusPx,
                        center = thumbCenter
                    )
                }
            }
    )
}