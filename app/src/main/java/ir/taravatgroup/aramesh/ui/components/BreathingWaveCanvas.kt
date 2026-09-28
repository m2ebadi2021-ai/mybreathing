package ir.taravatgroup.aramesh.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import ir.taravatgroup.aramesh.data.model.BreathingPhase
import ir.taravatgroup.aramesh.ui.theme.AmberDark
import ir.taravatgroup.aramesh.ui.theme.AmberGlow
import ir.taravatgroup.aramesh.ui.theme.AmberOrange
import ir.taravatgroup.aramesh.ui.theme.AmberPrimary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Breathing Wave displaying the precise trajectory:
 * Inhale: Climbs upward smoothly
 * Hold: Stays flat horizontal at the top plateau
 * Exhale: Descends downward smoothly
 * Hold Out: Stays flat horizontal at the bottom baseline
 * And repeats continuously across the screen with a flowing glowing orb!
 */
@Composable
fun BreathingWaveCanvas(
    currentPhase: BreathingPhase,
    phaseProgress: Float, // 0f to 1f within current phase
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveAnim")

    // Continuous subtle background pulse
    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraPulse"
    )

    // Flowing offset for continuous wave movement across time
    val flowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flowOffset"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val topY = height * 0.24f    // High peak during Hold In
            val bottomY = height * 0.76f // Low base during Hold Out / start of Inhale
            val waveHeight = bottomY - topY

            // We define 1 full breath cycle across a normalized length:
            // 1. Inhale (Rise): 28% of cycle
            // 2. Hold In (Flat Top): 22% of cycle
            // 3. Exhale (Fall): 28% of cycle
            // 4. Hold Out (Flat Bottom): 22% of cycle
            val cycleWidth = width * 0.72f // Shows ~1.4 continuous cycles on screen

            // Draw faint grid lines to emphasize plateau levels
            drawLine(
                color = AmberPrimary.copy(alpha = 0.12f),
                start = Offset(0f, topY),
                end = Offset(width, topY),
                strokeWidth = 1f
            )
            drawLine(
                color = AmberPrimary.copy(alpha = 0.08f),
                start = Offset(0f, bottomY),
                end = Offset(width, bottomY),
                strokeWidth = 1f
            )

            // Calculate height at any normalized cycle phase fraction (0f to 1f)
            fun evaluateTrajectoryY(cycleFraction: Float): Float {
                val f = ((cycleFraction % 1.0f) + 1.0f) % 1.0f
                return when {
                    // 1. Inhale (0.00 to 0.28): rises from bottomY to topY with smooth S-curve
                    f < 0.28f -> {
                        val subProgress = f / 0.28f
                        val smooth = (1f - cos(subProgress * PI.toFloat())) / 2f
                        bottomY - (smooth * waveHeight)
                    }
                    // 2. Hold In (0.28 to 0.50): stays completely flat horizontal at topY
                    f < 0.50f -> {
                        topY
                    }
                    // 3. Exhale (0.50 to 0.78): falls from topY to bottomY with smooth S-curve
                    f < 0.78f -> {
                        val subProgress = (f - 0.50f) / 0.28f
                        val smooth = (1f - cos(subProgress * PI.toFloat())) / 2f
                        topY + (smooth * waveHeight)
                    }
                    // 4. Hold Out (0.78 to 1.00): stays completely flat horizontal at bottomY
                    else -> {
                        bottomY
                    }
                }
            }

            // Continuous Path generation across the screen
            val trajectoryPath = Path()
            val fillPath = Path()
            val steps = 140

            // Base horizontal offset for nice aesthetic framing
            val screenShift = width * 0.12f

            fillPath.moveTo(0f, height)

            for (i in 0..steps) {
                val x = (width * i) / steps
                val cycleFraction = (x - screenShift) / cycleWidth
                val y = evaluateTrajectoryY(cycleFraction)

                if (i == 0) {
                    trajectoryPath.moveTo(x, y)
                    fillPath.lineTo(x, y)
                } else {
                    trajectoryPath.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
            }

            fillPath.lineTo(width, height)
            fillPath.close()

            // 1. Draw glowing gradient under the trajectory
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AmberPrimary.copy(alpha = 0.20f),
                        AmberOrange.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    startY = topY,
                    endY = height
                )
            )

            // 2. Secondary soft glow line for depth
            drawPath(
                path = trajectoryPath,
                color = AmberPrimary.copy(alpha = 0.35f),
                style = Stroke(width = 10f, cap = StrokeCap.Round)
            )

            // 3. Primary crisp gradient stroke
            drawPath(
                path = trajectoryPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        AmberDark.copy(alpha = 0.5f),
                        AmberPrimary,
                        AmberGlow,
                        AmberOrange,
                        AmberDark.copy(alpha = 0.5f)
                    )
                ),
                style = Stroke(width = 4.5f, cap = StrokeCap.Round)
            )

            // 4. Compute Glowing Orb position along this exact trajectory
            // We map current phase and its progress to cycleFraction:
            // INHALE: 0.00..0.28
            // HOLD_IN: 0.28..0.50
            // EXHALE: 0.50..0.78
            // HOLD_OUT: 0.78..1.00
            val currentPhaseFraction = when (currentPhase) {
                BreathingPhase.INHALE -> 0.00f + (0.28f * phaseProgress.coerceIn(0f, 1f))
                BreathingPhase.HOLD_IN -> 0.28f + (0.22f * phaseProgress.coerceIn(0f, 1f))
                BreathingPhase.EXHALE -> 0.50f + (0.28f * phaseProgress.coerceIn(0f, 1f))
                BreathingPhase.HOLD_OUT -> 0.78f + (0.22f * phaseProgress.coerceIn(0f, 1f))
                BreathingPhase.PREPARE -> 0.00f
            }

            // Map cycleFraction to X on the screen inside the active cycle span
            val orbX = screenShift + (currentPhaseFraction * cycleWidth)
            val orbY = evaluateTrajectoryY(currentPhaseFraction)
            val orbCenter = Offset(orbX, orbY)

            // 5. Draw the Multi-layered Glowing Orb
            // Outer radiant aura
            val outerAura = 48f * auraPulse
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AmberGlow.copy(alpha = 0.38f),
                        AmberPrimary.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = orbCenter,
                    radius = outerAura
                ),
                radius = outerAura,
                center = orbCenter
            )

            // Middle amber glow
            val midAura = 22f * auraPulse
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AmberPrimary.copy(alpha = 0.9f),
                        AmberOrange.copy(alpha = 0.45f),
                        Color.Transparent
                    ),
                    center = orbCenter,
                    radius = midAura
                ),
                radius = midAura,
                center = orbCenter
            )

            // Crisp glowing halo ring
            drawCircle(
                color = AmberGlow,
                radius = 12f * auraPulse,
                center = orbCenter,
                style = Stroke(width = 2.2f)
            )

            // Core pure white luminous gem
            drawCircle(
                color = Color.White,
                radius = 7.5f,
                center = orbCenter
            )

            // Soft vertical glowing beam dropping from orb
            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(AmberPrimary.copy(alpha = 0.35f), Color.Transparent),
                    startY = orbY,
                    endY = height * 0.95f
                ),
                start = Offset(orbX, orbY + 8f),
                end = Offset(orbX, height * 0.95f),
                strokeWidth = 2f
            )
        }
    }
}
