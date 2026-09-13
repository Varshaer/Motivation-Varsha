package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GoldButton
import com.example.ui.components.MotivatorLogoEmblem
import com.example.ui.theme.CategoryCareer
import com.example.ui.theme.CategoryGrowth
import com.example.ui.theme.CategoryHealth
import com.example.ui.theme.CategoryMoney
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(
    onComplete: (name: String, goals: List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    var userName by remember { mutableStateOf("") }
    val selectedGoals = remember { mutableStateListOf("Career", "Health", "Personal Growth") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        when (step) {
            0 -> WelcomeStep(
                onGetStarted = { step = 1 }
            )
            1 -> NameInputStep(
                name = userName,
                onNameChange = { userName = it },
                onContinue = { step = 2 }
            )
            2 -> GoalSelectionStep(
                selectedGoals = selectedGoals,
                onToggleGoal = { goal ->
                    if (selectedGoals.contains(goal)) {
                        if (selectedGoals.size > 1) selectedGoals.remove(goal)
                    } else {
                        selectedGoals.add(goal)
                    }
                },
                onFinish = {
                    onComplete(userName, selectedGoals.toList())
                }
            )
        }
    }
}

@Composable
private fun WelcomeStep(
    onGetStarted: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.8f))

        MotivatorLogoEmblem(size = 96.dp)

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "MOTIVATOR",
            style = MaterialTheme.typography.labelLarge,
            color = GoldPrimary,
            letterSpacing = 4.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Better Habits.\nBigger Dreams.\nReal Success.",
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 42.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your daily motivation, goals and progress — all in one place.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        GoldButton(
            text = "Get Started",
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            onClick = onGetStarted,
            testTag = "onboarding_get_started"
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun NameInputStep(
    name: String,
    onNameChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "STEP 1 OF 2",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "What is your\nfirst name?",
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "We will personalize your daily dashboard, streaks, and motivation quotes.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(36.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("name_input"),
                placeholder = { Text("Enter your name...", color = TextMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { if (name.isNotBlank()) onContinue() }
                ),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                )
            )
        }

        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            GoldButton(
                text = "Continue",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = onContinue,
                enabled = name.isNotBlank(),
                testTag = "name_continue_button"
            )
        }
    }
}

data class GoalCategoryItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
private fun GoalSelectionStep(
    selectedGoals: List<String>,
    onToggleGoal: (String) -> Unit,
    onFinish: () -> Unit
) {
    val goalOptions = listOf(
        GoalCategoryItem("Career", "Level up skills, deep focus, productivity", Icons.Default.Work, CategoryCareer),
        GoalCategoryItem("Money", "Financial discipline, wealth growth, budgeting", Icons.Default.MonetizationOn, CategoryMoney),
        GoalCategoryItem("Health", "Daily fitness, workouts, sleep, nutrition", Icons.Default.FitnessCenter, CategoryHealth),
        GoalCategoryItem("Personal Growth", "Mindset, reading, discipline, self-mastery", Icons.Default.Psychology, CategoryGrowth)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "STEP 2 OF 2",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose your\nmain focus goals",
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Select all areas you want to crush. We will tailor your initial habits and daily quotes.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            goalOptions.forEach { item ->
                val isSelected = selectedGoals.contains(item.title)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onToggleGoal(item.title) }
                        .testTag("goal_chip_${item.title.lowercase()}"),
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSelected) DarkSurfaceElevated else DarkSurface,
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) GoldPrimary else DarkBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(item.color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = item.color,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) GoldPrimary else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) GoldPrimary else DarkBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF140F00),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            GoldButton(
                text = "Enter Motivator",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = onFinish,
                enabled = selectedGoals.isNotEmpty(),
                testTag = "finish_onboarding_button"
            )
        }
    }
}
