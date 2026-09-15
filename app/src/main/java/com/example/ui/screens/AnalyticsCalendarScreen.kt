package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.JalaaliCalendarHelper
import com.example.domain.model.JalaaliDate
import com.example.ui.theme.LalezarFont
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealSecondary
import com.example.ui.theme.VazirFont
import com.example.ui.viewmodel.ArameshViewModel

data class DayActivitySummary(
    val jDate: JalaaliDate,
    val gregStr: String,
    val gratitudesCount: Int,
    val checkInMood: Int?,
    val meditationSecs: Int,
    val breathingCycles: Int,
    val journalCount: Int
) {
    val hasAnyActivity: Boolean get() = gratitudesCount > 0 || checkInMood != null || meditationSecs > 0 || breathingCycles > 0 || journalCount > 0
}

@Composable
fun AnalyticsCalendarScreen(
    viewModel: ArameshViewModel,
    modifier: Modifier = Modifier
) {
    val treeState by viewModel.gratitudeTreeState.collectAsState()
    val streak by viewModel.currentStreak.collectAsState()
    val checkIns by viewModel.allCheckIns.collectAsState()
    val totalCycles by viewModel.totalBreathingCycles.collectAsState()
    val totalMeditationSecs by viewModel.totalMeditationSeconds.collectAsState()
    val journals by viewModel.allJournals.collectAsState()
    val gratitudes by viewModel.allGratitudes.collectAsState()
    val allBreathing by viewModel.allBreathing.collectAsState()
    val allMeditations by viewModel.allMeditations.collectAsState()

    val todayJalaali = remember { JalaaliCalendarHelper.todayJalaali() }
    var displayedYear by remember { mutableStateOf(todayJalaali.year) }
    var displayedMonth by remember { mutableStateOf(todayJalaali.month) }

    var selectedDaySummary by remember { mutableStateOf<DayActivitySummary?>(null) }

    // Map Gregorian string -> Activity count
    val activitiesByDate = remember(checkIns, gratitudes, journals, allBreathing, allMeditations) {
        val map = mutableMapOf<String, DayActivitySummary>()
        // Helper to get or init
        fun getSummary(gregStr: String): DayActivitySummary {
            return map[gregStr] ?: run {
                val jDate = JalaaliCalendarHelper.fromGregorianString(gregStr)
                DayActivitySummary(jDate, gregStr, 0, null, 0, 0, 0)
            }
        }

        gratitudes.forEach { g ->
            val curr = getSummary(g.date)
            map[g.date] = curr.copy(gratitudesCount = curr.gratitudesCount + 1)
        }
        checkIns.forEach { c ->
            val curr = getSummary(c.date)
            map[c.date] = curr.copy(checkInMood = c.mood)
        }
        journals.forEach { j ->
            val curr = getSummary(j.date)
            map[j.date] = curr.copy(journalCount = curr.journalCount + 1)
        }
        allBreathing.forEach { b ->
            val curr = getSummary(b.date)
            map[b.date] = curr.copy(breathingCycles = curr.breathingCycles + b.cycles)
        }
        allMeditations.forEach { m ->
            val curr = getSummary(m.date)
            map[m.date] = curr.copy(meditationSecs = curr.meditationSecs + m.durationSec)
        }
        map
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "تقویم خورشیدی و آمار رشد 📊",
                        fontFamily = LalezarFont,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "پایش دقیق روزهای پیوستگی و سوابق فعالیت‌های درون‌نگری",
                        fontFamily = VazirFont,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Overview Stats Cards
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "دستاوردهای کلی آرامش",
                            fontFamily = LalezarFont,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatBox(title = "پیوستگی", value = "${JalaaliCalendarHelper.toPersianNumber(streak)} روز", icon = "🔥")
                            StatBox(title = "سپاسگزاری", value = "${JalaaliCalendarHelper.toPersianNumber(treeState.totalEntries)} ثبت", icon = "🌳")
                            StatBox(title = "چرخه تنفس", value = "${JalaaliCalendarHelper.toPersianNumber(totalCycles)} بار", icon = "💨")
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatBox(title = "مراقبه", value = "${JalaaliCalendarHelper.toPersianNumber(totalMeditationSecs / 60)} دقیقه", icon = "🧘")
                            StatBox(title = "خاطرات", value = "${JalaaliCalendarHelper.toPersianNumber(journals.size)} نوشته", icon = "📖")
                            StatBox(title = "پایش حال", value = "${JalaaliCalendarHelper.toPersianNumber(checkIns.size)} روز", icon = "🧭")
                        }
                    }
                }
            }

            // Accurate Persian (Jalaali) Interactive Calendar Card
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Month navigation header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                if (displayedMonth == 1) {
                                    displayedMonth = 12
                                    displayedYear--
                                } else {
                                    displayedMonth--
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "ماه قبل",
                                    tint = TealPrimary
                                )
                            }

                            val monthDate = JalaaliDate(displayedYear, displayedMonth, 1)
                            Text(
                                text = "${monthDate.monthName()} ${JalaaliCalendarHelper.toPersianNumber(displayedYear)} خورشیدی",
                                fontFamily = LalezarFont,
                                fontSize = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            IconButton(onClick = {
                                if (displayedMonth == 12) {
                                    displayedMonth = 1
                                    displayedYear++
                                } else {
                                    displayedMonth++
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "ماه بعد",
                                    tint = TealPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Weekday labels (شنبه تا جمعه)
                        val weekDays = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            weekDays.forEach { d ->
                                Text(
                                    text = d,
                                    fontFamily = VazirFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (d == "ج") Color(0xFFE53935) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Correct start day and total days
                        val startDayOfWeek = JalaaliCalendarHelper.getFirstDayOfWeek(displayedYear, displayedMonth)
                        val daysInMonth = JalaaliCalendarHelper.getDaysInMonth(displayedYear, displayedMonth)
                        val totalCells = startDayOfWeek + daysInMonth
                        val numRows = (totalCells + 6) / 7

                        for (r in 0 until numRows) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (c in 0..6) {
                                    val cellIndex = r * 7 + c
                                    val dayNum = cellIndex - startDayOfWeek + 1

                                    if (dayNum in 1..daysInMonth) {
                                        val thisJDate = JalaaliDate(displayedYear, displayedMonth, dayNum)
                                        val gregStr = JalaaliCalendarHelper.jalaaliToGregorianString(thisJDate)
                                        val isToday = thisJDate.year == todayJalaali.year &&
                                                thisJDate.month == todayJalaali.month &&
                                                thisJDate.day == todayJalaali.day

                                        val isSelected = selectedDaySummary?.jDate == thisJDate
                                        val summary = activitiesByDate[gregStr]
                                        val hasActivity = summary?.hasAnyActivity == true

                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        isToday -> TealPrimary
                                                        isSelected -> TealSecondary.copy(alpha = 0.3f)
                                                        hasActivity -> TealPrimary.copy(alpha = 0.12f)
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .border(
                                                    width = when {
                                                        isSelected -> 2.dp
                                                        isToday -> 1.5.dp
                                                        else -> 0.dp
                                                    },
                                                    color = if (isToday) TealPrimary else if (isSelected) TealSecondary else Color.Transparent,
                                                    shape = CircleShape
                                                )
                                                .clickable {
                                                    selectedDaySummary = summary ?: DayActivitySummary(
                                                        jDate = thisJDate,
                                                        gregStr = gregStr,
                                                        gratitudesCount = 0,
                                                        checkInMood = null,
                                                        meditationSecs = 0,
                                                        breathingCycles = 0,
                                                        journalCount = 0
                                                    )
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = JalaaliCalendarHelper.toPersianNumber(dayNum),
                                                    fontFamily = VazirFont,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isToday || hasActivity || isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = when {
                                                        isToday -> Color.White
                                                        c == 6 -> Color(0xFFE53935)
                                                        else -> MaterialTheme.colorScheme.onSurface
                                                    }
                                                )
                                                if (hasActivity && !isToday) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(TealPrimary)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(42.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Selected Day Inspection Card
            item {
                selectedDaySummary?.let { summary ->
                    ElevatedCard(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.EventAvailable,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${JalaaliCalendarHelper.toPersianNumber(summary.jDate.day)} ${summary.jDate.monthName()} ${JalaaliCalendarHelper.toPersianNumber(summary.jDate.year)}",
                                        fontFamily = LalezarFont,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (summary.hasAnyActivity) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = TealPrimary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "روز پرثمر ✓",
                                            fontFamily = VazirFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = TealPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (!summary.hasAnyActivity) {
                                Text(
                                    text = "در این روز خورشیدی فعالیتی ثبت نشده بود.",
                                    fontFamily = VazirFont,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (summary.checkInMood != null) {
                                        val moodEmoji = when (summary.checkInMood) {
                                            5 -> "😍 فوق‌العاده"
                                            4 -> "😊 شاد و خوب"
                                            3 -> "😐 آرام"
                                            2 -> "😔 خسته"
                                            else -> "😫 مضطرب"
                                        }
                                        Text(
                                            text = "• وضعیت مود: $moodEmoji",
                                            fontFamily = VazirFont,
                                            fontSize = 13.sp
                                        )
                                    }
                                    if (summary.gratitudesCount > 0) {
                                        Text(
                                            text = "• سپاسگزاری‌ها: ${JalaaliCalendarHelper.toPersianNumber(summary.gratitudesCount)} نعمت ثبت شده",
                                            fontFamily = VazirFont,
                                            fontSize = 13.sp
                                        )
                                    }
                                    if (summary.meditationSecs > 0) {
                                        Text(
                                            text = "• مراقبه: ${JalaaliCalendarHelper.toPersianNumber(summary.meditationSecs / 60)} دقیقه سکوت و آگاهی",
                                            fontFamily = VazirFont,
                                            fontSize = 13.sp
                                        )
                                    }
                                    if (summary.breathingCycles > 0) {
                                        Text(
                                            text = "• تنفس آگاهانه: ${JalaaliCalendarHelper.toPersianNumber(summary.breathingCycles)} چرخه تنفس",
                                            fontFamily = VazirFont,
                                            fontSize = 13.sp
                                        )
                                    }
                                    if (summary.journalCount > 0) {
                                        Text(
                                            text = "• خاطرات: ${JalaaliCalendarHelper.toPersianNumber(summary.journalCount)} نوشته در دفترچه خاطرات",
                                            fontFamily = VazirFont,
                                            fontSize = 13.sp
                                        )
                                    }
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
    }
}

@Composable
private fun StatBox(title: String, value: String, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontFamily = VazirFont,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            text = title,
            fontFamily = VazirFont,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
