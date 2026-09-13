package com.example.ui.screens.habits

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.ui.MainViewModel
import com.example.ui.components.CategoryChip
import com.example.ui.components.GoldButton
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
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
fun HabitsScreen(
    viewModel: MainViewModel,
    habits: List<HabitEntity>,
    todayCompletions: List<HabitCompletionEntity>,
    allCompletions: List<HabitCompletionEntity>,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingHabit by remember { mutableStateOf<HabitEntity?>(null) }
    var deletingHabit by remember { mutableStateOf<HabitEntity?>(null) }

    val todayCompletedCount = habits.count { h -> todayCompletions.any { it.habitId == h.id } }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding(),
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = GoldPrimary,
                contentColor = Color(0xFF140F00),
                shape = CircleShape,
                modifier = Modifier.testTag("fab_create_habit")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Habit Engine",
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "We are what we repeatedly do. Excellence is a habit.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            // Summary Banner
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${habits.size}",
                                style = MaterialTheme.typography.headlineLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Active Habits",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(36.dp)
                                .background(DarkBorder)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$todayCompletedCount/${habits.size}",
                                style = MaterialTheme.typography.headlineLarge,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Done Today",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(36.dp)
                                .background(DarkBorder)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val maxStreak = habits.maxOfOrNull { it.bestStreak } ?: 0
                            Text(
                                text = "${maxStreak}d",
                                style = MaterialTheme.typography.headlineLarge,
                                color = StreakFlame,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Record Streak",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Habits List
            if (habits.isEmpty()) {
                item {
                    EmptyHabitsFullCard(onCreateHabit = { showCreateDialog = true })
                }
            } else {
                items(habits, key = { it.id }) { habit ->
                    val isDoneToday = todayCompletions.any { it.habitId == habit.id }
                    val habitCompletions = allCompletions.filter { it.habitId == habit.id }

                    HabitFullCard(
                        habit = habit,
                        isDoneToday = isDoneToday,
                        completions = habitCompletions,
                        onToggleToday = { viewModel.toggleHabitToday(habit, isDoneToday) },
                        onEdit = { editingHabit = habit },
                        onDelete = { deletingHabit = habit }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Add Habit Dialog
    if (showCreateDialog) {
        HabitEditorDialog(
            habit = null,
            onDismiss = { showCreateDialog = false },
            onSave = { title, category, frequency ->
                viewModel.createHabit(title, category, frequency)
                showCreateDialog = false
            }
        )
    }

    // Edit Habit Dialog
    if (editingHabit != null) {
        HabitEditorDialog(
            habit = editingHabit,
            onDismiss = { editingHabit = null },
            onSave = { title, category, frequency ->
                viewModel.updateHabit(
                    editingHabit!!.copy(
                        title = title,
                        category = category,
                        frequency = frequency
                    )
                )
                editingHabit = null
            }
        )
    }

    // Delete Confirmation
    if (deletingHabit != null) {
        AlertDialog(
            onDismissRequest = { deletingHabit = null },
            containerColor = DarkSurface,
            title = { Text("Delete Habit", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete \"${deletingHabit?.title}\"? Its completion history will be removed.", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        deletingHabit?.let { viewModel.deleteHabit(it) }
                        deletingHabit = null
                    },
                    modifier = Modifier.testTag("confirm_delete_habit")
                ) {
                    Text("Delete", color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingHabit = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun HabitFullCard(
    habit: HabitEntity,
    isDoneToday: Boolean,
    completions: List<HabitCompletionEntity>,
    onToggleToday: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("habit_card_${habit.id}"),
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, if (isDoneToday) SuccessGreen.copy(alpha = 0.4f) else DarkBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Title & Action menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Checkbox toggle
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isDoneToday) SuccessGreen else Color.Transparent)
                            .border(2.dp, if (isDoneToday) SuccessGreen else DarkBorder, CircleShape)
                            .clickable(onClick = onToggleToday)
                            .testTag("toggle_habit_${habit.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDoneToday) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color(0xFF101012),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = habit.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDoneToday) TextMuted else TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CategoryChip(category = habit.category)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = habit.frequency,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp).testTag("edit_habit_${habit.id}")) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).testTag("delete_habit_${habit.id}")) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Streaks Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = StreakFlame,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Current: ${habit.currentStreak} days",
                        style = MaterialTheme.typography.labelSmall,
                        color = StreakFlame,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Best: ${habit.bestStreak} days",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 7-Day Completion History Dots
            Text(
                text = "Past 7 Days Activity",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Past7DaysDotsRow(completions = completions)
        }
    }
}

@Composable
fun Past7DaysDotsRow(
    completions: List<HabitCompletionEntity>
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val dayFormat = SimpleDateFormat("EEE", Locale.US)

    val calendar = Calendar.getInstance()
    val days = (6 downTo 0).map { offset ->
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -offset)
        }
        val dateStr = sdf.format(cal.time)
        val label = dayFormat.format(cal.time).take(2)
        val isCompleted = completions.any { it.dateString == dateStr }
        Triple(dateStr, label, isCompleted)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { (_, label, isCompleted) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) SuccessGreen else DarkSurfaceElevated)
                        .border(1.dp, if (isCompleted) SuccessGreen else DarkBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF101012),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = if (isCompleted) TextPrimary else TextMuted
                )
            }
        }
    }
}

@Composable
fun EmptyHabitsFullCard(onCreateHabit: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
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
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "No Habits Created",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Atomic habits compound into life-changing success. Start with one daily action.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(18.dp))
            GoldButton(
                text = "Create Habit",
                icon = Icons.Default.Add,
                onClick = onCreateHabit,
                testTag = "empty_create_habit_btn"
            )
        }
    }
}

@Composable
fun HabitEditorDialog(
    habit: HabitEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, frequency: String) -> Unit
) {
    var title by remember { mutableStateOf(habit?.title ?: "") }
    var category by remember { mutableStateOf(habit?.category ?: "Health") }
    var frequency by remember { mutableStateOf(habit?.frequency ?: "Daily") }

    val categories = listOf("Career", "Money", "Health", "Personal Growth")
    val frequencies = listOf("Daily", "Weekdays", "Custom")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = if (habit == null) "Add Daily Habit" else "Edit Habit",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Title", color = TextSecondary) },
                    placeholder = { Text("e.g. 20 Min Morning Exercise", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_habit_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Text("Category", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        CategoryChip(
                            category = cat,
                            isSelected = category == cat,
                            onClick = { category = cat }
                        )
                    }
                }

                Text("Frequency", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    frequencies.forEach { freq ->
                        val isSelected = frequency == freq
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { frequency = freq },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GoldPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) GoldPrimary else DarkBorder)
                        ) {
                            Text(
                                text = freq,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) GoldPrimary else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            GoldButton(
                text = "Save Habit",
                enabled = title.isNotBlank(),
                onClick = { onSave(title, category, frequency) },
                modifier = Modifier.width(130.dp),
                testTag = "save_habit_button"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
