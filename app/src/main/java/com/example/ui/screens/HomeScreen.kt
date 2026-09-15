package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.NaturePeople
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AmbientSoundType
import com.example.domain.model.JalaaliCalendarHelper
import com.example.ui.canvas.GratitudeTreeCanvas
import com.example.ui.theme.LalezarFont
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealSecondary
import com.example.ui.theme.VazirFont
import com.example.ui.viewmodel.ArameshViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: ArameshViewModel,
    onNavigateToGratitude: () -> Unit,
    onNavigateToBreathing: () -> Unit,
    onNavigateToMeditation: () -> Unit,
    onNavigateToCheckIn: () -> Unit,
    onNavigateToGrowth: (subTab: String) -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToBadges: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val treeState by viewModel.gratitudeTreeState.collectAsState()
    val streak by viewModel.currentStreak.collectAsState()
    val todayCheckIn by viewModel.todayCheckIn.collectAsState()
    val unlockedBadges by viewModel.unlockedBadges.collectAsState()
    val currentWisdom by viewModel.currentWisdom.collectAsState()

    val activeSound by viewModel.audioEngine.activeSoundType.collectAsState()
    val isSoundPlaying by viewModel.audioEngine.isPlaying.collectAsState()

    val formattedPersianToday = remember { JalaaliCalendarHelper.todayPersianFormatted() }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header Greeting & Streak
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        TealPrimary.copy(alpha = 0.12f),
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "امروز، لحظه‌ای برای آرامش 🌿",
                                    fontFamily = LalezarFont,
                                    fontSize = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formattedPersianToday,
                                    fontFamily = VazirFont,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Streak Badge
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFFF9800).copy(alpha = 0.15f),
                                modifier = Modifier.clip(RoundedCornerShape(16.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "روزهای پیوستگی",
                                        tint = Color(0xFFF57C00),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${JalaaliCalendarHelper.toPersianNumber(streak)} روز پیوسته",
                                        fontFamily = VazirFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFFE65100)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Daily Wisdom & Inspirational Quote Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = TealPrimary.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "حکمت و پیام روز",
                                    fontFamily = LalezarFont,
                                    fontSize = 16.sp,
                                    color = TealPrimary
                                )
                            }

                            IconButton(
                                onClick = { viewModel.nextWisdomQuote() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "پیام دیگر",
                                    tint = TealPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "«${currentWisdom.second}»",
                            fontFamily = VazirFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "— ${currentWisdom.first}",
                            fontFamily = VazirFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TealPrimary,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 3. Quick Mood Check-In Card (1-Tap direct action)
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "حال درونی امروز شما چطور است؟",
                                    fontFamily = LalezarFont,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (todayCheckIn != null) "ثبت شده امروز ✓ (قابل تغییر با یک لمس)" else "یک شکلک را برای ثبت حس‌وحال انتخاب کن",
                                    fontFamily = VazirFont,
                                    fontSize = 12.sp,
                                    color = if (todayCheckIn != null) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (todayCheckIn != null) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 5 Mood Emoji Buttons
                        val moods = listOf(
                            Pair(5, "😍"), // عالی
                            Pair(4, "😊"), // شاد
                            Pair(3, "😐"), // آرام
                            Pair(2, "😔"), // خسته
                            Pair(1, "😫")  // مضطرب
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            moods.forEach { (score, emoji) ->
                                val isSelected = todayCheckIn?.mood == score
                                val bgColor by animateColorAsState(
                                    targetValue = if (isSelected) TealPrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    label = "moodBg"
                                )

                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(bgColor)
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) TealPrimary else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            viewModel.recordQuickMoodCheckIn(score)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = emoji,
                                        fontSize = 26.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "ثبت جزئیات خواب و انرژی ←",
                                fontFamily = VazirFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TealPrimary,
                                modifier = Modifier.clickable { onNavigateToCheckIn() }
                            )
                        }
                    }
                }
            }

            // 4. Gratitude Garden Preview Card
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToGratitude() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mini interactive canvas
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(TealPrimary.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            GratitudeTreeCanvas(
                                state = treeState,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "باغ سپاسگزاری شما 🌳",
                                fontFamily = LalezarFont,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "مرحله: ${treeState.stage} (${JalaaliCalendarHelper.toPersianNumber(treeState.totalGratitudes)} میوه شکرگزاری)",
                                fontFamily = VazirFont,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { treeState.progressToNextStage },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = TealPrimary,
                                trackColor = TealPrimary.copy(alpha = 0.2f),
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "ورود به باغ",
                            tint = TealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 5. Mini Nature Soundscape Bar
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
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
                                    tint = TealSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "نوای پس‌زمینه طبیعت",
                                    fontFamily = LalezarFont,
                                    fontSize = 16.sp
                                )
                            }

                            if (isSoundPlaying) {
                                Button(
                                    onClick = { viewModel.audioEngine.stopAll() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("توقف", fontSize = 11.sp, fontFamily = VazirFont)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val ambientOptions = listOf(
                                Triple(AmbientSoundType.RAIN, "باران ملایم", "🌧️"),
                                Triple(AmbientSoundType.OCEAN, "امواج اقیانوس", "🌊"),
                                Triple(AmbientSoundType.FIRE, "آتش هیزمی", "🔥"),
                                Triple(AmbientSoundType.FOREST, "جنگل و پرندگان", "🌲")
                            )

                            items(ambientOptions) { (type, label, icon) ->
                                val isSelected = activeSound == type && isSoundPlaying
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            if (isSelected) {
                                                viewModel.audioEngine.stopAmbient()
                                            } else {
                                                viewModel.audioEngine.playAmbient(type)
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = icon, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = label,
                                            fontFamily = VazirFont,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6. Comprehensive Options & Features Hub (مرکز تمام بخش‌ها)
            item {
                Text(
                    text = "گزینه‌ها و امکانات آرامش 🧭",
                    fontFamily = LalezarFont,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 2,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. تنفس آگاهانه
                    QuickOptionCard(
                        title = "تنفس آگاهانه",
                        subtitle = "۱۰ الگوی تنفس ضد اضطراب",
                        icon = "💨",
                        badge = "صوتی",
                        color = Color(0xFF00897B),
                        onClick = onNavigateToBreathing,
                        modifier = Modifier.weight(1f)
                    )

                    // 2. مراقبه و سکوت
                    QuickOptionCard(
                        title = "مراقبه و سکوت",
                        subtitle = "۷ نوع مراقبه با زنگ تبتی",
                        icon = "🧘",
                        badge = "هدایت‌شده",
                        color = Color(0xFF5E35B1),
                        onClick = onNavigateToMeditation,
                        modifier = Modifier.weight(1f)
                    )

                    // 3. نامه به آینده
                    QuickOptionCard(
                        title = "نامه به آینده",
                        subtitle = "مهر و موم با تقویم شمسی",
                        icon = "✉️",
                        badge = "شخصی",
                        color = Color(0xFFD81B60),
                        onClick = { onNavigateToGrowth("letters") },
                        modifier = Modifier.weight(1f)
                    )

                    // 4. عشق به خود
                    QuickOptionCard(
                        title = "عشق به خود",
                        subtitle = "۳۰ گام شفقت به خویشتن",
                        icon = "❤️",
                        badge = "تمرین",
                        color = Color(0xFFE53935),
                        onClick = { onNavigateToGrowth("self_love") },
                        modifier = Modifier.weight(1f)
                    )

                    // 5. خودشناسی عمیق
                    QuickOptionCard(
                        title = "خودشناسی عمیق",
                        subtitle = "کشف ابعاد پنهان روان",
                        icon = "🧭",
                        badge = "روانشناسی",
                        color = Color(0xFF1E88E5),
                        onClick = { onNavigateToGrowth("self_knowledge") },
                        modifier = Modifier.weight(1f)
                    )

                    // 6. عزت نفس و پیروزی‌ها
                    QuickOptionCard(
                        title = "عزت نفس",
                        subtitle = "صندوق ثبت پیروزی‌ها",
                        icon = "🏆",
                        badge = "انگیزشی",
                        color = Color(0xFFFB8C00),
                        onClick = { onNavigateToGrowth("self_esteem") },
                        modifier = Modifier.weight(1f)
                    )

                    // 7. دفترچه خاطرات
                    QuickOptionCard(
                        title = "دفترچه خاطرات",
                        subtitle = "ثبت امن احساسات روزمره",
                        icon = "📖",
                        badge = "محرمانه",
                        color = Color(0xFF43A047),
                        onClick = { onNavigateToGrowth("journal") },
                        modifier = Modifier.weight(1f)
                    )

                    // 8. تقویم شمسی و آمار
                    QuickOptionCard(
                        title = "تقویم و آمار رشد",
                        subtitle = "پایش روزهای خورشیدی",
                        icon = "📅",
                        badge = "شمسی",
                        color = Color(0xFF00ACC1),
                        onClick = onNavigateToCalendar,
                        modifier = Modifier.weight(1f)
                    )

                    // 9. گنجه نشان‌های افتخار
                    QuickOptionCard(
                        title = "گنجه نشان‌ها",
                        subtitle = "${JalaaliCalendarHelper.toPersianNumber(unlockedBadges.size)} نشان آزاد شده",
                        icon = "🎖️",
                        badge = "جوایز",
                        color = Color(0xFF8E24AA),
                        onClick = onNavigateToBadges,
                        modifier = Modifier.weight(1f)
                    )

                    // 10. تنظیمات و تم
                    QuickOptionCard(
                        title = "تنظیمات برنامه",
                        subtitle = "تغییر تم، فونت و پشتیبان",
                        icon = "⚙️",
                        badge = "شخصی‌سازی",
                        color = Color(0xFF546E7A),
                        onClick = onNavigateToSettings,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
fun QuickOptionCard(
    title: String,
    subtitle: String,
    icon: String,
    badge: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 22.sp)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = color.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = badge,
                        fontFamily = VazirFont,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontFamily = LalezarFont,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontFamily = VazirFont,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
