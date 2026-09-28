package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.BreathingSessionScreen
import com.example.ui.screens.ExercisesListScreen
import com.example.ui.screens.StatsDashboardScreen
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BreathworkViewModel

enum class MainTab(
    val titlePersian: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    BREATH(
        titlePersian = "تنفس",
        selectedIcon = Icons.Filled.GraphicEq,
        unselectedIcon = Icons.Filled.GraphicEq
    ),
    EXERCISES(
        titlePersian = "تمرین‌ها",
        selectedIcon = Icons.Filled.SelfImprovement,
        unselectedIcon = Icons.Outlined.SelfImprovement
    ),
    STATS(
        titlePersian = "آمار و تقویم",
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    )
}

@Composable
fun MainAppScreen(
    viewModel: BreathworkViewModel = viewModel()
) {
    // Provide Right-To-Left (RTL) Layout Direction for Persian language
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var selectedTabIndex by remember { mutableIntStateOf(0) }

        val sessionState by viewModel.sessionState.collectAsStateWithLifecycle()
        val customExercises by viewModel.customExercises.collectAsStateWithLifecycle()
        val allSessions by viewModel.allSessions.collectAsStateWithLifecycle()
        val totalDurationSeconds by viewModel.totalDurationSeconds.collectAsStateWithLifecycle()
        val totalSessionsCount by viewModel.totalSessionsCount.collectAsStateWithLifecycle()

        val streakDays = remember(allSessions) {
            viewModel.calculateCurrentStreak(allSessions)
        }

        val weeklyStats = remember(allSessions) {
            viewModel.getWeeklyStats(allSessions)
        }

        // BackHandler to navigate back to Breath tab if user presses back on secondary tabs
        BackHandler(enabled = selectedTabIndex != 0) {
            selectedTabIndex = 0
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = DarkBackground,
            bottomBar = {
                // If a session is actively running, keep UI clean or keep bar accessible
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(24.dp))
                            .testTag("main_bottom_nav"),
                        containerColor = DarkSurfaceElevated,
                        tonalElevation = 8.dp
                    ) {
                        MainTab.values().forEachIndexed { index, tab ->
                            val isSelected = selectedTabIndex == index
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selectedTabIndex = index },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.titlePersian,
                                        tint = if (isSelected) AmberPrimary else TextMuted
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.titlePersian,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) AmberGlow else TextMuted,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AmberPrimary,
                                    selectedTextColor = AmberGlow,
                                    indicatorColor = AmberDark.copy(alpha = 0.25f),
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tabTransition",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> BreathingSessionScreen(
                        uiState = sessionState,
                        viewModel = viewModel,
                        onNavigateToExercises = { selectedTabIndex = 1 }
                    )
                    1 -> ExercisesListScreen(
                        currentSelected = sessionState.selectedExercise,
                        customExercises = customExercises,
                        onSelectExercise = { exercise ->
                            viewModel.selectExercise(exercise)
                        },
                        onSaveCustomExercise = { exercise ->
                            viewModel.saveCustomExercise(exercise)
                        },
                        onDeleteCustomExercise = { exercise ->
                            viewModel.deleteCustomExercise(exercise)
                        },
                        onNavigateToSession = {
                            selectedTabIndex = 0
                        }
                    )
                    2 -> StatsDashboardScreen(
                        sessions = allSessions,
                        totalDurationSeconds = totalDurationSeconds,
                        totalSessionsCount = totalSessionsCount,
                        streakDays = streakDays,
                        weeklyStats = weeklyStats
                    )
                }
            }
        }
    }
}
