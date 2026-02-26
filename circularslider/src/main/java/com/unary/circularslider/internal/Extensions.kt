package com.unary.circularslider.internal

import kotlin.math.PI

/**
 * Converts this [Float] value from degrees to radians for use in calculations.
 *
 * @return The value in radians.
 */
internal val Float.radians get() = (this * PI / 180f).toFloat()

/**
 * Converts this [Float] value from radians to degrees for use in calculations.
 *
 * @return The value in degrees.
 */
internal val Float.degrees get() = (this * 180f / PI).toFloat()

/**
 * Converts this [Float] value (in degrees) to a normalized 0f ≤ angle < 360f.
 *
 * @return The coerced degrees.
 */
internal val Float.coerceAngle get() = (this % 360f + 360f) % 360f