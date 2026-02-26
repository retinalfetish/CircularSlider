package com.unary.circularslider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * A Compose friendly state holder that manages current slider settings.
 *
 * @param initialValue The initial slider value.
 */
@Stable
class CircularSliderState internal constructor(initialValue: Float) {

    /** The current slider value. */
    var value by mutableFloatStateOf(value = initialValue)

    companion object {

        /** Saves and restores a [CircularSliderState]. */
        val Saver: Saver<CircularSliderState, Float> = Saver(
            save = { it.value },
            restore = { CircularSliderState(initialValue = it) }
        )
    }
}

/**
 * Creates and remembers a [CircularSliderState] that survives configuration changes.
 *
 * @param initialValue The initial slider value.
 * @return The remembered [CircularSliderState].
 */
@Composable
fun rememberCircularSliderState(initialValue: Float = 0f): CircularSliderState =
    rememberSaveable(saver = CircularSliderState.Saver) {
        CircularSliderState(initialValue = initialValue)
    }