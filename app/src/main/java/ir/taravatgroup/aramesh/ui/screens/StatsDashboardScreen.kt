package ir.taravatgroup.aramesh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.taravatgroup.aramesh.data.model.PracticeSession
import ir.taravatgroup.aramesh.ui.components.DailyPracticeStat
import ir.taravatgroup.aramesh.ui.components.PersianJalaliCalendarView
import ir.taravatgroup.aramesh.ui.components.StatCard
import ir.taravatgroup.aramesh.ui.components.WeeklyBarChart
import ir.taravatgroup.aramesh.ui.theme.AmberDark
import ir.taravatgroup.aramesh.ui.theme.AmberGlow
import ir.taravatgroup.aramesh.ui.theme.AmberOrange
import ir.taravatgroup.aramesh.ui.theme.AmberPrimary
import ir.taravatgroup.aramesh.ui.theme.DarkBackground
import ir.taravatgroup.aramesh.ui.theme.DarkSurfaceBorder
import ir.taravatgroup.aramesh.ui.theme.DarkSurfaceElevated
import ir.taravatgroup.aramesh.ui.theme.FireOrange
import ir.taravatgroup.aramesh.ui.theme.FireRed
import ir.taravatgroup.aramesh.ui.theme.TextMuted
import ir.taravatgroup.aramesh.ui.theme.TextPrimary
import ir.taravatgroup.aramesh.ui.theme.TextSecondary
import ir.taravatgroup.aramesh.util.JalaliCalendarHelper
import ir.taravatgroup.aramesh.util.JalaliDate
import ir.taravatgroup.aramesh.util.toPersianDigits

@Composable
fun StatsDashboardScreen(
    sessions: List<PracticeSession>,
    totalDurationSeconds: Int,
    totalSessionsCount: Int,
    streakDays: Int,
    weeklyStats: List<DailyPracticeStat>,
    modifier: Modifier = Modifier
) {
    var selectedDateInfo by remember { mutableStateOf<String?>(null) }

    val distinctDates = remember(sessions) {
        sessions.map { it.jalaliDate }.toSet()
    }

    val totalMinutes = totalDurationSeconds / 60
    val today = remember { JalaliCalendarHelper.now() }
    val practicedToday = distinctDates.contains(today.formattedShort)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            // Title
            Column {
                Text(
                    text = "آمار و پیشرفت تنفس",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = today.toFullPersianFormatted(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AmberGlow
                )
            }
        }

        // Streak Fire Card (مطابق طرح مرجع)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                DarkSurfaceElevated,
                                AmberDark.copy(alpha = 0.35f),
                                FireOrange.copy(alpha = 0.25f)
                            )
                        )
                    )
                    .border(
                        1.2.dp,
                        Brush.horizontalGradient(
                            listOf(
                                AmberPrimary.copy(alpha = 0.5f),
                                FireOrange.copy(alpha = 0.8f)
                            )
                        ),
                        RoundedCornerShape(26.dp)
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "آتش استمرار",
                                tint = FireOrange,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "زنجیره تنفس پیوسته",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = AmberGlow
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${streakDays.toPersianDigits()} روز پیاپی",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (practicedToday) "تمرین امروز شما با موفقیت ثبت شد!" else "با یک جلسه تنفس کوتاه امروز، زنجیره خود را حفظ کنید",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (practicedToday) AmberGlow else TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(FireOrange.copy(alpha = 0.18f))
                            .border(1.dp, FireOrange.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = 32.sp
                        )
                    }
                }
            }
        }

        // Metrics Grid (2 Cards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "کل زمان تمرین",
                    value = "$totalMinutes دقیقه",
                    subtitle = "تمرکز و آگاهی عمیق",
                    icon = Icons.Default.Timer,
                    iconTint = AmberPrimary,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "کل جلسات",
                    value = "$totalSessionsCount جلسه",
                    subtitle = if (practicedToday) "امروز فعال" else "امروز انجام نشده",
                    icon = if (practicedToday) Icons.Default.CheckCircle else Icons.Default.SelfImprovement,
                    iconTint = if (practicedToday) AmberGlow else TextSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Persian Jalali Calendar
        item {
            Column {
                Text(
                    text = "تقویم شمسی تمرینات",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                PersianJalaliCalendarView(
                    practicedDates = distinctDates,
                    onDateSelected = { date ->
                        val dateKey = date.formattedShort
                        val dateSessions = sessions.filter { it.jalaliDate == dateKey }
                        selectedDateInfo = if (dateSessions.isNotEmpty()) {
                            val count = dateSessions.size
                            val min = dateSessions.sumOf { it.durationSeconds } / 60
                            "در ${date.toPersianFormatted()}: ${count.toPersianDigits()} جلسه (${min.toPersianDigits()} دقیقه)"
                        } else {
                            "در ${date.toPersianFormatted()} تمرینی ثبت نشده است"
                        }
                    }
                )

                if (selectedDateInfo != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, AmberDark.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = selectedDateInfo!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = AmberGlow
                        )
                    }
                }
            }
        }

        // Weekly Bar Chart
        item {
            WeeklyBarChart(
                weeklyStats = weeklyStats
            )
        }

        // Recent Sessions Log
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تاریخچه جلسات اخیر",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (sessions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "هنوز جلسه‌ای انجام نشده است",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "اولین تمرین تنفس خود را از برگه تنفس شروع کنید",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(sessions.take(6), key = { it.id }) { session ->
                SessionHistoryItem(session = session)
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun SessionHistoryItem(session: PracticeSession) {
    val durationMin = (session.durationSeconds / 60).coerceAtLeast(1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(AmberPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AmberGlow,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = session.exerciseTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "تاریخ: ${session.jalaliDate.toPersianDigits()} • ${session.completedCycles.toPersianDigits()} دور کامل",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkBackground)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${durationMin.toPersianDigits()} دقیقه",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AmberGlow
                )
            }
        }
    }
}
