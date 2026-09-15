package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.JournalEntity
import com.example.data.local.entity.LetterEntity
import com.example.domain.model.JalaaliCalendarHelper
import com.example.domain.model.JalaaliDate
import com.example.ui.theme.GoldenFruitColor
import com.example.ui.theme.LalezarFont
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealSecondary
import com.example.ui.theme.TealSecondaryContainer
import com.example.ui.theme.VazirFont
import com.example.ui.viewmodel.ArameshViewModel
import kotlinx.coroutines.delay

enum class GrowthSubTab(val titleFa: String, val icon: String) {
    MINDFULNESS("ذهن‌آگاهی", "🌿"),
    LETTERS("نامه‌ها", "✉️"),
    SELF_LOVE("عشق به خود", "❤️"),
    SELF_KNOWLEDGE("خودشناسی", "🧭"),
    SELF_ESTEEM("عزت نفس", "🏆"),
    JOURNAL("خاطرات", "📖")
}

@Composable
fun PersonalGrowthScreen(
    viewModel: ArameshViewModel,
    modifier: Modifier = Modifier
) {
    val targetTab by viewModel.targetGrowthTab.collectAsState()
    var selectedTab by remember { mutableStateOf(GrowthSubTab.MINDFULNESS) }

    LaunchedEffect(targetTab) {
        when (targetTab) {
            "mindfulness" -> selectedTab = GrowthSubTab.MINDFULNESS
            "letters" -> selectedTab = GrowthSubTab.LETTERS
            "self_love" -> selectedTab = GrowthSubTab.SELF_LOVE
            "self_knowledge" -> selectedTab = GrowthSubTab.SELF_KNOWLEDGE
            "self_esteem" -> selectedTab = GrowthSubTab.SELF_ESTEEM
            "journal" -> selectedTab = GrowthSubTab.JOURNAL
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Sub-tabs scrollable row
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = TealPrimary
            ) {
                GrowthSubTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(tab.icon, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(tab.titleFa, fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    GrowthSubTab.MINDFULNESS -> MindfulnessSubView(viewModel)
                    GrowthSubTab.LETTERS -> LettersSubView(viewModel)
                    GrowthSubTab.SELF_LOVE -> SelfLoveSubView(viewModel)
                    GrowthSubTab.SELF_KNOWLEDGE -> SelfKnowledgeSubView(viewModel)
                    GrowthSubTab.SELF_ESTEEM -> SelfEsteemSubView(viewModel)
                    GrowthSubTab.JOURNAL -> JournalSubView(viewModel)
                }
            }
        }
    }
}

