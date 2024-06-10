package com.sarathi.smallgroupmodule.utils

import com.nudge.core.BLANK_STRING
import com.sarathi.smallgroupmodule.constatns.SmallGroupConstants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

fun Long?.getDate(pattern: String = "dd/MM/yyyy"): String {
    if (this == null)
        return BLANK_STRING

    val formatter = SimpleDateFormat(pattern)
    return formatter.format(Date(this))
}

fun String.getDateInMillis(): Long {
    val dateSplit = this.split("/")
    val calendar: Calendar = Calendar.getInstance()
    calendar.set(dateSplit[2].toInt(), dateSplit[1].toInt(), dateSplit[0].toInt())
    return calendar.timeInMillis
}

fun Boolean.getAttendanceFromBoolean(): String {
    return if (this) SmallGroupConstants.ATTENDANCE_PRESENT else SmallGroupConstants.ATTENDANCE_ABSENT
}

fun String?.getBooleanValueFromAttendance(): Boolean {
    return this?.equals(SmallGroupConstants.ATTENDANCE_PRESENT) ?: false
}