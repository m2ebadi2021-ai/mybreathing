package com.example.util

import java.util.Calendar
import java.util.TimeZone

data class JalaliDate(
    val year: Int,
    val month: Int, // 1..12
    val day: Int,   // 1..31
    val dayOfWeek: Int = 0 // 0=Saturday (شنبه), ..., 6=Friday (جمعه)
) {
    val monthName: String
        get() = JalaliCalendarHelper.MONTH_NAMES.getOrElse(month - 1) { "" }

    val dayOfWeekName: String
        get() = JalaliCalendarHelper.WEEKDAY_NAMES.getOrElse(dayOfWeek) { "" }

    val formattedShort: String
        get() = String.format("%04d/%02d/%02d", year, month, day)

    val monthKey: String
        get() = String.format("%04d/%02d", year, month)

    fun toPersianFormatted(): String {
        return "${day.toPersianDigits()} $monthName ${year.toPersianDigits()}"
    }

    fun toFullPersianFormatted(): String {
        return "$dayOfWeekName، ${day.toPersianDigits()} $monthName ${year.toPersianDigits()}"
    }
}

object JalaliCalendarHelper {
    val MONTH_NAMES = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    val WEEKDAY_NAMES = listOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه"
    )

    val WEEKDAY_SHORT = listOf(
        "ش", "ی", "د", "س", "چ", "پ", "ج"
    )

    val tehranTimeZone: TimeZone = TimeZone.getTimeZone("Asia/Tehran")

    /**
     * Converts a timestamp in milliseconds to JalaliDate using Iran standard time
     */
    fun fromTimestamp(millis: Long): JalaliDate {
        val cal = Calendar.getInstance(tehranTimeZone)
        cal.timeInMillis = millis
        val gYear = cal.get(Calendar.YEAR)
        val gMonth = cal.get(Calendar.MONTH) + 1
        val gDay = cal.get(Calendar.DAY_OF_MONTH)
        val gDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sunday, 7=Saturday

        // Convert Gregorian day of week (1=Sun..7=Sat) to Persian (0=Sat, 1=Sun..6=Fri)
        val pDayOfWeek = when (gDayOfWeek) {
            Calendar.SATURDAY -> 0
            Calendar.SUNDAY -> 1
            Calendar.MONDAY -> 2
            Calendar.TUESDAY -> 3
            Calendar.WEDNESDAY -> 4
            Calendar.THURSDAY -> 5
            Calendar.FRIDAY -> 6
            else -> 0
        }

        val (jYear, jMonth, jDay) = gregorianToJalali(gYear, gMonth, gDay)
        return JalaliDate(jYear, jMonth, jDay, pDayOfWeek)
    }

    fun now(): JalaliDate = fromTimestamp(System.currentTimeMillis())

    /**
     * Checks if a Jalali year is a leap year (Kabiseh)
     */
    fun isJalaliLeapYear(year: Int): Boolean {
        val breaks = listOf(
            -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210,
            1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178
        )
        var jp = breaks[0]
        var jump = 0
        for (j in 1 until breaks.size) {
            val jm = breaks[j]
            jump = jm - jp
            if (year < jm) break
            jp = jm
        }
        var n = year - jp
        if (jump - n < 6) n = n - jump + (jump + 4) / 33 * 33
        var leap = -1
        if (n >= 0) {
            var mod = (n + 1) % 33
            if (mod == 1 || mod == 5 || mod == 9 || mod == 13 || mod == 17 || mod == 22 || mod == 26 || mod == 30) {
                leap = 1
            } else {
                leap = 0
            }
        }
        return leap == 1
    }

    /**
     * Returns the number of days in a given Jalali month (1..12)
     */
    fun getDaysInMonth(year: Int, month: Int): Int {
        return when {
            month in 1..6 -> 31
            month in 7..11 -> 30
            month == 12 -> if (isJalaliLeapYear(year)) 30 else 29
            else -> 30
        }
    }

    /**
     * Finds Persian day of week (0=Sat..6=Fri) for the first day of the given Jalali year and month
     */
    fun getFirstDayOfWeek(year: Int, month: Int): Int {
        val (gY, gM, gD) = jalaliToGregorian(year, month, 1)
        val cal = Calendar.getInstance(tehranTimeZone)
        cal.set(gY, gM - 1, gD)
        val gDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        return when (gDayOfWeek) {
            Calendar.SATURDAY -> 0
            Calendar.SUNDAY -> 1
            Calendar.MONDAY -> 2
            Calendar.TUESDAY -> 3
            Calendar.WEDNESDAY -> 4
            Calendar.THURSDAY -> 5
            Calendar.FRIDAY -> 6
            else -> 0
        }
    }

    /**
     * Gregorian to Jalali conversion algorithm
     */
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gy2 = gy
        if (gy2 % 4 == 0 && (gy2 % 100 != 0 || gy2 % 400 == 0)) {
            gDaysInMonth[2] = 29
        }

        var totalDays = 0
        for (i in 1 until gm) {
            totalDays += gDaysInMonth[i]
        }
        totalDays += gd

        var jy: Int
        var jm: Int
        var jd: Int

        if (totalDays > 79) {
            val daysAfterNowruz = totalDays - 79
            jy = gy2 - 621
            if (daysAfterNowruz <= 186) {
                jm = 1 + (daysAfterNowruz - 1) / 31
                jd = 1 + (daysAfterNowruz - 1) % 31
            } else {
                val rem = daysAfterNowruz - 186
                jm = 7 + (rem - 1) / 30
                jd = 1 + (rem - 1) % 30
            }
        } else {
            jy = gy2 - 622
            val isPrevLeap = (gy2 - 1) % 4 == 0 && ((gy2 - 1) % 100 != 0 || (gy2 - 1) % 400 == 0)
            val daysInPrevGregorianYear = if (isPrevLeap) 366 else 365
            val daysFromFarvardinPrevYear = daysInPrevGregorianYear - 79 + totalDays
            if (daysFromFarvardinPrevYear <= 186) {
                jm = 1 + (daysFromFarvardinPrevYear - 1) / 31
                jd = 1 + (daysFromFarvardinPrevYear - 1) % 31
            } else {
                val rem = daysFromFarvardinPrevYear - 186
                jm = 7 + (rem - 1) / 30
                jd = 1 + (rem - 1) % 30
            }
        }
        return Triple(jy, jm, jd)
    }

    /**
     * Jalali to Gregorian conversion algorithm
     */
    fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
        var gy: Int
        val daysPassedInJalali = if (jm <= 6) {
            (jm - 1) * 31 + jd
        } else {
            6 * 31 + (jm - 7) * 30 + jd
        }

        if (daysPassedInJalali <= 286) {
            gy = jy + 621
            val dayOfYear = daysPassedInJalali + 79
            val (gm, gd) = getGregorianMonthAndDay(gy, dayOfYear)
            return Triple(gy, gm, gd)
        } else {
            gy = jy + 622
            val isLeap = gy % 4 == 0 && (gy % 100 != 0 || gy % 400 == 0)
            val totalDaysPrev = if (isLeap) 366 else 365
            val dayOfYear = daysPassedInJalali - (totalDaysPrev - 79)
            val (gm, gd) = getGregorianMonthAndDay(gy, dayOfYear)
            return Triple(gy, gm, gd)
        }
    }

    private fun getGregorianMonthAndDay(gy: Int, dayOfYear: Int): Pair<Int, Int> {
        val days = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        if (gy % 4 == 0 && (gy % 100 != 0 || gy % 400 == 0)) {
            days[2] = 29
        }
        var rem = dayOfYear
        var gm = 1
        while (gm <= 12 && rem > days[gm]) {
            rem -= days[gm]
            gm++
        }
        return Pair(gm.coerceIn(1, 12), rem.coerceIn(1, 31))
    }

    /**
     * Calculates consecutive daily practice streak
     */
    fun calculateStreak(distinctJalaliDates: Set<String>): Int {
        if (distinctJalaliDates.isEmpty()) return 0
        val today = now()
        val todayStr = today.formattedShort

        // Check if today was practiced; if not, check yesterday
        var currentCheck = today
        var streak = 0

        val practicedToday = distinctJalaliDates.contains(todayStr)
        if (!practicedToday) {
            // Check if practiced yesterday to preserve streak
            currentCheck = getPreviousDay(today)
            if (!distinctJalaliDates.contains(currentCheck.formattedShort)) {
                return 0
            }
        }

        while (distinctJalaliDates.contains(currentCheck.formattedShort)) {
            streak++
            currentCheck = getPreviousDay(currentCheck)
        }

        return streak
    }

    private fun getPreviousDay(date: JalaliDate): JalaliDate {
        var y = date.year
        var m = date.month
        var d = date.day - 1
        var dow = (date.dayOfWeek + 6) % 7
        if (d < 1) {
            m -= 1
            if (m < 1) {
                m = 12
                y -= 1
            }
            d = getDaysInMonth(y, m)
        }
        return JalaliDate(y, m, d, dow)
    }
}

/**
 * Converts English digits 0-9 to Persian digits ۰-۹
 */
fun Any?.toPersianDigits(): String {
    if (this == null) return ""
    val str = this.toString()
    val builder = java.lang.StringBuilder()
    for (char in str) {
        val persianChar = when (char) {
            '0' -> '۰'
            '1' -> '۱'
            '2' -> '۲'
            '3' -> '۳'
            '4' -> '۴'
            '5' -> '۵'
            '6' -> '۶'
            '7' -> '۷'
            '8' -> '۸'
            '9' -> '۹'
            else -> char
        }
        builder.append(persianChar)
    }
    return builder.toString()
}
