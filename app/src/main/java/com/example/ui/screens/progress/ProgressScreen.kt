package com.example.ui.screens.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AchievementEntity
import com.example.data.model.DailyActivityEntity
import com.example.data.model.GoalWithTasks
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.UserPreferences
import com.example.ui.MainViewModel
import com.example.ui.components.CircularProgressMeter
import com.example.ui.components.MotivatorCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatBadge
import com.example.ui.theme.CategoryCareer
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StreakFlame
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ProgressScreen(
    viewModel: MainViewModel,
    preferences: UserPreferences?,
    todayProgress: Int,
    habits: List<HabitEntity>,
    goals: List<GoalWithTasks>,
    allCompletions: List<HabitCompletionEntity>,
    achievements: List<AchievementEntity>,
    recentActivities: List<DailyActivityEntity>,
    modifier: Modifier = Modifier
) {
    val points = preferences?.momentumPoints ?: 50
    val levelInfo = viewModel.calculateUserLevel(points)
    val currentStreak = preferences?.currentStreak ?: 1
    val bestStreak = preferences?.bestStreak ?: 1
    val completedGoalsCount = goals.count { g -> g.progressPercentage == 100 }
    val totalCompletionsCount = allCompletions.size

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
            Text(
                text = "Performance Metrics",
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Real data tracking your discipline, consistency, and growth.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        // Level and XP Card
        item {
            LevelTierCard(levelInfo = levelInfo)
        }

        // 7-Day Completion Bar Chart
        item {
            WeeklyProgressChart(allCompletions = allCompletions)
        }

        // Key Lifetime Stats Grid
        item {
            SectionHeader(title = "Discipline Statistics")
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCardItem(
                        label = "Current Streak",
                        value = "$currentStreak days",
                        icon = Icons.Default.LocalFireDepartment,
                        color = StreakFlame,
                        modifier = Modifier.weight(1f)
                    )
                    StatCardItem(
                        label = "Best Streak",
                        value = "$bestStreak days",
                        icon = Icons.Default.WorkspacePremium,
                        color = GoldPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCardItem(
                        label = "Goals Conquered",
                        value = "$completedGoalsCount/${goals.size}",
                        icon = Icons.Default.Flag,
                        color = CategoryCareer,
                        modifier = Modifier.weight(1f)
                    )
                    StatCardItem(
                        label = "Habits Checked",
                        value = "$totalCompletionsCount times",
                        icon = Icons.Default.Repeat,
                        color = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Achievements Section
        item {
            val unlockedCount = achievements.count { it.isUnlocked }
            SectionHeader(
                title = "Achievements",
                subtitle = "$unlockedCount of ${achievements.size} badges unlocked"
            )
        }

        items(achievements, key = { it.id }) { achievement ->
            AchievementBadgeItem(achievement = achievement)
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun LevelTierCard(levelInfo: com.example.ui.UserLevelInfo) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(GoldPrimary.copy(alpha = 0.1f), Color.Transparent)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldPrimary
                            ) {
                                Text(
                                    text = "LVL ${levelInfo.level}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF140F00),
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = levelInfo.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${levelInfo.currentPoints} Total Momentum Points",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(GoldPrimary.copy(alpha = 0.18f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // XP Progress bar
                val animatedProgress by animateFloatAsState(targetValue = levelInfo.progress, label = "xp")
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = GoldPrimary,
                    trackColor = DarkSurfaceElevated,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${levelInfo.minPointsForLevel} PTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Text(
                        text = if (levelInfo.level < 5) "Next Tier: ${levelInfo.maxPointsForLevel} PTS" else "Max Tier Reached",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyProgressChart(
    allCompletions: List<HabitCompletionEntity>
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val dayFormat = SimpleDateFormat("EEE", Locale.US)

    val past7Days = (6 downTo 0).map { offset ->
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -offset)
        }
        val dateStr = sdf.format(cal.time)
        val dayLabel = dayFormat.format(cal.time).take(3)
        val count = allCompletions.count { it.dateString == dateStr }
        Pair(dayLabel, count)
    }

    val maxCount = maxOf(1, past7Days.maxOf { it.second })

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "7-Day Activity Velocity",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${allCompletions.size} total entries",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bars Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                past7Days.forEach { (day, count) ->
                    val ratio = (count.toFloat() / maxCount).coerceIn(0.08f, 1f)
                    val isToday = day == past7Days.last().first

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (count > 0) "$count" else "-",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (count > 0) GoldLight else TextMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .fillMaxHeight(ratio)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    if (count > 0) {
                                        Brush.verticalGradient(
                                            listOf(GoldLight, if (isToday) GoldPrimary else GoldAccent)
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            listOf(DarkSurfaceElevated, DarkSurfaceElevated)
                                        )
                                    }
                                )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = if (isToday) GoldPrimary else TextSecondary,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCardItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun AchievementBadgeItem(
    achievement: AchievementEntity
) {
    val isUnlocked = achievement.isUnlocked
    val icon = when (achievement.iconName) {
        "local_fire_department" -> Icons.Default.LocalFireDepartment
        "bolt" -> Icons.Default.Bolt
        "military_tech" -> Icons.Default.MilitaryTech
        "verified" -> Icons.Default.Verified
        "trending_up" -> Icons.Default.TrendingUp
        "repeat" -> Icons.Default.Repeat
        "workspace_premium" -> Icons.Default.WorkspacePremium
        else -> Icons.Default.Star
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("achievement_${achievement.code}"),
        shape = RoundedCornerShape(18.dp),
        color = if (isUnlocked) DarkSurfaceElevated else DarkSurface,
        border = BorderStroke(1.dp, if (isUnlocked) GoldPrimary.copy(alpha = 0.45f) else DarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) GoldPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant
                    )
                    .border(
                        1.5.dp,
                        if (isUnlocked) GoldPrimary else DarkBorder,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUnlocked) icon else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) GoldPrimary else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = achievement.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isUnlocked) TextPrimary else TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    if (isUnlocked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SuccessGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "UNLOCKED",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked) TextSecondary else TextMuted
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isUnlocked) GoldPrimary.copy(alpha = 0.15f) else DarkSurfaceVariant
            ) {
                Text(
                    text = "+${achievement.pointsReward} PTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUnlocked) GoldPrimary else TextMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
