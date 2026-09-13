package com.example.ui.screens.home

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyChallenge
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.MotivationalQuote
import com.example.data.model.UserPreferences
import com.example.ui.MainViewModel
import com.example.ui.components.CategoryChip
import com.example.ui.components.CategoryColor
import com.example.ui.components.CircularProgressMeter
import com.example.ui.components.MotivationalQuoteCard
import com.example.ui.components.MotivatorCard
import com.example.ui.components.MotivatorLogoEmblem
import com.example.ui.components.MotivatorTab
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CategoryCareer
import com.example.ui.theme.CategoryDiscipline
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MomentumSpark
import com.example.ui.theme.StreakFlame
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    preferences: UserPreferences?,
    habits: List<HabitEntity>,
    todayCompletions: List<HabitCompletionEntity>,
    todayProgress: Int,
    quotes: List<MotivationalQuote>,
    onNavigateTab: (MotivatorTab) -> Unit,
    onOpenCreateGoal: () -> Unit,
    onOpenCreateHabit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val greeting = rememberGreeting()
    val headline = viewModel.getDailyHeadline()
    val encouragement = viewModel.getEncouragingMessage(todayProgress)
    val streak = preferences?.currentStreak ?: 1
    val points = preferences?.momentumPoints ?: 50
    val levelInfo = viewModel.calculateUserLevel(points)

    // Daily quote: pick one by day of year
    val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    val dailyQuote = if (quotes.isNotEmpty()) quotes[dayOfYear % quotes.size] else null

    // Daily challenge
    val (challengeIndex, challenge) = viewModel.currentDailyChallenge
    val isChallengeDone = preferences?.dailyChallengeDate == viewModel.todayDateString && preferences.dailyChallengeCompleted

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            HeaderRow(
                greeting = greeting,
                name = preferences?.name?.ifEmpty { "Champion" } ?: "Champion",
                streak = streak,
                points = points
            )
        }

        // Daily Motivational Headline
        item {
            HeadlineCard(headline = headline)
        }

        // Today's Progress Card
        item {
            TodayProgressCard(
                progress = todayProgress,
                habitsCompleted = habits.count { h -> todayCompletions.any { it.habitId == h.id } },
                totalHabits = habits.size,
                encouragement = encouragement
            )
        }

        // Daily Challenge Card
        item {
            DailyChallengeCard(
                challenge = challenge,
                isCompleted = isChallengeDone,
                onComplete = { viewModel.completeDailyChallenge() }
            )
        }

        // Quick Actions
        item {
            QuickActionsRow(
                onSetGoal = onOpenCreateGoal,
                onAddHabit = onOpenCreateHabit,
                onMotivation = { onNavigateTab(MotivatorTab.MOTIVATION) },
                onViewProgress = { onNavigateTab(MotivatorTab.PROFILE) }
            )
        }

        // Today's Habits Header
        item {
            SectionHeader(
                title = "Today's Targets",
                subtitle = "${habits.count { h -> todayCompletions.any { it.habitId == h.id } }} of ${habits.size} completed",
                actionText = if (habits.isNotEmpty()) "View all" else "Add habit",
                onActionClick = {
                    if (habits.isNotEmpty()) onNavigateTab(MotivatorTab.HABITS) else onOpenCreateHabit()
                }
            )
        }

        // Habits List
        if (habits.isEmpty()) {
            item {
                EmptyHabitsHomeCard(onAddHabit = onOpenCreateHabit)
            }
        } else {
            items(habits) { habit ->
                val isDone = todayCompletions.any { it.habitId == habit.id }
                HabitCheckItem(
                    habit = habit,
                    isCompleted = isDone,
                    onToggle = { viewModel.toggleHabitToday(habit, isDone) }
                )
            }
        }

        // Daily Motivation Quote Card
        if (dailyQuote != null) {
            item {
                SectionHeader(title = "Daily Motivation")
            }
            item {
                MotivationalQuoteCard(
                    quote = dailyQuote,
                    onToggleFavorite = { viewModel.toggleQuoteFavorite(dailyQuote) },
                    onShare = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "\"${dailyQuote.quote}\"\n— ${dailyQuote.author}\n\nShared via Motivator App 🔥")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Motivation"))
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeaderRow(
    greeting: String,
    name: String,
    streak: Int,
    points: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$greeting,",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                text = name,
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Streak badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceVariant,
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = StreakFlame,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$streak",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Momentum points badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceVariant,
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Points",
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$points pts",
                        style = MaterialTheme.typography.labelLarge,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HeadlineCard(headline: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f))
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(GoldPrimary.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(GoldPrimary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "\"$headline\"",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
private fun TodayProgressCard(
    progress: Int,
    habitsCompleted: Int,
    totalHabits: Int,
    encouragement: String
) {
    MotivatorCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "TODAY'S MOMENTUM",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$habitsCompleted of $totalHabits Targets Done",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = encouragement,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            CircularProgressMeter(
                percentage = progress,
                size = 90.dp
            )
        }
    }
}

@Composable
private fun DailyChallengeCard(
    challenge: DailyChallenge,
    isCompleted: Boolean,
    onComplete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, if (isCompleted) SuccessGreen.copy(alpha = 0.5f) else DarkBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (isCompleted) SuccessGreen.copy(alpha = 0.2f) else GoldPrimary.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.Check else Icons.Default.Bolt,
                            contentDescription = null,
                            tint = if (isCompleted) SuccessGreen else GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "DAILY CHALLENGE",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isCompleted) SuccessGreen else GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "+${challenge.pointsReward} PTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = challenge.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (isCompleted) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SuccessGreen.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Completed for Today (+50 pts earned!)",
                            style = MaterialTheme.typography.labelMedium,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(onClick = onComplete)
                        .testTag("complete_challenge_button"),
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurfaceVariant,
                    border = BorderStroke(1.dp, GoldPrimary)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mark Challenge Completed",
                            style = MaterialTheme.typography.labelMedium,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onSetGoal: () -> Unit,
    onAddHabit: () -> Unit,
    onMotivation: () -> Unit,
    onViewProgress: () -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                label = "Set Goal",
                icon = Icons.Default.Flag,
                color = CategoryCareer,
                onClick = onSetGoal,
                modifier = Modifier.weight(1f),
                testTag = "quick_set_goal"
            )
            QuickActionButton(
                label = "Add Habit",
                icon = Icons.Default.Repeat,
                color = GoldPrimary,
                onClick = onAddHabit,
                modifier = Modifier.weight(1f),
                testTag = "quick_add_habit"
            )
            QuickActionButton(
                label = "Motivation",
                icon = Icons.Default.Lightbulb,
                color = CategoryDiscipline,
                onClick = onMotivation,
                modifier = Modifier.weight(1f),
                testTag = "quick_motivation"
            )
            QuickActionButton(
                label = "Progress",
                icon = Icons.Default.TrendingUp,
                color = SuccessGreen,
                onClick = onViewProgress,
                modifier = Modifier.weight(1f),
                testTag = "quick_progress"
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "quick_action"
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun HabitCheckItem(
    habit: HabitEntity,
    isCompleted: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onToggle)
            .testTag("habit_item_${habit.id}"),
        shape = RoundedCornerShape(18.dp),
        color = if (isCompleted) DarkSurfaceVariant else DarkSurface,
        border = BorderStroke(1.dp, if (isCompleted) SuccessGreen.copy(alpha = 0.4f) else DarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated checkbox
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) SuccessGreen else Color.Transparent)
                    .border(
                        2.dp,
                        if (isCompleted) SuccessGreen else DarkBorder,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color(0xFF101012),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isCompleted) TextMuted else TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryChip(category = habit.category)
                    Spacer(modifier = Modifier.width(8.dp))
                    if (habit.currentStreak > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = StreakFlame,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${habit.currentStreak}d streak",
                                style = MaterialTheme.typography.labelSmall,
                                color = StreakFlame,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (isCompleted) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SuccessGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "+25 pts",
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyHabitsHomeCard(onAddHabit: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(GoldPrimary.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Habits Configured Yet",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Add your first recurring habit to build daily streaks and earn momentum points.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onAddHabit)
                    .testTag("add_first_habit_button"),
                shape = RoundedCornerShape(12.dp),
                color = GoldPrimary
            ) {
                Text(
                    text = "Add Habit",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF140F00),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun rememberGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
}
