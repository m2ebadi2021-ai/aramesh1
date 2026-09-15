package com.example.ui.canvas

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.domain.model.GratitudeTreeState
import com.example.ui.theme.BlossomPink
import com.example.ui.theme.BlossomSoftPink
import com.example.ui.theme.GoldenFruitColor
import com.example.ui.theme.GoldenFruitGlow
import com.example.ui.theme.LeafGreenColor
import com.example.ui.theme.RedFruitColor
import com.example.ui.theme.TrunkWoodColor
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GratitudeTreeCanvas(
    state: GratitudeTreeState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "treeWind")
    val windSway by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "windSway"
    )

    val goldenPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "goldenPulse"
    )

    val sparkleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkleRotation"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            if (width <= 0 || height <= 0) return@Canvas

            val groundY = height * 0.88f
            val trunkBaseX = width * 0.5f

            // 1. Draw Earth / Mound
            drawMound(trunkBaseX, groundY, width)

            // 2. Draw Trunk & Roots
            drawTrunkAndBranches(trunkBaseX, groundY, width, height, windSway)

            // 3. Draw Foliage / Leaf Clusters
            drawCanopyFoliage(trunkBaseX, groundY, width, height, windSway, state)

            // 4. Draw Blossoms (Up to state.currentBlossoms or tier blooms)
            drawBlossoms(trunkBaseX, groundY, width, height, windSway, state.currentBlossoms)

            // 5. Draw Red Fruits (Up to state.currentRedFruits)
            drawRedFruits(trunkBaseX, groundY, width, height, windSway, state.currentRedFruits)

            // 6. Draw Golden Fruits (Up to state.currentGoldenFruits)
            drawGoldenFruits(
                trunkBaseX,
                groundY,
                width,
                height,
                windSway,
                state.currentGoldenFruits,
                goldenPulse,
                sparkleRotation
            )
        }
    }
}

private fun DrawScope.drawMound(centerX: Float, groundY: Float, width: Float) {
    val moundPath = Path().apply {
        moveTo(width * 0.1f, groundY + 30f)
        quadraticTo(centerX, groundY - 20f, width * 0.9f, groundY + 30f)
        lineTo(width, groundY + 80f)
        lineTo(0f, groundY + 80f)
        close()
    }
    drawPath(
        path = moundPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF388E3C), Color(0xFF1B5E20)),
            startY = groundY - 20f,
            endY = groundY + 80f
        )
    )
}