// 1. MINDFULNESS SUB-VIEW
@Composable
private fun MindfulnessSubView(viewModel: ArameshViewModel) {
    val mindfulnessLogs by viewModel.allMindfulness.collectAsState()

    var activeWizard by remember { mutableStateOf<String?>(null) } // "ground", "body_scan", "one_min"

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text(
                text = "تمرین‌های حضور در لحظه اکنون",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // 5-4-3-2-1 Sensory Grounding Wizard Card
        item {
            ElevatedCard(
                onClick = { activeWizard = "ground" },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🖐️", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "تکنیک اتصال به زمین (۵-۴-۳-۲-۱)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(
                            text = "مهار اضطراب با فعال‌سازی حواس بینایی، بساوایی، شنوایی، بویایی و چشایی",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Body Scan Wizard Card
        item {
            ElevatedCard(
                onClick = { activeWizard = "body_scan" },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🧘", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "اسکن بدن (Body Scan)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(
                            text = "مشاهده آگاهانه تنش‌های عضلانی از نوک پا تا تارک سر بدون قضاوت",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 1-Minute Presence Timer Card
        item {
            ElevatedCard(
                onClick = { activeWizard = "one_min" },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⏱️", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "یک دقیقه مکث آگاهانه", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(
                            text = "شصت ثانیه توقف کارها، چشم‌بستن و حس ضربان زندگی",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "جلسات تکمیل‌شده (${JalaaliCalendarHelper.toPersianNumber(mindfulnessLogs.size)})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(mindfulnessLogs) { log ->
            val title = when (log.kind) {
                "ground" -> "تمرین حواس پنجگانه ۵-۴-۳-۲-۱"
                "body_scan" -> "تمرین اسکن آگاهانه بدن"
                else -> "یک دقیقه مکث طلایی"
            }
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text(text = log.date, style = MaterialTheme.typography.labelSmall, color = TealPrimary)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(48.dp)) }
    }

    // Interactive Grounding Wizard Dialog
    if (activeWizard == "ground") {
        GroundingWizardDialog(
            onComplete = {
                viewModel.recordMindfulness("ground", "تکمیل تمرین ۵-۴-۳-۲-۱ حواس پنجگانه")
                activeWizard = null
            },
            onDismiss = { activeWizard = null }
        )
    }

    // Body scan dialog
    if (activeWizard == "body_scan") {
        BodyScanWizardDialog(
            onComplete = {
                viewModel.recordMindfulness("body_scan", "تکمیل تمرین اسکن بدن")
                activeWizard = null
            },
            onDismiss = { activeWizard = null }
        )
    }

    // One minute presence dialog
    if (activeWizard == "one_min") {
        OneMinutePresenceDialog(
            onComplete = {
                viewModel.recordMindfulness("one_min", "یک دقیقه مکث آگاهانه")
                activeWizard = null
            },
            onDismiss = { activeWizard = null }
        )
    }
}

// 2. LETTERS SUB-VIEW (Sealed letters with customizable Persian opening date!)
@Composable
private fun LettersSubView(viewModel: ArameshViewModel) {
    val letters by viewModel.allLetters.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var readingLetter by remember { mutableStateOf<LetterEntity?>(null) }

    val todayStr = JalaaliCalendarHelper.todayGregorianString()
    val todayJalaali = remember { JalaaliCalendarHelper.todayJalaali() }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "نامه‌های مهر و موم شده ✉️",
                        fontFamily = LalezarFont,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "نامه‌ای برای آینده بنویس؛ تا تاریخ مقرر پنهان و محفوظ می‌ماند",
                        fontFamily = VazirFont,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("نامه جدید", fontFamily = VazirFont, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (letters.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📬", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "صندوق نامه‌ها خالی است",
                            fontFamily = LalezarFont,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "برای خودت در آینده نامه‌ای بنویس و تاریخ دقیق بازگشایی خورشیدی را تعیین کن!",
                            fontFamily = VazirFont,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(letters, key = { it.id }) { letter ->
                val isSealed = letter.openDate > todayStr
                val openJalaali = remember(letter.openDate) {
                    JalaaliCalendarHelper.fromGregorianString(letter.openDate)
                }
                val formattedOpenDate = remember(letter.openDate) {
                    "${JalaaliCalendarHelper.toPersianNumber(openJalaali.day)} ${openJalaali.monthName()} ${JalaaliCalendarHelper.toPersianNumber(openJalaali.year)}"
                }

                Card(
                    onClick = { readingLetter = letter },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSealed) Color(0xFFFFF8E1) else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isSealed) Color(0xFFFFECB3) else TealPrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSealed) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = if (isSealed) Color(0xFFFFA000) else TealPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = letter.title,
                                    fontFamily = LalezarFont,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isSealed) "🔒 مهر و موم تا: $formattedOpenDate" else "🔓 بازگشایی شده ($formattedOpenDate)",
                                    fontFamily = VazirFont,
                                    fontSize = 11.sp,
                                    color = if (isSealed) Color(0xFFE65100) else TealPrimary
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.deleteLetter(letter) }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Gray)
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(48.dp)) }
    }

    // Create Letter Dialog with Configurable Date
    if (showCreateDialog) {
        var title by remember { mutableStateOf("") }
        var body by remember { mutableStateOf("") }
        var audience by remember { mutableStateOf("future") }

        // Date selection mode: 0 = Today, 7 = 7 days, 30 = 1 month, 90 = 3 months, 365 = 1 year, -1 = Custom Solar
        var selectedPresetDays by remember { mutableIntStateOf(30) }
        var customYear by remember { mutableIntStateOf(todayJalaali.year) }
        var customMonth by remember { mutableIntStateOf(todayJalaali.month) }
        var customDay by remember { mutableIntStateOf(todayJalaali.day) }

        // Function to compute final Gregorian date string
        fun computeFinalOpenDate(): String {
            if (selectedPresetDays == -1) {
                // Use custom Jalaali date
                return JalaaliCalendarHelper.jalaaliToGregorianString(JalaaliDate(customYear, customMonth, customDay))
            } else {
                val cal = java.util.Calendar.getInstance()
                cal.add(java.util.Calendar.DAY_OF_YEAR, selectedPresetDays)
                val y = cal.get(java.util.Calendar.YEAR)
                val m = cal.get(java.util.Calendar.MONTH) + 1
                val d = cal.get(java.util.Calendar.DAY_OF_MONTH)
                return "%04d-%02d-%02d".format(y, m, d)
            }
        }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = "نگارش نامه به خویشتن ✉️",
                    fontFamily = LalezarFont,
                    fontSize = 19.sp
                )
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("عنوان نامه", fontFamily = VazirFont) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = body,
                            onValueChange = { body = it },
                            label = { Text("متن نامه برای آینده...", fontFamily = VazirFont) },
                            minLines = 4,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text(
                            text = "تاریخ بازگشایی نامه (قابل تنظیم):",
                            fontFamily = LalezarFont,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val presets = listOf(
                                Pair(0, "امروز"),
                                Pair(3, "۳ روز دیگر"),
                                Pair(7, "۱ هفته دیگر"),
                                Pair(30, "۱ ماه دیگر"),
                                Pair(90, "۳ ماه دیگر"),
                                Pair(365, "۱ سال دیگر"),
                                Pair(-1, "تاریخ دلخواه 🗓️")
                            )
                            items(presets) { (days, label) ->
                                FilterChip(
                                    selected = selectedPresetDays == days,
                                    onClick = { selectedPresetDays = days },
                                    label = { Text(label, fontFamily = VazirFont, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TealPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Custom Solar Hijri Stepper UI
                    if (selectedPresetDays == -1) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "تنظیم تاریخ شمسی بازگشایی:",
                                        fontFamily = VazirFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TealPrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Year row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("سال:", fontFamily = VazirFont, fontSize = 12.sp)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = { if (customYear > 1400) customYear-- }) {
                                                Text("−", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Text(
                                                text = JalaaliCalendarHelper.toPersianNumber(customYear),
                                                fontFamily = VazirFont,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            IconButton(onClick = { if (customYear < 1420) customYear++ }) {
                                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    // Month row
                                    val monthNames = listOf(
                                        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
                                        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("ماه:", fontFamily = VazirFont, fontSize = 12.sp)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = { if (customMonth > 1) customMonth-- }) {
                                                Text("−", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Text(
                                                text = monthNames.getOrElse(customMonth - 1) { "" },
                                                fontFamily = VazirFont,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            IconButton(onClick = { if (customMonth < 12) customMonth++ }) {
                                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    // Day row
                                    val maxDays = JalaaliCalendarHelper.getDaysInMonth(customYear, customMonth)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("روز:", fontFamily = VazirFont, fontSize = 12.sp)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = { if (customDay > 1) customDay-- }) {
                                                Text("−", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Text(
                                                text = JalaaliCalendarHelper.toPersianNumber(customDay),
                                                fontFamily = VazirFont,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            IconButton(onClick = { if (customDay < maxDays) customDay++ }) {
                                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        val previewDateStr = computeFinalOpenDate()
                        val previewJDate = JalaaliCalendarHelper.fromGregorianString(previewDateStr)
                        Text(
                            text = "🗓️ تاریخ بازگشایی: ${JalaaliCalendarHelper.toPersianNumber(previewJDate.day)} ${previewJDate.monthName()} ${JalaaliCalendarHelper.toPersianNumber(previewJDate.year)} خورشیدی",
                            fontFamily = VazirFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TealPrimary
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && body.isNotBlank()) {
                            val openDateStr = computeFinalOpenDate()
                            viewModel.addLetter(title, audience, body, openDateStr)
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("مهر و موم و ذخیره", fontFamily = VazirFont, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("انصراف", fontFamily = VazirFont)
                }
            }
        )
    }

    // Read Letter Dialog (handles sealed logic in Persian!)
    readingLetter?.let { letter ->
        val isSealed = letter.openDate > todayStr
        val openJalaali = remember(letter.openDate) {
            JalaaliCalendarHelper.fromGregorianString(letter.openDate)
        }
        val formattedOpenDate = remember(letter.openDate) {
            "${JalaaliCalendarHelper.toPersianNumber(openJalaali.day)} ${openJalaali.monthName()} ${JalaaliCalendarHelper.toPersianNumber(openJalaali.year)}"
        }

        AlertDialog(
            onDismissRequest = { readingLetter = null },
            title = {
                Text(
                    text = letter.title,
                    fontFamily = LalezarFont,
                    fontSize = 19.sp
                )
            },
            text = {
                if (isSealed) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "🔒", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "این نامه هنوز در آینده در انتظار شماست!",
                            fontFamily = LalezarFont,
                            fontSize = 16.sp,
                            color = Color(0xFFE65100)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "تاریخ بازگشایی خورشیدی:\n$formattedOpenDate\n\nلطفاً تا فرا رسیدن این تاریخ صبور باشید؛ این نامه برای آینده شما نگاشته شده است.",
                            fontFamily = VazirFont,
                            fontSize = 12.sp,
                            lineHeight = 20.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column {
                        Text(
                            text = "🔓 این نامه اکنون بازگشایی شده است:",
                            fontFamily = VazirFont,
                            fontSize = 11.sp,
                            color = TealPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = letter.body,
                            fontFamily = VazirFont,
                            fontSize = 14.sp,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { readingLetter = null },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("بستن", fontFamily = VazirFont)
                }
            }
        )
    }
}

// 3. SELF LOVE SUB-VIEW
@Composable
private fun SelfLoveSubView(viewModel: ArameshViewModel) {
    var completedMirror by remember { mutableStateOf(false) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text(text = "عشق به خود و شفقت درونی ❤️", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        // Daily Affirmation
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💖", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "جمله تاکیدی مهربانی با خود", fontWeight = FontWeight.Bold, color = Color(0xFFC2185B))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "«من با تمام کاستی‌ها و زیبایی‌هایم، سزاوار عشق، احترام و آرامش هستم. من خانه امن خویشتنم.»",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 26.sp,
                        color = Color(0xFF880E4F)
                    )
                }
            }
        }

        // Mirror Exercise
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🪞", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "تمرین نگاه در آینه (Mirror Work)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "برای ۲ دقیقه در آینه به چشمان خودت نگاه کن، لبخند بزن و از قلبت بگو: دوستت دارم، تو کافی هستی.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            completedMirror = true
                            viewModel.recordSelfLove("mirror", "تکمیل تمرین آینه")
                        },
                        enabled = !completedMirror,
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (completedMirror) "امروز انجام شد ✨" else "این تمرین را انجام دادم")
                    }
                }
            }
        }

        // Kindness Task
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "دعوت به مهربانی امروز 🌸", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "امروز یک نوشیدنی مورد علاقه‌ات را در سکوت بنوش یا ۱۰ دقیقه بدون گوشی در هوای آزاد قدم بزن.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(48.dp)) }
    }
}

// 4. SELF KNOWLEDGE SUB-VIEW
@Composable
private fun SelfKnowledgeSubView(viewModel: ArameshViewModel) {
    val coreValues by viewModel.coreValues.collectAsState()
    val strengths by viewModel.personalStrengths.collectAsState()

    var newValueText by remember { mutableStateOf("") }
    var answerText by remember { mutableStateOf("") }
    var hasAnsweredToday by remember { mutableStateOf(false) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text(text = "خودشناسی و قطب‌نمای درون 🧭", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        // Daily Question
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "پرسش تامل‌برانگیز امروز:", fontWeight = FontWeight.Bold, color = TealPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "اگر هیچ ترسی از قضاوت دیگران نداشتی، فردا چه تصمیمی می‌گرفتی؟",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (!hasAnsweredToday) {
                        OutlinedTextField(
                            value = answerText,
                            onValueChange = { answerText = it },
                            placeholder = { Text("پاسخ شما...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (answerText.isNotBlank()) {
                                    viewModel.answerSelfKnowQuestion("اگر هیچ ترسی نداشتی چه می‌کردی؟", answerText)
                                    hasAnsweredToday = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("ثبت پاسخ")
                        }
                    } else {
                        Text(text = "✓ پاسخ شما با موفقیت ثبت شد.", color = TealPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Core Values (max 8)
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
                        Text(text = "ارزش‌های بنیادین شما (حداکثر ۸ مورد)", fontWeight = FontWeight.Bold)
                        Text(text = "${coreValues.size} / ۸", style = MaterialTheme.typography.labelSmall, color = TealPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(coreValues) { item ->
                            FilterChip(
                                selected = true,
                                onClick = { viewModel.deleteSelfKnowItem(item) },
                                label = { Text(item.content) },
                                trailingIcon = { Icon(Icons.Default.Close, contentDescription = "حذف", modifier = Modifier.size(14.dp)) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealSecondaryContainer)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    if (coreValues.size < 8) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = newValueText,
                                onValueChange = { newValueText = it },
                                placeholder = { Text("مثلاً: آزادی، صداقت، رشد...") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (newValueText.isNotBlank()) {
                                        viewModel.addSelfKnowItem("values", newValueText)
                                        newValueText = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                            ) {
                                Text("افزودن")
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(48.dp)) }
    }
}

// 5. SELF ESTEEM SUB-VIEW
@Composable
private fun SelfEsteemSubView(viewModel: ArameshViewModel) {
    val wins by viewModel.allEsteemWins.collectAsState()
    var newWinText by remember { mutableStateOf("") }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text(text = "عزت نفس و افتخارات درونی 🏆", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        // Daily Challenge Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "چالش جسارت امروز 🦁", fontWeight = FontWeight.Bold, color = Color(0xFFF57F17))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "امروز به یک درخواست ناخواسته با احترام و قاطعیت «نه» بگو و به حریم آرامشت احترام بگذار.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }

        // Win Logbook Input
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "دفترچه پیروزی‌های من (Win Log)", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newWinText,
                            onValueChange = { newWinText = it },
                            placeholder = { Text("پیروزی یا کار خوبی که امروز انجام دادی...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newWinText.isNotBlank()) {
                                    viewModel.addEsteemWin(newWinText)
                                    newWinText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Text("ثبت")
                        }
                    }
                }
            }
        }

        // Wins List
        items(wins) { win ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⭐", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = win.title, fontWeight = FontWeight.SemiBold)
                    }
                    Text(text = win.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(48.dp)) }
    }
}

// 6. JOURNAL SUB-VIEW
@Composable
private fun JournalSubView(viewModel: ArameshViewModel) {
    val journals by viewModel.allJournals.collectAsState()
    var showEditor by remember { mutableStateOf(false) }

    val randomPrompts = listOf(
        "امروز چه حسی بیشترین حضور را در قلبم داشت؟",
        "اگر امروز آخرین روز ماه بود، چه دستاوردی خوشحالم می‌کرد؟",
        "چه موضوعی ذهنم را مشغول کرده و چطور رهایش کنم؟",
        "زیباترین گفتگویی که این هفته داشتم چه بود؟"
    )

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "دفتر خاطرات و تاملات روزانه 📖", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = "نوشتن، افکار آشفته را شفاف و آرام می‌کند",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = { showEditor = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("نوشته تازه")
                }
            }
        }

        if (journals.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🖋️", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "هنوز یادداشتی ننوشته‌اید", fontWeight = FontWeight.Bold)
                        Text(
                            text = "افکارتان را روی کاغذ بیاورید تا ذهنتان سبک شود.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(journals, key = { it.id }) { journal ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = journal.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = when (journal.mood) {
                                        5 -> "😍"
                                        4 -> "😊"
                                        3 -> "😐"
                                        2 -> "😔"
                                        else -> "😫"
                                    },
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { viewModel.deleteJournal(journal) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = journal.body,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = journal.date, style = MaterialTheme.typography.labelSmall, color = TealPrimary)
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(48.dp)) }
    }

    // Journal Editor Dialog
    if (showEditor) {
        var title by remember { mutableStateOf("") }
        var body by remember { mutableStateOf("") }
        var mood by remember { mutableIntStateOf(4) }

        AlertDialog(
            onDismissRequest = { showEditor = false },
            title = { Text("یادداشت روزانه جدید", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Random prompt chip
                    OutlinedButton(
                        onClick = { title = randomPrompts.random() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = TealPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ایده رندوم برای نوشتن", fontSize = 12.sp)
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("موضوع یا عنوان") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = body,
                        onValueChange = { if (it.length <= 5000) body = it },
                        label = { Text("جریان افکار، احساسات و رویدادها...") },
                        minLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Mood selector
                    Text("حال و هوای این نوشته:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val emojis = listOf(Pair(1, "😫"), Pair(2, "😔"), Pair(3, "😐"), Pair(4, "😊"), Pair(5, "😍"))
                        emojis.forEach { (m, emoji) ->
                            FilterChip(
                                selected = mood == m,
                                onClick = { mood = m },
                                label = { Text(emoji, fontSize = 20.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && body.isNotBlank()) {
                            viewModel.saveJournal(title, body, mood)
                            showEditor = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("ثبت در دفترچه")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditor = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}

// Dialogs for Grounding and Wizards
@Composable
private fun GroundingWizardDialog(onComplete: () -> Unit, onDismiss: () -> Unit) {
    var step by remember { mutableIntStateOf(5) }

    val instruction = when (step) {
        5 -> "👀 ۵ چیزی که اطرافت می‌بینی را نام ببر و جزئیات رنگ و فرمشان را تماشا کن."
        4 -> "✋ ۴ چیزی که می‌توانی لمس کنی (مثل بافت لباست، سردی لیوان آب، سطح میز)."
        3 -> "👂 ۳ صدایی که هم‌اکنون در محیط می‌شنوی (صدای باد، تیک‌تاک، تنفست)."
        2 -> "👃 ۲ بویی که حس می‌کنی (عطر هوا، بوی قهوه یا خاک باران‌خورده)."
        else -> "👅 ۱ مزه‌ای که در دهانت حس می‌کنی یا یک نفس بسیار عمیق و سپاسگزارانه بکش."
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تمرین حواس پنجگانه (گام $step از ۵)", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                LinearProgressIndicator(
                    progress = { (6 - step) / 5f },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = TealPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = instruction, style = MaterialTheme.typography.bodyLarge, lineHeight = 26.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (step > 1) {
                        step--
                    } else {
                        onComplete()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text(if (step > 1) "گام بعدی" else "تکمیل تمرین ✨")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("بستن") }
        }
    )
}

@Composable
private fun BodyScanWizardDialog(onComplete: () -> Unit, onDismiss: () -> Unit) {
    var partIndex by remember { mutableIntStateOf(0) }
    val parts = listOf(
        "نوک انگشتان پا و کف پاها: حس سنگینی و تماس با زمین را بدون قضاوت حس کن.",
        "ساق و زانوها: هرگونه تنش جمع‌شده را با یک بازدم آرام آزاد کن.",
        "شکم و سینه: بالا و پایین رفتن نرم قفسه سینه را نظاره‌گر باش.",
        "شاخه‌ها و شانه‌ها: بگذار شانه‌هایت از گوش‌هایت فاصله بگیرند و رها شوند.",
        "عضلات صورت و پیشانی: فک را شل کن و اخم پیشانی را به لبخندی درونی بدل نما."
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("مراقبه اسکن بدن", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                LinearProgressIndicator(
                    progress = { (partIndex + 1f) / parts.size },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = TealPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = parts[partIndex], style = MaterialTheme.typography.bodyLarge, lineHeight = 26.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (partIndex < parts.size - 1) {
                        partIndex++
                    } else {
                        onComplete()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text(if (partIndex < parts.size - 1) "بخش بعدی بدن" else "پایان اسکن ✨")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

@Composable
private fun OneMinutePresenceDialog(onComplete: () -> Unit, onDismiss: () -> Unit) {
    var secondsLeft by remember { mutableIntStateOf(60) }

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        onComplete()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("یک دقیقه حضور خالص", fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "$secondsLeft",
                    fontSize = 54.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TealPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "چشمانت را ببند، تنها به جریان هوای ورودی از بینی توجه کن...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(onClick = onComplete, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                Text("تکمیل")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}
