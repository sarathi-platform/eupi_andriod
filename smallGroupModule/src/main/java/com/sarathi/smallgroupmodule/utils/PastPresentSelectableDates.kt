package com.sarathi.smallgroupmodule.utils

//object PastPresentSelectableDates: SelectableDates {
//    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
//        return utcTimeMillis <= System.currentTimeMillis()
//    }
//
//    override fun isSelectableYear(year: Int): Boolean {
//        return year <= LocalDate.now().year
//    }
//}

//fun Long?.millisToLocalDateTime(): LocalDateTime {
//    return Instant.fromEpochMilliseconds(this ?: 0)
//        .toLocalDateTime(TimeZone.currentSystemDefault())
//}
//
//// Used in Composable
//fun Long?.millisToLocalDate(): LocalDate {
//    return this.millisToLocalDateTime().date
//}
