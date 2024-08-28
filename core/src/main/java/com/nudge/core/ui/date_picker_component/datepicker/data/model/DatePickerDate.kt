package com.nudge.core.ui.date_picker_component.datepicker.data.model

import android.icu.util.Calendar

data class DatePickerDate(
    val year: Int,
    val month: Int,
    val day: Int
)

object DefaultDate {
    private val calendar = Calendar.getInstance()
    val defaultDate = DatePickerDate(
        calendar[Calendar.YEAR],
        calendar[Calendar.MONTH],
        calendar[Calendar.DAY_OF_MONTH]
    )
}
