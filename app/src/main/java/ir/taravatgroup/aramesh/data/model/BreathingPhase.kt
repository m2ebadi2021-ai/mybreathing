package ir.taravatgroup.aramesh.data.model

enum class BreathingPhase(
    val titlePersian: String,
    val subtitlePersian: String,
    val instructionPersian: String
) {
    PREPARE(
        titlePersian = "آماده‌باش",
        subtitlePersian = "آرام بنشینید و تمرکز کنید",
        instructionPersian = "آماده‌سازی برای تنفس آگاهانه"
    ),
    INHALE(
        titlePersian = "دم (نفس بکشید)",
        subtitlePersian = "به آرامی از بینی هوا را به درون بکشید",
        instructionPersian = "جریان هوا را در ریه‌ها حس کنید"
    ),
    HOLD_IN(
        titlePersian = "حبس نفس",
        subtitlePersian = "هوا را در سینه نگه دارید",
        instructionPersian = "در آرامش و سکون بمانید"
    ),
    EXHALE(
        titlePersian = "بازدم (خالی کنید)",
        subtitlePersian = "به نرمی از دهان یا بینی بازدم کنید",
        instructionPersian = "تمام تنش‌ها را رها کنید"
    ),
    HOLD_OUT(
        titlePersian = "حبس بازدم",
        subtitlePersian = "ریه‌ها را خالی نگه دارید",
        instructionPersian = "سکون و آرامش عمیق"
    )
}
