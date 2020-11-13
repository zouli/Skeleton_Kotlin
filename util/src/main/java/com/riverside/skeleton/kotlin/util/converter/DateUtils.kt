package com.riverside.skeleton.kotlin.util.converter

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期工具    1.0
 * b_e  2019/05/07
 */
class DateUtils {
    companion object {
        const val DATE_FORMAT_PATTERN1 = "yyyy年MM月dd日"
        const val DATE_FORMAT_PATTERN2 = "yyyy-MM-dd HH:mm:ss"
        const val DATE_FORMAT_PATTERN3 = "yyyy-MM-dd"
        const val TIME_FORMAT_PATTERN1 = "%02d:%02d:%02d"
    }
}

val Date.calendar: Calendar get() = Calendar.getInstance().apply { time = this@calendar }

fun Date.toString(pattern: String): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun Calendar.toString(pattern: String): String = this.time.toString(pattern)

fun String.toDate(pattern: String): Date =
    try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
    } catch (_: ParseException) {
        Date(0)
    }

fun String.toCalendar(pattern: String): Calendar = this.toDate(pattern).calendar

/**
 * 取得日期0点
 */
val Calendar.timeZero: Calendar
    get() = Calendar.getInstance().apply {
        set(
            this@timeZero.get(Calendar.YEAR),
            this@timeZero.get(Calendar.MONTH),
            this@timeZero.get(Calendar.DAY_OF_MONTH),
            0, 0, 0
        )
    }

operator fun Calendar.minus(cal2: Calendar): TimeInterval =
    TimeInterval(this.timeInMillis - cal2.timeInMillis)

/**
 * 判断是否为同一天
 */
infix fun Calendar.isSame(cal2: Calendar): Boolean = (this - cal2).days == 0

/**
 * 时间间隔类    1.0
 * b_e  2019/05/07
 */
class TimeInterval(timeInMillis: Long) {
    val millis: Long = timeInMillis
    val seconds: Int = (millis / 1000).toInt()
    val minutes: Int = seconds / 60
    val hours: Int = minutes / 60
    val days: Int = hours / 24

    fun format(pattern: String) =
        pattern.format(
            Locale.getDefault(),
            this.seconds / 3600,
            this.seconds % 3600 / 60,
            this.seconds % 60
        )

    val formatHMS = this.format(DateUtils.TIME_FORMAT_PATTERN1)
}