package ir.taravatgroup.aramesh.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_exercises")
data class BreathingExercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uniqueKey: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val inhaleSeconds: Int,
    val holdInSeconds: Int,
    val exhaleSeconds: Int,
    val holdOutSeconds: Int,
    val defaultCycles: Int = 5,
    val isCustom: Boolean = false,
    val category: String = "آرامش",
    val accentTag: String = "محبوب"
) {
    val cycleDurationSeconds: Int
        get() = inhaleSeconds + holdInSeconds + exhaleSeconds + holdOutSeconds

    val totalDurationSeconds: Int
        get() = cycleDurationSeconds * defaultCycles

    fun getPatternString(): String {
        return "$inhaleSeconds - $holdInSeconds - $exhaleSeconds - $holdOutSeconds"
    }

    companion object {
        val PRESETS = listOf(
            BreathingExercise(
                id = -1,
                uniqueKey = "preset_4_7_8",
                title = "تنفس ۴-۷-۸ آرامش عمیق",
                subtitle = "آرام‌بخش قوی برای خواب و کاهش استرس",
                description = "تکنیک معروف دکتر ویل برای فعال‌سازی سیستم پاراسمپاتیک و آرامش ذهن قبل از خواب.",
                inhaleSeconds = 4,
                holdInSeconds = 7,
                exhaleSeconds = 8,
                holdOutSeconds = 0,
                defaultCycles = 4,
                isCustom = false,
                category = "خواب و آرامش",
                accentTag = "۴-۷-۸"
            ),
            BreathingExercise(
                id = -2,
                uniqueKey = "preset_box_4_4_4_4",
                title = "تنفس جعبه‌ای ۴-۴-۴-۴",
                subtitle = "تقویت تمرکز و شفافیت ذهن",
                description = "مورد استفاده نیروهای ویژه و ورزشکاران برای بازگشت سریع تمرکز، تعادل و کنترل هیجانات.",
                inhaleSeconds = 4,
                holdInSeconds = 4,
                exhaleSeconds = 4,
                holdOutSeconds = 4,
                defaultCycles = 5,
                isCustom = false,
                category = "تمرکز و کارایی",
                accentTag = "جعبه‌ای"
            ),
            BreathingExercise(
                id = -3,
                uniqueKey = "preset_flow_5_5_5_5",
                title = "جریان هماهنگ ۵-۵-۵-۵",
                subtitle = "تعادل اعصاب و تنفس عمیق شکمی",
                description = "الگوی متقارن ۵ ثانیه‌ای برای باز کردن مجاری تنفسی و هماهنگی کامل ضربان قلب.",
                inhaleSeconds = 5,
                holdInSeconds = 5,
                exhaleSeconds = 5,
                holdOutSeconds = 5,
                defaultCycles = 6,
                isCustom = false,
                category = "تعادل بدن",
                accentTag = "متعادل"
            ),
            BreathingExercise(
                id = -4,
                uniqueKey = "preset_awake_4_2_4_2",
                title = "تنفس بیداری و انرژی ۴-۲-۴-۲",
                subtitle = "افزایش اکسیژن‌رسانی و شادابی صبحگاهی",
                description = "الگوی فعال و ریتمیک پرانایاما جهت بازیابی نشاط، شروع پرانرژی روز و رفع خستگی.",
                inhaleSeconds = 4,
                holdInSeconds = 2,
                exhaleSeconds = 4,
                holdOutSeconds = 2,
                defaultCycles = 8,
                isCustom = false,
                category = "انرژی و نشاط",
                accentTag = "صبحگاهی"
            ),
            BreathingExercise(
                id = -5,
                uniqueKey = "preset_coherence_6_0_6_0",
                title = "هماهنگی قلب و عروق ۶-۰-۶-۰",
                subtitle = "تنفس تشدید قلبی (Resonance Breathing)",
                description = "تنفس آهسته و پیوسته بدون وقفه برای بیشینه‌سازی تغییرپذیری ضربان قلب (HRV).",
                inhaleSeconds = 6,
                holdInSeconds = 0,
                exhaleSeconds = 6,
                holdOutSeconds = 0,
                defaultCycles = 5,
                isCustom = false,
                category = "سلامت قلب",
                accentTag = "HRV"
            )
        )
    }
}
