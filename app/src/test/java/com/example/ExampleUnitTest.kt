package com.example

import com.example.domain.model.GratitudeTreeState
import com.example.domain.model.JalaaliCalendarHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testGratitudeTreeGrowthMath() {
        // 0 entries
        val state0 = GratitudeTreeState.calculate(0)
        assertEquals(0, state0.currentBlossoms)
        assertEquals(0, state0.currentRedFruits)
        assertEquals(0, state0.currentGoldenFruits)
        assertEquals("بذر اولیه سپاس", state0.levelNameFa)

        // 3 entries -> 1 blossom
        val state3 = GratitudeTreeState.calculate(3)
        assertEquals(1, state3.currentBlossoms)
        assertEquals(0, state3.currentRedFruits)
        assertEquals(0, state3.currentGoldenFruits)

        // 9 entries (3 blossoms) -> 1 red fruit
        val state9 = GratitudeTreeState.calculate(9)
        assertEquals(0, state9.currentBlossoms)
        assertEquals(1, state9.currentRedFruits)
        assertEquals(0, state9.currentGoldenFruits)

        // 27 entries (3 red fruits) -> 1 golden fruit
        val state27 = GratitudeTreeState.calculate(27)
        assertEquals(0, state27.currentBlossoms)
        assertEquals(0, state27.currentRedFruits)
        assertEquals(1, state27.currentGoldenFruits)

        // 35 entries -> 1 golden fruit, 0 red fruit, 2 blossoms, 2 raw entries
        val state35 = GratitudeTreeState.calculate(35)
        assertEquals(1, state35.currentGoldenFruits)
        assertEquals(0, state35.currentRedFruits)
        assertEquals(2, state35.currentBlossoms)
        assertEquals(2, state35.entriesToNextBlossom)
    }

    @Test
    fun testJalaaliCalendarConversion() {
        val jalaaliDate = JalaaliCalendarHelper.gregorianToJalaali(2026, 3, 21) // Nowruz
        assertEquals(1405, jalaaliDate.year)
        assertEquals(1, jalaaliDate.month)
        assertEquals(1, jalaaliDate.day)
        assertEquals("فروردین", jalaaliDate.monthName())

        val persianNum = JalaaliCalendarHelper.toPersianNumber(12345)
        assertEquals("۱۲۳۴۵", persianNum)
    }
}
