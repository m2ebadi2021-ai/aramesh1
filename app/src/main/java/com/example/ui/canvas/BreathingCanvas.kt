package com.example.ui.canvas

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealSecondary
import kotlinx.coroutines.delay
import kotlin.math.sin

enum class BreathPhase(val titleFa: String, val titleEn: String) {
    INHALE("دم (نفس بکشید)", "Inhale"),
    HOLD_IN("حبس نفس", "Hold"),
    EXHALE("بازدم (رها کنید)", "Exhale"),
    HOLD_OUT("درنگ و آرامش", "Rest / Pause")
}

data class BreathingPattern(
    val id: String,
    val nameFa: String,
    val nameEn: String,
    val inhaleSec: Int,
    val holdInSec: Int,
    val exhaleSec: Int,
    val holdOutSec: Int
) {
    val totalCycleSec: Int get() = inhaleSec + holdInSec + exhaleSec + holdOutSec

    companion object {
        val ALL = listOf(
            BreathingPattern("box", "تنفس مربعی (۴-۴-۴-۴)", "Box Breathing", 4, 4, 4, 4),
            BreathingPattern("478", "تنفس خواب آرام (۴-۷-۸)", "4-7-8 Deep Sleep", 4, 7, 8, 0),
            BreathingPattern("sigh", "آه فیزیولوژیک هوبرمن (۲-۱-۶-۱)", "Physiological Sigh (Anti-Anxiety)", 2, 1, 6, 1),
            BreathingPattern("coherent", "هماهنگی قلب و مغز (۵-۰-۵-۰)", "Heart Coherence", 5, 0, 5, 0),
            BreathingPattern("calm", "تنفس آرامش درون (۴-۲-۶-۱)", "Calm & Peace", 4, 2, 6, 1),
            BreathingPattern("deep", "تنفس عمیق شفابخش (۵-۳-۷-۲)", "Deep Healing", 5, 3, 7, 2),
            BreathingPattern("pranayama", "پرانایاما تعادل ذهن (۴-۴-۴-۰)", "Pranayama Mind Balance", 4, 4, 4, 0),
            BreathingPattern("energize", "انرژی و سرزندگی (۳-۱-۲-۰)", "Awakening & Energy", 3, 1, 2, 0),
            BreathingPattern("relax", "تن‌آرامی پیشرفته (۴-۰-۷-۲)", "Muscle Relaxation", 4, 0, 7, 2),
            BreathingPattern("focus", "تمرکز و پالایش فکر (۴-۴-۴-۲)", "Focus & Clarity", 4, 4, 4, 2)
        )
    }
}

@Composable
fun BreathingHaloCanvas(
    pattern: BreathingPattern,
    isRunning: Boolean,
    onPhaseChange: (BreathPhase) -> Unit,
    onCycleCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(0.45f) }
    val haloPulseAnim = remember { Animatable(0.3f) }
    var currentPhase by remember { mutableStateOf(BreathPhase.INHALE) }
    var secondsRemainingInPhase by remember { mutableStateOf(pattern.inhaleSec) }

    LaunchedEffect(isRunning, pattern) {
        if (!isRunning) {
            scaleAnim.snapTo(0.45f)
            haloPulseAnim.snapTo(0.3f)
            currentPhase = BreathPhase.INHALE
            secondsRemainingInPhase = pattern.inhaleSec
            return@LaunchedEffect
        }

        while (isRunning) {
            // 1. INHALE
            currentPhase = BreathPhase.INHALE
            onPhaseChange(BreathPhase.INHALE)
            val inDuration = pattern.inhaleSec * 1000
            for (s in pattern.inhaleSec downTo 1) {
                secondsRemainingInPhase = s
                if (s == pattern.inhaleSec) {
                    scaleAnim.animateTo(1.0f, animationSpec = tween(inDuration, easing = FastOutSlowInEasing))
                }
                delay(1000)
            }

            // 2. HOLD IN
            if (pattern.holdInSec > 0) {
                currentPhase = BreathPhase.HOLD_IN
                onPhaseChange(BreathPhase.HOLD_IN)
                for (s in pattern.holdInSec downTo 1) {
                    secondsRemainingInPhase = s
                    delay(1000)
                }
            }

            // 3. EXHALE
            currentPhase = BreathPhase.EXHALE
            onPhaseChange(BreathPhase.EXHALE)
            val exDuration = pattern.exhaleSec * 1000
            for (s in pattern.exhaleSec downTo 1) {
                secondsRemainingInPhase = s
                if (s == pattern.exhaleSec) {
                    scaleAnim.animateTo(0.45f, animationSpec = tween(exDuration, easing = FastOutSlowInEasing))
                }
                delay(1000)
            }

            // 4. HOLD OUT / REST
            if (pattern.holdOutSec > 0) {
                currentPhase = BreathPhase.HOLD_OUT
                onPhaseChange(BreathPhase.HOLD_OUT)
                for (s in pattern.holdOutSec downTo 1) {
                    secondsRemainingInPhase = s
                    delay(1000)
                }
            }

            onCycleCompleted()
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension / 2f * 0.75f
            val currentScale = scaleAnim.value

            // 1. Outer Halo Rings (multiple radiating transparent rings)
            val haloColors = listOf(
                TealSecondary.copy(alpha = 0.08f),
                TealSecondary.copy(alpha = 0.16f),
                TealPrimary.copy(alpha = 0.28f)
            )

            drawCircle(
                color = haloColors[0],
                radius = baseRadius * (currentScale + 0.32f),
                center = center
            )
            drawCircle(
                color = haloColors[1],
                radius = baseRadius * (currentScale + 0.18f),
                center = center
            )
            drawCircle(
                color = haloColors[2],
                radius = baseRadius * (currentScale + 0.06f),
                center = center
            )

            // 2. Core Breathing Gradient Sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF80CBC4),
                        TealSecondary,
                        TealPrimary
                    ),
                    center = center,
                    radius = baseRadius * currentScale
                ),
                radius = baseRadius * currentScale,
                center = center
            )

            // 3. Ring outline with subtle glow
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = baseRadius * currentScale,
                center = center,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Center Instruction Label & Countdown Seconds
        Box(contentAlignment = Alignment.Center) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isRunning) currentPhase.titleFa else "آماده برای شروع",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (isRunning) {
                    Text(
                        text = "$secondsRemainingInPhase",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = currentPhase.titleEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
fun BreathingWaveCanvas(
    isRunning: Boolean,
    modifier: Modifier = Modifier
) {
    val phaseAnim = remember { Animatable(0f) }

    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect
        while (isRunning) {
            phaseAnim.animateTo(
                targetValue = phaseAnim.value + (2 * Math.PI).toFloat(),
                animationSpec = tween(4000, easing = LinearEasing)
            )
        }
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        if (width <= 0 || height <= 0) return@Canvas

        val midY = height / 2f
        val amplitude = height * 0.35f
        val phase = phaseAnim.value

        val wavePath = Path().apply {
            moveTo(0f, midY)
            val steps = 60
            for (i in 0..steps) {
                val x = (width * i) / steps
                val angle = phase + (i.toDouble() / steps) * 2.5 * Math.PI
                val y = midY + (sin(angle) * amplitude).toFloat()
                lineTo(x, y)
            }
        }

        drawPath(
            path = wavePath,
            brush = Brush.horizontalGradient(
                colors = listOf(TealSecondary.copy(alpha = 0.3f), TealPrimary, TealSecondary.copy(alpha = 0.3f))
            ),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