private fun DrawScope.drawTrunkAndBranches(
    centerX: Float,
    groundY: Float,
    width: Float,
    height: Float,
    windSway: Float
) {
    val trunkTopY = groundY - height * 0.38f
    val trunkTopX = centerX + windSway * 0.5f

    // Roots
    val rootColor = TrunkWoodColor.copy(alpha = 0.9f)
    drawLine(
        color = rootColor,
        start = Offset(centerX - 15f, groundY),
        end = Offset(centerX - 65f, groundY + 20f),
        strokeWidth = 14f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = rootColor,
        start = Offset(centerX + 15f, groundY),
        end = Offset(centerX + 65f, groundY + 20f),
        strokeWidth = 14f,
        cap = StrokeCap.Round
    )

    // Main Trunk
    val trunkPath = Path().apply {
        moveTo(centerX - 30f, groundY + 10f)
        cubicTo(
            centerX - 24f, groundY - height * 0.15f,
            trunkTopX - 18f, groundY - height * 0.28f,
            trunkTopX - 12f, trunkTopY
        )
        lineTo(trunkTopX + 12f, trunkTopY)
        cubicTo(
            trunkTopX + 18f, groundY - height * 0.28f,
            centerX + 24f, groundY - height * 0.15f,
            centerX + 30f, groundY + 10f
        )
        close()
    }

    drawPath(
        path = trunkPath,
        brush = Brush.horizontalGradient(
            colors = listOf(TrunkWoodColor, Color(0xFF795548), Color(0xFF4E342E)),
            startX = centerX - 30f,
            endX = centerX + 30f
        )
    )

    // Primary Left Branch
    val leftBranchEnd = Offset(centerX - width * 0.28f + windSway, groundY - height * 0.52f)
    val leftPath = Path().apply {
        moveTo(trunkTopX - 8f, trunkTopY + 15f)
        quadraticTo(
            centerX - width * 0.12f, trunkTopY - 20f,
            leftBranchEnd.x, leftBranchEnd.y
        )
    }
    drawPath(
        path = leftPath,
        color = TrunkWoodColor,
        style = Stroke(width = 16f, cap = StrokeCap.Round)
    )

    // Primary Right Branch
    val rightBranchEnd = Offset(centerX + width * 0.28f + windSway, groundY - height * 0.50f)
    val rightPath = Path().apply {
        moveTo(trunkTopX + 8f, trunkTopY + 10f)
        quadraticTo(
            centerX + width * 0.12f, trunkTopY - 25f,
            rightBranchEnd.x, rightBranchEnd.y
        )
    }
    drawPath(
        path = rightPath,
        color = TrunkWoodColor,
        style = Stroke(width = 16f, cap = StrokeCap.Round)
    )

    // Upper Center Branch
    val topBranchEnd = Offset(trunkTopX + windSway * 1.5f, groundY - height * 0.62f)
    drawLine(
        color = TrunkWoodColor,
        start = Offset(trunkTopX, trunkTopY),
        end = topBranchEnd,
        strokeWidth = 14f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawCanopyFoliage(
    centerX: Float,
    groundY: Float,
    width: Float,
    height: Float,
    windSway: Float,
    state: GratitudeTreeState
) {
    val leafBrush = Brush.radialGradient(
        colors = listOf(Color(0xFF66BB6A), LeafGreenColor, Color(0xFF2E7D32)),
        center = Offset(centerX, groundY - height * 0.5f),
        radius = width * 0.45f
    )

    val scale = if (state.totalEntries == 0) 0.65f else 1f

    // Canopy clouds/clusters
    val clusters = listOf(
        Triple(centerX - width * 0.22f + windSway * 0.6f, groundY - height * 0.54f, 75f * scale),
        Triple(centerX + width * 0.22f + windSway * 0.6f, groundY - height * 0.52f, 78f * scale),
        Triple(centerX + windSway * 0.8f, groundY - height * 0.62f, 85f * scale),
        Triple(centerX - width * 0.10f + windSway * 0.7f, groundY - height * 0.42f, 65f * scale),
        Triple(centerX + width * 0.10f + windSway * 0.7f, groundY - height * 0.44f, 65f * scale)
    )

    for ((cx, cy, radius) in clusters) {
        drawCircle(
            brush = leafBrush,
            radius = radius,
            center = Offset(cx, cy)
        )
        // Soft leaf highlight overlay
        drawCircle(
            color = Color(0x33A5D6A7),
            radius = radius * 0.55f,
            center = Offset(cx - radius * 0.25f, cy - radius * 0.25f)
        )
    }
}

private fun DrawScope.drawBlossoms(
    centerX: Float,
    groundY: Float,
    width: Float,
    height: Float,
    windSway: Float,
    blossomCount: Int
) {
    if (blossomCount <= 0) return

    val blossomAnchors = listOf(
        Offset(centerX - width * 0.26f + windSway, groundY - height * 0.56f),
        Offset(centerX + width * 0.24f + windSway, groundY - height * 0.55f),
        Offset(centerX + width * 0.02f + windSway * 1.2f, groundY - height * 0.66f)
    )

    val count = blossomCount.coerceAtMost(blossomAnchors.size)
    for (i in 0 until count) {
        val anchor = blossomAnchors[i]
        drawBlossomPetals(anchor.x, anchor.y, radius = 18f)
    }
}

private fun DrawScope.drawBlossomPetals(cx: Float, cy: Float, radius: Float) {
    val petalCount = 5
    for (i in 0 until petalCount) {
        val angle = (i * (360.0 / petalCount)) * (Math.PI / 180.0)
        val px = (cx + radius * 0.6f * cos(angle)).toFloat()
        val py = (cy + radius * 0.6f * sin(angle)).toFloat()
        drawCircle(
            color = BlossomPink,
            radius = radius * 0.52f,
            center = Offset(px, py)
        )
        drawCircle(
            color = BlossomSoftPink,
            radius = radius * 0.32f,
            center = Offset(px, py)
        )
    }
    // Golden center pistil
    drawCircle(
        color = Color(0xFFFFD54F),
        radius = radius * 0.35f,
        center = Offset(cx, cy)
    )
}

private fun DrawScope.drawRedFruits(
    centerX: Float,
    groundY: Float,
    width: Float,
    height: Float,
    windSway: Float,
    redFruitCount: Int
) {
    if (redFruitCount <= 0) return

    val fruitAnchors = listOf(
        Offset(centerX - width * 0.16f + windSway * 0.7f, groundY - height * 0.46f),
        Offset(centerX + width * 0.18f + windSway * 0.7f, groundY - height * 0.44f),
        Offset(centerX - width * 0.05f + windSway * 0.9f, groundY - height * 0.56f)
    )

    val count = redFruitCount.coerceAtMost(fruitAnchors.size)
    for (i in 0 until count) {
        val anchor = fruitAnchors[i]
        // Stem
        drawLine(
            color = TrunkWoodColor,
            start = Offset(anchor.x, anchor.y - 18f),
            end = Offset(anchor.x + 3f, anchor.y - 6f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
        // Red fruit body
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF6B6B), RedFruitColor, Color(0xFF9E0012)),
                center = Offset(anchor.x - 4f, anchor.y - 4f),
                radius = 18f
            ),
            radius = 16f,
            center = anchor
        )
        // Glossy specular highlight
        drawCircle(
            color = Color(0x99FFFFFF),
            radius = 4f,
            center = Offset(anchor.x - 5f, anchor.y - 5f)
        )
    }
}

