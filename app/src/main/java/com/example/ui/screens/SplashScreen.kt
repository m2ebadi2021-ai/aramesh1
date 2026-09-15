package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AmbientAudioEngine
import com.example.ui.theme.LalezarFont
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealSecondary
import com.example.ui.theme.VazirFont
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    audioEngine: AmbientAudioEngine?,
    onSplashFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    // Breathing halo infinite animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo"
    )

    LaunchedEffect(Unit) {
        audioEngine?.playBellChime()
        // Entrance anim
        alpha.animateTo(1f, tween(800))
        scale.animateTo(1f, tween(1000, easing = FastOutSlowInEasing))
    }

    LaunchedEffect(Unit) {
        // 3 seconds progress
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        )
        delay(150)
        onSplashFinished()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF004D40),
                            Color(0xFF00695C),
                            Color(0xFF00796B),
                            Color(0xFF1DE9B6)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Background animated peaceful circles
            Canvas(
                modifier = Modifier
                    .size(340.dp)
                    .scale(haloPulse)
            ) {
                val center = Offset(size.width / 2, size.height / 2)
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = size.minDimension / 2,
                    style = Stroke(width = 4.dp.toPx())
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.09f),
                    radius = size.minDimension / 2.6f,
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.12f),
                    radius = size.minDimension / 3.4f
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Sacred Zen Lotus / Tree Icon
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(scale.value)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🪷",
                        fontSize = 58.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title with Lalezar Font
                Text(
                    text = "آرامش",
                    fontFamily = LalezarFont,
                    fontSize = 52.sp,
                    color = Color.White,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle in Vazir Font
                Text(
                    text = "همدم ذهن‌آگاهی، سپاسگزاری و آرامش درون",
                    fontFamily = VazirFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.92f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "«درون تو، سکوتی است که همه چیز را می‌داند»",
                    fontFamily = VazirFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // 3-second animated progress bar
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .fillMaxWidth(fraction = progress.value)
                            .background(Color.White)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Optional Quick Skip Button
                Button(
                    onClick = onSplashFinished,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.18f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "ورود به آرامش",
                        fontFamily = VazirFont,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
