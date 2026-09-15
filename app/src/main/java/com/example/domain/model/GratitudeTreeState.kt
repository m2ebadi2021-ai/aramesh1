package com.example.domain.model

data class GratitudeTreeState(
    val totalEntries: Int = 0,
    val totalBlossoms: Int = 0,
    val totalRedFruits: Int = 0,
    val totalGoldenFruits: Int = 0,
    // Active counts displayed on the current tree tier:
    val currentBlossoms: Int = 0,
    val currentRedFruits: Int = 0,
    val currentGoldenFruits: Int = 0,
    // Progress towards next blossom (0, 1, or 2 out of 3)
    val entriesToNextBlossom: Int = 0,
    val levelNameFa: String = "نهال سپاسگزاری",
    val levelNameEn: String = "Gratitude Sprout"
) {
    val stage: String get() = levelNameFa
    val totalGratitudes: Int get() = totalEntries
    val progressToNextStage: Float get() = (entriesToNextBlossom.toFloat() / 3f).coerceIn(0f, 1f)

    companion object {
        fun calculate(totalEntries: Int): GratitudeTreeState {
            val totalB = totalEntries / 3
            val totalR = totalB / 3
            val totalG = totalR / 3

            val currentB = totalB % 3
            val currentR = totalR % 3
            val currentG = totalG

            val entriesToNext = totalEntries % 3

            val (levelFa, levelEn) = when {
                totalG >= 1 -> Pair("درخت زرین جاودان", "Eternal Golden Sovereign Tree")
                totalR >= 2 -> Pair("درخت پربار میوه‌دار", "Bountiful Fruiting Tree")
                totalR >= 1 -> Pair("درخت سرخ نوبر", "Red Fruit Orchard Tree")
                totalB >= 2 -> Pair("درخت پرشکوفه بهاری", "Spring Bloom Tree")
                totalB >= 1 -> Pair("جوانه پرشکوفه", "Blooming Sapling")
                totalEntries >= 1 -> Pair("نهال امید و قدردانی", "Sprout of Hope")
                else -> Pair("بذر اولیه سپاس", "Initial Seed")
            }

            return GratitudeTreeState(
                totalEntries = totalEntries,
                totalBlossoms = totalB,
                totalRedFruits = totalR,
                totalGoldenFruits = totalG,
                currentBlossoms = currentB,
                currentRedFruits = currentR,
                currentGoldenFruits = currentG,
                entriesToNextBlossom = entriesToNext,
                levelNameFa = levelFa,
                levelNameEn = levelEn
            )
        }
    }
}
