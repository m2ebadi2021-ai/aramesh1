package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.JalaaliCalendarHelper
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealSecondary
import com.example.ui.viewmodel.ArameshViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCheckInScreen(
    viewModel: ArameshViewModel,
    modifier: Modifier = Modifier
) {
    val todayCheckIn by viewModel.todayCheckIn.collectAsState()
    val allCheckIns by viewModel.allCheckIns.collectAsState()

    var mood by remember { mutableIntStateOf(3) }
    var energy by remember { mutableIntStateOf(2) }
    var sleep by remember { mutableIntStateOf(2) }
    var note by remember { mutableStateOf("") }
    var savedSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(todayCheckIn) {
        todayCheckIn?.let {
            mood = it.mood
            energy = it.energy
            sleep = it.sleep
            note = it.note
        }
    }

    val moodOptions = listOf(
        Pair(1, "خسته و گرفته 😫"),
        Pair(2, "کمی غمگین 😔"),
        Pair(3, "معمولی و آرام 😐"),
        Pair(4, "خوب و پرانرژی 😊"),
        Pair(5, "عالی و شاداب 😍")
    )

    val levelLabels = listOf(Pair(1, "کم 🔋"), Pair(2, "متوسط ⚡"), Pair(3, "زیاد 🚀"))
    val sleepLabels = listOf(Pair(1, "ناآرام 🌙"), Pair(2, "معمولی 💤"), Pair(3, "عمیق و آرام ✨"))

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
                        text = "پایش روزانه حال درونی 🧭",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "ثبت صادقانه حس و حال روزانه، آگاهی را وسعت می‌بخشد",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Check-in Form Card
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // 1. Mood
                        Text(
                            text = "۱. حس و حال کلی امروز شما چگونه است؟",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val emojis = listOf(Pair(1, "😫"), Pair(2, "😔"), Pair(3, "😐"), Pair(4, "😊"), Pair(5, "😍"))
                            emojis.forEach { (m, emoji) ->
                                val isSelected = mood == m
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(
                                            color = if (isSelected) TealPrimary.copy(alpha = 0.2f) else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { mood = m },
                                        label = { Text(emoji, fontSize = 24.sp) },
                                        shape = CircleShape
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // 2. Energy
                        Text(
                            text = "۲. سطح انرژی بدنی امروز:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            levelLabels.forEach { (lvl, label) ->
                                FilterChip(
                                    selected = energy == lvl,
                                    onClick = { energy = lvl },
                                    label = { Text(label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TealPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // 3. Sleep
                        Text(
                            text = "۳. کیفیت خواب شب گذشته:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            sleepLabels.forEach { (slp, label) ->
                                FilterChip(
                                    selected = sleep == slp,
                                    onClick = { sleep = slp },
                                    label = { Text(label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TealPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // 4. Note
                        Text(
                            text = "۴. یادداشت روزانه (احساسات، عوامل موثر):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = note,
                            onValueChange = { if (it.length <= 600) note = it },
                            placeholder = { Text("امروز چطور گذشت؟ چه افکاری در سر داشتی؟") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(14.dp)
                        )
                        Text(
                            text = "${JalaaliCalendarHelper.toPersianNumber(note.length)} / ۶۰۰",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.End)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Save Button
                        Button(
                            onClick = {
                                viewModel.saveCheckIn(mood, energy, sleep, note)
                                savedSuccess = true
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (todayCheckIn != null) "به‌روزرسانی پایش امروز" else "ثبت پایش امروز",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (savedSuccess) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "✓ اطلاعات با موفقیت ثبت و ذخیره شد!",
                                color = TealPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }

            // Check-in History List
            item {
                Text(
                    text = "تاریخچه پایش روزهای گذشته (${JalaaliCalendarHelper.toPersianNumber(allCheckIns.size)})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(allCheckIns, key = { it.date }) { item ->
                val jalaaliDate = JalaaliCalendarHelper.fromGregorianString(item.date)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val emoji = when (item.mood) {
                                5 -> "😍"
                                4 -> "😊"
                                3 -> "😐"
                                2 -> "😔"
                                else -> "😫"
                            }
                            Text(text = emoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${jalaaliDate.day} ${jalaaliDate.monthName()} ${jalaaliDate.year}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                if (item.note.isNotBlank()) {
                                    Text(
                                        text = item.note,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "انرژی: ${item.energy}/۳",
                                style = MaterialTheme.typography.labelSmall,
                                color = TealSecondary
                            )
                            Text(
                                text = "خواب: ${item.sleep}/۳",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
