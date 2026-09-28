package ir.taravatgroup.aramesh.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.taravatgroup.aramesh.ui.theme.AmberDark
import ir.taravatgroup.aramesh.ui.theme.AmberGlow
import ir.taravatgroup.aramesh.ui.theme.AmberPrimary
import ir.taravatgroup.aramesh.ui.theme.DarkSurface
import ir.taravatgroup.aramesh.ui.theme.DarkSurfaceBorder
import ir.taravatgroup.aramesh.ui.theme.DarkSurfaceElevated
import ir.taravatgroup.aramesh.ui.theme.TextMuted
import ir.taravatgroup.aramesh.ui.theme.TextPrimary
import ir.taravatgroup.aramesh.ui.theme.TextSecondary
import ir.taravatgroup.aramesh.util.JalaliCalendarHelper
import ir.taravatgroup.aramesh.util.JalaliDate
import ir.taravatgroup.aramesh.util.toPersianDigits

@Composable
fun PersianJalaliCalendarView(
    practicedDates: Set<String>, // Dates in format "YYYY/MM/DD"
    onDateSelected: (JalaliDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = remember { JalaliCalendarHelper.now() }
    var displayedYear by remember { mutableStateOf(today.year) }
    var displayedMonth by remember { mutableStateOf(today.month) }

    val daysInMonth = remember(displayedYear, displayedMonth) {
        JalaliCalendarHelper.getDaysInMonth(displayedYear, displayedMonth)
    }

    val firstDayOfWeek = remember(displayedYear, displayedMonth) {
        JalaliCalendarHelper.getFirstDayOfWeek(displayedYear, displayedMonth)
    }

    val monthName = remember(displayedMonth) {
        JalaliCalendarHelper.MONTH_NAMES.getOrElse(displayedMonth - 1) { "" }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        // Month Header & Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Next month button (in RTL, next goes left or right depending on arrow)
            IconButton(
                onClick = {
                    if (displayedMonth == 12) {
                        displayedMonth = 1
                        displayedYear++
                    } else {
                        displayedMonth++
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "ماه بعد",
                    tint = AmberGlow
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$monthName ${displayedYear.toPersianDigits()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "امروز: ${today.toFullPersianFormatted()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberGlow,
                    fontWeight = FontWeight.Medium
                )
            }

            // Previous month button
            IconButton(
                onClick = {
                    if (displayedMonth == 1) {
                        displayedMonth = 12
                        displayedYear--
                    } else {
                        displayedMonth--
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "ماه قبل",
                    tint = AmberGlow
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Weekdays Header (شنبه تا جمعه)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val weekdays = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
            weekdays.forEachIndexed { index, name ->
                Text(
                    text = name,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (index == 6) AmberPrimary else TextMuted // Friday highlighted
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid
        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNum = cellIndex - firstDayOfWeek + 1

                    if (dayNum in 1..daysInMonth) {
                        val dateKey = String.format("%04d/%02d/%02d", displayedYear, displayedMonth, dayNum)
                        val isPracticed = practicedDates.contains(dateKey)
                        val isToday = (today.year == displayedYear && today.month == displayedMonth && today.day == dayNum)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when {
                                        isPracticed -> AmberDark.copy(alpha = 0.45f)
                                        isToday -> AmberPrimary.copy(alpha = 0.15f)
                                        else -> Color.Transparent
                                    }
                                )
                                .border(
                                    width = if (isToday) 1.5.dp else if (isPracticed) 1.dp else 0.dp,
                                    color = if (isToday) AmberPrimary else if (isPracticed) AmberGlow.copy(alpha = 0.6f) else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    val jDate = JalaliDate(
                                        year = displayedYear,
                                        month = displayedMonth,
                                        day = dayNum,
                                        dayOfWeek = (cellIndex % 7)
                                    )
                                    onDateSelected(jDate)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = dayNum.toPersianDigits(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isToday || isPracticed) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isToday -> AmberGlow
                                        isPracticed -> Color.White
                                        col == 6 -> AmberPrimary.copy(alpha = 0.7f) // Friday
                                        else -> TextSecondary
                                    }
                                )

                                if (isPracticed) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(AmberPrimary)
                                    )
                                }
                            }
                        }
                    } else {
                        // Empty slot
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(AmberPrimary)
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(
                text = "روزهای دارای تمرین",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.padding(horizontal = 12.dp))

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .border(1.dp, AmberPrimary, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(
                text = "امروز",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
