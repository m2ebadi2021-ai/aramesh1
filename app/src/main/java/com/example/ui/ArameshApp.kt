package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.domain.model.JalaaliCalendarHelper
import com.example.ui.components.BadgeCelebrationDialog
import com.example.ui.screens.AnalyticsCalendarScreen
import com.example.ui.screens.BadgesCabinetScreen
import com.example.ui.screens.BreathingScreen
import com.example.ui.screens.DailyCheckInScreen
import com.example.ui.screens.GratitudeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MeditationScreen
import com.example.ui.screens.PersonalGrowthScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.ArameshTheme
import com.example.ui.theme.LalezarFont
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.VazirFont
import com.example.ui.viewmodel.ArameshViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

sealed class Screen(val route: String, val titleFa: String, val icon: ImageVector) {
    object Home : Screen("home", "خانه", Icons.Default.Home)
    object Gratitude : Screen("gratitude", "سپاسگزاری", Icons.Default.Park)
    object Breathing : Screen("breathing", "تنفس", Icons.Default.Air)
    object Meditation : Screen("meditation", "مراقبه", Icons.Default.SelfImprovement)
    object Growth : Screen("growth", "رشد فردی", Icons.Default.Favorite)
    object CheckIn : Screen("check_in", "پایش حال", Icons.Default.Favorite)
    object Analytics : Screen("analytics", "تقویم و آمار", Icons.Default.CalendarMonth)
    object Badges : Screen("badges", "نشان‌ها", Icons.Default.EmojiEvents)
    object Settings : Screen("settings", "تنظیمات", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Gratitude,
    Screen.Breathing,
    Screen.Meditation,
    Screen.Growth
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArameshApp(
    viewModel: ArameshViewModel = viewModel()
) {
    val themeSetting by viewModel.themeSetting.collectAsState()
    val celebrationBadge by viewModel.celebrationBadge.collectAsState()
    val unlockedBadges by viewModel.unlockedBadges.collectAsState()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val isTopLevelDestination = bottomNavItems.any { it.route == currentRoute }
    var showSplash by remember { mutableStateOf(true) }

    ArameshTheme(themeSetting = themeSetting) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            if (showSplash) {
                SplashScreen(
                    audioEngine = viewModel.audioEngine,
                    onSplashFinished = { showSplash = false }
                )
            } else {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = when (currentRoute) {
                                        Screen.Home.route -> "آرامش (Aramesh)"
                                        Screen.Gratitude.route -> "باغ سپاسگزاری"
                                        Screen.Breathing.route -> "تنفس آگاهانه"
                                        Screen.Meditation.route -> "مراقبه و سکوت"
                                        Screen.Growth.route -> "رشد و ذهن‌آگاهی"
                                        Screen.CheckIn.route -> "پایش درونی روزانه"
                                        Screen.Analytics.route -> "تقویم و آمار رشد"
                                        Screen.Badges.route -> "گنجه نشان‌های افتخار"
                                        Screen.Settings.route -> "تنظیمات برنامه"
                                        else -> "آرامش"
                                    },
                                    fontFamily = LalezarFont,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            navigationIcon = {
                                if (!isTopLevelDestination) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "بازگشت"
                                        )
                                    }
                                }
                            },
                            actions = {
                                // Calendar action
                                IconButton(onClick = { navController.navigate(Screen.Analytics.route) }) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "تقویم و آمار",
                                        tint = if (currentRoute == Screen.Analytics.route) TealPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                // Badges Cabinet action with count
                                IconButton(onClick = { navController.navigate(Screen.Badges.route) }) {
                                    BadgedBox(
                                        badge = {
                                            if (unlockedBadges.isNotEmpty()) {
                                                Badge(containerColor = TealPrimary) {
                                                    Text(
                                                        text = JalaaliCalendarHelper.toPersianNumber(unlockedBadges.size),
                                                        fontFamily = VazirFont,
                                                        fontSize = 10.sp,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = "نشان‌ها",
                                            tint = if (currentRoute == Screen.Badges.route) TealPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                // Settings
                                IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "تنظیمات",
                                        tint = if (currentRoute == Screen.Settings.route) TealPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            bottomNavItems.forEach { screen ->
                                val isSelected = currentRoute == screen.route
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.titleFa
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.titleFa,
                                            fontFamily = VazirFont,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    },
                                    selected = isSelected,
                                    onClick = {
                                        if (currentRoute != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = TealPrimary,
                                        selectedTextColor = TealPrimary,
                                        indicatorColor = TealPrimary.copy(alpha = 0.15f)
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route
                        ) {
                            composable(Screen.Home.route) {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToGratitude = { navController.navigate(Screen.Gratitude.route) },
                                    onNavigateToBreathing = { navController.navigate(Screen.Breathing.route) },
                                    onNavigateToMeditation = { navController.navigate(Screen.Meditation.route) },
                                    onNavigateToCheckIn = { navController.navigate(Screen.CheckIn.route) },
                                    onNavigateToGrowth = { subTab ->
                                        viewModel.navigateToGrowthTab(subTab)
                                        navController.navigate(Screen.Growth.route)
                                    },
                                    onNavigateToCalendar = { navController.navigate(Screen.Analytics.route) },
                                    onNavigateToBadges = { navController.navigate(Screen.Badges.route) },
                                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                                )
                            }
                            composable(Screen.Gratitude.route) {
                                GratitudeScreen(viewModel = viewModel)
                            }
                            composable(Screen.Breathing.route) {
                                BreathingScreen(viewModel = viewModel)
                            }
                            composable(Screen.Meditation.route) {
                                MeditationScreen(viewModel = viewModel)
                            }
                            composable(Screen.Growth.route) {
                                PersonalGrowthScreen(viewModel = viewModel)
                            }
                            composable(Screen.CheckIn.route) {
                                DailyCheckInScreen(viewModel = viewModel)
                            }
                            composable(Screen.Analytics.route) {
                                AnalyticsCalendarScreen(viewModel = viewModel)
                            }
                            composable(Screen.Badges.route) {
                                BadgesCabinetScreen(viewModel = viewModel)
                            }
                            composable(Screen.Settings.route) {
                                SettingsScreen(viewModel = viewModel)
                            }
                        }
                    }
                }

                // Real-time Badge Unlock Celebration Dialog
                BadgeCelebrationDialog(
                    badge = celebrationBadge,
                    onDismiss = { viewModel.dismissCelebration() }
                )
            }
        }
    }
}
