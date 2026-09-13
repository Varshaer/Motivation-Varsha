package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MotivatorDatabase
import com.example.data.model.AchievementEntity
import com.example.data.model.DailyActivityEntity
import com.example.data.model.DailyChallenge
import com.example.data.model.GoalEntity
import com.example.data.model.GoalTaskEntity
import com.example.data.model.GoalWithTasks
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.MotivationalQuote
import com.example.data.model.UserPreferences
import com.example.data.receiver.NotificationHelper
import com.example.data.receiver.ReminderBroadcastReceiver
import com.example.data.repository.MotivatorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UserLevelInfo(
    val level: Int,
    val title: String,
    val currentPoints: Int,
    val minPointsForLevel: Int,
    val maxPointsForLevel: Int,
    val progress: Float
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MotivatorDatabase.getDatabase(application, viewModelScope)
    private val repository = MotivatorRepository(
        prefDao = database.userPreferencesDao(),
        goalDao = database.goalDao(),
        habitDao = database.habitDao(),
        quoteDao = database.quoteDao(),
        achievementDao = database.achievementDao(),
        dailyActivityDao = database.dailyActivityDao()
    )

    val todayDateString: String = repository.getTodayDateString()

    val preferences: StateFlow<UserPreferences?> = repository.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val goals: StateFlow<List<GoalWithTasks>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<HabitEntity>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCompletions: StateFlow<List<HabitCompletionEntity>> = repository.getCompletionsForDate(todayDateString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCompletions: StateFlow<List<HabitCompletionEntity>> = repository.allCompletions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quotes: StateFlow<List<MotivationalQuote>> = repository.allQuotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements: StateFlow<List<AchievementEntity>> = repository.allAchievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentActivities: StateFlow<List<DailyActivityEntity>> = repository.recentActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter states
    private val _selectedGoalCategory = MutableStateFlow("All")
    val selectedGoalCategory: StateFlow<String> = _selectedGoalCategory.asStateFlow()

    private val _selectedGoalStatus = MutableStateFlow("All") // All, Active, Completed
    val selectedGoalStatus: StateFlow<String> = _selectedGoalStatus.asStateFlow()

    private val _selectedQuoteCategory = MutableStateFlow("All")
    val selectedQuoteCategory: StateFlow<String> = _selectedQuoteCategory.asStateFlow()

    private val _onlyFavoriteQuotes = MutableStateFlow(false)
    val onlyFavoriteQuotes: StateFlow<Boolean> = _onlyFavoriteQuotes.asStateFlow()

    private val _quoteSearchQuery = MutableStateFlow("")
    val quoteSearchQuery: StateFlow<String> = _quoteSearchQuery.asStateFlow()

    // Daily Challenge
    val currentDailyChallenge: Pair<Int, DailyChallenge> = repository.getChallengeForDate(todayDateString)

    // Calculated progress today
    val todayProgressPercentage: StateFlow<Int> = combine(habits, todayCompletions) { habitList, completions ->
        if (habitList.isEmpty()) return@combine 0
        val completedCount = habitList.count { habit ->
            completions.any { it.habitId == habit.id }
        }
        ((completedCount.toFloat() / habitList.size) * 100).toInt()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun getEncouragingMessage(progress: Int): String {
        return when {
            progress == 0 -> "A clean slate awaits. Small steps lead to giant leaps today!"
            progress in 1..49 -> "Great momentum building! Stay disciplined and keep pushing."
            progress in 50..99 -> "You are more than halfway there! Finish the day strong!"
            else -> "Outstanding performance! You conquered all today's targets! 🔥"
        }
    }

    fun getDailyHeadline(): String {
        val headlines = listOf(
            "Today's discipline builds tomorrow's empire.",
            "Action cures fear. Take the bold step today.",
            "Consistency is your superpower. Show up strong.",
            "Great things never come from comfort zones.",
            "Make today so productive that yesterday gets jealous.",
            "Your goals don't care about feelings. Execute.",
            "One habit at a time, you are transforming your life."
        )
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        return headlines[dayOfYear % headlines.size]
    }

    fun calculateUserLevel(points: Int): UserLevelInfo {
        return when {
            points < 200 -> {
                val progress = (points.toFloat() / 200f).coerceIn(0f, 1f)
                UserLevelInfo(1, "Initiate", points, 0, 200, progress)
            }
            points < 500 -> {
                val progress = ((points - 200).toFloat() / 300f).coerceIn(0f, 1f)
                UserLevelInfo(2, "Momentum Builder", points, 200, 500, progress)
            }
            points < 1000 -> {
                val progress = ((points - 500).toFloat() / 500f).coerceIn(0f, 1f)
                UserLevelInfo(3, "Disciplined Achiever", points, 500, 1000, progress)
            }
            points < 2000 -> {
                val progress = ((points - 1000).toFloat() / 1000f).coerceIn(0f, 1f)
                UserLevelInfo(4, "Master of Habits", points, 1000, 2000, progress)
            }
            else -> {
                val progress = 1f
                UserLevelInfo(5, "Unstoppable Force", points, 2000, 2000, progress)
            }
        }
    }

    fun setGoalCategory(category: String) {
        _selectedGoalCategory.value = category
    }

    fun setGoalStatus(status: String) {
        _selectedGoalStatus.value = status
    }

    fun setQuoteCategory(category: String) {
        _selectedQuoteCategory.value = category
    }

    fun setOnlyFavorites(onlyFavs: Boolean) {
        _onlyFavoriteQuotes.value = onlyFavs
    }

    fun setQuoteSearch(query: String) {
        _quoteSearchQuery.value = query
    }

    // Actions
    fun completeOnboarding(name: String, selectedGoals: List<String>) {
        viewModelScope.launch {
            repository.completeOnboarding(name, selectedGoals)
            scheduleDefaultNotifications()
        }
    }

    fun createGoal(title: String, category: String, description: String, targetDate: String, subtasks: List<String>) {
        viewModelScope.launch {
            repository.addGoal(title, category, description, targetDate, subtasks)
        }
    }

    fun updateGoal(goal: GoalEntity, subtasks: List<GoalTaskEntity>) {
        viewModelScope.launch {
            repository.updateGoal(goal, subtasks)
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    fun toggleTask(task: GoalTaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    fun createHabit(title: String, category: String, frequency: String) {
        viewModelScope.launch {
            repository.addHabit(title, category, frequency)
        }
    }

    fun updateHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun toggleHabitToday(habit: HabitEntity, currentlyCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habit, todayDateString, !currentlyCompleted)
        }
    }

    fun completeDailyChallenge() {
        viewModelScope.launch {
            repository.completeDailyChallenge(todayDateString, currentDailyChallenge.first)
        }
    }

    fun toggleQuoteFavorite(quote: MotivationalQuote) {
        viewModelScope.launch {
            repository.toggleQuoteFavorite(quote)
        }
    }

    fun updateProfile(
        name: String,
        selectedGoals: List<String>,
        appearanceMode: String,
        morningReminder: Boolean,
        morningTime: String,
        habitReminder: Boolean,
        habitTime: String,
        eveningReminder: Boolean,
        eveningTime: String
    ) {
        viewModelScope.launch {
            val current = preferences.value ?: return@launch
            val updated = current.copy(
                name = name.trim().ifEmpty { current.name },
                selectedGoals = selectedGoals.joinToString(","),
                appearanceMode = appearanceMode,
                morningReminderEnabled = morningReminder,
                morningReminderTime = morningTime,
                habitReminderEnabled = habitReminder,
                habitReminderTime = habitTime,
                eveningReminderEnabled = eveningReminder,
                eveningReminderTime = eveningTime
            )
            repository.updatePreferences(updated)

            // Reschedule reminders
            updateAlarms(updated)
        }
    }

    fun resetTodayProgress() {
        viewModelScope.launch {
            repository.resetTodayProgress(todayDateString)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
        }
    }

    fun triggerTestNotification() {
        NotificationHelper.showNotification(getApplication(), ReminderBroadcastReceiver.TYPE_TEST)
    }

    private fun scheduleDefaultNotifications() {
        NotificationHelper.createNotificationChannel(getApplication())
        NotificationHelper.scheduleReminder(getApplication(), ReminderBroadcastReceiver.TYPE_MORNING, 8, 0)
        NotificationHelper.scheduleReminder(getApplication(), ReminderBroadcastReceiver.TYPE_HABIT, 13, 0)
        NotificationHelper.scheduleReminder(getApplication(), ReminderBroadcastReceiver.TYPE_EVENING, 20, 0)
    }

    private fun updateAlarms(prefs: UserPreferences) {
        if (prefs.morningReminderEnabled) {
            val parts = prefs.morningReminderTime.split(":")
            val h = parts.getOrNull(0)?.toIntOrNull() ?: 8
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
            NotificationHelper.scheduleReminder(getApplication(), ReminderBroadcastReceiver.TYPE_MORNING, h, m)
        }
        if (prefs.habitReminderEnabled) {
            val parts = prefs.habitReminderTime.split(":")
            val h = parts.getOrNull(0)?.toIntOrNull() ?: 13
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
            NotificationHelper.scheduleReminder(getApplication(), ReminderBroadcastReceiver.TYPE_HABIT, h, m)
        }
        if (prefs.eveningReminderEnabled) {
            val parts = prefs.eveningReminderTime.split(":")
            val h = parts.getOrNull(0)?.toIntOrNull() ?: 20
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
            NotificationHelper.scheduleReminder(getApplication(), ReminderBroadcastReceiver.TYPE_EVENING, h, m)
        }
    }
}
