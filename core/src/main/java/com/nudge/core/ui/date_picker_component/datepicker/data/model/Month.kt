package com.nudge.core.ui.date_picker_component.datepicker.data.model

import com.nudge.core.ui.date_picker_component.datepicker.enums.Days

data class Month(
    val name: String,
    val numberOfDays: Int,
    val firstDayOfMonth: Days,
    val number: Int
)