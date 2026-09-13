package com.example.ui.screens.profile

import android.widget.Toast
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserPreferences
import com.example.ui.MainViewModel
import com.example.ui.components.CategoryChip
import com.example.ui.components.GoldButton
import com.example.ui.components.GoldOutlinedButton
import com.example.ui.components.MotivatorLogoEmblem
import com.example.ui.components.SectionHeader
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import androidx.compose.foundation.border
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StreakFlame
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    preferences: UserPreferences?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showResetTodayDialog by remember { mutableStateOf(false) }
    var showResetAllDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val name = preferences?.name?.ifEmpty { "Champion" } ?: "Champion"
    val goalsList = preferences?.selectedGoals?.split(",")?.filter { it.isNotBlank() } ?: listOf("Career", "Health")
    val currentMode = preferences?.appearanceMode ?: "Dark"

    val morningEnabled = preferences?.morningReminderEnabled ?: true
    val morningTime = preferences?.morningReminderTime ?: "08:00"
    val habitEnabled = preferences?.habitReminderEnabled ?: true
    val habitTime = preferences?.habitReminderTime ?: "13:00"
    val eveningEnabled = preferences?.eveningReminderEnabled ?: true
    val eveningTime = preferences?.eveningReminderTime ?: "20:00"

    val allGoalOptions = listOf("Career", "Money", "Health", "Personal Growth")

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
                text = "Profile & Settings",
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Customize your habits, notifications, and focus areas.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        // Profile Identity Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MotivatorLogoEmblem(size = 64.dp)

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${preferences?.momentumPoints ?: 50} Momentum Points",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight
                        )
                    }

                    IconButton(
                        onClick = { showEditNameDialog = true },
                        modifier = Modifier.testTag("edit_profile_name_btn")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Name", tint = GoldPrimary)
                    }
                }
            }
        }

        // Focus Areas Selector
        item {
            SectionHeader(title = "Primary Focus Areas")
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Select active focus areas to tailor daily motivation and challenge recommendations:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(allGoalOptions) { goalOpt ->
                            val isSelected = goalsList.contains(goalOpt)
                            CategoryChip(
                                category = goalOpt,
                                isSelected = isSelected,
                                onClick = {
                                    val updated = if (isSelected) {
                                        if (goalsList.size > 1) goalsList - goalOpt else goalsList
                                    } else {
                                        goalsList + goalOpt
                                    }
                                    viewModel.updateProfile(
                                        name = name,
                                        selectedGoals = updated,
                                        appearanceMode = currentMode,
                                        morningReminder = morningEnabled,
                                        morningTime = morningTime,
                                        habitReminder = habitEnabled,
                                        habitTime = habitTime,
                                        eveningReminder = eveningEnabled,
                                        eveningTime = eveningTime
                                    )
                                },
                                modifier = Modifier.testTag("profile_goal_$goalOpt")
                            )
                        }
                    }
                }
            }
        }

        // Appearance Mode
        item {
            SectionHeader(title = "Appearance")
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Color Theme",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val modes = listOf("Dark", "Light", "System")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        modes.forEach { mode ->
                            val isSel = currentMode == mode
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.updateProfile(
                                            name = name,
                                            selectedGoals = goalsList,
                                            appearanceMode = mode,
                                            morningReminder = morningEnabled,
                                            morningTime = morningTime,
                                            habitReminder = habitEnabled,
                                            habitTime = habitTime,
                                            eveningReminder = eveningEnabled,
                                            eveningTime = eveningTime
                                        )
                                    }
                                    .testTag("theme_mode_$mode"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) GoldPrimary else DarkSurfaceVariant,
                                border = BorderStroke(1.dp, if (isSel) GoldPrimary else DarkBorder)
                            ) {
                                Text(
                                    text = mode,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSel) Color(0xFF140F00) else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Notification Settings
        item {
            SectionHeader(title = "Reminders & Notifications")
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Morning reminder
                    ReminderSettingRow(
                        title = "Morning Motivation",
                        time = morningTime,
                        isEnabled = morningEnabled,
                        onToggle = { enabled ->
                            viewModel.updateProfile(
                                name = name,
                                selectedGoals = goalsList,
                                appearanceMode = currentMode,
                                morningReminder = enabled,
                                morningTime = morningTime,
                                habitReminder = habitEnabled,
                                habitTime = habitTime,
                                eveningReminder = eveningEnabled,
                                eveningTime = eveningTime
                            )
                        },
                        onTimeChange = { newTime ->
                            viewModel.updateProfile(
                                name = name,
                                selectedGoals = goalsList,
                                appearanceMode = currentMode,
                                morningReminder = morningEnabled,
                                morningTime = newTime,
                                habitReminder = habitEnabled,
                                habitTime = habitTime,
                                eveningReminder = eveningEnabled,
                                eveningTime = eveningTime
                            )
                        },
                        tag = "morning"
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkBorder))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Midday habit reminder
                    ReminderSettingRow(
                        title = "Midday Habit Check-In",
                        time = habitTime,
                        isEnabled = habitEnabled,
                        onToggle = { enabled ->
                            viewModel.updateProfile(
                                name = name,
                                selectedGoals = goalsList,
                                appearanceMode = currentMode,
                                morningReminder = morningEnabled,
                                morningTime = morningTime,
                                habitReminder = enabled,
                                habitTime = habitTime,
                                eveningReminder = eveningEnabled,
                                eveningTime = eveningTime
                            )
                        },
                        onTimeChange = { newTime ->
                            viewModel.updateProfile(
                                name = name,
                                selectedGoals = goalsList,
                                appearanceMode = currentMode,
                                morningReminder = morningEnabled,
                                morningTime = morningTime,
                                habitReminder = habitEnabled,
                                habitTime = newTime,
                                eveningReminder = eveningEnabled,
                                eveningTime = eveningTime
                            )
                        },
                        tag = "habit"
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkBorder))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Evening progress reminder
                    ReminderSettingRow(
                        title = "Evening Victory Review",
                        time = eveningTime,
                        isEnabled = eveningEnabled,
                        onToggle = { enabled ->
                            viewModel.updateProfile(
                                name = name,
                                selectedGoals = goalsList,
                                appearanceMode = currentMode,
                                morningReminder = morningEnabled,
                                morningTime = morningTime,
                                habitReminder = habitEnabled,
                                habitTime = habitTime,
                                eveningReminder = enabled,
                                eveningTime = eveningTime
                            )
                        },
                        onTimeChange = { newTime ->
                            viewModel.updateProfile(
                                name = name,
                                selectedGoals = goalsList,
                                appearanceMode = currentMode,
                                morningReminder = morningEnabled,
                                morningTime = morningTime,
                                habitReminder = habitEnabled,
                                habitTime = habitTime,
                                eveningReminder = eveningEnabled,
                                eveningTime = newTime
                            )
                        },
                        tag = "evening"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GoldOutlinedButton(
                        text = "Trigger Test Notification",
                        icon = Icons.Default.NotificationsActive,
                        onClick = {
                            viewModel.triggerTestNotification()
                            Toast.makeText(context, "Test notification dispatched!", Toast.LENGTH_SHORT).show()
                        },
                        testTag = "test_notification_btn"
                    )
                }
            }
        }

        // Danger Zone / Resets
        item {
            SectionHeader(title = "Data Management")
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Reset Today's progress
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showResetTodayDialog = true }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .testTag("reset_today_btn"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Reset Today's Check-ins", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text("Clear all habit completions checked off today", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkBorder))

                    // Reset All Data
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showResetAllDialog = true }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .testTag("reset_all_btn"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Reset All Data & Progress", style = MaterialTheme.typography.titleMedium, color = Color(0xFFF43F5E))
                            Text("Permanently delete custom goals, habits, and streak history", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // About Motivator Button
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showAboutDialog = true }
                    .testTag("about_motivator_btn"),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "About Motivator App",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
        var newNameInput by remember { mutableStateOf(name) }
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            containerColor = DarkSurface,
            title = { Text("Edit First Name", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newNameInput,
                    onValueChange = { newNameInput = it },
                    singleLine = true,
                    label = { Text("First Name", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("edit_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            },
            confirmButton = {
                GoldButton(
                    text = "Save",
                    enabled = newNameInput.isNotBlank(),
                    onClick = {
                        viewModel.updateProfile(
                            name = newNameInput.trim(),
                            selectedGoals = goalsList,
                            appearanceMode = currentMode,
                            morningReminder = morningEnabled,
                            morningTime = morningTime,
                            habitReminder = habitEnabled,
                            habitTime = habitTime,
                            eveningReminder = eveningEnabled,
                            eveningTime = eveningTime
                        )
                        showEditNameDialog = false
                    },
                    modifier = Modifier.width(100.dp),
                    testTag = "save_name_btn"
                )
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Reset Today Dialog
    if (showResetTodayDialog) {
        AlertDialog(
            onDismissRequest = { showResetTodayDialog = false },
            containerColor = DarkSurface,
            title = { Text("Reset Today's Progress?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This will uncheck all habits marked completed today and reset today's completion rate to 0%.", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetTodayProgress()
                        showResetTodayDialog = false
                        Toast.makeText(context, "Today's check-ins reset.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("confirm_reset_today")
                ) {
                    Text("Reset Today", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetTodayDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Reset All Dialog
    if (showResetAllDialog) {
        AlertDialog(
            onDismissRequest = { showResetAllDialog = false },
            containerColor = DarkSurface,
            title = { Text("Reset All Data?", color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold) },
            text = { Text("This will erase all your custom goals, habits, completion logs, and streak counters. This cannot be undone.", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllData()
                        showResetAllDialog = false
                        Toast.makeText(context, "All data reset.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("confirm_reset_all")
                ) {
                    Text("Reset Everything", color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetAllDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MotivatorLogoEmblem(size = 32.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Motivator v1.0", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "“Better Habits. Bigger Dreams. Real Success.”",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldLight,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Motivator is your private, offline-first personal motivation, goal-setting, and habit-building sanctuary. Built with Room database local persistence, Jetpack Compose, and premium craftsmanship.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Got It", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun ReminderSettingRow(
    title: String,
    time: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onTimeChange: (String) -> Unit,
    tag: String
) {
    var editingTime by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { editingTime = true }
                    .padding(vertical = 2.dp)
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Scheduled at $time (tap to edit)",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldLight
                )
            }
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF140F00),
                checkedTrackColor = GoldPrimary,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = DarkSurfaceElevated
            ),
            modifier = Modifier.testTag("switch_$tag")
        )
    }

    if (editingTime) {
        var inputTime by remember { mutableStateOf(time) }
        AlertDialog(
            onDismissRequest = { editingTime = false },
            containerColor = DarkSurface,
            title = { Text("Set $title Time", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = inputTime,
                    onValueChange = { inputTime = it },
                    label = { Text("Time (24h format, e.g. 08:00)", color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_time_$tag"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (inputTime.isNotBlank()) {
                            onTimeChange(inputTime.trim())
                        }
                        editingTime = false
                    }
                ) {
                    Text("Save", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTime = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
