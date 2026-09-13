package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.components.MotivatorBottomBar
import com.example.ui.components.MotivatorLogoEmblem
import com.example.ui.components.MotivatorTab
import com.example.ui.screens.goals.GoalEditorDialog
import com.example.ui.screens.goals.GoalsScreen
import com.example.ui.screens.habits.HabitEditorDialog
import com.example.ui.screens.habits.HabitsScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.motivation.MotivationScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.progress.ProgressScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: MainViewModel = viewModel()
            val preferences by viewModel.preferences.collectAsState()

            // Theme mode selection
            val isSystemDark = isSystemInDarkTheme()
            val isDarkTheme = when (preferences?.appearanceMode) {
                "Light" -> false
                "Dark" -> true
                else -> true // Motivator signature luxury dark palette by default
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                MotivatorApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MotivatorApp(viewModel: MainViewModel) {
    val preferences by viewModel.preferences.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val todayCompletions by viewModel.todayCompletions.collectAsState()
    val allCompletions by viewModel.allCompletions.collectAsState()
    val quotes by viewModel.quotes.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val recentActivities by viewModel.recentActivities.collectAsState()

    val selectedGoalCategory by viewModel.selectedGoalCategory.collectAsState()
    val selectedGoalStatus by viewModel.selectedGoalStatus.collectAsState()
    val selectedQuoteCategory by viewModel.selectedQuoteCategory.collectAsState()
    val onlyFavoriteQuotes by viewModel.onlyFavoriteQuotes.collectAsState()
    val quoteSearchQuery by viewModel.quoteSearchQuery.collectAsState()
    val todayProgress by viewModel.todayProgressPercentage.collectAsState()

    var currentTab by remember { mutableStateOf(MotivatorTab.HOME) }
    var showQuickCreateGoalDialog by remember { mutableStateOf(false) }
    var showQuickCreateHabitDialog by remember { mutableStateOf(false) }

    // If data is loading initially, show branded splash
    if (preferences == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            MotivatorLogoEmblem(size = 80.dp)
        }
        return
    }

    // Automatically transition to the app dashboard if onboarding is not yet marked done
    LaunchedEffect(preferences) {
        val pref = preferences
        if (pref != null && !pref.isOnboarded) {
            viewModel.completeOnboarding(
                name = if (pref.name.isNotBlank()) pref.name else "Champion",
                selectedGoals = listOf("Career", "Health", "Personal Growth", "Money")
            )
        }
    }

    // Main App Scaffold
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MotivatorBottomBar(
                selectedTab = currentTab,
                onTabSelected = { tab -> currentTab = tab }
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Crossfade(
                targetState = currentTab,
                animationSpec = tween(durationMillis = 200),
                label = "tab_fade"
            ) { tab ->
                when (tab) {
                    MotivatorTab.HOME -> HomeScreen(
                        viewModel = viewModel,
                        preferences = preferences,
                        habits = habits,
                        todayCompletions = todayCompletions,
                        todayProgress = todayProgress,
                        quotes = quotes,
                        onNavigateTab = { currentTab = it },
                        onOpenCreateGoal = { showQuickCreateGoalDialog = true },
                        onOpenCreateHabit = { showQuickCreateHabitDialog = true }
                    )
                    MotivatorTab.GOALS -> GoalsScreen(
                        viewModel = viewModel,
                        goals = goals,
                        selectedCategory = selectedGoalCategory,
                        selectedStatus = selectedGoalStatus
                    )
                    MotivatorTab.HABITS -> HabitsScreen(
                        viewModel = viewModel,
                        habits = habits,
                        todayCompletions = todayCompletions,
                        allCompletions = allCompletions
                    )
                    MotivatorTab.MOTIVATION -> MotivationScreen(
                        viewModel = viewModel,
                        quotes = quotes,
                        selectedCategory = selectedQuoteCategory,
                        onlyFavorites = onlyFavoriteQuotes,
                        searchQuery = quoteSearchQuery
                    )
                    MotivatorTab.PROFILE -> ProfileScreen(
                        viewModel = viewModel,
                        preferences = preferences
                    )
                }
            }
        }
    }

    // Quick Goal Dialog (from Home action button)
    if (showQuickCreateGoalDialog) {
        GoalEditorDialog(
            goalWithTasks = null,
            onDismiss = { showQuickCreateGoalDialog = false },
            onSave = { title, category, description, targetDate, subtasks ->
                viewModel.createGoal(title, category, description, targetDate, subtasks)
                showQuickCreateGoalDialog = false
            }
        )
    }

    // Quick Habit Dialog (from Home action button)
    if (showQuickCreateHabitDialog) {
        HabitEditorDialog(
            habit = null,
            onDismiss = { showQuickCreateHabitDialog = false },
            onSave = { title, category, frequency ->
                viewModel.createHabit(title, category, frequency)
                showQuickCreateHabitDialog = false
            }
        )
    }
}
