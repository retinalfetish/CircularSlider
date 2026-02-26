package com.unary.circularslider.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unary.circularslider.CircularSlider
import com.unary.circularslider.demo.ui.theme.CircularSliderTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CircularSliderTheme {
                Surface {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicAnimation()
//                        SteppedAnimation()
//                        GravityAnimation()
                    }
                }
            }
        }
    }
}

/**
 * Demonstrates a slider that animates to its destination using a spring effect.
 */
@Composable
fun BasicAnimation() {
    var value by rememberSaveable { mutableFloatStateOf(value = 0f) }

    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "BasicFloatAnimation"
    )

    CircularSlider(
        value = animatedValue,
        onValueChange = { value = it },
        modifier = Modifier.size(size = 280.dp),
        startAngle = 135f,
        sweepAngle = 270f
    )
    Text(
        text = "${(value * 100).roundToInt()}",
        style = MaterialTheme.typography.displayLarge
    )
}

/**
 * An example of a "snapping" effect where the slider animates to discrete intervals.
 */
@Composable
fun SteppedAnimation() {
    var value by rememberSaveable { mutableFloatStateOf(value = 0f) }

    val steps = 5
    val stepSize = 1f / (steps - 1)

    val steppedTarget = remember(key1 = value) {
        (value / stepSize).roundToInt() * stepSize
    }

    val animatedValue by animateFloatAsState(
        targetValue = steppedTarget,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "SteppedFloatAnimation"
    )

    CircularSlider(
        value = animatedValue,
        onValueChange = { value = it },
        modifier = Modifier.size(size = 280.dp),
        startAngle = 135f,
        sweepAngle = 270f
    )
    Text(
        text = "${(value * (steps - 1) + 1).roundToInt()}",
        style = MaterialTheme.typography.displayLarge
    )
}

/**
 * A "gravity" or "sticky" effect where the slider animates after user interaction.
 */
@Composable
fun GravityAnimation() {
    var value by rememberSaveable { mutableFloatStateOf(value = 0f) }

    val steps = 5
    val stepSize = 1f / (steps - 1)

    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "GravityFloatAnimation"
    )

    CircularSlider(
        value = animatedValue,
        onValueChange = { value = it },
        modifier = Modifier.size(size = 280.dp),
        onValueChangeFinished = {
            value = (value / stepSize).roundToInt() * stepSize
        },
        startAngle = 135f,
        sweepAngle = 270f
    )
    Text(
        text = "${(value * (steps - 1) + 1).roundToInt()}",
        style = MaterialTheme.typography.displayLarge
    )
}