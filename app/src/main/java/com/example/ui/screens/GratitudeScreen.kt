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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GratitudeEntity
import com.example.domain.model.JalaaliCalendarHelper
import com.example.ui.canvas.GratitudeTreeCanvas
import com.example.ui.theme.GoldenFruitColor
import com.example.ui.theme.RedFruitColor
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealSecondary
import com.example.ui.viewmodel.ArameshViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GratitudeScreen(
    viewModel: ArameshViewModel,
    modifier: Modifier = Modifier
) {
    val treeState by viewModel.gratitudeTreeState.collectAsState()
    val gratitudes by viewModel.allGratitudes.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var editingEntity by remember { mutableStateOf<GratitudeEntity?>(null) }
    var showLegendDialog by remember { mutableStateOf(false) }

    val promptSuggestions = listOf(
        "امروز بابت چه لبخندی سپاسگزارم؟",
        "چه دوست مهربانی در زندگی دارم؟",
        "کدام نعمت سلامتی را قدر می‌دانم؟",
        "زیبایی کوچکی که امروز دیدم...",
        "فرصتی که برای رشد یافتم..."
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header & Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "باغ سپاسگزاری 🌳",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "هر سپاس، بذری برای رویش و آرامش جان است",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showLegendDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "قوانین رشد درخت",
                            tint = TealPrimary
                        )
                    }
                }
            }

            // 2. Full Gratitude Tree Canvas Card
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = treeState.levelNameFa,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            )
                            Text(
                                text = "مجموع: ${JalaaliCalendarHelper.toPersianNumber(treeState.totalEntries)} ثبت",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFFE8F5E9),
                                            Color(0xFFF1F8E9),
                                            Color(0xFFE0F2F1)
                                        )
                                    )
                                )
                        ) {
                            GratitudeTreeCanvas(
                                state = treeState,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Detailed Tree Legend Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            TreeLegendItem(
                                icon = "🌸",
                                count = treeState.currentBlossoms,
                                label = "شکوفه",
                                rule = "هر ۳ سپاس"
                            )
                            TreeLegendItem(
                                icon = "🍎",
                                count = treeState.currentRedFruits,
                                label = "میوه سرخ",
                                rule = "هر ۳ شکوفه"
                            )
                            TreeLegendItem(
                                icon = "✨",
                                count = treeState.currentGoldenFruits,
                                label = "میوه زرین",
                                rule = "هر ۳ میوه سرخ"
                            )
                        }
                    }
                }
            }

            // 3. Gratitude Input Box
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "همین حالا بابت چه چیزی قدردانی می‌کنی؟",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Inspiration chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            items(promptSuggestions) { prompt ->
                                FilterChip(
                                    selected = false,
                                    onClick = { inputText = prompt },
                                    label = { Text(text = prompt, fontSize = 12.sp) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = {
                                if (it.length <= 280) inputText = it
                            },
                            placeholder = { Text("مثلاً: خدایا شکرت برای بوی باران و فنجان چای گرم...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${JalaaliCalendarHelper.toPersianNumber(inputText.length)} / ۲۸۰ نویسه",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (inputText.length > 250) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = {
                                    if (inputText.isNotBlank()) {
                                        viewModel.addGratitude(inputText)
                                        inputText = ""
                                    }
                                },
                                enabled = inputText.isNotBlank(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ثبت سپاسگزاری", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 4. Gratitude Records List Header
            item {
                Text(
                    text = "دفترچه خاطرات قدردانی (${JalaaliCalendarHelper.toPersianNumber(gratitudes.size)})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }

            // 5. Gratitude Items List
            if (gratitudes.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🌱", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "هنوز سپاسگزاری ثبت نکرده‌اید",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "اولین بذر را با نوشتن یک حس خوب در خاک باغ بکارید!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(gratitudes, key = { it.id }) { item ->
                    GratitudeItemCard(
                        entity = item,
                        onEdit = { editingEntity = item },
                        onDelete = { viewModel.deleteGratitude(item.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // Edit Dialog
        editingEntity?.let { entity ->
            var editText by remember { mutableStateOf(entity.body) }
            AlertDialog(
                onDismissRequest = { editingEntity = null },
                title = { Text("ویرایش سپاسگزاری", fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = editText,
                        onValueChange = { if (it.length <= 280) editText = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editText.isNotBlank()) {
                                viewModel.updateGratitude(entity.copy(body = editText.trim(), updatedAt = System.currentTimeMillis()))
                                editingEntity = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("ذخیره تغییرات")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingEntity = null }) {
                        Text("انصراف")
                    }
                }
            )
        }

        // Legend Info Dialog
        if (showLegendDialog) {
            AlertDialog(
                onDismissRequest = { showLegendDialog = false },
                title = {
                    Text(
                        text = "الگوریتم رشد درخت سپاسگزاری 🌳",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "درخت سپاسگزاری با هر نگاه قدرشناسانه شما رشد می‌کند و تغییر چهره می‌دهد:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌸", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "هر ۳ سپاسگزاری = ۱ شکوفه بهاری",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🍎", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "هر ۳ شکوفه (۹ سپاس) = ۱ میوه سرخ رسیده",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "هر ۳ میوه سرخ (۲۷ سپاس) = ۱ میوه زرین جاودان",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showLegendDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("متوجه شدم")
                    }
                }
            )
        }
    }
}

@Composable
private fun TreeLegendItem(
    icon: String,
    count: Int,
    label: String,
    rule: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = JalaaliCalendarHelper.toPersianNumber(count),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Text(
            text = rule,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GratitudeItemCard(
    entity: GratitudeEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val jalaaliDate = JalaaliCalendarHelper.fromGregorianString(entity.date)

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
                    text = "${jalaaliDate.day} ${jalaaliDate.monthName()} ${jalaaliDate.year}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "ویرایش",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = entity.body,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}
