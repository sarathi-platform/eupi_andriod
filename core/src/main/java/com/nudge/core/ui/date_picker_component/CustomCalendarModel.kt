package com.nudge.core.ui.date_picker_component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Immutable
import java.util.Locale

@ExperimentalMaterial3Api
interface CustomCalendarModel {

    val today: CustomCalendarDate

    val firstDayOfWeek: Int

    val weekdayNames: List<Pair<String, String>>

    fun getDateInputFormat(locale: Locale = Locale.ENGLISH): CustomDateInputFormat

    fun getCanonicalDate(timeInMillis: Long): CustomCalendarDate

    fun getMonth(timeInMillis: Long): CustomCalendarMonth

    fun getMonth(date: CustomCalendarDate): CustomCalendarMonth

    fun getMonth(year: Int, /* @IntRange(from = 1, to = 12) */ month: Int): CustomCalendarMonth

    fun getDayOfWeek(date: CustomCalendarDate): Int

    fun plusMonths(from: CustomCalendarMonth, addedMonthsCount: Int): CustomCalendarMonth

    fun minusMonths(from: CustomCalendarMonth, subtractedMonthsCount: Int): CustomCalendarMonth

    fun formatWithSkeleton(
        month: CustomCalendarMonth,
        skeleton: String,
        locale: Locale = Locale.ENGLISH
    ): String =
        formatWithSkeleton(month.startUtcTimeMillis, skeleton, locale)

    fun formatWithSkeleton(
        date: CustomCalendarDate,
        skeleton: String,
        locale: Locale = Locale.ENGLISH
    ): String = formatWithSkeleton(date.utcTimeMillis, skeleton, locale)

    fun formatWithPattern(
        utcTimeMillis: Long,
        pattern: String = "dd/MM/yyyy",
        locale: Locale = Locale.ENGLISH
    ): String

    fun parse(date: String, pattern: String): CustomCalendarDate?
}

@ExperimentalMaterial3Api
data class CustomCalendarDate(
    val year: Int,
    val month: Int,
    val dayOfMonth: Int,
    val utcTimeMillis: Long
) : Comparable<CustomCalendarDate> {
    override operator fun compareTo(other: CustomCalendarDate): Int =
        this.utcTimeMillis.compareTo(other.utcTimeMillis)

    fun format(
        calendarModel: CustomCalendarModel,
        skeleton: String,
        locale: Locale = Locale.ENGLISH
    ): String =
        calendarModel.formatWithSkeleton(this, skeleton, locale)
}

@ExperimentalMaterial3Api
data class CustomCalendarMonth(
    val year: Int,
    val month: Int,
    val numberOfDays: Int,
    val daysFromStartOfWeekToFirstOfMonth: Int,
    val startUtcTimeMillis: Long
) {

    val endUtcTimeMillis: Long = startUtcTimeMillis + (numberOfDays * MillisecondsIn24Hours) - 1

    fun indexIn(years: IntRange): Int {
        return (year - years.first) * 12 + month - 1
    }

    fun format(
        calendarModel: CustomCalendarModel,
        skeleton: String,
        locale: Locale = Locale.ENGLISH
    ): String =
        calendarModel.formatWithSkeleton(this, skeleton, locale)
}

@ExperimentalMaterial3Api
@Immutable
data class CustomDateInputFormat(
    val patternWithDelimiters: String,
    val delimiter: Char
) {
    val patternWithoutDelimiters: String = patternWithDelimiters.replace(delimiter.toString(), "")
}

@ExperimentalMaterial3Api
fun datePatternAsInputFormat(localeFormat: String): CustomDateInputFormat {
    val patternWithDelimiters = localeFormat.replace(Regex("[^dMy/\\-.]"), "")
        .replace(Regex("d{1,2}"), "dd")
        .replace(Regex("M{1,2}"), "MM")
        .replace(Regex("y{1,4}"), "yyyy")
        .replace("My", "M/y") // Edge case for the Kako locale
        .removeSuffix(".") // Removes a dot suffix that appears in some formats

    val delimiterRegex = Regex("[/\\-.]")
    val delimiterMatchResult = delimiterRegex.find(patternWithDelimiters)
    val delimiterIndex = delimiterMatchResult!!.groups[0]!!.range.first
    val delimiter = patternWithDelimiters.substring(delimiterIndex, delimiterIndex + 1)
    return CustomDateInputFormat(
        patternWithDelimiters = patternWithDelimiters,
        delimiter = delimiter[0]
    )
}

const val DaysInWeek: Int = 7
const val MillisecondsIn24Hours = 86400000L
