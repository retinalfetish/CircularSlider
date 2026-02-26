package com.unary.circularslider

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color

/**
 * Defines the full color scheme for a circular slider.
 *
 * Each part (track, progress, and thumb) has its own colors for enabled and disabled.
 *
 * @param trackColor Track color when enabled.
 * @param progressColor Progress color when enabled.
 * @param thumbColor Thumb color when enabled.
 * @param disabledTrackColor Track color when disabled.
 * @param disabledProgressColor Progress color when disabled.
 * @param disabledThumbColor Thumb color when disabled.
 */
@Immutable
class CircularSliderColors(
    val trackColor: Color,
    val progressColor: Color,
    val thumbColor: Color,
    val disabledTrackColor: Color,
    val disabledProgressColor: Color,
    val disabledThumbColor: Color
) {

    /**
     * Creates a copy of this CircularSliderColors, optionally overriding values.
     *
     * @param trackColor Track color when enabled.
     * @param progressColor Progress color when enabled.
     * @param thumbColor Thumb color when enabled.
     * @param disabledTrackColor Track color when disabled.
     * @param disabledProgressColor Progress color when disabled.
     * @param disabledThumbColor Thumb color when disabled.
     * @return A copy of the [CircularSliderColors]
     */
    fun copy(
        trackColor: Color = this.trackColor,
        progressColor: Color = this.progressColor,
        thumbColor: Color = this.thumbColor,
        disabledTrackColor: Color = this.disabledTrackColor,
        disabledProgressColor: Color = this.disabledProgressColor,
        disabledThumbColor: Color = this.disabledThumbColor
    ): CircularSliderColors = CircularSliderColors(
        trackColor = trackColor,
        progressColor = progressColor,
        thumbColor = thumbColor,
        disabledTrackColor = disabledTrackColor,
        disabledProgressColor = disabledProgressColor,
        disabledThumbColor = disabledThumbColor
    )

    /**
     * Returns an animated track color based on the enabled state.
     *
     * The color transitions smoothly when switching between enabled and disabled.
     *
     * @param enabled Whether the slider is enabled.
     * @return A [State] for the current track color.
     */
    @Composable
    internal fun trackColor(enabled: Boolean): State<Color> =
        animateColorAsState(
            targetValue = if (enabled) trackColor else disabledTrackColor,
            animationSpec = tween(durationMillis = 100, easing = LinearEasing),
            label = "TrackColorAnimation"
        )

    /**
     * Returns an animated progress color based on the enabled state.
     *
     * The color transitions smoothly when switching between enabled and disabled.
     *
     * @param enabled Whether the slider is enabled.
     * @return A [State] for the current progress color.
     */
    @Composable
    internal fun progressColor(enabled: Boolean): State<Color> =
        animateColorAsState(
            targetValue = if (enabled) progressColor else disabledProgressColor,
            animationSpec = tween(durationMillis = 100, easing = LinearEasing),
            label = "ProgressColorAnimation"
        )

    /**
     * Returns an animated thumb color based on the enabled state.
     *
     * The color transitions smoothly when switching between enabled and disabled.
     *
     * @param enabled Whether the slider is enabled.
     * @return A [State] for the current thumb color.
     */
    @Composable
    internal fun thumbColor(enabled: Boolean): State<Color> =
        animateColorAsState(
            targetValue = if (enabled) thumbColor else disabledThumbColor,
            animationSpec = tween(durationMillis = 100, easing = LinearEasing),
            label = "ThumbColorAnimation"
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CircularSliderColors) return false

        if (trackColor != other.trackColor) return false
        if (progressColor != other.progressColor) return false
        if (thumbColor != other.thumbColor) return false
        if (disabledTrackColor != other.disabledTrackColor) return false
        if (disabledProgressColor != other.disabledProgressColor) return false
        if (disabledThumbColor != other.disabledThumbColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = trackColor.hashCode()

        result = 31 * result + progressColor.hashCode()
        result = 31 * result + thumbColor.hashCode()
        result = 31 * result + disabledTrackColor.hashCode()
        result = 31 * result + disabledProgressColor.hashCode()
        result = 31 * result + disabledThumbColor.hashCode()

        return result
    }
}