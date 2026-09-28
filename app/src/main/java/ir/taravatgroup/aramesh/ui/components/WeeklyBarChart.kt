package ir.taravatgroup.aramesh.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.taravatgroup.aramesh.ui.theme.AmberDark
import ir.taravatgroup.aramesh.ui.theme.AmberGlow
import ir.taravatgroup.aramesh.ui.theme.AmberOrange
import ir.taravatgroup.aramesh.ui.theme.AmberPrimary
import ir.taravatgroup.aramesh.ui.theme.DarkSurfaceBorder
import ir.taravatgroup.aramesh.ui.theme.DarkSurfaceElevated
import ir.taravatgroup.aramesh.ui.theme.TextMuted
import ir.taravatgroup.aramesh.ui.theme.TextPrimary
import ir.taravatgroup.aramesh.ui.theme.TextSecondary
import ir.taravatgroup.aramesh.util.toPersianDigits

data class DailyPracticeStat(
    val dayName: String,
    val minutes: Int,
    val isToday: Boolean
)

@Composable
fun WeeklyBarChart(
    weeklyStats: List<DailyPracticeStat>,
    modifier: Modifier = Modifier
) {
    val maxMinutes = (weeklyStats.maxOfOrNull { it.minutes } ?: 15).coerceAtLeast(10)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "فعالیت هفتگی (دقیقه)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            val totalWeekMin = weeklyStats.sumOf { it.minutes }
            Text(
                text = "مجموع: ${totalWeekMin.toPersianDigits()} دقیقه",
                style = MaterialTheme.typography.labelSmall,
                color = AmberGlow
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            weeklyStats.forEach { stat ->
                val barFraction = if (maxMinutes > 0) {
                    (stat.minutes.toFloat() / maxMinutes.toFloat()).coerceIn(0.04f, 1f)
                } else 0.04f

                val animatedHeight by animateFloatAsState(
                    targetValue = barFraction,
                    animationSpec = tween(durationMillis = 800),
                    label = "barHeight"
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    if (stat.minutes > 0) {
                        Text(
                            text = stat.minutes.toPersianDigits(),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (stat.isToday) AmberGlow else TextSecondary,
                            fontWeight = if (stat.isToday) FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Box(
                        modifier = Modifier
                            .width(22.dp)
                            .fillMaxHeight(fraction = animatedHeight)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                            .background(
                                if (stat.minutes > 0) {
                                    Brush.verticalGradient(
                                        colors = if (stat.isToday) {
                                            listOf(AmberGlow, AmberOrange, AmberDark)
                                        } else {
                                            listOf(AmberPrimary.copy(alpha = 0.85f), AmberDark)
                                        }
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(
                                            DarkSurfaceBorder.copy(alpha = 0.6f),
                                            DarkSurfaceBorder.copy(alpha = 0.3f)
                                        )
                                    )
                                }
                            )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stat.dayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (stat.isToday) AmberPrimary else TextMuted,
                        fontWeight = if (stat.isToday) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
