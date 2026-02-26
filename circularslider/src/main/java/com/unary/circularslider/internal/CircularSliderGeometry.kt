package com.unary.circularslider.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Returns whether [offset] lies within touch distance of the elliptical arc.
 *
 * The point is projected onto the ellipse and the Euclidean distance to the ellipse
 * perimeter is compared against [inset].
 *
 * @param offset The current touch coordinates.
 * @param size The dimensions of the component.
 * @param startAngle Start angle of the arc in degrees.
 * @param sweepAngle Sweep angle of the arc in degrees.
 * @param inset The touch target radius.
 *
 * @return `true` if found within the hit zone.
 */
internal fun isTouchOnArc(
    offset: Offset,
    size: IntSize,
    startAngle: Float,
    sweepAngle: Float,
    inset: Float
): Boolean {
    val rx = (size.center.x - inset).coerceAtLeast(minimumValue = 0.001f)
    val ry = (size.center.y - inset).coerceAtLeast(minimumValue = 0.001f)

    val px = offset.x - size.center.x
    val py = offset.y - size.center.y

    if (px == 0f && py == 0f) return false

    val angleRadians = pointToAngle(
        x = 0f,
        y = 0f,
        px = px,
        py = py,
        rx = rx,
        ry = ry
    )

    val marginDegrees = radialInsetToMarginDegrees(
        inset = inset,
        angle = angleRadians,
        rx = rx,
        ry = ry
    )

    val pointInArcAngle = isPointInArcAngle(
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        marginDegrees = marginDegrees,
        px = px,
        py = py,
        rx = rx,
        ry = ry
    )

    if (!pointInArcAngle) return false

    val distance = distanceToEllipsePerimeter(
        px = px,
        py = py,
        rx = rx,
        ry = ry
    )
    return distance <= inset
}

/**
 * Returns whether a point relative to an ellipse center lies within a given arc sweep.
 *
 * @param startAngle Start angle of the arc in degrees.
 * @param sweepAngle Sweep angle of the arc in degrees.
 * @param marginDegrees The touch margin in degrees.
 * @param px The relative x position of the point.
 * @param py The relative y position of the point.
 * @param rx Horizontal radius of the ellipse.
 * @param ry Vertical radius of the ellipse.
 *
 * @return `true` if the point is along the arc sweep.
 */
private fun isPointInArcAngle(
    startAngle: Float,
    sweepAngle: Float,
    marginDegrees: Float,
    px: Float,
    py: Float,
    rx: Float,
    ry: Float
): Boolean {
    val angleDegrees = pointToAngle(
        x = 0f,
        y = 0f,
        px = px,
        py = py,
        rx = rx,
        ry = ry
    ).degrees.coerceAngle

    var relativeAngle = (angleDegrees - startAngle).coerceAngle
    if (sweepAngle < 0) relativeAngle = (360f - relativeAngle).coerceAngle

    val absSweep = abs(x = sweepAngle)

    return relativeAngle <= (absSweep + marginDegrees) ||
            relativeAngle >= (360f - marginDegrees)
}

/**
 * Approximates an angular margin (in degrees) for a given radial inset on an ellipse.
 *
 * @param inset The radial distance.
 * @param angle The angle on the ellipse in radians.
 * @param rx Horizontal radius of the ellipse.
 * @param ry Vertical radius of the ellipse.
 *
 * @return Approximate angular margin in degrees.
 */
private fun radialInsetToMarginDegrees(
    inset: Float,
    angle: Float,
    rx: Float,
    ry: Float
): Float {
    val radiusAtAngle = hypot(x = cos(x = angle) * rx, y = sin(x = angle) * ry)

    val clampedInset = inset.coerceAtMost(maximumValue = radiusAtAngle - 0.001f)
    val marginRadians = asin(x = clampedInset / radiusAtAngle)

    return marginRadians.degrees
}

/**
 * Converts a touch [offset] into a fractional progress value along an ellipse.
 *
 * Calculates the relative angle of the point within the specified ellipse, accounting
 * for scaling.
 *
 * @param offset The current touch coordinates.
 * @param size The dimensions of the component.
 * @param startAngle The start angle in degrees.
 * @param sweepAngle The sweep angle in degrees.
 * @param inset The distance from the edge.
 *
 * @return A fractional progress value in [0f, 1f].
 */
