package com.example.domain.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class JalaaliDate(
    val year: Int,
    val month: Int,
    val day: Int
) {
    fun format(): String {
        return "%04d/%02d/%02d".format(Locale.US, year, month, day)
    }

    fun toPersianDigits(): String {
        return format().map { c ->
            when (c) {
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
                else -> c
            }
        }.joinToString("")
    }

    fun formatPersian(): String {
        return "${JalaaliCalendarHelper.toPersianNumber(day)} ${monthName()} ${JalaaliCalendarHelper.toPersianNumber(year)}"
    }

    fun monthName(): String {
        return when (month) {
            1 -> "فروردین"
            2 -> "اردیبهشت"
            3 -> "خرداد"
            4 -> "تیر"
            5 -> "مرداد"
            6 -> "شهریور"
            7 -> "مهر"
            8 -> "آبان"
            9 -> "آذر"
            10 -> "دی"
            11 -> "بهمن"
            12 -> "اسفند"
            else -> ""
        }
    }
}

object JalaaliCalendarHelper {

    fun todayGregorianString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    fun todayJalaali(): JalaaliDate {
        val cal = Calendar.getInstance()
        return gregorianToJalaali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun todayPersianFormatted(): String {
        val today = todayJalaali()
        val cal = Calendar.getInstance()
        val weekdayFa = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> "شنبه"
            Calendar.SUNDAY -> "یکشنبه"
            Calendar.MONDAY -> "دوشنبه"
            Calendar.TUESDAY -> "سه‌شنبه"
            Calendar.WEDNESDAY -> "چهارشنبه"
            Calendar.THURSDAY -> "پنج‌شنبه"
            Calendar.FRIDAY -> "جمعه"
            else -> ""
        }
        return "$weekdayFa، ${toPersianNumber(today.day)} ${today.monthName()} ${toPersianNumber(today.year)}"
    }

    fun gregorianToJalaali(gy: Int, gm: Int, gd: Int): JalaaliDate {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy2 = gy - 1600
        val gm2 = gm - 1
        val gd2 = gd - 1

        var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400

        for (i in 0 until gm2) {
            gDayNo += gDaysInMonth[i]
        }
        if (gm2 > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) {
            gDayNo++
        }
        gDayNo += gd2

        var jDayNo = gDayNo - 79

        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        for (i in 0..11) {
            if (jDayNo < jDaysInMonth[i]) {
                jm = i + 1
                break
            }
            jDayNo -= jDaysInMonth[i]
        }
        val jd = jDayNo + 1
        return JalaaliDate(jy, jm, jd)
    }

    fun jalaaliToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
        val jy2 = jy - 979
        val jm2 = jm - 1
        val jd2 = jd - 1

        var jDayNo = 365 * jy2 + (jy2 / 33) * 8 + ((jy2 % 33 + 3) / 4)
        for (i in 0 until jm2) {
            jDayNo += jDaysInMonth[i]
        }
        jDayNo += jd2

        var gDayNo = jDayNo + 79

        var gy = 1600 + 400 * (gDayNo / 146097)
        gDayNo %= 146097

        var leap = true
        if (gDayNo >= 36525) {
            gDayNo--
            gy += 100 * (gDayNo / 36524)
            gDayNo %= 36524

            if (gDayNo >= 365) {
                gDayNo++
            } else {
                leap = false
            }
        }

        gy += 4 * (gDayNo / 1461)
        gDayNo %= 1461

        if (gDayNo >= 366) {
            leap = false
            gDayNo--
            gy += gDayNo / 365
            gDayNo %= 365
        }

        val gDaysInMonth = intArrayOf(31, if (leap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gm = 0
        for (i in 0..11) {
            if (gDayNo < gDaysInMonth[i]) {
                gm = i + 1
                break
            }
            gDayNo -= gDaysInMonth[i]
        }
        val gd = gDayNo + 1
        return Triple(gy, gm, gd)
    }

    fun getFirstDayOfWeek(jy: Int, jm: Int): Int {
        val (gy, gm, gd) = jalaaliToGregorian(jy, jm, 1)
        val cal = Calendar.getInstance()
        cal.set(gy, gm - 1, gd)
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
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

    fun getDaysInMonth(jy: Int, jm: Int): Int {
        if (jm in 1..6) return 31
        if (jm in 7..11) return 30
        val r = jy % 33
        val isLeap = r in intArrayOf(1, 5, 9, 13, 17, 22, 26, 30)
        return if (isLeap) 30 else 29
    }

    fun addDaysToToday(days: Int): Pair<String, JalaaliDate> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)
        val gregStr = "%04d-%02d-%02d".format(Locale.US, gy, gm, gd)
        val jDate = gregorianToJalaali(gy, gm, gd)
        return Pair(gregStr, jDate)
    }

    fun jalaaliToGregorianString(jDate: JalaaliDate): String {
        val (gy, gm, gd) = jalaaliToGregorian(jDate.year, jDate.month, jDate.day)
        return "%04d-%02d-%02d".format(Locale.US, gy, gm, gd)
    }

    fun fromGregorianString(dateStr: String): JalaaliDate {
        return try {
            val parts = dateStr.split("-")
            val gy = parts[0].toInt()
            val gm = parts[1].toInt()
            val gd = parts[2].toInt()
            gregorianToJalaali(gy, gm, gd)
        } catch (e: Exception) {
            val cal = Calendar.getInstance()
            gregorianToJalaali(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
        }
    }

    fun toPersianNumber(number: Int): String {
        return toPersianNumber(number.toString())
    }

    fun toPersianNumber(number: Long): String {
        return toPersianNumber(number.toString())
    }

    fun toPersianNumber(text: String): String {
        return text.map { c ->
            when (c) {
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
                else -> c
            }
        }.joinToString("")
    }
}
