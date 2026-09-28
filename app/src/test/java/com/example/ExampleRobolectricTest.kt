package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BreathingExercise
import com.example.util.JalaliCalendarHelper
import com.example.util.toPersianDigits
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("تنفس آرامش", appName)
    }

    @Test
    fun testPersianDigitsConversion() {
        assertEquals("۱۲۳۴۵۶۷۸۹۰", "1234567890".toPersianDigits())
        assertEquals("۴", 4.toPersianDigits())
    }

    @Test
    fun testJalaliDateConversion() {
        val now = JalaliCalendarHelper.now()
        assertTrue(now.year >= 1400)
        assertTrue(now.month in 1..12)
        assertTrue(now.day in 1..31)
    }

    @Test
    fun testTodayIsSixMehrFourteenZeroFive() {
        val now = JalaliCalendarHelper.now()
        assertEquals(1405, now.year)
        assertEquals(7, now.month) // Mehr
        assertEquals(6, now.day)   // 6
        assertEquals("دوشنبه", now.dayOfWeekName)
        assertEquals("دوشنبه، ۶ مهر ۱۴۰۵", now.toFullPersianFormatted())
    }

    @Test
    fun testBreathingExerciseCycleDuration() {
        val boxBreathing = BreathingExercise.PRESETS.first { it.uniqueKey == "preset_box_4_4_4_4" }
        assertEquals(16, boxBreathing.cycleDurationSeconds)
        assertEquals(16 * boxBreathing.defaultCycles, boxBreathing.totalDurationSeconds)
    }

    @Test
    fun testStreakCalculation() {
        val today = JalaliCalendarHelper.now()
        val dates = setOf(today.formattedShort)
        val streak = JalaliCalendarHelper.calculateStreak(dates)
        assertEquals(1, streak)
    }
}
