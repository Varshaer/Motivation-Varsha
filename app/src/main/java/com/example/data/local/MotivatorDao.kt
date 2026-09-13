package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.AchievementEntity
import com.example.data.model.DailyActivityEntity
import com.example.data.model.GoalEntity
import com.example.data.model.GoalTaskEntity
import com.example.data.model.GoalWithTasks
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.MotivationalQuote
import com.example.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun getPreferences(): Flow<UserPreferences?>

    @Query("SELECT * FROM user_preferences WHERE id = 1")
    suspend fun getPreferencesOnce(): UserPreferences?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(preferences: UserPreferences)

    @Query("UPDATE user_preferences SET momentumPoints = :points WHERE id = 1")
    suspend fun updateMomentumPoints(points: Int)

    @Query("UPDATE user_preferences SET currentStreak = :current, bestStreak = :best, lastActiveDate = :lastDate WHERE id = 1")
    suspend fun updateStreak(current: Int, best: Int, lastDate: String)

    @Query("DELETE FROM user_preferences")
    suspend fun clear()
}

@Dao
interface GoalDao {
    @Transaction
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoalsWithTasks(): Flow<List<GoalWithTasks>>

    @Transaction
    @Query("SELECT * FROM goals WHERE id = :id")
    fun getGoalWithTasksById(id: Long): Flow<GoalWithTasks?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: GoalTaskEntity): Long

    @Update
    suspend fun updateTask(task: GoalTaskEntity)

    @Delete
    suspend fun deleteTask(task: GoalTaskEntity)

    @Query("DELETE FROM goal_tasks WHERE goalId = :goalId")
    suspend fun deleteTasksForGoal(goalId: Long)

    @Query("SELECT COUNT(*) FROM goals WHERE isCompleted = 1")
    fun countCompletedGoals(): Flow<Int>

    @Query("SELECT COUNT(*) FROM goals")
    fun countTotalGoals(): Flow<Int>

    @Query("SELECT COUNT(*) FROM goal_tasks WHERE isCompleted = 1")
    fun countCompletedTasks(): Flow<Int>

    @Query("DELETE FROM goals")
    suspend fun clearGoals()

    @Query("DELETE FROM goal_tasks")
    suspend fun clearTasks()
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT * FROM habit_completions WHERE dateString = :dateString")
    fun getCompletionsForDate(dateString: String): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions ORDER BY completedAt DESC")
    fun getAllCompletions(): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId")
    fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(completion: HabitCompletionEntity): Long

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND dateString = :dateString")
    suspend fun deleteCompletion(habitId: Long, dateString: String)

    @Query("SELECT COUNT(*) FROM habit_completions")
    fun countTotalCompletions(): Flow<Int>

    @Query("DELETE FROM habits")
    suspend fun clearHabits()

    @Query("DELETE FROM habit_completions")
    suspend fun clearCompletions()

    @Query("DELETE FROM habit_completions WHERE dateString = :dateString")
    suspend fun clearCompletionsForDate(dateString: String)
}

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes ORDER BY id ASC")
    fun getAllQuotes(): Flow<List<MotivationalQuote>>

    @Query("SELECT * FROM quotes WHERE category = :category ORDER BY id ASC")
    fun getQuotesByCategory(category: String): Flow<List<MotivationalQuote>>

    @Query("SELECT * FROM quotes WHERE isFavorite = 1 ORDER BY id ASC")
    fun getFavoriteQuotes(): Flow<List<MotivationalQuote>>

    @Update
    suspend fun updateQuote(quote: MotivationalQuote)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(quotes: List<MotivationalQuote>)

    @Query("SELECT COUNT(*) FROM quotes")
    suspend fun countQuotes(): Int
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY id ASC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE code = :code LIMIT 1")
    suspend fun getAchievementByCode(code: String): AchievementEntity?

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Query("SELECT COUNT(*) FROM achievements")
    suspend fun countAchievements(): Int

    @Query("UPDATE achievements SET isUnlocked = 0, unlockedAt = 0")
    suspend fun resetAchievements()
}

@Dao
interface DailyActivityDao {
    @Query("SELECT * FROM daily_activities WHERE dateString = :dateString LIMIT 1")
    fun getActivity(dateString: String): Flow<DailyActivityEntity?>

    @Query("SELECT * FROM daily_activities ORDER BY dateString DESC LIMIT :limit")
    fun getRecentActivities(limit: Int = 7): Flow<List<DailyActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(activity: DailyActivityEntity)

    @Query("DELETE FROM daily_activities")
    suspend fun clearActivities()

    @Query("DELETE FROM daily_activities WHERE dateString = :dateString")
    suspend fun clearActivityForDate(dateString: String)
}
