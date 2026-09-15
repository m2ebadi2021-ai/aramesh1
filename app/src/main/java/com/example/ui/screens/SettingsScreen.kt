package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppThemeSetting
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.ArameshViewModel

@Composable
fun SettingsScreen(
    viewModel: ArameshViewModel,
    modifier: Modifier = Modifier
) {
    val currentTheme by viewModel.themeSetting.collectAsState()
    val keepAwake by viewModel.keepScreenAwake.collectAsState()
    val dailyReminders by viewModel.dailyRemindersEnabled.collectAsState()

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
                        text = "تنظیمات و سفارشی‌سازی ⚙️",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "شخصی‌سازی ظاهر و اعلان‌های آرامش",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 1. Theme Picker Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = TealPrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "تم و پالت رنگی برنامه",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))

                        AppThemeSetting.values().forEach { theme ->
                            val isSelected = currentTheme == theme
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setTheme(theme) },
                                label = {
                                    Text(
                                        text = "${theme.titleFa} - ${theme.titleEn}",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TealPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // 2. Behavioral Toggles Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Daily Habit Reminders
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = TealPrimary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = "یادآور روزانه سپاسگزاری و تنفس", fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "ارسال پیام الهام‌بخش در طول روز",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = dailyReminders,
                                onCheckedChange = { viewModel.setDailyRemindersEnabled(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Keep Screen Awake
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Visibility, contentDescription = null, tint = TealPrimary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = "روشن ماندن صفحه هنگام تمرین", fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "جلوگیری از قفل صفحه در تنفس و مراقبه",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = keepAwake,
                                onCheckedChange = { viewModel.setKeepScreenAwake(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary)
                            )
                        }
                    }
                }
            }

            // 3. Privacy & Offline-First Badge
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = TealPrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "حریم خصوصی و ۱۰۰٪ آفلاین",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "تمام داده‌های شما شامل سپاسگزاری‌ها، نامه‌ها، پایش‌های مود و خاطرات منحصراً در پایگاه داده محلی (SQLite / Room) گوشی شما ذخیره شده و هیچ داده‌ای به هیچ سروری ارسال نمی‌شود.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // 4. About App
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = TealPrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "درباره آرامش (Aramesh)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "اپلیکیشن بومی ذهن‌آگاهی، سپاسگزاری، تنفس آگاهانه و رشد فردی\nنسخه ۱.۰.۰ - آماده برای کافه بازار و مایکت و گوگل پلی",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
