package com.example.ui.screens.goals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GoalEntity
import com.example.data.model.GoalTaskEntity
import com.example.data.model.GoalWithTasks
import com.example.ui.MainViewModel
import com.example.ui.components.CategoryChip
import com.example.ui.components.GoldButton
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GoalsScreen(
    viewModel: MainViewModel,
    goals: List<GoalWithTasks>,
    selectedCategory: String,
    selectedStatus: String,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<GoalWithTasks?>(null) }
    var deletingGoal by remember { mutableStateOf<GoalEntity?>(null) }

    val categories = listOf("All", "Career", "Money", "Health", "Personal Growth")
    val statuses = listOf("All", "Active", "Completed")

    // Filter goals
    val filteredGoals = goals.filter { goalWithTasks ->
        val catMatches = selectedCategory == "All" || goalWithTasks.goal.category.equals(selectedCategory, ignoreCase = true)
        val isAllTasksDone = goalWithTasks.tasks.isNotEmpty() && goalWithTasks.tasks.all { it.isCompleted }
        val isCompleted = goalWithTasks.goal.isCompleted || isAllTasksDone
        val statusMatches = when (selectedStatus) {
            "Active" -> !isCompleted
            "Completed" -> isCompleted
            else -> true
        }
        catMatches && statusMatches
    }

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
                modifier = Modifier.testTag("fab_create_goal")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Goal")
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
                    text = "Goal Mastery",
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Turn grand visions into actionable daily victories.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            // Category Filter Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = selectedCategory == category,
                            onClick = { viewModel.setGoalCategory(category) },
                            modifier = Modifier.testTag("filter_cat_$category")
                        )
                    }
                }
            }

            // Status Filter Row
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statuses.forEach { status ->
                        val isSelected = selectedStatus == status
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.setGoalStatus(status) }
                                .testTag("filter_status_$status"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GoldPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) GoldPrimary else DarkBorder)
                        ) {
                            Text(
                                text = status,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) GoldPrimary else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Goal Cards List
            if (filteredGoals.isEmpty()) {
                item {
                    EmptyGoalsCard(
                        selectedCategory = selectedCategory,
                        onCreateGoal = { showCreateDialog = true }
                    )
                }
            } else {
                items(filteredGoals, key = { it.goal.id }) { goalWithTasks ->
                    GoalCard(
                        goalWithTasks = goalWithTasks,
                        onToggleTask = { task -> viewModel.toggleTask(task) },
                        onEdit = { editingGoal = goalWithTasks },
                        onDelete = { deletingGoal = goalWithTasks.goal }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Create Goal Dialog
    if (showCreateDialog) {
        GoalEditorDialog(
            goalWithTasks = null,
            onDismiss = { showCreateDialog = false },
            onSave = { title, category, description, targetDate, subtasks ->
                viewModel.createGoal(title, category, description, targetDate, subtasks)
                showCreateDialog = false
            }
        )
    }

    // Edit Goal Dialog
    if (editingGoal != null) {
        GoalEditorDialog(
            goalWithTasks = editingGoal,
            onDismiss = { editingGoal = null },
            onSave = { title, category, description, targetDate, subtasks ->
                val current = editingGoal!!
                val updatedGoal = current.goal.copy(
                    title = title,
                    category = category,
                    description = description,
                    targetDate = targetDate
                )
                val newTasks = subtasks.map { taskTitle ->
                    val existing = current.tasks.find { it.title == taskTitle }
                    existing ?: GoalTaskEntity(goalId = current.goal.id, title = taskTitle, isCompleted = false)
                }
                viewModel.updateGoal(updatedGoal, newTasks)
                editingGoal = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (deletingGoal != null) {
        AlertDialog(
            onDismissRequest = { deletingGoal = null },
            containerColor = DarkSurface,
            title = { Text("Delete Goal", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete \"${deletingGoal?.title}\" and all its subtasks?", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        deletingGoal?.let { viewModel.deleteGoal(it) }
                        deletingGoal = null
                    },
                    modifier = Modifier.testTag("confirm_delete_goal")
                ) {
                    Text("Delete", color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingGoal = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun GoalCard(
    goalWithTasks: GoalWithTasks,
    onToggleTask: (GoalTaskEntity) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val progress = goalWithTasks.progressPercentage
    val isDone = progress == 100

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("goal_card_${goalWithTasks.goal.id}"),
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, if (isDone) SuccessGreen.copy(alpha = 0.5f) else DarkBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Category & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(category = goalWithTasks.goal.category)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp).testTag("edit_goal_${goalWithTasks.goal.id}")) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).testTag("delete_goal_${goalWithTasks.goal.id}")) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Description
            Text(
                text = goalWithTasks.goal.title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            if (goalWithTasks.goal.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = goalWithTasks.goal.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }

            // Target Date
            if (goalWithTasks.goal.targetDate.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Target: ${goalWithTasks.goal.targetDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar and percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress (${goalWithTasks.tasks.count { it.isCompleted }}/${goalWithTasks.tasks.size} tasks)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = "$progress%",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isDone) SuccessGreen else GoldPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val animatedProgress by animateFloatAsState(targetValue = progress / 100f, label = "progress")
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isDone) SuccessGreen else GoldPrimary,
                trackColor = DarkSurfaceElevated,
                strokeCap = StrokeCap.Round
            )

            // Subtasks toggle section
            if (goalWithTasks.tasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { expanded = !expanded }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expanded) "Hide action steps" else "View action steps (${goalWithTasks.tasks.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        goalWithTasks.tasks.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkSurfaceElevated)
                                    .clickable { onToggleTask(task) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("task_check_${task.id}"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { onToggleTask(task) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = SuccessGreen,
                                        uncheckedColor = DarkBorder,
                                        checkmarkColor = Color(0xFF121214)
                                    ),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (task.isCompleted) TextMuted else TextPrimary,
                                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyGoalsCard(
    selectedCategory: String,
    onCreateGoal: () -> Unit
) {
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
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = if (selectedCategory == "All") "No Goals Yet" else "No $selectedCategory Goals",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Define your ultimate destination and break it down into achievable tasks.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(18.dp))
            GoldButton(
                text = "Create First Goal",
                icon = Icons.Default.Add,
                onClick = onCreateGoal,
                testTag = "empty_create_goal_btn"
            )
        }
    }
}

@Composable
fun GoalEditorDialog(
    goalWithTasks: GoalWithTasks?,
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, description: String, targetDate: String, subtasks: List<String>) -> Unit
) {
    var title by remember { mutableStateOf(goalWithTasks?.goal?.title ?: "") }
    var category by remember { mutableStateOf(goalWithTasks?.goal?.category ?: "Career") }
    var description by remember { mutableStateOf(goalWithTasks?.goal?.description ?: "") }
    var targetDate by remember {
        mutableStateOf(
            goalWithTasks?.goal?.targetDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(
                Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)
            )
        )
    }
    var newTaskTitle by remember { mutableStateOf("") }
    val subtasks = remember {
        mutableStateListOf<String>().apply {
            if (goalWithTasks != null) {
                addAll(goalWithTasks.tasks.map { it.title })
            } else {
                addAll(listOf("Research & outline step 1", "Execute core milestone"))
            }
        }
    }

    val categories = listOf("Career", "Money", "Health", "Personal Growth")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = if (goalWithTasks == null) "Create New Goal" else "Edit Goal",
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
                    label = { Text("Goal Title", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_goal_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Category selector
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

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Why this matters", color = TextSecondary) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("input_goal_desc"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text("Target Date (YYYY-MM-DD)", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_goal_date"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Subtasks section
                Text("Subtasks / Action Steps", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        placeholder = { Text("Add action step...", color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_subtask"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newTaskTitle.isNotBlank()) {
                                subtasks.add(newTaskTitle.trim())
                                newTaskTitle = ""
                            }
                        },
                        modifier = Modifier.testTag("add_subtask_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Step", tint = GoldPrimary)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    subtasks.forEachIndexed { index, task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkSurfaceElevated, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(task, style = MaterialTheme.typography.bodySmall, color = TextPrimary, modifier = Modifier.weight(1f))
                            IconButton(onClick = { subtasks.removeAt(index) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            GoldButton(
                text = "Save Goal",
                enabled = title.isNotBlank(),
                onClick = {
                    onSave(title, category, description, targetDate, subtasks.toList())
                },
                modifier = Modifier.width(130.dp),
                testTag = "save_goal_button"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
