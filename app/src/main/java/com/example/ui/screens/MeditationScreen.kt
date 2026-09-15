package com.example.ui.screens

import android.app.Activity
import android.net.Uri
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import coil.compose.AsyncImage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AmbientSoundType
import com.example.domain.model.JalaaliCalendarHelper
import com.example.ui.theme.LalezarFont
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealSecondary
import com.example.ui.theme.VazirFont
import com.example.ui.viewmodel.ArameshViewModel
import kotlinx.coroutines.delay

enum class MeditationPractice(
    val id: String,
    val titleFa: String,
    val subtitleFa: String,
    val icon: String,
    val defaultMinutes: Int,
    val guidanceSteps: List<String>
) {
    MINDFULNESS(
        "mindfulness",
        "حضور در اکنون (Mindfulness)",
        "تمرکز بر تنفس طبیعی و مهار پرش افکار",
        "🌿",
        10,
        listOf(
            "به آرامی چشم‌هایت را ببند و بدنت را کاملاً رها کن.",
            "توجهت را به ورود و خروج طبیعی نفس معطوف کن.",
            "اگر فکری پدیدار شد، قضاوت نکن؛ فقط به عنوان ناظر نگاهش کن و دوباره به نفس برگرد.",
            "از سکوت و آرامش ناب این لحظه لذت ببر."
        )
    ),
    BODY_SCAN(
        "body_scan",
        "تن‌آرامی و اسکن بدن (Body Scan)",
        "رهاسازی تنش‌های عضلانی از پا تا فرق سر",
        "✨",
        15,
        listOf(
            "توجهت را به انگشتان پا بیاور؛ با هر بازدم تنش‌ها را رها کن.",
            "آگاهی را به ساق پا، زانوها و ران‌ها منتقل کن و عضلات را شل کن.",
            "ناحیه شکم، سینه و شانه‌ها را سبک و رها ساز.",
            "تمام عضلات صورت، پیشانی و فک را در آرامش محض غوطه‌ور کن."
        )
    ),
    LOVING_KINDNESS(
        "loving_kindness",
        "مهر و شفقت قلبی (Metta)",
        "گسترش عشق، مهربانی و بخشش به خود و دیگران",
        "❤️",
        10,
        listOf(
            "دستت را روی قلبت بگذار و بگو: من سزاوار صلح و آرامشم.",
            "آرزوی سلامتی و رهایی را برای کسانی که دوستشان داری بفرست.",
            "نور مهر قلبی‌ات را به تمام موجودات روی زمین گسترش بده."
        )
    ),
    ZEN_SILENCE(
        "zen_silence",
        "سکوت ژرف ذن (Vipassana)",
        "غوطه‌وری در سکوت و آگاهی ناب با زنگ کاسه تبتی",
        "🧘",
        20,
        listOf(
            "تنها در سکوت بنشین و بدون درگیری، شاهد جریان زندگی باش.",
            "طنین زنگ تبتی تو را هر لحظه به لنگرگاه اکنون بازمی‌گرداند."
        )
    ),
    DEEP_SLEEP(
        "deep_sleep",
        "یوگا نیدرا و خواب عمیق",
        "رهایی از استرس‌های روزمره و ورود به خواب آرام",
        "🌙",
        15,
        listOf(
            "امروز با تمام رویدادهایش پایان یافت؛ اکنون وقت استراحت عمیق روان است.",
            "سنگینی بدنت را به زمین بسپار؛ نفس‌هایت عمیق، آرام و سبک می‌شوند.",
            "ذهنت پاک و زلال است؛ با آرامش به خوابی گوارا فرو می‌روی."
        )
    ),
    GRATITUDE(
        "gratitude",
        "مراقبه شکرگزاری و فراوانی",
        "هماهنگی با ارتعاش بالای سپاسگزاری قلبی",
        "🌸",
        10,
        listOf(
            "سه نعمت بزرگ زندگی‌ات را در نظر بیاور و با تمام وجود بگو: سپاسگزارم.",
            "احساس قدردانی را همچون نوری گرم در سراسر قلبت احساس کن."
        )
    ),
    CUSTOM(
        "custom",
        "مراقبه دلخواه و آزاد",
        "تنظیم اختصاصی زمان، زنگ‌های فواصل و نوای پس‌زمینه",
        "⚙️",
        10,
        listOf("تمرکز دلخواه بر سکوت و صلح درون")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationScreen(
    viewModel: ArameshViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalSecondsFromDb by viewModel.totalMeditationSeconds.collectAsState()
    val keepAwakeSetting by viewModel.keepScreenAwake.collectAsState()

    val activeSound by viewModel.audioEngine.activeSoundType.collectAsState()
    val isSoundPlaying by viewModel.audioEngine.isPlaying.collectAsState()

    val serverAudios by viewModel.serverAudios.collectAsState()
    val currentOnlineAudio by viewModel.currentPlayingOnlineAudio.collectAsState()
    val isOnlinePlaying by viewModel.isOnlineAudioPlaying.collectAsState()
    val onlineProgress by viewModel.onlineAudioProgressSeconds.collectAsState()

    var selectedPractice by remember { mutableStateOf(MeditationPractice.MINDFULNESS) }
    var selectedMinutes by remember { mutableIntStateOf(selectedPractice.defaultMinutes) }
    var totalSessionSeconds by remember { mutableIntStateOf(selectedPractice.defaultMinutes * 60) }
    var secondsRemaining by remember { mutableIntStateOf(selectedPractice.defaultMinutes * 60) }
    var isRunning by remember { mutableStateOf(false) }

    // Audio options
    var audioGuidanceEnabled by remember { mutableStateOf(true) }
    var intervalBellsEnabled by remember { mutableStateOf(true) }
    var intervalMinutes by remember { mutableIntStateOf(3) }
    var currentGuidanceIndex by remember { mutableIntStateOf(0) }

    // File picker for custom user audio
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.audioEngine.playCustomAudio(it)
        }
    }

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

    // Timer loop with interval audio announcements
    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect
        while (isRunning && secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--

            val elapsed = totalSessionSeconds - secondsRemaining

            // Interval bell check
            if (intervalBellsEnabled && intervalMinutes > 0 && elapsed > 0 && elapsed % (intervalMinutes * 60) == 0) {
                viewModel.audioEngine.playTibetanBowl()
                // Guidance step advance
                val steps = selectedPractice.guidanceSteps
                if (steps.isNotEmpty()) {
                    currentGuidanceIndex = (currentGuidanceIndex + 1) % steps.size
                    if (audioGuidanceEnabled) {
                        viewModel.audioEngine.speakAnnouncement(steps[currentGuidanceIndex])
                    }
                }
            }
        }
        if (isRunning && secondsRemaining == 0) {
            isRunning = false
            viewModel.audioEngine.playSessionComplete()
            viewModel.recordMeditationSession(totalSessionSeconds, audioGuidanceEnabled)
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
                            text = "مراقبه و سکوت درون 🧘",
                            fontFamily = LalezarFont,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "سکوت، خلوت با خویشتن و رهایی از هیاهوی جهان",
                            fontFamily = VazirFont,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 2. Meditation Practice Types Selector
            item {
                Text(
                    text = "نوع تمرین مراقبه:",
                    fontFamily = LalezarFont,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(MeditationPractice.values()) { practice ->
                        FilterChip(
                            selected = selectedPractice == practice,
                            onClick = {
                                if (!isRunning) {
                                    selectedPractice = practice
                                    selectedMinutes = practice.defaultMinutes
                                    totalSessionSeconds = practice.defaultMinutes * 60
                                    secondsRemaining = practice.defaultMinutes * 60
                                    currentGuidanceIndex = 0
                                }
                            },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(practice.icon, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = practice.titleFa,
                                        fontFamily = VazirFont,
                                        fontWeight = if (selectedPractice == practice) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                }
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

            // 3. Duration Selector & Fine-Tuning
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مدت زمان مراقبه: ${JalaaliCalendarHelper.toPersianNumber(selectedMinutes)} دقیقه",
                                fontFamily = LalezarFont,
                                fontSize = 16.sp
                            )

                            if (!isRunning) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            if (selectedMinutes > 1) {
                                                selectedMinutes = (selectedMinutes - 5).coerceAtLeast(1)
                                                totalSessionSeconds = selectedMinutes * 60
                                                secondsRemaining = totalSessionSeconds
                                            }
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.Remove, contentDescription = "-5 دقیقه")
                                    }
                                    IconButton(
                                        onClick = {
                                            selectedMinutes = (selectedMinutes + 5).coerceAtMost(120)
                                            totalSessionSeconds = selectedMinutes * 60
                                            secondsRemaining = totalSessionSeconds
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "+5 دقیقه")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val presetMinutes = listOf(5, 10, 15, 20, 30, 45, 60)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(presetMinutes) { mins ->
                                FilterChip(
                                    selected = selectedMinutes == mins,
                                    onClick = {
                                        if (!isRunning) {
                                            selectedMinutes = mins
                                            totalSessionSeconds = mins * 60
                                            secondsRemaining = mins * 60
                                        }
                                    },
                                    label = {
                                        Text(
                                            "${JalaaliCalendarHelper.toPersianNumber(mins)} دقیقه",
                                            fontFamily = VazirFont,
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

                        Spacer(modifier = Modifier.height(14.dp))

                        // Audio toggles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "راهنمایی صوتی و اعلان مراحل",
                                    fontFamily = VazirFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "پخش پیام‌های آرامش‌بخش راهنما در طول تمرین",
                                    fontFamily = VazirFont,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = audioGuidanceEnabled,
                                onCheckedChange = { audioGuidanceEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "طنین زنگ کاسه تبتی در فواصل زمانی",
                                    fontFamily = VazirFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "پخش زنگ هشیاری هر ${JalaaliCalendarHelper.toPersianNumber(intervalMinutes)} دقیقه",
                                    fontFamily = VazirFont,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = intervalBellsEnabled,
                                onCheckedChange = { intervalBellsEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary)
                            )
                        }
                    }
                }
            }

            // 4. Circular Progress & Active Guidance Step Card
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val progress = if (totalSessionSeconds > 0) {
                            (totalSessionSeconds - secondsRemaining).toFloat() / totalSessionSeconds
                        } else 0f

                        val minsLeft = secondsRemaining / 60
                        val secsLeft = secondsRemaining % 60
                        val formattedTime = "%02d:%02d".format(minsLeft, secsLeft).map { c ->
                            when (c) {
                                '0' -> '۰'; '1' -> '۱'; '2' -> '۲'; '3' -> '۳'; '4' -> '۴'
                                '5' -> '۵'; '6' -> '۶'; '7' -> '۷'; '8' -> '۸'; '9' -> '۹'
                                else -> c
                            }
                        }.joinToString("")

                        Box(
                            modifier = Modifier.size(240.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { progress },
                                strokeWidth = 10.dp,
                                color = TealPrimary,
                                trackColor = TealPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxSize()
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = selectedPractice.icon,
                                    fontSize = 36.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = formattedTime,
                                    fontFamily = LalezarFont,
                                    fontSize = 42.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isRunning) "در حال مراقبه..." else "آماده سکوت",
                                    fontFamily = VazirFont,
                                    fontSize = 12.sp,
                                    color = if (isRunning) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Current Guidance Message Card
                        val guidanceSteps = selectedPractice.guidanceSteps
                        if (guidanceSteps.isNotEmpty()) {
                            val activeStep = guidanceSteps[currentGuidanceIndex.coerceIn(0, guidanceSteps.size - 1)]
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = TealPrimary.copy(alpha = 0.08f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "پیام راهنما:",
                                        fontFamily = LalezarFont,
                                        fontSize = 14.sp,
                                        color = TealPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = activeStep,
                                        fontFamily = VazirFont,
                                        fontSize = 13.sp,
                                        lineHeight = 20.sp,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (!isRunning) {
                                        isRunning = true
                                        viewModel.audioEngine.playTibetanBowl()
                                        if (audioGuidanceEnabled && selectedPractice.guidanceSteps.isNotEmpty()) {
                                            viewModel.audioEngine.speakAnnouncement(selectedPractice.guidanceSteps[0])
                                        }
                                    } else {
                                        isRunning = false
                                        val elapsedSec = totalSessionSeconds - secondsRemaining
                                        if (elapsedSec > 30) {
                                            viewModel.recordMeditationSession(elapsedSec, audioGuidanceEnabled)
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
                                    text = if (isRunning) "پایان مراقبه" else "آغاز مراقبه",
                                    fontFamily = LalezarFont,
                                    fontSize = 16.sp
                                )
                            }

                            if (!isRunning && secondsRemaining < totalSessionSeconds) {
                                OutlinedButton(
                                    onClick = {
                                        secondsRemaining = totalSessionSeconds
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

            // 5. Ambient Sounds & Custom Audio
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "نوای پس‌زمینه در حین مراقبه",
                                    fontFamily = LalezarFont,
                                    fontSize = 16.sp
                                )
                            }

                            if (isSoundPlaying) {
                                TextButton(onClick = { viewModel.audioEngine.stopAll() }) {
                                    Text("توقف صدا ✕", fontFamily = VazirFont, color = Color(0xFFE53935))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val sounds = listOf(
                            Triple(AmbientSoundType.RAIN, "باران ملایم", "🌧️"),
                            Triple(AmbientSoundType.OCEAN, "امواج اقیانوس", "🌊"),
                            Triple(AmbientSoundType.FIRE, "آتش هیزمی", "🔥"),
                            Triple(AmbientSoundType.FOREST, "جنگل و پرندگان", "🌲")
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(sounds) { (type, name, icon) ->
                                val isSelected = activeSound == type && isSoundPlaying
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) {
                                            viewModel.audioEngine.stopAmbient()
                                        } else {
                                            viewModel.audioEngine.playAmbient(type)
                                        }
                                    },
                                    label = {
                                        Text("$icon $name", fontFamily = VazirFont, fontSize = 12.sp)
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TealPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }

                            item {
                                OutlinedButton(
                                    onClick = { audioPickerLauncher.launch("audio/*") },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("فایل دلخواه 📁", fontFamily = VazirFont, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 6. Online Audio Meditations from User Server
            if (serverAudios.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        Text(
                            text = "مراقبه‌های صوتی آنلاین سرور ☁️🎧",
                            fontFamily = LalezarFont,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "جلسات صوتی با کیفیت بالا از سرور اختصاصی طراوت",
                            fontFamily = VazirFont,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                items(serverAudios) { audio ->
                    val isThisPlaying = currentOnlineAudio?.id == audio.id && isOnlinePlaying
                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (isThisPlaying) TealPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!audio.fullImageUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = audio.fullImageUrl,
                                        contentDescription = audio.title,
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                } else {
                                    Surface(
                                        color = TealPrimary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.size(52.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = "🧘", fontSize = 24.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = audio.title,
                                        fontFamily = LalezarFont,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    if (audio.description.isNotBlank()) {
                                        Text(
                                            text = audio.description,
                                            fontFamily = VazirFont,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (audio.category.isNotBlank()) {
                                            Surface(
                                                color = TealSecondary.copy(alpha = 0.15f),
                                                shape = CircleShape
                                            ) {
                                                Text(
                                                    text = audio.category,
                                                    fontFamily = VazirFont,
                                                    fontSize = 10.sp,
                                                    color = TealSecondary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        if (audio.durationSeconds > 0) {
                                            Text(
                                                text = "⏱️ ${audio.formattedDuration}",
                                                fontFamily = VazirFont,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        if (isThisPlaying) {
                                            viewModel.pauseOnlineAudio()
                                        } else {
                                            viewModel.playOnlineAudio(audio)
                                        }
                                    }
                                ) {
                                    Surface(
                                        color = if (isThisPlaying) Color(0xFFE53935) else TealPrimary,
                                        shape = CircleShape,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (isThisPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            if (currentOnlineAudio?.id == audio.id && audio.durationSeconds > 0) {
                                Spacer(modifier = Modifier.height(10.dp))
                                val progressRatio = (onlineProgress.toFloat() / audio.durationSeconds.toFloat()).coerceIn(0f, 1f)
                                androidx.compose.material3.LinearProgressIndicator(
                                    progress = { progressRatio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = TealPrimary
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
