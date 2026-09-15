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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.BadgeCatalog
import com.example.domain.model.BadgeDefinition
import com.example.domain.model.JalaaliCalendarHelper
import com.example.ui.theme.GoldenFruitColor
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.ArameshViewModel

@Composable
fun BadgesCabinetScreen(
    viewModel: ArameshViewModel,
    modifier: Modifier = Modifier
) {
    val unlockedEntities by viewModel.unlockedBadges.collectAsState()
    val unlockedKeyMap = remember(unlockedEntities) {
        unlockedEntities.associateBy { it.key }
    }

    var selectedBadge by remember { mutableStateOf<BadgeDefinition?>(null) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "گنجه افتخارات و مدال‌ها 🏆",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "هر گام آگاهانه در این مسیر، شایسته تکریم و ثبت در گنجه است",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
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
                        Text(
                            text = "پیشرفت بازگشایی نشان‌ها",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${JalaaliCalendarHelper.toPersianNumber(unlockedEntities.size)} از ۱۷",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { unlockedEntities.size / 17f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = GoldenFruitColor,
                        trackColor = GoldenFruitColor.copy(alpha = 0.2f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Badges Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 60.dp)
            ) {
                items(BadgeCatalog.allBadges, key = { it.key }) { badge ->
                    val isUnlocked = unlockedKeyMap.containsKey(badge.key)

                    Card(
                        onClick = { selectedBadge = badge },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(
                                        color = if (isUnlocked) GoldenFruitColor.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isUnlocked) badge.iconEmoji else "🔒",
                                    fontSize = 26.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = badge.titleFa,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Text(
                                text = if (isUnlocked) "✓ باز شده" else "قفل",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isUnlocked) TealPrimary else Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Details Dialog
        selectedBadge?.let { badge ->
            val unlockedInfo = unlockedKeyMap[badge.key]
            val isUnlocked = unlockedInfo != null

            AlertDialog(
                onDismissRequest = { selectedBadge = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (isUnlocked) badge.iconEmoji else "🔒", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = badge.titleFa, fontWeight = FontWeight.Bold)
                            Text(text = badge.titleEn, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = badge.descFa, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isUnlocked) "وضعیت: بازگشایی شده در ${unlockedInfo.unlockedAt}" else "وضعیت: این نشان هنوز قفل است. با استمرار در تمرینات آن را به دست آورید.",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isUnlocked) TealPrimary else Color(0xFFE65100),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { selectedBadge = null },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("متوجه شدم")
                    }
                }
            )
        }
    }
}
