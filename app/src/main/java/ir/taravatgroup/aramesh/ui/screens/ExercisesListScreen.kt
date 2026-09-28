package ir.taravatgroup.aramesh.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.taravatgroup.aramesh.data.model.BreathingExercise
import ir.taravatgroup.aramesh.ui.theme.AmberDark
import ir.taravatgroup.aramesh.ui.theme.AmberGlow
import ir.taravatgroup.aramesh.ui.theme.AmberOrange
import ir.taravatgroup.aramesh.ui.theme.AmberPrimary
import ir.taravatgroup.aramesh.ui.theme.DarkBackground
import ir.taravatgroup.aramesh.ui.theme.DarkSurfaceBorder
import ir.taravatgroup.aramesh.ui.theme.DarkSurfaceElevated
import ir.taravatgroup.aramesh.ui.theme.FireOrange
import ir.taravatgroup.aramesh.ui.theme.TextMuted
import ir.taravatgroup.aramesh.ui.theme.TextPrimary
import ir.taravatgroup.aramesh.ui.theme.TextSecondary
import ir.taravatgroup.aramesh.util.toPersianDigits

@Composable
fun ExercisesListScreen(
    currentSelected: BreathingExercise,
    customExercises: List<BreathingExercise>,
    onSelectExercise: (BreathingExercise) -> Unit,
    onSaveCustomExercise: (BreathingExercise) -> Unit,
    onDeleteCustomExercise: (BreathingExercise) -> Unit,
    onNavigateToSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateSheet = true },
                containerColor = AmberPrimary,
                contentColor = Color(0xFF221100),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("create_custom_exercise_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "افزودن تمرین")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "الگوی جدید",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "الگوهای تمرین تنفس",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "تکنیک‌های علمی تنفس و الگوهای شخصی‌سازی شده",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(AmberPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = AmberGlow,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            // Custom Exercises Section (if any exist)
            if (customExercises.isNotEmpty()) {
                item {
                    Text(
                        text = "الگوهای اختصاصی شما (${customExercises.size.toPersianDigits()})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmberGlow,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(customExercises, key = { "custom_${it.id}" }) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        isSelected = exercise.uniqueKey == currentSelected.uniqueKey,
                        onSelect = {
                            onSelectExercise(exercise)
                            onNavigateToSession()
                        },
                        onDelete = { onDeleteCustomExercise(exercise) }
                    )
                }
            }

            // Standard Preset Exercises Section
            item {
                Text(
                    text = "الگوهای استاندارد و پرکاربرد",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            items(BreathingExercise.PRESETS, key = { it.uniqueKey }) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    isSelected = exercise.uniqueKey == currentSelected.uniqueKey,
                    onSelect = {
                        onSelectExercise(exercise)
                        onNavigateToSession()
                    },
                    onDelete = null
                )
            }

            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }

    if (showCreateSheet) {
        CreateCustomExerciseSheet(
            onDismiss = { showCreateSheet = false },
            onSave = { exercise ->
                showCreateSheet = false
                onSaveCustomExercise(exercise)
                onNavigateToSession()
            }
        )
    }
}

@Composable
private fun ExerciseCard(
    exercise: BreathingExercise,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: (() -> Unit)?
) {
    val borderColor = if (isSelected) AmberPrimary else DarkSurfaceBorder

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(DarkSurfaceElevated)
            .border(if (isSelected) 1.8.dp else 1.dp, borderColor, RoundedCornerShape(22.dp))
            .clickable { onSelect() }
            .padding(18.dp)
            .testTag("exercise_card_${exercise.uniqueKey}")
    ) {
        Column {
            // Header Row: Title + Tag + Delete (if custom)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AmberDark.copy(alpha = 0.35f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = exercise.accentTag,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = AmberGlow
                        )
                    }
                }

                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف الگوی اختصاصی",
                            tint = FireOrange.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = exercise.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AmberPrimary.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4-Phase Stepper Pills (دم، حبس، بازدم، حبس)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PhasePill(label = "دم", seconds = exercise.inhaleSeconds, modifier = Modifier.weight(1f))
                PhasePill(label = "حبس", seconds = exercise.holdInSeconds, modifier = Modifier.weight(1f))
                PhasePill(label = "بازدم", seconds = exercise.exhaleSeconds, modifier = Modifier.weight(1f))
                PhasePill(label = "حبس", seconds = exercise.holdOutSeconds, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Actions: Duration + Select Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val totalMins = (exercise.totalDurationSeconds / 60).coerceAtLeast(1)
                Text(
                    text = "${exercise.defaultCycles.toPersianDigits()} دور • حدود ${totalMins.toPersianDigits()} دقیقه",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )

                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) AmberPrimary else DarkBackground,
                        contentColor = if (isSelected) Color(0xFF221100) else AmberGlow
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, AmberDark.copy(alpha = 0.5f)) else null
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "انتخاب شده", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "انتخاب و تمرین", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PhasePill(
    label: String,
    seconds: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkBackground)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = TextMuted
            )
            Text(
                text = "${seconds.toPersianDigits()} ث",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (seconds > 0) AmberGlow else TextMuted
            )
        }
    }
}
