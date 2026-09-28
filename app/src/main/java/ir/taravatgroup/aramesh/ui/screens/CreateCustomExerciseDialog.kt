package ir.taravatgroup.aramesh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.taravatgroup.aramesh.data.model.BreathingExercise
import ir.taravatgroup.aramesh.ui.theme.AmberDark
import ir.taravatgroup.aramesh.ui.theme.AmberGlow
import ir.taravatgroup.aramesh.ui.theme.AmberOrange
import ir.taravatgroup.aramesh.ui.theme.AmberPrimary
import ir.taravatgroup.aramesh.ui.theme.DarkSurface
import ir.taravatgroup.aramesh.ui.theme.DarkSurfaceBorder
import ir.taravatgroup.aramesh.ui.theme.DarkSurfaceElevated
import ir.taravatgroup.aramesh.ui.theme.TextMuted
import ir.taravatgroup.aramesh.ui.theme.TextPrimary
import ir.taravatgroup.aramesh.ui.theme.TextSecondary
import ir.taravatgroup.aramesh.util.toPersianDigits

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomExerciseSheet(
    onDismiss: () -> Unit,
    onSave: (BreathingExercise) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf("تمرین من") }
    var subtitle by remember { mutableStateOf("الگوی تنفس سفارشی") }
    var inhaleSec by remember { mutableIntStateOf(4) }
    var holdInSec by remember { mutableIntStateOf(4) }
    var exhaleSec by remember { mutableIntStateOf(4) }
    var holdOutSec by remember { mutableIntStateOf(4) }
    var cycles by remember { mutableIntStateOf(5) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ساخت الگوی تنفسی جدید",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "تنظیم دقیق زمان هر مرحله از تنفس",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "بستن",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Exercise Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("نام تمرین") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberPrimary,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedLabelColor = AmberGlow,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("توضیح کوتاه یا هدف") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberPrimary,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedLabelColor = AmberGlow,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "زمان‌بندی ۴ مرحله تنفس (ثانیه)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AmberGlow
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Inhale Stepper
            DurationStepper(
                label = "۱. دم (ورود هوا)",
                sublabel = "مدت زمان دم عمیق از بینی",
                value = inhaleSec,
                min = 1,
                max = 30,
                onValueChange = { inhaleSec = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Hold after Inhale Stepper
            DurationStepper(
                label = "۲. حبس نفس (ریه پر)",
                sublabel = "نگهداری آرام هوا در سینه",
                value = holdInSec,
                min = 0,
                max = 30,
                onValueChange = { holdInSec = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Exhale Stepper
            DurationStepper(
                label = "۳. بازدم (خروج هوا)",
                sublabel = "خالی کردن تدریجی و آرام بازدم",
                value = exhaleSec,
                min = 1,
                max = 30,
                onValueChange = { exhaleSec = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Hold after Exhale Stepper
            DurationStepper(
                label = "۴. حبس بازدم (ریه خالی)",
                sublabel = "سکون پیش از دم بعدی",
                value = holdOutSec,
                min = 0,
                max = 30,
                onValueChange = { holdOutSec = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Cycles Stepper
            DurationStepper(
                label = "تعداد تکرار دوره (دور)",
                sublabel = "تعداد چرخه‌های کامل در هر جلسه",
                value = cycles,
                min = 1,
                max = 30,
                unit = "دور",
                onValueChange = { cycles = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Summary Pill
            val cycleSec = inhaleSec + holdInSec + exhaleSec + holdOutSec
            val totalSec = cycleSec * cycles
            val totalMin = totalSec / 60
            val totalRemSec = totalSec % 60

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, AmberDark.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "الگوی تنفس: ${inhaleSec.toPersianDigits()} - ${holdInSec.toPersianDigits()} - ${exhaleSec.toPersianDigits()} - ${holdOutSec.toPersianDigits()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "هر دوره: ${cycleSec.toPersianDigits()} ثانیه",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (totalMin > 0) "${totalMin.toPersianDigits()} دقیقه و ${totalRemSec.toPersianDigits()} ثانیه" else "${totalSec.toPersianDigits()} ثانیه",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AmberGlow
                        )
                        Text(
                            text = "کل زمان جلسه",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    val custom = BreathingExercise(
                        uniqueKey = "custom_${System.currentTimeMillis()}",
                        title = title.ifBlank { "الگوی اختصاصی" },
                        subtitle = subtitle.ifBlank { "الگوی تنفس سفارشی شما" },
                        description = "الگوی سفارشی ساخته شده با زمان‌های $inhaleSec - $holdInSec - $exhaleSec - $holdOutSec ثانیه.",
                        inhaleSeconds = inhaleSec,
                        holdInSeconds = holdInSec,
                        exhaleSeconds = exhaleSec,
                        holdOutSeconds = holdOutSec,
                        defaultCycles = cycles,
                        isCustom = true,
                        category = "سفارشی",
                        accentTag = "شخصی"
                    )
                    onSave(custom)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberPrimary,
                    contentColor = Color(0xFF241200)
                )
            ) {
                Text(
                    text = "ذخیره و انتخاب تمرین",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DurationStepper(
    label: String,
    sublabel: String,
    value: Int,
    min: Int,
    max: Int,
    unit: String = "ثانیه",
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = sublabel,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Minus Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (value > min) AmberDark.copy(alpha = 0.35f) else DarkSurfaceBorder.copy(alpha = 0.3f))
                    .clickable(enabled = value > min) { onValueChange(value - 1) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "کاهش",
                    tint = if (value > min) AmberGlow else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Value Display
            Text(
                text = "${value.toPersianDigits()} $unit",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = AmberPrimary,
                modifier = Modifier.width(64.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Plus Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (value < max) AmberPrimary.copy(alpha = 0.25f) else DarkSurfaceBorder.copy(alpha = 0.3f))
                    .clickable(enabled = value < max) { onValueChange(value + 1) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "افزایش",
                    tint = if (value < max) AmberGlow else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
