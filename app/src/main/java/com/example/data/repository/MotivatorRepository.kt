package com.example.data.repository

import com.example.data.local.AchievementDao
import com.example.data.local.DailyActivityDao
import com.example.data.local.GoalDao
import com.example.data.local.HabitDao
import com.example.data.local.QuoteDao
import com.example.data.local.UserPreferencesDao
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MotivatorRepository(
    private val prefDao: UserPreferencesDao,
    private val goalDao: GoalDao,
    private val habitDao: HabitDao,
    private val quoteDao: QuoteDao,
    private val achievementDao: AchievementDao,
    private val dailyActivityDao: DailyActivityDao
) {
    val userPreferences: Flow<UserPreferences?> = prefDao.getPreferences()
    val allGoals: Flow<List<GoalWithTasks>> = goalDao.getAllGoalsWithTasks()
    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()
    val allCompletions: Flow<List<HabitCompletionEntity>> = habitDao.getAllCompletions()
    val allQuotes: Flow<List<MotivationalQuote>> = quoteDao.getAllQuotes()
    val allAchievements: Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()
    val recentActivities: Flow<List<DailyActivityEntity>> = dailyActivityDao.getRecentActivities(7)

    fun getCompletionsForDate(dateString: String): Flow<List<HabitCompletionEntity>> =
        habitDao.getCompletionsForDate(dateString)

    val dailyChallenges = listOf(
        DailyChallenge("Early Victory", "Complete one critical task before noon today.", 50, "Discipline"),
        DailyChallenge("Mind & Body", "Exercise, stretch, or walk intentionally for 20 minutes.", 50, "Health"),
        DailyChallenge("Knowledge Boost", "Spend 15 minutes reading or learning something new.", 50, "Personal Growth"),
        DailyChallenge("Clarity Mapping", "Write down your #1 biggest goal and review your action plan.", 50, "Career"),
        DailyChallenge("Deep Focus Sprint", "Work with zero phone or notification distractions for 45 minutes.", 50, "Discipline"),
        DailyChallenge("Hydration & Energy", "Drink at least 2 liters of water and nourish your body today.", 50, "Health"),
        DailyChallenge("Evening Audit", "Reflect on your wins today and prepare top 3 priorities for tomorrow.", 50, "Personal Growth")
    )

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    fun getChallengeForDate(dateString: String): Pair<Int, DailyChallenge> {
        val hash = kotlin.math.abs(dateString.hashCode())
        val index = hash % dailyChallenges.size
        return Pair(index, dailyChallenges[index])
    }

    suspend fun completeOnboarding(name: String, selectedGoals: List<String>) = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val prefs = UserPreferences(
            id = 1,
            name = name.trim().ifEmpty { "Champion" },
            isOnboarded = true,
            selectedGoals = selectedGoals.joinToString(","),
            momentumPoints = 100,
            currentStreak = 1,
            bestStreak = 1,
            lastActiveDate = today,
            appearanceMode = "DARK"
        )
        prefDao.insertOrUpdate(prefs)

        // Seed customized starter habits based on selected goals
        val starterHabits = mutableListOf<HabitEntity>()
        if (selectedGoals.contains("Health")) {
            starterHabits.add(HabitEntity(title = "20 Min Morning Exercise", category = "Health", frequency = "Daily"))
            starterHabits.add(HabitEntity(title = "Drink 2L Fresh Water", category = "Health", frequency = "Daily"))
        }
        if (selectedGoals.contains("Personal Growth")) {
            starterHabits.add(HabitEntity(title = "Read 10 Pages of Non-Fiction", category = "Personal Growth", frequency = "Daily"))
        }
        if (selectedGoals.contains("Career")) {
            starterHabits.add(HabitEntity(title = "60 Min Deep Focus Work", category = "Career", frequency = "Weekdays"))
        }
        if (selectedGoals.contains("Money")) {
            starterHabits.add(HabitEntity(title = "Track Daily Expenses", category = "Money", frequency = "Daily"))
        }
        if (starterHabits.isEmpty()) {
            starterHabits.add(HabitEntity(title = "Morning Motivation & Plan", category = "Personal Growth", frequency = "Daily"))
            starterHabits.add(HabitEntity(title = "Physical Exercise", category = "Health", frequency = "Daily"))
        }

        starterHabits.forEach { habitDao.insertHabit(it) }

        // Seed a starter goal
        val goalId = goalDao.insertGoal(
            GoalEntity(
                title = "Master Daily Discipline & Focus",
                category = if (selectedGoals.isNotEmpty()) selectedGoals.first() else "Personal Growth",
                description = "Build a consistent routine to conquer my biggest dreams.",
                targetDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000))
            )
        )
        goalDao.insertTask(GoalTaskEntity(goalId = goalId, title = "Complete first 7-day habit streak", isCompleted = false))
        goalDao.insertTask(GoalTaskEntity(goalId = goalId, title = "Read 1 book on personal mastery", isCompleted = false))
        goalDao.insertTask(GoalTaskEntity(goalId = goalId, title = "Review weekly progress every Sunday", isCompleted = false))
    }

    suspend fun addGoal(title: String, category: String, description: String, targetDate: String, subtasks: List<String>) = withContext(Dispatchers.IO) {
        val goalId = goalDao.insertGoal(
            GoalEntity(
                title = title.trim(),
                category = category,
                description = description.trim(),
                targetDate = targetDate,
                isCompleted = false
            )
        )
        subtasks.filter { it.isNotBlank() }.forEach { taskTitle ->
            goalDao.insertTask(GoalTaskEntity(goalId = goalId, title = taskTitle.trim(), isCompleted = false))
        }
        awardMomentumPoints(30)
    }

    suspend fun updateGoal(goal: GoalEntity, subtasks: List<GoalTaskEntity>) = withContext(Dispatchers.IO) {
        goalDao.updateGoal(goal)
        subtasks.forEach { task ->
            if (task.id == 0L) {
                goalDao.insertTask(task.copy(goalId = goal.id))
            } else {
                goalDao.updateTask(task)
            }
        }
    }

    suspend fun deleteGoal(goal: GoalEntity) = withContext(Dispatchers.IO) {
        goalDao.deleteTasksForGoal(goal.id)
        goalDao.deleteGoal(goal)
    }

    suspend fun toggleTaskCompletion(task: GoalTaskEntity) = withContext(Dispatchers.IO) {
        val newStatus = !task.isCompleted
        goalDao.updateTask(task.copy(isCompleted = newStatus))
        val today = getTodayDateString()

        if (newStatus) {
            awardMomentumPoints(20)
            updateDailyActivity(today, tasksDelta = 1, pointsDelta = 20)
            checkAchievements()
        } else {
            awardMomentumPoints(-20)
            updateDailyActivity(today, tasksDelta = -1, pointsDelta = -20)
        }
    }

    suspend fun addHabit(title: String, category: String, frequency: String) = withContext(Dispatchers.IO) {
        habitDao.insertHabit(
            HabitEntity(
                title = title.trim(),
                category = category,
                frequency = frequency
            )
        )
        awardMomentumPoints(25)
    }

    suspend fun updateHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.deleteHabit(habit)
    }

    suspend fun toggleHabitCompletion(habit: HabitEntity, dateString: String, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        if (isCompleted) {
            habitDao.insertCompletion(HabitCompletionEntity(habitId = habit.id, dateString = dateString))
            val newStreak = habit.currentStreak + 1
            val newBest = maxOf(habit.bestStreak, newStreak)
            habitDao.updateHabit(habit.copy(currentStreak = newStreak, bestStreak = newBest))

            awardMomentumPoints(25)
            updateDailyActivity(dateString, habitsDelta = 1, pointsDelta = 25)
            updateUserStreakOnActivity(dateString)
            checkAchievements()
        } else {
            habitDao.deleteCompletion(habit.id, dateString)
            val newStreak = maxOf(0, habit.currentStreak - 1)
            habitDao.updateHabit(habit.copy(currentStreak = newStreak))

            awardMomentumPoints(-25)
            updateDailyActivity(dateString, habitsDelta = -1, pointsDelta = -25)
        }
    }

    suspend fun completeDailyChallenge(todayDate: String, challengeIndex: Int) = withContext(Dispatchers.IO) {
        val prefs = prefDao.getPreferencesOnce() ?: return@withContext
        if (prefs.dailyChallengeDate == todayDate && prefs.dailyChallengeCompleted) return@withContext

        prefDao.insertOrUpdate(
            prefs.copy(
                dailyChallengeDate = todayDate,
                dailyChallengeCompleted = true,
                dailyChallengeIndex = challengeIndex
            )
        )
        awardMomentumPoints(50)
        updateDailyActivity(todayDate, pointsDelta = 50)
        checkAchievements()
    }

    suspend fun toggleQuoteFavorite(quote: MotivationalQuote) = withContext(Dispatchers.IO) {
        quoteDao.updateQuote(quote.copy(isFavorite = !quote.isFavorite))
    }

    suspend fun awardMomentumPoints(points: Int) = withContext(Dispatchers.IO) {
        val prefs = prefDao.getPreferencesOnce() ?: return@withContext
        val newPoints = maxOf(0, prefs.momentumPoints + points)
        prefDao.updateMomentumPoints(newPoints)
        checkAchievements()
    }

    private suspend fun updateUserStreakOnActivity(today: String) = withContext(Dispatchers.IO) {
        val prefs = prefDao.getPreferencesOnce() ?: return@withContext
        if (prefs.lastActiveDate == today) return@withContext

        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayParsed = sdf.parse(today) ?: Date()
        calendar.time = todayParsed
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = sdf.format(calendar.time)

        val newStreak = if (prefs.lastActiveDate == yesterday) {
            prefs.currentStreak + 1
        } else if (prefs.lastActiveDate.isEmpty()) {
            1
        } else {
            1
        }
        val newBest = maxOf(prefs.bestStreak, newStreak)
        prefDao.updateStreak(newStreak, newBest, today)
    }

    private suspend fun updateDailyActivity(dateString: String, habitsDelta: Int = 0, tasksDelta: Int = 0, pointsDelta: Int = 0) = withContext(Dispatchers.IO) {
        val current = dailyActivityDao.getActivity(dateString).firstOrNull()
            ?: DailyActivityEntity(dateString = dateString, habitsCompleted = 0, tasksCompleted = 0, pointsEarned = 0)

        val updated = current.copy(
            habitsCompleted = maxOf(0, current.habitsCompleted + habitsDelta),
            tasksCompleted = maxOf(0, current.tasksCompleted + tasksDelta),
            pointsEarned = maxOf(0, current.pointsEarned + pointsDelta)
        )
        dailyActivityDao.insertOrUpdate(updated)
    }

    suspend fun checkAchievements() = withContext(Dispatchers.IO) {
        val prefs = prefDao.getPreferencesOnce() ?: return@withContext
        val totalCompletions = habitDao.countTotalCompletions().firstOrNull() ?: 0
        val completedTasks = goalDao.countCompletedTasks().firstOrNull() ?: 0
        val completedGoals = goalDao.countCompletedGoals().firstOrNull() ?: 0

        // 1. FIRST_STEP: 1 task or 1 habit
        if (totalCompletions >= 1 || completedTasks >= 1) {
            unlockAchievement("FIRST_STEP")
        }
        // 2. STREAK_7: 7-day streak
        if (prefs.currentStreak >= 7 || prefs.bestStreak >= 7) {
            unlockAchievement("STREAK_7")
        }
        // 3. STREAK_30: 30-day streak
        if (prefs.currentStreak >= 30 || prefs.bestStreak >= 30) {
            unlockAchievement("STREAK_30")
        }
        // 4. GOAL_5: 5 goals completed
        if (completedGoals >= 5) {
            unlockAchievement("GOAL_5")
        }
        // 5. HABIT_14: 14 habits completed
        if (totalCompletions >= 14) {
            unlockAchievement("HABIT_14")
        }
        // 6. MOMENTUM_500: 500 momentum points
        if (prefs.momentumPoints >= 500) {
            unlockAchievement("MOMENTUM_500")
        }
        // 7. HABIT_10: 10 habits completed
        if (totalCompletions >= 10) {
            unlockAchievement("HABIT_10")
        }
        // 8. POINTS_1000: 1000 points
        if (prefs.momentumPoints >= 1000) {
            unlockAchievement("POINTS_1000")
        }
    }

    private suspend fun unlockAchievement(code: String) {
        val item = achievementDao.getAchievementByCode(code) ?: return
        if (!item.isUnlocked) {
            achievementDao.updateAchievement(
                item.copy(
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                )
            )
            // Bonus points for unlocking
            val prefs = prefDao.getPreferencesOnce()
            if (prefs != null) {
                prefDao.updateMomentumPoints(prefs.momentumPoints + item.pointsReward)
            }
        }
    }

    suspend fun resetTodayProgress(today: String) = withContext(Dispatchers.IO) {
        habitDao.clearCompletionsForDate(today)
        dailyActivityDao.clearActivityForDate(today)
        val prefs = prefDao.getPreferencesOnce()
        if (prefs != null) {
            prefDao.insertOrUpdate(
                prefs.copy(
                    dailyChallengeCompleted = if (prefs.dailyChallengeDate == today) false else prefs.dailyChallengeCompleted
                )
            )
        }
    }

    suspend fun resetAllData() = withContext(Dispatchers.IO) {
        goalDao.clearTasks()
        goalDao.clearGoals()
        habitDao.clearCompletions()
        habitDao.clearHabits()
        dailyActivityDao.clearActivities()
        achievementDao.resetAchievements()
        prefDao.clear()
        // re-insert default fresh preferences
        prefDao.insertOrUpdate(
            UserPreferences(
                id = 1,
                name = "",
                isOnboarded = false,
                selectedGoals = "Career,Health,Personal Growth",
                momentumPoints = 50,
                currentStreak = 1,
                bestStreak = 1,
                lastActiveDate = "",
                appearanceMode = "DARK"
            )
        )
    }

    suspend fun updatePreferences(prefs: UserPreferences) = withContext(Dispatchers.IO) {
        prefDao.insertOrUpdate(prefs)
    }
}