private fun DrawScope.drawGoldenFruits(
    centerX: Float,
    groundY: Float,
    width: Float,
    height: Float,
    windSway: Float,
    goldenCount: Int,
    pulse: Float,
    sparkleRot: Float
) {
    if (goldenCount <= 0) return

    val goldenAnchors = listOf(
        Offset(centerX + windSway * 1.3f, groundY - height * 0.68f),
        Offset(centerX - width * 0.18f + windSway, groundY - height * 0.60f),
        Offset(centerX + width * 0.18f + windSway, groundY - height * 0.59f)
    )

    val count = goldenCount.coerceAtMost(goldenAnchors.size)
    for (i in 0 until count) {
        val anchor = goldenAnchors[i]

        // Radiating Golden Glow Rings
        drawCircle(
            color = GoldenFruitGlow.copy(alpha = 0.35f),
            radius = 28f * pulse,
            center = anchor
        )
        drawCircle(
            color = GoldenFruitGlow.copy(alpha = 0.2f),
            radius = 38f * pulse,
            center = anchor
        )

        // Sparkle rays
        val rayCount = 4
        for (r in 0 until rayCount) {
            val angle = ((r * 90) + sparkleRot) * (Math.PI / 180.0)
            val rx = (anchor.x + 22f * cos(angle)).toFloat()
            val ry = (anchor.y + 22f * sin(angle)).toFloat()
            drawLine(
                color = GoldenFruitColor,
                start = anchor,
                end = Offset(rx, ry),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )
        }

        // Golden fruit body
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFF9C4), GoldenFruitColor, Color(0xFFF57F17)),
                center = Offset(anchor.x - 5f, anchor.y - 5f),
                radius = 20f
            ),
            radius = 18f,
            center = anchor
        )

        // Golden center shine
        drawCircle(
            color = Color(0xCCFFFFFF),
            radius = 5f,
            center = Offset(anchor.x - 5f, anchor.y - 5f)
        )
    }
}
