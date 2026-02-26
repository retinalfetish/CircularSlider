package com.unary.circularslider

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Default configuration values for circular slider behavior.
 *
 * These values follow the current [MaterialTheme] and can be overridden to customize the
 * appearance of the slider.
 */
@Stable
object CircularSliderDefaults {

    /**
     * Returns a [CircularSliderColors] that defines the color styling for a slider.
     *
     * Colors are derived from the current [MaterialTheme.colorScheme] and each enabled
     * and disabled color can be overridden.
     *
     * @param trackColor Track color when enabled.
     * @param progressColor Progress color when enabled.
     * @param thumbColor Thumb color when enabled.
     * @param disabledTrackColor Track color when disabled.
     * @param disabledProgressColor Progress color when disabled.
     * @param disabledThumbColor Thumb color when disabled.
     *
     * @return A [CircularSliderColors] instance of colors.
     */
    @Composable
    fun colors(
        trackColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        progressColor: Color = MaterialTheme.colorScheme.primary,
        thumbColor: Color = MaterialTheme.colorScheme.primary,
        disabledTrackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        disabledProgressColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        disabledThumbColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    ): CircularSliderColors {
        return CircularSliderColors(
            trackColor = trackColor,
            progressColor = progressColor,
            thumbColor = thumbColor,
            disabledTrackColor = disabledTrackColor,
            disabledProgressColor = disabledProgressColor,
            disabledThumbColor = disabledThumbColor
        )
    }

    /** Default track thickness in dp. */
    val trackThickness: Dp = 16.dp

    /** Default thumb radius in dp. */
    val thumbRadius: Dp = 14.dp

    /** Default minimum width in dp */
    internal val minWidth = 150.dp

    /** Default minimum height in dp */
    internal val minHeight = 150.dp
}