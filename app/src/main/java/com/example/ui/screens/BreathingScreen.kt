package com.example.ui.screens

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AmbientSoundType
import com.example.domain.model.JalaaliCalendarHelper
import com.example.ui.canvas.BreathPhase
import com.example.ui.canvas.BreathingHaloCanvas
import com.example.ui.canvas.BreathingPattern
import com.example.ui.canvas.BreathingWaveCanvas
import com.example.ui.theme.LalezarFont
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealSecondary
import com.example.ui.theme.VazirFont
import com.example.ui.viewmodel.ArameshViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingScreen(
    viewModel: ArameshViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalCyclesFromDb by viewModel.totalBreathingCycles.collectAsState()
    val keepAwakeSetting by viewModel.keepScreenAwake.collectAsState()

    val availablePatterns = remember {
        mutableStateOf(BreathingPattern.ALL.toMutableList())
    }

    var selectedPattern by remember { mutableStateOf(availablePatterns.value[0]) }
    var isRunning by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf(BreathPhase.INHALE) }
    var sessionCycles by remember { mutableIntStateOf(0) }
    var sessionSeconds by remember { mutableIntStateOf(0) }

    // Audio & Haptic announcement states
    var voiceAnnounceEnabled by remember { mutableStateOf(true) }
    var chimeTonesEnabled by remember { mutableStateOf(true) }
    var hapticFeedbackEnabled by remember { mutableStateOf(true) }

    // Custom pattern dialog
    var showCustomDialog by remember { mutableStateOf(false) }
    var customInhale by remember { mutableIntStateOf(4) }
    var customHoldIn by remember { mutableIntStateOf(4) }
    var customExhale by remember { mutableIntStateOf(4) }
    var customHoldOut by remember { mutableIntStateOf(2) }

    // Keep screen awake during session
    DisposableEffect(isRunning, keepAwakeSetting) {
        val window = (context as? Activity)?.window
        if (isRunning && keepAwakeSetting) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "تنفس آگاهانه 💨",
                            fontFamily = LalezarFont,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "تنظیم ریتم ضربان قلب و امواج مغزی با الگوهای علمی",
                            fontFamily = VazirFont,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 2. Pattern Selector Chips + Custom Creator
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "انتخاب الگوی تنفس:",
                        fontFamily = LalezarFont,
                        fontSize = 16.sp
                    )

                    TextButton(
                        onClick = { showCustomDialog = true },
                        enabled = !isRunning
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("الگوی دلخواه ⚙️", fontFamily = VazirFont, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(availablePatterns.value) { pattern ->
                        FilterChip(
                            selected = selectedPattern.id == pattern.id,
                            onClick = {
                                if (!isRunning) {
                                    selectedPattern = pattern
                                }
                            },
                            label = {
                                Text(
                                    text = pattern.nameFa,
                                    fontFamily = VazirFont,
                                    fontWeight = if (selectedPattern.id == pattern.id) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = Color.White
                            ),
                            enabled = !isRunning
                        )
                    }
                }
            }

            // 3. Audio & Haptic Announcement Settings Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "اعلان‌های صوتی و راهنما",
                                    fontFamily = LalezarFont,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "اعلان صوتی فارسی (دم / حبس / بازدم)",
                                fontFamily = VazirFont,
                                fontSize = 13.sp
                            )
                            Switch(
                                checked = voiceAnnounceEnabled,
                                onCheckedChange = { voiceAnnounceEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "لرزش هپتیک (Haptic) هنگام بسته بودن چشم",
                                fontFamily = VazirFont,
                                fontSize = 13.sp
                            )
                            Switch(
                                checked = hapticFeedbackEnabled,
                                onCheckedChange = { hapticFeedbackEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary)
                            )
                        }
                    }
                }
            }

            // 4. Interactive Breathing Halo Canvas Box
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = selectedPattern.nameEn,
                            fontFamily = VazirFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Halo Canvas
                        Box(
                            modifier = Modifier
                                .size(280.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            BreathingHaloCanvas(
                                pattern = selectedPattern,
                                isRunning = isRunning,
                                onPhaseChange = { phase ->
                                    currentPhase = phase
                                    if (isRunning) {
                                        if (voiceAnnounceEnabled || chimeTonesEnabled) {
                                            viewModel.audioEngine.playBreathPhaseCue(phase, voiceAnnounceEnabled)
                                        }
                                        if (hapticFeedbackEnabled) {
                                            viewModel.triggerBreathHaptic(phase)
                                        }
                                    }
                                },
                                onCycleCompleted = {
                                    sessionCycles++
                                    sessionSeconds += selectedPattern.totalCycleSec
                                    viewModel.audioEngine.playBellChime()
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Wave canvas
                        BreathingWaveCanvas(
                            isRunning = isRunning,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Status & Cycles count
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "چرخه‌های جلسه",
                                    fontFamily = VazirFont,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = JalaaliCalendarHelper.toPersianNumber(sessionCycles),
                                    fontFamily = LalezarFont,
                                    fontSize = 22.sp,
                                    color = TealPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "کل چرخه‌های ثبت‌شده",
                                    fontFamily = VazirFont,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = JalaaliCalendarHelper.toPersianNumber(totalCyclesFromDb),
                                    fontFamily = LalezarFont,
                                    fontSize = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Control Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (!isRunning) {
                                        isRunning = true
                                        viewModel.audioEngine.playTibetanBowl()
                                    } else {
                                        isRunning = false
                                        if (sessionCycles > 0) {
                                            viewModel.recordBreathingSession(
                                                selectedPattern.id,
                                                sessionCycles,
                                                sessionSeconds
                                            )
                                            viewModel.audioEngine.playSessionComplete()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRunning) Color(0xFFE53935) else TealPrimary
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                            ) {
                                Icon(
                                    imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isRunning) "پایان و ذخیره تمرین" else "شروع تمرین تنفس",
                                    fontFamily = LalezarFont,
                                    fontSize = 16.sp
                                )
                            }

                            if (!isRunning && sessionCycles > 0) {
                                OutlinedButton(
                                    onClick = {
                                        sessionCycles = 0
                                        sessionSeconds = 0
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.height(52.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "بازنشانی")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        // Custom Pattern Builder Dialog
        if (showCustomDialog) {
            AlertDialog(
                onDismissRequest = { showCustomDialog = false },
                title = {
                    Text(
                        text = "تنظیم الگوی تنفس دلخواه ⚙️",
                        fontFamily = LalezarFont,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "مدت زمان هر مرحله از تنفس را بر حسب ثانیه مشخص کنید:",
                            fontFamily = VazirFont,
                            fontSize = 12.sp
                        )

                        // Inhale
                        Column {
                            Text(
                                text = "دم (Inhale): ${JalaaliCalendarHelper.toPersianNumber(customInhale)} ثانیه",
                                fontFamily = VazirFont,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Slider(
                                value = customInhale.toFloat(),
                                onValueChange = { customInhale = it.toInt() },
                                valueRange = 1f..10f,
                                steps = 8,
                                colors = SliderDefaults.colors(thumbColor = TealPrimary, activeTrackColor = TealPrimary)
                            )
                        }

                        // Hold In
                        Column {
                            Text(
                                text = "حبس بعد از دم (Hold In): ${JalaaliCalendarHelper.toPersianNumber(customHoldIn)} ثانیه",
                                fontFamily = VazirFont,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Slider(
                                value = customHoldIn.toFloat(),
                                onValueChange = { customHoldIn = it.toInt() },
                                valueRange = 0f..12f,
                                steps = 11,
                                colors = SliderDefaults.colors(thumbColor = TealPrimary, activeTrackColor = TealPrimary)
                            )
                        }

                        // Exhale
                        Column {
                            Text(
                                text = "بازدم (Exhale): ${JalaaliCalendarHelper.toPersianNumber(customExhale)} ثانیه",
                                fontFamily = VazirFont,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Slider(
                                value = customExhale.toFloat(),
                                onValueChange = { customExhale = it.toInt() },
                                valueRange = 1f..12f,
                                steps = 10,
                                colors = SliderDefaults.colors(thumbColor = TealPrimary, activeTrackColor = TealPrimary)
                            )
                        }

                        // Hold Out
                        Column {
                            Text(
                                text = "درنگ بعد از بازدم (Rest): ${JalaaliCalendarHelper.toPersianNumber(customHoldOut)} ثانیه",
                                fontFamily = VazirFont,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Slider(
                                value = customHoldOut.toFloat(),
                                onValueChange = { customHoldOut = it.toInt() },
                                valueRange = 0f..8f,
                                steps = 7,
                                colors = SliderDefaults.colors(thumbColor = TealPrimary, activeTrackColor = TealPrimary)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newCustomPattern = BreathingPattern(
                                id = "custom_${System.currentTimeMillis()}",
                                nameFa = "الگوی اختصاصی ($customInhale-$customHoldIn-$customExhale-$customHoldOut)",
                                nameEn = "Custom ($customInhale-$customHoldIn-$customExhale-$customHoldOut)",
                                inhaleSec = customInhale,
                                holdInSec = customHoldIn,
                                exhaleSec = customExhale,
                                holdOutSec = customHoldOut
                            )
                            val list = availablePatterns.value.toMutableList()
                            list.add(0, newCustomPattern)
                            availablePatterns.value = list
                            selectedPattern = newCustomPattern
                            showCustomDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("اعمال و انتخاب", fontFamily = VazirFont, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCustomDialog = false }) {
                        Text("انصراف", fontFamily = VazirFont)
                    }
                }
            )
        }
    }
}