internal fun offsetToProgress(
    offset: Offset,
    size: IntSize,
    startAngle: Float,
    sweepAngle: Float,
    inset: Float
): Float {
    val rx = (size.center.x - inset).coerceAtLeast(minimumValue = 0.001f)
    val ry = (size.center.y - inset).coerceAtLeast(minimumValue = 0.001f)

    val angleDegrees = pointToAngle(
        x = size.center.x.toFloat(),
        y = size.center.y.toFloat(),
        px = offset.x,
        py = offset.y,
        rx = rx,
        ry = ry
    ).degrees.coerceAngle

    var relativeAngle = (angleDegrees - startAngle).coerceAngle
    if (sweepAngle < 0) relativeAngle = (360f - relativeAngle).coerceAngle

    val absSweep = abs(x = sweepAngle)
    if (absSweep < 0.001f) return 0f
    if (absSweep >= 360f) return relativeAngle / 360f

    return when {
        relativeAngle <= absSweep -> relativeAngle / absSweep
        relativeAngle < (absSweep + 360f) / 2f -> 1f
        else -> 0f
    }
}

/**
 * Converts a fractional [value] into a coordinate [Offset] along an ellipse.
 *
 * Maps a progress value (0f to 1f) to a specific point on an ellipse based on the
 * provided radii and angles.
 *
 * @param value The fractional progress to convert.
 * @param size The dimensions of the component.
 * @param startAngle The start angle of the arc in degrees.
 * @param sweepAngle The sweep angle of the arc in degrees.
 * @param inset The distance from the edge.
 *
 * @return A coordinate [Offset] on the ellipse.
 */
internal fun progressToOffset(
    value: Float,
    size: Size,
    startAngle: Float,
    sweepAngle: Float,
    inset: Float
): Offset {
    val rx = (size.center.x - inset).coerceAtLeast(minimumValue = 0.001f)
    val ry = (size.center.y - inset).coerceAtLeast(minimumValue = 0.001f)

    val angleRadians = (startAngle + sweepAngle * value).radians

    return angleToPoint(
        x = size.center.x,
        y = size.center.y,
        rx = rx,
        ry = ry,
        angle = angleRadians
    )
}

/**
 * Returns a centered [Rect] derived from the given [size] and [inset].
 *
 * @param size The dimensions of the component.
 * @param inset The distance from the edge.
 *
 * @return A new [Rect] centered within the size.
 */
internal fun arcRect(
    size: Size,
    inset: Float
): Rect {
    val rx = size.center.x - inset
    val ry = size.center.y - inset

    return Rect(
        left = size.center.x - rx,
        top = size.center.y - ry,
        right = size.center.x + rx,
        bottom = size.center.y + ry
    )
}

/**
 * Returns the approximate distance from a point to the perimeter of an ellipse.
 *
 * @param px The x position of the point.
 * @param py The y position of the point.
 * @param rx The horizontal radius of the ellipse.
 * @param ry The vertical radius of the ellipse.
 *
 * @return Distance to the ellipse perimeter.
 */
private fun distanceToEllipsePerimeter(
    px: Float,
    py: Float,
    rx: Float,
    ry: Float
): Float {
    val length = hypot(x = px / rx, y = py / ry).coerceAtLeast(minimumValue = 0.001f)

    val nx = px / length
    val ny = py / length

    return hypot(x = px - nx, y = py - ny)
}

/**
 * Returns the angle in from an ellipse at (x, y) for the given position.
 *
 * @param x The center x of the ellipse.
 * @param y The center y of the ellipse.
 * @param px The relative x position of the point.
 * @param py The relative y position of the point.
 * @param rx The horizontal radius of the ellipse.
 * @param ry The vertical radius of the ellipse.
 *
 * @return The angle in radians.
 */
private fun pointToAngle(
    x: Float,
    y: Float,
    px: Float,
    py: Float,
    rx: Float,
    ry: Float
): Float {
    val dx = (px - x) / rx
    val dy = (py - y) / ry

    return atan2(y = dy, x = dx)
}

/**
 * Returns the point on an ellipse centered at (x, y) for the given angle.
 *
 * @param x The center x of the ellipse.
 * @param y The center y of the ellipse.
 * @param rx The horizontal radius of the ellipse.
 * @param ry The vertical radius of the ellipse.
 * @param angle The angle on the ellipse in radians.
 *
 * @return The computed [Offset].
 */
private fun angleToPoint(
    x: Float,
    y: Float,
    rx: Float,
    ry: Float,
    angle: Float,
): Offset {
    val px = x + cos(x = angle) * rx
    val py = y + sin(x = angle) * ry

    return Offset(x = px, y = py)
}