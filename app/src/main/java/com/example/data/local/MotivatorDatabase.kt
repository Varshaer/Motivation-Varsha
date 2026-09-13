package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AchievementEntity
import com.example.data.model.DailyActivityEntity
import com.example.data.model.GoalEntity
import com.example.data.model.GoalTaskEntity
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.MotivationalQuote
import com.example.data.model.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserPreferences::class,
        GoalEntity::class,
        GoalTaskEntity::class,
        HabitEntity::class,
        HabitCompletionEntity::class,
        MotivationalQuote::class,
        AchievementEntity::class,
        DailyActivityEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MotivatorDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun goalDao(): GoalDao
    abstract fun habitDao(): HabitDao
    abstract fun quoteDao(): QuoteDao
    abstract fun achievementDao(): AchievementDao
    abstract fun dailyActivityDao(): DailyActivityDao

    companion object {
        @Volatile
        private var INSTANCE: MotivatorDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MotivatorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MotivatorDatabase::class.java,
                    "motivator_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }
    }
}

suspend fun populateInitialData(database: MotivatorDatabase) {
    // 1. Initial User Preferences
    val prefDao = database.userPreferencesDao()
    if (prefDao.getPreferencesOnce() == null) {
        prefDao.insertOrUpdate(
            UserPreferences(
                id = 1,
                name = "Champion",
                isOnboarded = true,
                selectedGoals = "Career,Health,Personal Growth,Money",
                momentumPoints = 50,
                currentStreak = 1,
                bestStreak = 1,
                lastActiveDate = "",
                appearanceMode = "DARK"
            )
        )
    }

    // 2. Initial Achievements
    val achievementDao = database.achievementDao()
    if (achievementDao.countAchievements() == 0) {
        val initialAchievements = listOf(
            AchievementEntity(
                code = "FIRST_STEP",
                title = "First Step",
                description = "Complete your first task or habit",
                iconName = "star",
                pointsReward = 50
            ),
            AchievementEntity(
                code = "STREAK_7",
                title = "7-Day Warrior",
                description = "Maintain a 7-day streak",
                iconName = "local_fire_department",
                pointsReward = 150
            ),
            AchievementEntity(
                code = "STREAK_30",
                title = "30-Day Unstoppable",
                description = "Maintain a 30-day streak",
                iconName = "bolt",
                pointsReward = 500
            ),
            AchievementEntity(
                code = "GOAL_5",
                title = "Goal Crusher",
                description = "Complete 5 goals",
                iconName = "military_tech",
                pointsReward = 300
            ),
            AchievementEntity(
                code = "HABIT_14",
                title = "Consistency King/Queen",
                description = "Complete habits for 14 days",
                iconName = "verified",
                pointsReward = 250
            ),
            AchievementEntity(
                code = "MOMENTUM_500",
                title = "Momentum Builder",
                description = "Earn 500 momentum points",
                iconName = "trending_up",
                pointsReward = 200
            ),
            AchievementEntity(
                code = "HABIT_10",
                title = "Habit Novice",
                description = "Complete 10 daily habits total",
                iconName = "repeat",
                pointsReward = 100
            ),
            AchievementEntity(
                code = "POINTS_1000",
                title = "Grandmaster of Will",
                description = "Accumulate 1000 momentum points",
                iconName = "workspace_premium",
                pointsReward = 400
            )
        )
        achievementDao.insertAll(initialAchievements)
    }

    // 3. Initial Motivational Quotes Library across 8 categories
    val quoteDao = database.quoteDao()
    if (quoteDao.countQuotes() == 0) {
        val quotes = listOf(
            // Discipline
            MotivationalQuote(
                quote = "Discipline is choosing between what you want now and what you want most.",
                author = "Abraham Lincoln",
                category = "Discipline"
            ),
            MotivationalQuote(
                quote = "We must all suffer one of two things: the pain of discipline or the pain of regret.",
                author = "Jim Rohn",
                category = "Discipline"
            ),
            MotivationalQuote(
                quote = "Small disciplines repeated with consistency every day lead to great achievements gained slowly over time.",
                author = "John C. Maxwell",
                category = "Discipline"
            ),
            MotivationalQuote(
                quote = "Rule your mind or it will rule you.",
                author = "Horace",
                category = "Discipline"
            ),

            // Success
            MotivationalQuote(
                quote = "Success is not final, failure is not fatal: it is the courage to continue that counts.",
                author = "Winston Churchill",
                category = "Success"
            ),
            MotivationalQuote(
                quote = "The secret of getting ahead is getting started.",
                author = "Mark Twain",
                category = "Success"
            ),
            MotivationalQuote(
                quote = "Success usually comes to those who are too busy to be looking for it.",
                author = "Henry David Thoreau",
                category = "Success"
            ),
            MotivationalQuote(
                quote = "Don't watch the clock; do what it does. Keep going.",
                author = "Sam Levenson",
                category = "Success"
            ),

            // Confidence
            MotivationalQuote(
                quote = "Believe you can and you're halfway there.",
                author = "Theodore Roosevelt",
                category = "Confidence"
            ),
            MotivationalQuote(
                quote = "With realization of one's own potential and self-confidence in one's ability, one can build a better world.",
                author = "Dalai Lama",
                category = "Confidence"
            ),
            MotivationalQuote(
                quote = "You gain strength, courage, and confidence by every experience in which you really stop to look fear in the face.",
                author = "Eleanor Roosevelt",
                category = "Confidence"
            ),

            // Career
            MotivationalQuote(
                quote = "The only way to do great work is to love what you do.",
                author = "Steve Jobs",
                category = "Career"
            ),
            MotivationalQuote(
                quote = "Opportunities don't happen. You create them.",
                author = "Chris Grosser",
                category = "Career"
            ),
            MotivationalQuote(
                quote = "Focus on being productive instead of busy.",
                author = "Tim Ferriss",
                category = "Career"
            ),

            // Fitness
            MotivationalQuote(
                quote = "Take care of your body. It's the only place you have to live.",
                author = "Jim Rohn",
                category = "Fitness"
            ),
            MotivationalQuote(
                quote = "The body achieves what the mind believes.",
                author = "Napoleon Hill",
                category = "Fitness"
            ),
            MotivationalQuote(
                quote = "Action is the foundational key to all physical strength and vitality.",
                author = "Pablo Picasso",
                category = "Fitness"
            ),

            // Money
            MotivationalQuote(
                quote = "Financial freedom is available to those who learn about it and work for it.",
                author = "Robert Kiyosaki",
                category = "Money"
            ),
            MotivationalQuote(
                quote = "Do not save what is left after spending, but spend what is left after saving.",
                author = "Warren Buffett",
                category = "Money"
            ),
            MotivationalQuote(
                quote = "A budget is telling your money where to go instead of wondering where it went.",
                author = "Dave Ramsey",
                category = "Money"
            ),

            // Personal Growth
            MotivationalQuote(
                quote = "The only person you are destined to become is the person you decide to be.",
                author = "Ralph Waldo Emerson",
                category = "Personal Growth"
            ),
            MotivationalQuote(
                quote = "Be not afraid of growing slowly, be afraid only of standing still.",
                author = "Chinese Proverb",
                category = "Personal Growth"
            ),
            MotivationalQuote(
                quote = "Continuous improvement is better than delayed perfection.",
                author = "Mark Twain",
                category = "Personal Growth"
            ),

            // Overcoming Failure
            MotivationalQuote(
                quote = "I have not failed. I've just found 10,000 ways that won't work.",
                author = "Thomas Edison",
                category = "Overcoming Failure"
            ),
            MotivationalQuote(
                quote = "Failure is simply the opportunity to begin again, this time more intelligently.",
                author = "Henry Ford",
                category = "Overcoming Failure"
            ),
            MotivationalQuote(
                quote = "It is during our darkest moments that we must focus to see the light.",
                author = "Aristotle Onassis",
                category = "Overcoming Failure"
            ),
            MotivationalQuote(
                quote = "Rock bottom became the solid foundation on which I rebuilt my life.",
                author = "J.K. Rowling",
                category = "Overcoming Failure"
            )
        )
        quoteDao.insertAll(quotes)
    }

    // 4. Sample starters so first time user gets immediate value
    val habitDao = database.habitDao()
    val goalDao = database.goalDao()
    val habitCount = habitDao.getAllHabits()
    // We will let user create or onboard with personalized habits
}
