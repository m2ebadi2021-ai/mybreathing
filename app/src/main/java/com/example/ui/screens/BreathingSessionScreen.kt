package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BreathingPhase
import com.example.ui.components.BreathingWaveCanvas
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.FireOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BreathSessionUiState
import com.example.ui.viewmodel.BreathworkViewModel
import com.example.util.toPersianDigits

@Composable
fun BreathingSessionScreen(
    uiState: BreathSessionUiState,
    viewModel: BreathworkViewModel,
    onNavigateToExercises: () -> Unit,
    modifier: Modifier = Modifier
) {
    val phaseColor by animateColorAsState(
        targetValue = when (uiState.currentPhase) {
            BreathingPhase.INHALE -> AmberPrimary
            BreathingPhase.HOLD_IN -> AmberGlow
            BreathingPhase.EXHALE -> AmberOrange
            BreathingPhase.HOLD_OUT -> AmberDark
            BreathingPhase.PREPARE -> AmberPrimary.copy(alpha = 0.85f)
        },
        animationSpec = tween(durationMillis = 400),
        label = "phaseColor"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Header: App Branding + Sound & Haptic Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "تنفس و مراقبه آرامش",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "هماهنگی نبض، تنفس و ذهن آگاه",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sound Toggle Button
                IconButton(
                    onClick = { viewModel.toggleSound() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (uiState.isSoundEnabled) AmberPrimary.copy(alpha = 0.18f) else DarkSurfaceElevated)
                        .border(1.dp, if (uiState.isSoundEnabled) AmberPrimary.copy(alpha = 0.45f) else DarkSurfaceBorder, CircleShape)
                        .testTag("sound_toggle_button")
                ) {
                    Icon(
                        imageVector = if (uiState.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "تنظیم صدا",
                        tint = if (uiState.isSoundEnabled) AmberGlow else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Haptic Toggle Button
                IconButton(
                    onClick = { viewModel.toggleHaptics() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (uiState.isHapticsEnabled) AmberPrimary.copy(alpha = 0.18f) else DarkSurfaceElevated)
                        .border(1.dp, if (uiState.isHapticsEnabled) AmberPrimary.copy(alpha = 0.45f) else DarkSurfaceBorder, CircleShape)
                        .testTag("haptic_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = "تنظیم لرزش",
                        tint = if (uiState.isHapticsEnabled) AmberGlow else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selected Exercise Banner Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(20.dp))
                .clickable { onNavigateToExercises() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .testTag("selected_exercise_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = uiState.selectedExercise.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AmberDark.copy(alpha = 0.4f))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = uiState.selectedExercise.accentTag,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = AmberGlow
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "الگو: ${uiState.selectedExercise.getPatternString().toPersianDigits()} ثانیه • دور ${uiState.currentCycle.toPersianDigits()} از ${uiState.totalCycles.toPersianDigits()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Text(
                    text = "تغییر >",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TOTAL DURATION METRICS BAR (زمان کلی تمرین)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceElevated.copy(alpha = 0.7f))
                .border(1.dp, DarkSurfaceBorder.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Total Duration
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = AmberGlow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "کل تمرین",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = uiState.getFormattedTotal().toPersianDigits(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            // Elapsed Time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = null,
                    tint = AmberPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "سپری‌شده",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = uiState.getFormattedElapsed().toPersianDigits(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmberPrimary
                    )
                }
            }

            // Remaining Time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.HourglassBottom,
                    contentDescription = null,
                    tint = AmberOrange,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "باقی‌مانده",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = uiState.getFormattedRemaining().toPersianDigits(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmberGlow
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Phase Title & Guidance
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (uiState.isSessionRunning) {
                    when (uiState.currentPhase) {
                        BreathingPhase.INHALE -> "دم (ورود هوا - صعود)"
                        BreathingPhase.HOLD_IN -> "حبس نفس (سکون بالا - افقی)"
                        BreathingPhase.EXHALE -> "بازدم (خروج هوا - فرود)"
                        BreathingPhase.HOLD_OUT -> "حبس بازدم (سکون پایین - افقی)"
                        BreathingPhase.PREPARE -> "آماده‌باش..."
                    }
                } else "آماده برای شروع",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = phaseColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (uiState.isSessionRunning) uiState.currentPhase.subtitlePersian else "روی منحنی تنفس متمرکز شده و دکمه شروع را لمس کنید",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Centerpiece: The Continuous Breathing Wave Canvas with Trajectory
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            DarkSurfaceElevated.copy(alpha = 0.85f),
                            DarkBackground
                        )
                    )
                )
                .border(1.2.dp, DarkSurfaceBorder, RoundedCornerShape(26.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Continuous Breathing Wave (Inhale Up, Hold Flat, Exhale Down, Hold Out Flat)
            BreathingWaveCanvas(
                currentPhase = uiState.currentPhase,
                phaseProgress = uiState.phaseProgress,
                modifier = Modifier.fillMaxSize()
            )

            // Central Floating Countdown Timer & Cycle Counter Overlay
            if (uiState.isSessionRunning) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 50.dp)
                ) {
                    Text(
                        text = uiState.phaseTimeRemainingSec.toPersianDigits(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 54.sp
                    )

                    Text(
                        text = "ثانیه",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberGlow
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkBackground.copy(alpha = 0.75f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "دور ${uiState.currentCycle.toPersianDigits()} از ${uiState.totalCycles.toPersianDigits()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = AmberGlow.copy(alpha = 0.8f),
                        modifier = Modifier.size(46.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "منحنی تنفسی پیوسته",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "دم: بالا • حبس: افقی • بازدم: پایین",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberPrimary
                    )
                }
            }

            // Overall Session Progress Bar at Bottom of Canvas
            if (uiState.isSessionRunning && uiState.sessionTotalSeconds > 0) {
                val overallProgress = (uiState.sessionElapsedSeconds.toFloat() / uiState.sessionTotalSeconds.toFloat()).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { overallProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AmberPrimary,
                        trackColor = DarkSurfaceBorder
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!uiState.isSessionRunning) {
                // Large Start Button
                Button(
                    onClick = { viewModel.startSession() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("start_session_button"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberPrimary,
                        contentColor = Color(0xFF221100)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "شروع تمرین تنفس",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            } else {
                // Running Controls: Pause / Resume + Stop
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stop Button
                    Button(
                        onClick = { viewModel.stopSession(saveIfPracticed = true) },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("stop_session_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurfaceElevated,
                            contentColor = TextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            tint = FireOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "پایان",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Pause / Resume Button
                    Button(
                        onClick = {
                            if (uiState.isPaused) viewModel.resumeSession() else viewModel.pauseSession()
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(54.dp)
                            .testTag("pause_resume_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = Color(0xFF221100)
                        )
                    ) {
                        Icon(
                            imageVector = if (uiState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isPaused) "ادامه تنفس" else "توقف موقت",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Session Completed Dialog
    if (uiState.sessionJustCompleted) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissSessionCompletedDialog() },
            containerColor = DarkSurfaceElevated,
            icon = {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(AmberPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AmberGlow,
                        modifier = Modifier.size(34.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "آفرین! تمرین به پایان رسید",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "یک گام دیگر به سوی آرامش ذهن و سلامت عمیق برداشتید. این تمرین در تقویم شمسی شما ثبت شد.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkBackground)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${uiState.totalCycles.toPersianDigits()}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AmberGlow
                            )
                            Text(
                                text = "دور کامل",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val mins = (uiState.sessionElapsedSeconds / 60).coerceAtLeast(1)
                            Text(
                                text = "${mins.toPersianDigits()} دقیقه",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AmberPrimary
                            )
                            Text(
                                text = "مدت زمان",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissSessionCompletedDialog() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberPrimary,
                        contentColor = Color(0xFF221100)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "عالی، بازگشت به خانه", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
