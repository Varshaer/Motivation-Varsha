package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val isOnboarded: Boolean = false,
    val selectedGoals: String = "Career,Health,Personal Growth", // Comma-separated
    val momentumPoints: Int = 50,
    val currentStreak: Int = 1,
    val bestStreak: Int = 1,
    val lastActiveDate: String = "", // YYYY-MM-DD
    val appearanceMode: String = "DARK", // DARK, LIGHT, SYSTEM
    val morningReminderEnabled: Boolean = true,
    val morningReminderTime: String = "08:00",
    val habitReminderEnabled: Boolean = true,
    val habitReminderTime: String = "13:00",
    val eveningReminderEnabled: Boolean = true,
    val eveningReminderTime: String = "20:00",
    val dailyChallengeDate: String = "",
    val dailyChallengeCompleted: Boolean = false,
    val dailyChallengeIndex: Int = 0
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // Career, Money, Health, Personal Growth
    val description: String = "",
    val targetDate: String = "", // e.g. "2026-12-31"
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "goal_tasks",
    indices = [Index("goalId")]
)
data class GoalTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val title: String,
    val isCompleted: Boolean = false
)

data class GoalWithTasks(
    @Embedded val goal: GoalEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "goalId"
    )
    val tasks: List<GoalTaskEntity> = emptyList()
) {
    val progressPercentage: Int
        get() {
            if (tasks.isEmpty()) return if (goal.isCompleted) 100 else 0
            val completed = tasks.count { it.isCompleted }
            return ((completed.toFloat() / tasks.size) * 100).toInt()
        }
}

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // Career, Money, Health, Personal Growth
    val frequency: String = "Daily", // Daily, Weekdays, Custom
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "habit_completions",
    indices = [Index(value = ["habitId", "dateString"], unique = true)]
)
data class HabitCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val dateString: String, // YYYY-MM-DD
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quotes")
data class MotivationalQuote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quote: String,
    val author: String,
    val category: String, // Discipline, Success, Confidence, Career, Fitness, Money, Personal Growth, Overcoming Failure
    val isFavorite: Boolean = false
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String, // Unique identifier e.g. FIRST_STEP
    val title: String,
    val description: String,
    val iconName: String,
    val pointsReward: Int = 50,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L
)

@Entity(tableName = "daily_activities")
data class DailyActivityEntity(
    @PrimaryKey val dateString: String, // YYYY-MM-DD
    val habitsCompleted: Int = 0,
    val tasksCompleted: Int = 0,
    val pointsEarned: Int = 0
)

data class DailyChallenge(
    val title: String,
    val description: String,
    val pointsReward: Int = 50,
    val category: String
)
