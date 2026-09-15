package com.example.domain.model

data class BadgeDefinition(
    val key: String,
    val titleFa: String,
    val titleEn: String,
    val descFa: String,
    val descEn: String,
    val iconEmoji: String,
    val category: String
)

object BadgeCatalog {
    val allBadges = listOf(
        BadgeDefinition(
            key = "FIRST_SEED",
            titleFa = "نخستین بذر",
            titleEn = "First Seed",
            descFa = "ثبت اولین سپاسگزاری در دفترچه قدردانی",
            descEn = "Log your first gratitude entry",
            iconEmoji = "🌱",
            category = "gratitude"
        ),
        BadgeDefinition(
            key = "BLOSSOM_BLOOM",
            titleFa = "شکوفه بهاری",
            titleEn = "Blossom Bloom",
            descFa = "رسیدن به اولین شکوفه (۳ سپاسگزاری)",
            descEn = "Reach your first blossom (3 gratitudes)",
            iconEmoji = "🌸",
            category = "gratitude"
        ),
        BadgeDefinition(
            key = "RED_HARVEST",
            titleFa = "میوه سرخ",
            titleEn = "Red Fruit Harvest",
            descFa = "رسیدن به اولین میوه سرخ (۹ سپاسگزاری)",
            descEn = "Earn your first red fruit (9 gratitudes)",
            iconEmoji = "🍎",
            category = "gratitude"
        ),
        BadgeDefinition(
            key = "GOLDEN_CROWN",
            titleFa = "میوه زرین",
            titleEn = "Golden Fruit Sovereign",
            descFa = "رویش باشکوه میوه زرین (۲۷ سپاسگزاری)",
            descEn = "Bloom a golden fruit (27 gratitudes)",
            iconEmoji = "✨",
            category = "gratitude"
        ),
        BadgeDefinition(
            key = "FIRST_CHECKIN",
            titleFa = "نگاهی به درون",
            titleEn = "First Check-in",
            descFa = "ثبت نخستین پایش روزانه حال و انرژی",
            descEn = "Complete your first daily check-in",
            iconEmoji = "🧭",
            category = "checkin"
        ),
        BadgeDefinition(
            key = "SEVEN_DAY_STREAK",
            titleFa = "زنجیره هفت‌روزه",
            titleEn = "7-Day Streak",
            descFa = "۷ روز پیوسته تمرین خودآگاهی و آرامش",
            descEn = "7 consecutive days of mindfulness habits",
            iconEmoji = "🔥",
            category = "streak"
        ),
        BadgeDefinition(
            key = "BREATH_ROOKIE",
            titleFa = "نفس آگاهانه",
            titleEn = "Conscious Breath",
            descFa = "انجام اولین تمرین تنفس عمیق",
            descEn = "Complete your first breathing session",
            iconEmoji = "💨",
            category = "breathing"
        ),
        BadgeDefinition(
            key = "PRANA_MASTER",
            titleFa = "استاد دم و بازدم",
            titleEn = "Prana Master",
            descFa = "تکمیل ۵۰ چرخه تنفس آرام‌بخش",
            descEn = "Complete 50 breathing cycles",
            iconEmoji = "🌊",
            category = "breathing"
        ),
        BadgeDefinition(
            key = "ZEN_BEGINNER",
            titleFa = "آغاز سکوت",
            titleEn = "Zen Beginner",
            descFa = "اتمام نخستین جلسه مدیتیشن و مراقبه",
            descEn = "Finish your first meditation session",
            iconEmoji = "🧘",
            category = "meditation"
        ),
        BadgeDefinition(
            key = "MINDFUL_HOUR",
            titleFa = "ساعت درنگ",
            titleEn = "Mindful Hour",
            descFa = "مجموع ۶۰ دقیقه مراقبه و حضور در لحظه",
            descEn = "Accumulate 60 minutes of meditation",
            iconEmoji = "⏳",
            category = "meditation"
        ),
        BadgeDefinition(
            key = "SENSORY_GROUND",
            titleFa = "پیوند با زمین",
            titleEn = "Sensory Grounding",
            descFa = "تکمیل تمرین حواس‌پنج‌گانه ۵-۴-۳-۲-۱",
            descEn = "Complete 5-4-3-2-1 sensory grounding",
            iconEmoji = "🌿",
            category = "mindfulness"
        ),
        BadgeDefinition(
            key = "BODY_SANCTUARY",
            titleFa = "معبد تن",
            titleEn = "Body Sanctuary",
            descFa = "انجام مراقبه اسکن بدن و رهاسازی تنش‌ها",
            descEn = "Complete full body scan mindfulness",
            iconEmoji = "🕊️",
            category = "mindfulness"
        ),
        BadgeDefinition(
            key = "TIME_TRAVELER",
            titleFa = "مسافر زمان",
            titleEn = "Time Traveler",
            descFa = "نوشتن نامه‌ای مهر و موم شده برای آینده",
            descEn = "Write a sealed letter to your future self",
            iconEmoji = "✉️",
            category = "letters"
        ),
        BadgeDefinition(
            key = "MIRROR_SOUL",
            titleFa = "آینه مهر",
            titleEn = "Mirror of Love",
            descFa = "انجام تمرین مهربانی با خود و آینه",
            descEn = "Complete self-love mirror exercise",
            iconEmoji = "🪞",
            category = "selflove"
        ),
        BadgeDefinition(
            key = "INNER_COMPASS",
            titleFa = "قطب‌نمای درون",
            titleEn = "Inner Compass",
            descFa = "تعریف و کشف ارزش‌های بنیادی فردی",
            descEn = "Define your core personal values",
            iconEmoji = "🧭",
            category = "selfknow"
        ),
        BadgeDefinition(
            key = "TRIUMPH_CHRONICLE",
            titleFa = "دفتر پیروزی‌ها",
            titleEn = "Triumph Chronicle",
            descFa = "ثبت دست‌کم ۵ پیروزی و دستاورد در عزت نفس",
            descEn = "Log 5 personal wins in self-esteem",
            iconEmoji = "🏆",
            category = "esteem"
        ),
        BadgeDefinition(
            key = "AUTHOR_OF_SELF",
            titleFa = "راوی خویشتن",
            titleEn = "Author of Self",
            descFa = "نگارش ۱۰ یادداشت در دفترچه روزانه خاطرات",
            descEn = "Write 10 journal reflections",
            iconEmoji = "📖",
            category = "journal"
        )
    )
}
